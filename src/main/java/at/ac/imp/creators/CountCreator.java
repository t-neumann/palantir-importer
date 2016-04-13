package at.ac.imp.creators;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import at.ac.imp.palantir.exceptions.DatabaseException;
import at.ac.imp.palantir.model.Alignment;
import at.ac.imp.palantir.model.Datapoint;
import at.ac.imp.palantir.model.ExpressionValue;
import at.ac.imp.palantir.model.Gene;
import at.ac.imp.palantir.model.QueueSampleMetaInfo;
import at.ac.imp.palantir.model.Reference;
import at.ac.imp.palantir.model.Result;
import at.ac.imp.palantir.model.Sample;
import at.ac.imp.palantir.util.QueueSampleInfoRetreiverBean;
import at.ac.imp.resources.EntityProvider;
import at.ac.imp.resources.PersistenceProvider;

public class CountCreator {

	private static final int ALIGNMENT_LEVEL = 3;
	private static final int REFERENCE_LEVEL = 2;
	private static final int SAMPLE_LEVEL = 4;
	private static final int BUILD_LEVEL = 5;

	private static final int FIELD_LIMIT = 3;

	private static final int GENE_FIELD = 0;
	private static final int LENGTH_FIELD = 1;
	private static final int COUNT_FIELD = 2;

	// private EntityManager em;
	private EntityProvider provider;

	public CountCreator() {
		// this.em = PersistenceProvider.INSTANCE.getEntityManager();
		this.provider = new EntityProvider();
	}

