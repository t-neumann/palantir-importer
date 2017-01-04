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
import at.ac.imp.palantir.model.Reference;
import at.ac.imp.palantir.model.Sample;
import at.ac.imp.resources.EntityProvider;

public class ExternalRNASeqImporter {
	
	private int counter = 0;

	private EntityProvider provider;

	private ExternalRNASeqEntry[] resourcePosArray;

	public ExternalRNASeqImporter() {
		this.provider = new EntityProvider();
	}

	public void createCounts(Path countFile) {
		
		provider.sessionStart();
		
		ExternalRNASeqResource resource = readData(countFile);
		
		linkGenesToResource(resource);
		
		provider.sessionEnd();
	}
	
	private void linkGenesToResource (ExternalRNASeqResource resource){
		List<Reference> references = provider.getAllReferences();
		
		for (Reference reference : references) {
			
			resource.addReference(reference);
			
			Map<String, Gene> geneMap = new HashMap<String, Gene>();
			for (Gene gene : reference.getGenes()) {
				geneMap.put(gene.getEntrezId(), gene);
			}
						
			for (ExternalRNASeqEntry entry : resource.getEntries()) {
				for (ExternalRNASeqDatapoint datapoint : entry.getDatapoints()) {
					Gene gene = geneMap.get(datapoint.getEntrezId());
					if (gene != null) {
						datapoint.addGene(gene);
					}
				}
			}
		}
	}

	private ExternalRNASeqResource readData(Path referenceFile) {
		
		ExternalRNASeqResource resource = new ExternalRNASeqResource();
		
		resource.setName(referenceFile.getFileName().toString());
		
		provider.persist(resource);

		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(referenceFile.toString()));
			String header = reader.readLine();
			String[] resources = header.split("\t");

			resourcePosArray = new ExternalRNASeqEntry[resources.length - 2];

			for (int i = 2; i < resources.length; ++i) {
				resourcePosArray[i - 2] = new ExternalRNASeqEntry();
				resourcePosArray[i - 2].setName(resources[i]);
				resourcePosArray[i - 2].setResource(resource);
			}

			Stream<String> lines = Files.lines(referenceFile, Charset.defaultCharset());

			lines.skip(1L).forEachOrdered(line -> createDatapointFromLine(line));
			
			lines.close();
			reader.close();
			
			for (int i = 0; i < resourcePosArray.length; ++i) {
				resource.addEntry(resourcePosArray[i]);
			}
			
			provider.persist(resource);
			
		} catch (IOException e) {
			e.getMessage();
		}
		
		return resource;
	}

	private void createDatapointFromLine(String line) {
		
		if (counter % 100 == 0 && counter > 0) {
			System.out.println("ExternalRNASeqImport:\tHandled line " + counter + ".");
		}
		
		String[] fields = line.split("\t");
		String entrezId = fields[0];
		String geneSymbol = fields[1];

		for (int i = 2; i < fields.length; ++i) {
			ExternalRNASeqDatapoint datapoint = new ExternalRNASeqDatapoint(entrezId, geneSymbol, Float.parseFloat(fields[i]));
			datapoint.setEntry(resourcePosArray[i - 2]);
			resourcePosArray[i - 2].addDatapoint(datapoint);
			provider.persist(resourcePosArray[i - 2]);
		}
		
		++counter;

	}
}
