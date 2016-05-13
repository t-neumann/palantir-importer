package at.ac.imp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.hibernate.cfg.Configuration;

import at.ac.imp.creators.CountCreator;
import at.ac.imp.creators.ReferenceCreator;
import at.ac.imp.resources.PersistenceProvider;
import at.ac.imp.util.FileCrawler;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Main {

	public static void main(String[] args) {

		OptionParser optionParser = new OptionParser();

		optionParser.accepts("f", "Force database reset");
		OptionSpec<String> rootDirPar = optionParser.accepts("r", "Root directory").withRequiredArg()
				.ofType(String.class);

		OptionSpec<?> help = optionParser.acceptsAll(Arrays.asList(new String[] { "?", "h", "help" }), "Show help");

		OptionSet options = optionParser.parse(args);

		if (!options.has(help) && options.has(rootDirPar)) {

			boolean force = options.has("f"); 
			Properties prop = readProperties();
			initHibernate(force);

			FileCrawler crawler = new FileCrawler();
			List<Path> referenceFiles = crawler.readFilesFromDirectory(prop.getProperty("referenceDir") + "bed");

			ReferenceCreator creator = new ReferenceCreator();

			int i = 1;

			for (Path file : referenceFiles) {
				System.out.println("Reference " + i + " out of " + referenceFiles.size());
				System.out.println(file);

				long startTime = System.currentTimeMillis();

				creator.createReference(file, force);

				long endTime = System.currentTimeMillis();

				System.out.println("Database import took " + (endTime - startTime) / 1000 + " seconds");
				++i;
			}

			//List<Path> countFiles = crawler.readFilesFromDirectory(prop.getProperty("rootDir"));
			List<Path> countFiles = crawler.readFilesFromDirectory(options.valueOf(rootDirPar), "summary");

			CountCreator counter = new CountCreator();

			i = 1;

			for (Path file : countFiles) {
				System.out.println("Countfile " + i + " out of " + countFiles.size());
				System.out.println(file);

				long startTime = System.currentTimeMillis();

				counter.createCounts(file);

				long endTime = System.currentTimeMillis();

				System.out.println("Database import took " + (endTime - startTime) / 1000 + " seconds");
				++i;
			}

			PersistenceProvider.INSTANCE.close();
		} else {
			try {
				optionParser.printHelpOn(System.err);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private static void initHibernate(boolean force) {

		Properties config = new Properties();
		config.setProperty("hibernate.show_sql", "false");
		config.setProperty("hibernate.format_sql", "true");
		config.setProperty("hibernate.use_sql_comments", "true");
		if (force) {
			config.setProperty("hibernate.hbm2ddl.auto", "create");
		} else {
			config.setProperty("hibernate.hbm2ddl.auto", "validate");
		}

		config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

		PersistenceProvider.INSTANCE.configureProvider(config);

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