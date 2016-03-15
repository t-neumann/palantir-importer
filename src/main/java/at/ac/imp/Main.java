package at.ac.imp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;

import at.ac.imp.creators.CountCreator;
import at.ac.imp.creators.ReferenceCreator;
import at.ac.imp.entities.Datapoint;
import at.ac.imp.entities.ExpressionValue;
import at.ac.imp.entities.FoldChange;
import at.ac.imp.entities.Gene;
import at.ac.imp.entities.Reference;
import at.ac.imp.entities.ScreenFoldChange;
import at.ac.imp.resources.PersistenceProvider;
import at.ac.imp.util.FileCrawler;

public class Main {
	
	public static void main(String[] args) {
		
		System.out.println(System.getProperty("java.class.path"));
		
		Properties prop = readProperties();
		
		System.out.println(prop.getProperty("referenceDir"));

//		Gene gene1 = new Gene("chr1", 0, 2, "Myc", 234234);
//		Gene gene2 = new Gene("chr2", 100, 2000, "Myc", 57285);
//
//		Datapoint point1 = new ExpressionValue(10, 0.1, 0.2);
//		Datapoint point2 = new FoldChange(-1.2, 0.04);
//		Datapoint point3 = new ScreenFoldChange(-2, 0.01, "serious", "shit");
//
//		point1.setGene(gene1);
//		point2.setGene(gene1);
//		point3.setGene(gene2);
//
//		Reference ref = new Reference();
//
//		ref.addGene(gene1);
//		ref.addGene(gene2);
//
//		EntityManager em = PersistenceProvider.INSTANCE.getEntityManager();
//
//		em.getTransaction().begin();
//		em.persist(ref);
//		em.persist(point1);
//		em.persist(point2);
//		em.persist(point3);
//		em.getTransaction().commit();
//		em.close();
//		//PersistenceProvider.INSTANCE.close();
		
		FileCrawler crawler = new FileCrawler();
		List<Path> referenceFiles = crawler.readFilesFromDirectory(prop.getProperty("referenceDir"));
		
		Path file = referenceFiles.get(0);
		
		ReferenceCreator creater = new ReferenceCreator();
		creater.createReference(file);
		
		List<Path> countFiles = crawler.readFilesFromDirectory(prop.getProperty("rootDir"));
		
		CountCreator counter = new CountCreator();
		
		for (Path file1 : countFiles) {
			counter.createCounts(file1);
			System.out.println(file1.getFileName());
		}
		
		PersistenceProvider.INSTANCE.close();
		
	}
	
	private static Properties readProperties() {
		
		Properties prop = new Properties();
		
		InputStream input = null;
		
		String filename = "config.properties";
		
		try {
		input = Main.class.getClassLoader().getResourceAsStream(filename);
		if (input == null) {
			throw new FileNotFoundException("Config file " + filename + " not found");
		}
		
		prop.load(input);
		
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}
}