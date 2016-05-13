package at.ac.imp.util;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileCrawler {

	// Wrapper to select all files (no file will contain tab)
	public List<Path> readFilesFromDirectory(String dir) {
		return readFilesFromDirectory(dir, "\t");
	}

	// Wrapper method around recursion
	public List<Path> readFilesFromDirectory(String dir, String ignore) {
		List<Path> result = new ArrayList<Path>();

		listFiles(dir, result, ignore);

		return result;
	}

	private void listFiles(String directoryName, List<Path> files, String ignore) {
		File directory = new File(directoryName);

		File[] fileList = directory.listFiles();
		for (File file : fileList) {
			if (file.isFile() && !file.getName().contains(ignore)) {
				files.add(file.toPath());
			} else if (file.isDirectory()) {
				listFiles(file.getAbsolutePath(), files, ignore);
			}
		}
	}

	public List<String> listParents(File file, int levels) {

		File root = file;
		List<String> parents = new ArrayList<String>();
		parents.add(file.getName());
		for (int i = 0; i < levels; ++i) {
			parents.add(root.getParentFile().getName());
			root = root.getParentFile();
		}
		return parents;
	}
}
