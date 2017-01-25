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

	public ExternalRNASeqImporter() {
		this.provider = new EntityProvider();
	}

	public void createCounts(Path countFile) {
		
		counter = 0;

		ExternalRNASeqResource resource = readData(countFile);

		linkGenesToResource(resource);
		
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

		resource.setName(name);
		
		provider.sessionStart();

		provider.persist(resource);
		
		provider.sessionEnd();

		try {

			BufferedReader reader = new BufferedReader(new FileReader(referenceFile.toString()));
			String header = reader.readLine();
			String[] resources = header.split("\t");

			resourcePosArray = new ExternalRNASeqEntry[resources.length - 2];
			
			provider.sessionStart();


			for (int i = 2; i < resources.length; ++i) {
				resourcePosArray[i - 2] = new ExternalRNASeqEntry();
				resourcePosArray[i - 2].setName(resources[i]);
				resourcePosArray[i - 2].setResource(resource);
				resource.addEntry(resourcePosArray[i - 2]);
			}
			
			provider.persist(resource);

			provider.sessionEnd();			

			Stream<String> lines = Files.lines(referenceFile, Charset.defaultCharset());

			provider.sessionStart();
			lines.skip(1L).forEachOrdered(line -> createDatapointFromLine(resource, line));
			provider.persist(resource);
			provider.sessionEnd();

			lines.close();
			reader.close();

		} catch (IOException e) {
			e.getMessage();
		}

		return resource;
	}

	private void createDatapointFromLine(ExternalRNASeqResource resource, String line) {
		
		if (counter % 1000 == 0 && counter > 0) {
			System.out.println("ExternalRNASeqImport:\tHandled line " + counter + ".");
			
			provider.persist(resource);
						
			provider.sessionEnd();
			
			provider.sessionStart();
		}

		String[] fields = line.split("\t");
		String entrezId = fields[0];
		String geneSymbol = fields[1];

		GenericGene gene = new GenericGene(entrezId, geneSymbol);

		resource.addGenericGene(gene);
		//provider.persist(gene);

		for (int i = 2; i < fields.length; ++i) {
			ExternalRNASeqDatapoint datapoint = new ExternalRNASeqDatapoint(Float.parseFloat(fields[i]));
			datapoint.setEntry(resourcePosArray[i - 2]);
			datapoint.setGene(gene);
			gene.addDatapoint(datapoint);
			resourcePosArray[i - 2].addDatapoint(datapoint);
			//provider.persist(resourcePosArray[i - 2]);
		}

		++counter;
		

	}
}
