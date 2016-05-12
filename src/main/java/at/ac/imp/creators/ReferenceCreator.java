package at.ac.imp.creators;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import at.ac.imp.palantir.model.Gene;
import at.ac.imp.palantir.model.Reference;
import at.ac.imp.resources.EntityProvider;

public class ReferenceCreator {

	private static final int CHRPOS = 0;
	private static final int STARTPOS = 1;
	private static final int ENDPOS = 2;
	private static final int IDPOS = 3;
	private static final int STRANDPOS = 5;
	private static final int GENESYMBOLPOS = 6;
	private static final int ANNOPOS = 7;

	private static final int FIELD_LIMIT = 7;

	private EntityProvider provider;

	public ReferenceCreator() {
		// this.em = PersistenceProvider.INSTANCE.getEntityManager();
		this.provider = new EntityProvider();
	}

	public void createReference(Path referenceFile, boolean force) {
		// Genome build is parent dir name
		String genomeBuild = referenceFile.getName(referenceFile.getNameCount() - 2).toString();
		String name = referenceFile.getFileName().toString();

		provider.sessionStart();
		Reference reference = provider.getReferenceByName(name);

		if (reference == null) {

			reference = new Reference(name, genomeBuild);

			Collection<Gene> genes = readGenes(referenceFile);

			for (Gene gene : genes) {
				gene.setReference(reference);
			}
			reference.setGenes(genes);

			provider.persist(reference);
		} else {
			Logger.getAnonymousLogger().log(java.util.logging.Level.INFO, "Reference " + reference.getName() + " exists. No import.");
		}
		provider.sessionEnd();

	}

	private Collection<Gene> readGenes(Path referenceFile) {

		List<Gene> genes = new ArrayList<Gene>();
		try (Stream<String> lines = Files.lines(referenceFile, Charset.defaultCharset())) {
			lines.forEachOrdered(line -> createGeneFromLine(line, genes));
		} catch (IOException e) {
			e.getMessage();
		}

		return genes;
	}

	private void createGeneFromLine(String line, List<Gene> genes) {
		String[] fields = line.split("\t");

		Gene gene = null;

		if (fields.length >= ReferenceCreator.FIELD_LIMIT) {
			
			String anno = "";
			
			if (fields.length >= (ReferenceCreator.FIELD_LIMIT + 1)) {
				anno = fields[ANNOPOS];
			}
			// gene = new Gene(fields[CHRPOS],
			// Integer.parseInt(fields[STARTPOS]),
			// Integer.parseInt(fields[ENDPOS]), fields[GENEPOS],
			// fields[STRANDPOS].equals("-") ? true : false, 0);
			gene = new Gene(fields[CHRPOS], Integer.parseInt(fields[STARTPOS]), Integer.parseInt(fields[ENDPOS]),
					fields[GENESYMBOLPOS], fields[STRANDPOS], fields[IDPOS], anno);
		} else {
			Logger.getAnonymousLogger().log(Level.WARNING, "Line " + line + " contains less than " + ReferenceCreator.FIELD_LIMIT + " fields");
		}

		if (gene != null) {
			genes.add(gene);
		}
	}
}
