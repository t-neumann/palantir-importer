package at.ac.imp.creators;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import at.ac.imp.palantir.model.Gene;
import at.ac.imp.palantir.model.Reference;
import at.ac.imp.resources.PersistenceProvider;

public class ReferenceCreator {
	
	private static final int CHRPOS = 0;
	private static final int STARTPOS = 1;
	private static final int ENDPOS = 2;
	private static final int GENEPOS = 3;
	private static final int STRANDPOS = 5;
	
	private static final int FIELD_LIMIT = 6;
	
	private EntityManager em;
	
	public ReferenceCreator() {
		this.em = PersistenceProvider.INSTANCE.getEntityManager();
	}
	
	public void createReference(Path referenceFile) {
		// Genome build is parent dir name
		String genomeBuild = referenceFile.getName(referenceFile.getNameCount() - 2).toString();
		String name = referenceFile.getFileName().toString();
		
		Reference reference = new Reference(name, genomeBuild);
		
		Collection<Gene> genes = readGenes(referenceFile);
		
		for (Gene gene : genes) {
			gene.setReference(reference);
		}
		reference.setGenes(genes);
		
		em.getTransaction().begin();
		em.persist(reference);
		em.getTransaction().commit();
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
			gene = new Gene(fields[CHRPOS], Integer.parseInt(fields[STARTPOS]), Integer.parseInt(fields[ENDPOS]), fields[GENEPOS], fields[STRANDPOS].equals("-") ? true : false, 0);
		} else {
			Logger.getLogger(ReferenceCreator.class).log(Level.WARN, "Line " + line + " contains less than " + ReferenceCreator.FIELD_LIMIT + " fields");
		}
		
		if (gene != null) {
			genes.add(gene);
		}
	}
}
