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
import at.ac.imp.palantir.model.EssentialomeDatapoint;
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

	private static final int ESSENTIALOME_START = 7;

	private int counter = 0;

	private EntityProvider provider;

	private List<EssentialomeEntry> entries;

	private Map<String, ScreenGene> geneMap = new HashMap<String, ScreenGene>();

	public EssentialomeImporter() {
		this.provider = new EntityProvider();
	}

	public void createCounts(Path countFile) {

		counter = 0;

		loadGenes();

		Essentialome essentialome = readData(countFile);
		
		if (essentialome != null) {

			linkGenesToEssentialome(essentialome);
		
		}

	}

	private void loadGenes() {

		provider.sessionStart();

		List<ScreenGene> publicGenes = provider.getAllScreenGenes();

		provider.sessionEnd();

		for (ScreenGene gene : publicGenes) {
			geneMap.put(gene.getEntrezId(), gene);
		}

	}

	private void linkGenesToEssentialome(Essentialome essentialome) {

		provider.sessionStart();

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

		provider.merge(essentialome);

		provider.sessionEnd();
	}

	private Essentialome readData(Path inputFile) {

		Essentialome essentialome = new Essentialome();

		String name = inputFile.getFileName().toString();
		name = name.replaceAll("\\.palantir.*", "");

		System.out.println("EssentialomeImport:\tImporting essentialome " + name + ".");
		
		Essentialome query = provider.findEssentialomeByName(name);
				
		if (query != null && !query.isValid()) {
			
			System.out.println("Deleting incomplete essentialome import.");
			
			provider.sessionStart();

			provider.delete(query);

			provider.sessionEnd();
		}

		if (provider.findEssentialomeByName(name) == null || !provider.findEssentialomeByName(name).isValid()) {

			essentialome.setName(name);

			provider.sessionStart();

			provider.persist(essentialome);

			provider.sessionEnd();

			try {

				BufferedReader reader = new BufferedReader(new FileReader(inputFile.toString()));
				String header = reader.readLine();
				String[] resources = header.split("\t");

				entries = new ArrayList<EssentialomeEntry>();

				provider.sessionStart();

				for (int i = EssentialomeImporter.ESSENTIALOME_START; i < resources.length; ++i) {
					EssentialomeEntry entry = new EssentialomeEntry();
					entry.setName(resources[i]);
					entry.setEssentialome(essentialome);
					essentialome.addEntry(entry);
					entries.add(entry);
				}

				provider.persist(essentialome);

				provider.sessionEnd();

				Stream<String> lines = Files.lines(inputFile, Charset.defaultCharset());

				provider.sessionStart();
				lines.skip(1L).forEachOrdered(line -> createDatapointFromLine(essentialome, line));
				provider.persist(essentialome);
				provider.sessionEnd();

				lines.close();
				reader.close();

			} catch (IOException e) {
				e.getMessage();
			}
			
			provider.sessionStart();
			essentialome.setValid(true);
			provider.persist(essentialome);
			provider.sessionEnd();

			return essentialome;

		} else {
			System.out.println("Essentialome imported already.");
			return null;
		}
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
		String essential = fields[2];
		String pool = fields[3];
		String aliases = fields[4];
		String chrLocation = fields[5];
		String type = fields[6];

		ScreenGene gene = null;

		if (!geneMap.containsKey(entrezId)) {
			gene = new ScreenGene(entrezId, geneSymbol, essential, pool, aliases, chrLocation, type);
		} else {
			gene = geneMap.get(entrezId);
		}

		gene.addEssentialome(essentialome);
		essentialome.addScreenGene(gene);

		for (int i = EssentialomeImporter.ESSENTIALOME_START; i < fields.length; ++i) {
			String toParse = fields[i];
			if (toParse.equals("NO")) {
				toParse = "0";
			} else if (toParse.equals("YES")) {
				toParse = "1";
			}

			if (!toParse.equals("NA")) {
				EssentialomeDatapoint datapoint = new EssentialomeDatapoint(Float.parseFloat(toParse));
				datapoint.setEntry(entries.get(i - EssentialomeImporter.ESSENTIALOME_START));
				datapoint.setGene(gene);
				gene.addDatapoint(entries.get(i - EssentialomeImporter.ESSENTIALOME_START).getName(), datapoint);
				entries.get(i - EssentialomeImporter.ESSENTIALOME_START).addDatapoint(datapoint);
			}
		}
		++counter;
	}
}
