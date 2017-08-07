package at.ac.imp.creators;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.persistence.TypedQuery;

import at.ac.imp.palantir.exceptions.DatabaseException;
import at.ac.imp.palantir.model.Essentialome;
import at.ac.imp.palantir.model.ExternalRNASeqDatapoint;
import at.ac.imp.palantir.model.ExternalRNASeqEntry;
import at.ac.imp.palantir.model.ExternalRNASeqResource;
import at.ac.imp.palantir.model.Gene;
import at.ac.imp.palantir.model.GenericGene;
import at.ac.imp.palantir.model.Reference;
import at.ac.imp.resources.EntityProvider;

public class ExternalRNASeqImporter {

	private int counter = 0;

	private EntityProvider provider;

	private ExternalRNASeqEntry[] resourcePosArray;

	private Map<String, GenericGene> geneMap = new HashMap<String, GenericGene>();

	public ExternalRNASeqImporter() {
		this.provider = new EntityProvider();
	}

	public void createCounts(Path countFile) {

		counter = 0;

		loadGenes();

		ExternalRNASeqResource resource = readData(countFile);

		if (resource != null) {

			linkGenesToResource(resource);

		}
	}

	private void loadGenes() {

		provider.sessionStart();

		List<GenericGene> publicGenes = provider.getAllGenericGenes();

		provider.sessionEnd();

		for (GenericGene gene : publicGenes) {
			geneMap.put(gene.getEntrezId(), gene);
		}

	}

	private void linkGenesToResource(ExternalRNASeqResource resource) {

		provider.sessionStart();

		List<Reference> references = provider.getAllReferences();

		for (Reference reference : references) {

			Map<String, Gene> geneMap = new HashMap<String, Gene>();
			for (Gene gene : reference.getGenes()) {
				geneMap.put(gene.getEntrezId(), gene);
			}

			for (GenericGene genericGene : resource.getGenes()) {
				Gene gene = geneMap.get(genericGene.getEntrezId());
				if (gene != null) {
					genericGene.addGene(gene);
				}
			}
		}

		provider.merge(resource);

		provider.sessionEnd();

	}

	private ExternalRNASeqResource readData(Path referenceFile) {

		ExternalRNASeqResource resource = new ExternalRNASeqResource();

		String name = referenceFile.getFileName().toString();
		name = name.replaceAll("\\.palantir.*", "");

		System.out.println("ExternalRNASeqImport:\tImporting ExternalRNASeqResource " + name + ".");

		ExternalRNASeqResource query = provider.findExternalRNASeqResourceByName(name);

		if (query != null && !query.isValid()) {

			System.out.println("Deleting incomplete ExternalRNASeqResource import.");

			provider.sessionStart();

			provider.delete(query);

			provider.sessionEnd();
		}

		if (provider.findExternalRNASeqResourceByName(name) == null
				|| !provider.findExternalRNASeqResourceByName(name).isValid()) {

			resource.setName(name);

			provider.sessionStart();

			provider.persist(resource);

			provider.sessionEnd();

			try {

				BufferedReader reader = new BufferedReader(new FileReader(referenceFile.toString()));
				String header = reader.readLine();
				// First lines contains # followed by number of header lines.
				int headerlines = Integer.parseInt(header.substring(1));

				String organism = reader.readLine().substring(1);

				header = reader.readLine();
				String[] resources = header.split("\t");

				String[] contexts = null;

				// No context info
				if (headerlines > 2) {
					header = reader.readLine();
					contexts = header.split("\t");
				}

				resourcePosArray = new ExternalRNASeqEntry[resources.length - 2];

				provider.sessionStart();

				for (int i = 2; i < resources.length; ++i) {
					resourcePosArray[i - 2] = new ExternalRNASeqEntry();
					resourcePosArray[i - 2].setName(resources[i]);
					resourcePosArray[i - 2].setResource(resource);
					if (contexts != null) {
						resourcePosArray[i - 2].setContext(contexts[i]);
					}
					resource.addEntry(resourcePosArray[i - 2]);
				}

				provider.persist(resource);

				provider.sessionEnd();

				Stream<String> lines = Files.lines(referenceFile, Charset.defaultCharset());

				provider.sessionStart();
				lines.skip(headerlines + 1).forEachOrdered(line -> createDatapointFromLine(resource, organism, line));
				provider.persist(resource);
				provider.sessionEnd();

				lines.close();
				reader.close();

			} catch (IOException e) {
				e.getMessage();
			}
			
			provider.sessionStart();
			resource.setValid(true);
			provider.persist(resource);
			provider.sessionEnd();

			return resource;

		} else {
			System.out.println("ExternalRNASeqResource imported already.");
			return null;
		}
	}

	private void createDatapointFromLine(ExternalRNASeqResource resource, String organism, String line) {

		if (counter % 1000 == 0 && counter > 0) {
			System.out.println("ExternalRNASeqImport:\tHandled line " + counter + ".");

			provider.persist(resource);

			provider.sessionEnd();

			provider.sessionStart();
		}

		// if (resource.getName().equals("Leucegene_CBFB-MYH11")) {
		// System.out.println("whatever");
		// }

		String[] fields = line.split("\t");
		String entrezId = fields[0];
		String geneSymbol = fields[1];

		GenericGene gene = null;

		if (!geneMap.containsKey(entrezId)) {
			gene = new GenericGene(entrezId, geneSymbol);
		} else {
			gene = geneMap.get(entrezId);
		}

		gene.setOrganism(organism);

		// GenericGene gene = new GenericGene(entrezId, geneSymbol);

		resource.addGenericGene(gene);
		gene.addResource(resource);
		// provider.persist(gene);

		for (int i = 2; i < fields.length; ++i) {
			ExternalRNASeqDatapoint datapoint = new ExternalRNASeqDatapoint(Float.parseFloat(fields[i]));
			datapoint.setEntry(resourcePosArray[i - 2]);
			datapoint.setGene(gene);
			gene.addDatapoint(datapoint);
			resourcePosArray[i - 2].addDatapoint(datapoint);
			// provider.persist(resourcePosArray[i - 2]);
		}
		++counter;
	}
}
