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
import javax.persistence.TypedQuery;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import at.ac.imp.entities.Alignment;
import at.ac.imp.entities.Gene;
import at.ac.imp.entities.RNASeqResult;
import at.ac.imp.entities.Reference;
import at.ac.imp.entities.Sample;
import at.ac.imp.resources.PersistenceProvider;

public class CountCreator {
	
	private static int ALIGNMENT_LEVEL = 3;
	private static int REFERENCE_LEVEL = 3;
	private static int SAMPLE_LEVEL = 3;
	
	private EntityManager em;
	
	public CountCreator() {
		this.em = PersistenceProvider.INSTANCE.getEntityManager();
	}
	
	public void createCounts(Path referenceFile) {
		// Genome build is parent dir name
		String reference = referenceFile.getName(referenceFile.getNameCount() - CountCreator.REFERENCE_LEVEL).toString();
		
		Alignment alignment = new Alignment();
		alignment.setName(referenceFile.getName(referenceFile.getNameCount() - CountCreator.ALIGNMENT_LEVEL).toString());
		
		Sample sample = new Sample(Integer.parseInt(referenceFile.getName(referenceFile.getNameCount() - CountCreator.SAMPLE_LEVEL).toString()));
		sample.addAlignment(alignment);
		
		TypedQuery<Reference> query = em.createNamedQuery("Reference.findByName", Reference.class);
		Reference result = query.setParameter("name", reference).getSingleResult();
		
		System.out.println(result.toString());
//		Reference reference = new Reference(name, genomeBuild);
//		
//		Collection<Gene> genes = readGenes(referenceFile);
//		
//		reference.setGenes(genes);
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
		
		if (fields.length > 0) {
		} else {
			Logger.getLogger(ReferenceCreator.class).log(Level.WARN, "Line " + line + " contains less than " + 0 + " fields");
		}
		
		if (gene != null) {
			genes.add(gene);
		}
	}

}
