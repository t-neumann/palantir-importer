package at.ac.imp.creators;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import at.ac.imp.palantir.exceptions.DatabaseException;
import at.ac.imp.palantir.model.Alignment;
import at.ac.imp.palantir.model.Datapoint;
import at.ac.imp.palantir.model.ExpressionValue;
import at.ac.imp.palantir.model.Gene;
import at.ac.imp.palantir.model.QueueSampleMetaInfo;
import at.ac.imp.palantir.model.Reference;
import at.ac.imp.palantir.model.Result;
import at.ac.imp.palantir.model.Sample;
import at.ac.imp.resources.EntityProvider;
import at.ac.imp.resources.PersistenceProvider;

public class CountCreator {
	
	private static final int ALIGNMENT_LEVEL = 3;
	private static final int REFERENCE_LEVEL = 2;
	private static final int SAMPLE_LEVEL = 4;
	private static final int BUILD_LEVEL = 5;
	
	private static final int FIELD_LIMIT = 3;
	
	private static final int COUNT_FIELD = 2;
	
	private EntityManager em;
	private EntityProvider provider;
	
	public CountCreator() {
		this.em = PersistenceProvider.INSTANCE.getEntityManager();
		this.provider = new EntityProvider();
	}
	
	public void createCounts(Path countFile) {
		// Genome build is parent dir name
		
		String referenceName = countFile.getName(countFile.getNameCount() - CountCreator.REFERENCE_LEVEL).toString();
		String alignmentName = countFile.getName(countFile.getNameCount() - CountCreator.ALIGNMENT_LEVEL).toString();
		int sampleId = Integer.parseInt(countFile.getName(countFile.getNameCount() - CountCreator.SAMPLE_LEVEL).toString());
		String build = countFile.getName(countFile.getNameCount() - CountCreator.BUILD_LEVEL).toString();
		
		Sample sample = provider.getSampleById(sampleId);
		
		if (sample == null) {
			sample = new Sample(sampleId);
			QueueSampleMetaInfo metainfo = new QueueSampleMetaInfo();
			metainfo.setSampleId(sampleId);
			sample.setMetaInfo(metainfo);
		}
		
		Alignment alignment = provider.getAlignmentFromSample(sample, alignmentName);
		
		if (alignment == null) {
			alignment = new Alignment();
			alignment.setName(countFile.getName(countFile.getNameCount() - CountCreator.ALIGNMENT_LEVEL).toString());
			alignment.setBuild(build);
			alignment.setSample(sample);
			sample.addAlignment(alignment);
		}
		
		Reference reference = null;
		
		try {
			reference = provider.getReferenceByName(referenceName);
			
			em.getTransaction().begin();
			
			Result result = new Result();
			result.setAlignment(alignment);
			result.setReference(reference);
			
			em.persist(sample);
			em.persist(result);
						
			Collection<Datapoint> datapoints = readData(countFile);
			
			Iterator<Gene> geneIterator = reference.getGenes().iterator();
			Iterator<Datapoint> datapointIterator = datapoints.iterator();
			
			while(geneIterator.hasNext()) {
				Gene gene = geneIterator.next();
				Datapoint datapoint = datapointIterator.next();
				gene.addDatapoint(datapoint);
				datapoint.setGene(gene);
				datapoint.setResult(result);
			}
			
			if (datapointIterator.hasNext()) {
				throw new DatabaseException("More genes in countfile than in reference");
			}
			
			result.setDatapoints(datapoints);
			
			em.getTransaction().commit();
			
		} catch (DatabaseException e) {
			Logger log = Logger.getLogger(this.getClass());
			log.log(Level.FATAL, "File " + countFile.toString() + " not added because reference " + referenceName + " not found");
			log.log(Level.FATAL, e.getMessage());
		}	
	}
	
	private Collection<Datapoint> readData(Path referenceFile) {
		
		List<Datapoint> datapoints = new ArrayList<Datapoint>();
		try (Stream<String> lines = Files.lines(referenceFile, Charset.defaultCharset())) {
			// Skip header
			lines.skip(1L).forEachOrdered(line -> createDatapointFromLine(line, datapoints));
		} catch (IOException e) {
			e.getMessage();
		}
		
		return datapoints;
	}
	
	private void createDatapointFromLine(String line, List<Datapoint> datapoints) {

		String[] fields = line.split("\t");
		
		Datapoint datapoint = null;
		
		if (fields.length >= CountCreator.FIELD_LIMIT) {
			datapoint = new ExpressionValue(Integer.parseInt(fields[CountCreator.COUNT_FIELD]), 0, 0);
		} else {
			Logger.getLogger(ReferenceCreator.class).log(Level.WARN, "Line " + line + " contains less than " + 3 + " fields");
		}
		
		if (datapoint != null) {
			datapoints.add(datapoint);
		}
	}

}
