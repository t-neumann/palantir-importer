package at.ac.imp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import at.ac.imp.creators.CountCreator;
import at.ac.imp.creators.ReferenceCreator;
import at.ac.imp.resources.PersistenceProvider;
import at.ac.imp.util.FileCrawler;

public class Main {
	
	public static void main(String[] args) {
		
		Properties prop = readProperties();
				
		FileCrawler crawler = new FileCrawler();
		List<Path> referenceFiles = crawler.readFilesFromDirectory(prop.getProperty("referenceDir") + "bed");
		
		ReferenceCreator creator = new ReferenceCreator();
		
		for (Path file : referenceFiles) {
			System.out.println(file);
			creator.createReference(file);
		}
		
		List<Path> countFiles = crawler.readFilesFromDirectory(prop.getProperty("rootDir"));
		
		CountCreator counter = new CountCreator();
		
		for (Path file : countFiles) {
			System.out.println(file);
			counter.createCounts(file);
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