	public void createCounts(Path countFile) {
		// Genome build is parent dir name

		provider.sessionStart();
		// em.getTransaction().begin();

		String referenceName = countFile.getName(countFile.getNameCount() - CountCreator.REFERENCE_LEVEL).toString();
		String alignmentName = countFile.getName(countFile.getNameCount() - CountCreator.ALIGNMENT_LEVEL).toString();

		int sampleId = Integer
				.parseInt(countFile.getName(countFile.getNameCount() - CountCreator.SAMPLE_LEVEL).toString());

		String build = countFile.getName(countFile.getNameCount() - CountCreator.BUILD_LEVEL).toString();

		Sample sample = provider.getSampleById(sampleId);

		QueueSampleInfoRetreiverBean queueSampleInfoRetreiver = new QueueSampleInfoRetreiverBean();

		if (sample == null) {
			sample = new Sample(sampleId);
			QueueSampleMetaInfo metainfo = queueSampleInfoRetreiver.getInfoForSample(sample.getId());
			sample.setMetaInfo(metainfo);
		} else {
			Logger.getAnonymousLogger().log(Level.INFO, "Sample " + sample.getId() + " exists. No import.");
		}

		Alignment alignment = provider.getAlignmentFromSample(sample, alignmentName);

		if (alignment == null) {
			alignment = new Alignment();
			alignment.setName(countFile.getName(countFile.getNameCount() - CountCreator.ALIGNMENT_LEVEL).toString());
			alignment.setBuild(build);
			alignment.setSample(sample);
			sample.addAlignment(alignment);
		} else {
			Logger.getAnonymousLogger().log(Level.INFO, "Alignment " + alignment.getId() + " exists. No import.");
		}

		Reference reference = null;

		try {
			reference = provider.getReferenceByName(referenceName);

			// em.getTransaction().begin();

			if (provider.findResultByAlignmentIdAndReference(alignment.getId(), reference.getName()) == null) {
				Logger.getAnonymousLogger().log(Level.WARNING, "No results for given reference " + reference.getName());

				Result result = new Result();
				result.setAlignment(alignment);
				result.setReference(reference);

				// em.persist(sample);
				// em.persist(result);
				provider.persist(sample);
				provider.persist(result);

				Map<String, ExpressionValue> datapoints = readData(countFile);

				calculateRPKMs(datapoints.values());
				calculateTPMs(datapoints.values());

				if (datapoints.size() != reference.getGenes().size()) {
					throw new DatabaseException("Different numbers of reads in reference and count file! Read "
							+ datapoints.size() + " vs " + reference.getGenes().size() + " in reference");
				}

				// Iterator<Gene> geneIterator =
				// reference.getGenes().iterator();
				// Iterator<ExpressionValue> datapointIterator =
				// datapoints.iterator();

				for (Gene gene : reference.getGenes()) {
					Datapoint datapoint = datapoints.get(gene.getGeneSymbol());
					// em.persist(datapoint);
					provider.persist(datapoint);
					gene.addDatapoint(datapoint);
					// em.merge(gene);
					provider.merge(gene);
					datapoint.setGene(gene);
					datapoint.setResult(result);
				}
				// while(geneIterator.hasNext()) {
				// Gene gene = geneIterator.next();
				// Datapoint datapoint = datapointIterator.next();
				// em.persist(datapoint);
				// gene.addDatapoint(datapoint);
				// em.merge(gene);
				// datapoint.setGene(gene);
				// datapoint.setResult(result);
				// }
				//
				// if (datapointIterator.hasNext()) {
				// throw new DatabaseException("More genes in countfile than in
				// reference");
				// }
				@SuppressWarnings("unchecked")
				Collection<Datapoint> toPut = (Collection<Datapoint>) (Collection<?>) datapoints.values();
				result.setDatapoints(toPut);
			} else {
				Logger.getAnonymousLogger().log(Level.INFO, "Results for sample " + sample.getId() + " alignment " + alignment.getId() + " reference " + reference.getName() + " exists. No import.");
			}

			// em.getTransaction().commit();
			provider.sessionEnd();

		} catch (DatabaseException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "File " + countFile.toString() + " not added because reference " + referenceName + " not found");
			Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage());
		}
	}

	private void calculateTPMs(Collection<ExpressionValue> datapoints) {

		float runningSum = 0.0f;
		for (ExpressionValue value : datapoints) {
			value.setTpm((float) value.getCount() / value.getLength());
			runningSum += value.getTpm();
		}
		for (ExpressionValue value : datapoints) {
			value.setTpm(value.getTpm() / runningSum * 1e6f);
		}

	}

	private void calculateRPKMs(Collection<ExpressionValue> datapoints) {
		float runningSum = 0.0f;
		for (ExpressionValue value : datapoints) {
			runningSum += value.getCount();
			value.setRpkm(value.getCount() * 1e9f / value.getLength());
		}
		for (ExpressionValue value : datapoints) {
			value.setRpkm(value.getRpkm() / runningSum);
		}

	}

	private Map<String, ExpressionValue> readData(Path referenceFile) {

		Map<String, ExpressionValue> datapoints = new HashMap<String, ExpressionValue>();
		// List<ExpressionValue> datapoints = new ArrayList<ExpressionValue>();
		try (Stream<String> lines = Files.lines(referenceFile, Charset.defaultCharset())) {
			// Skip header
			lines.skip(1L).forEachOrdered(line -> createDatapointFromLine(line, datapoints));
		} catch (IOException e) {
			e.getMessage();
		}

		return datapoints;
	}

	private void createDatapointFromLine(String line, Map<String, ExpressionValue> datapoints) {

		String[] fields = line.split("\t");

		ExpressionValue datapoint = null;

		if (fields.length >= CountCreator.FIELD_LIMIT) {
			datapoint = new ExpressionValue(Integer.parseInt(fields[CountCreator.COUNT_FIELD]), 0, 0);
			datapoint.setLength(Integer.parseInt(fields[CountCreator.LENGTH_FIELD]));
		} else {
			Logger.getAnonymousLogger().log(Level.WARNING, "Line " + line + " contains less than " + CountCreator.FIELD_LIMIT + " fields");
		}

		if (datapoint != null) {
			datapoints.put(fields[CountCreator.GENE_FIELD], datapoint);
		}
	}

}
