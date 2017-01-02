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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import at.ac.imp.palantir.model.ExternalRNASeqDatapoint;
import at.ac.imp.palantir.model.ExternalRNASeqResource;
import at.ac.imp.resources.EntityProvider;

public class ExternalRNASeqImporter {

	// private EntityManager em;
	private EntityProvider provider;

	private ExternalRNASeqResource[] resourcePosArray;

	public ExternalRNASeqImporter() {
		this.provider = new EntityProvider();
	}

	public void createCounts(Path countFile) {
		readData(countFile);
	}

	private Map<String, ExternalRNASeqResource> readData(Path referenceFile) {

		Map<String, ExternalRNASeqResource> datapoints = new HashMap<String, ExternalRNASeqResource>();
		// List<ExpressionValue> datapoints = new ArrayList<ExpressionValue>();

		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(referenceFile.toString()));
			String header = reader.readLine();
			String[] resources = header.split("\t");

			resourcePosArray = new ExternalRNASeqResource[resources.length - 1];

			for (int i = 1; i < resources.length; ++i) {
				resourcePosArray[i - 1] = new ExternalRNASeqResource();
				resourcePosArray[i - 1].setName(resources[i]);
			}

			Stream<String> lines = Files.lines(referenceFile, Charset.defaultCharset());

			lines.skip(1L).forEachOrdered(line -> createDatapointFromLine(line));
			
			lines.close();
			reader.close();
			
		} catch (IOException e) {
			e.getMessage();
		}
		
		return datapoints;
	}

	private void createDatapointFromLine(String line) {
		String[] fields = line.split("\t");
		int entrezId = Integer.parseInt(fields[0]);

		for (int i = 1; i < fields.length; ++i) {
			ExternalRNASeqDatapoint datapoint = new ExternalRNASeqDatapoint(entrezId, Float.parseFloat(fields[i]));
			resourcePosArray[i - 1].addDatapoint(datapoint);
		}

	}

}
