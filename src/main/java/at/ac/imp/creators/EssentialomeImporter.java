package at.ac.imp.creators;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import at.ac.imp.palantir.model.Essentialome;
import at.ac.imp.palantir.model.EssentialomeEntry;
import at.ac.imp.palantir.model.ExternalRNASeqDatapoint;
import at.ac.imp.palantir.model.ExternalRNASeqEntry;
import at.ac.imp.palantir.model.ExternalRNASeqResource;
import at.ac.imp.palantir.model.Gene;
import at.ac.imp.palantir.model.GenericGene;
import at.ac.imp.palantir.model.Reference;
import at.ac.imp.palantir.model.ScreenGene;
import at.ac.imp.resources.EntityProvider;

public class EssentialomeImporter {
	
	private int counter = 0;

	private EntityProvider provider;

	private ExternalRNASeqEntry[] resourcePosArray;

	public EssentialomeImporter() {
		this.provider = new EntityProvider();
	}

	public void createCounts(Path countFile) {

		Essentialome essentialome = readData(countFile);

		//linkGenesToEssentialome(essentialome);
	}

	private void linkGenesToEssentialome(Essentialome essentialome) {
		List<Reference> references = provider.getAllReferences();

		for (Reference reference : references) {

			Map<String, Gene> geneMap = new HashMap<String, Gene>();
			for (Gene gene : reference.getGenes()) {
				geneMap.put(gene.getEntrezId(), gene);
			}

			for (ScreenGene screenGene : essentialome.getGenes()) {
				Gene gene = geneMap.get(screenGene.getEntrezId());
				if (gene != null) {
					screenGene.addGene(gene);
				}
			}
		}
	}

	private Essentialome readData(Path referenceFile) {

		Essentialome essentialome = new Essentialome();

		essentialome.setName(referenceFile.getFileName().toString());
		
//		provider.sessionStart();
//
//		provider.persist(essentialome);
//		
//		provider.sessionEnd();

		try {

			BufferedReader reader = new BufferedReader(new FileReader(referenceFile.toString()));
			String header = reader.readLine();
			String[] resources = header.split("\t");
			
			List<EssentialomeEntry> entries = new ArrayList<EssentialomeEntry>();			

//			resourcePosArray = new ExternalRNASeqEntry[resources.length - 2];
//			
//			provider.sessionStart();
//
//
//			for (int i = 2; i < resources.length; ++i) {
//				resourcePosArray[i - 2] = new ExternalRNASeqEntry();
//				resourcePosArray[i - 2].setName(resources[i]);
//				resourcePosArray[i - 2].setResource(essentialome);
//				essentialome.addEntry(resourcePosArray[i - 2]);
//			}
//			
//			provider.persist(essentialome);
//
//			provider.sessionEnd();			
//
//			Stream<String> lines = Files.lines(referenceFile, Charset.defaultCharset());
//
//			provider.sessionStart();
//			lines.skip(1L).forEachOrdered(line -> createDatapointFromLine(essentialome, line));
//			provider.persist(essentialome);
//			provider.sessionEnd();
//
//			lines.close();
//			reader.close();


		} catch (IOException e) {
			e.getMessage();
		}

		return essentialome;
	}

	private void createDatapointFromLine(Essentialome essentialome, String line) {
		
		if (counter % 1000 == 0 && counter > 0) {
			System.out.println("EssentialomeImport:\tHandled line " + counter + ".");
			
			provider.persist(essentialome);
						
			provider.sessionEnd();
			
			provider.sessionStart();
		}

		String[] fields = line.split("\t");
		String entrezId = fields[0];
		String geneSymbol = fields[1];

//		GenericGene gene = new GenericGene(entrezId, geneSymbol);
//
//		essentialome.addGenericGene(gene);
//		//provider.persist(gene);
//
//		for (int i = 2; i < fields.length; ++i) {
//			ExternalRNASeqDatapoint datapoint = new ExternalRNASeqDatapoint(Float.parseFloat(fields[i]));
//			datapoint.setEntry(resourcePosArray[i - 2]);
//			datapoint.setGene(gene);
//			gene.addDatapoint(datapoint);
//			resourcePosArray[i - 2].addDatapoint(datapoint);
//			//provider.persist(resourcePosArray[i - 2]);
//		}

		++counter;
		

	}

}
