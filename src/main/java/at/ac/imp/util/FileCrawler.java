package at.ac.imp.util;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileCrawler {

	// Wrapper to select all files (no file will contain tab)
	public List<Path> readFilesFromDirectory(String dir) {
		return readFilesFromDirectory(dir, "\t", true);
	}

	// Wrapper method around recursion
	public List<Path> readFilesFromDirectory(String dir, String pattern, boolean ignore) {
		List<Path> result = new ArrayList<Path>();

		listFiles(dir, result, pattern, ignore);

		return result;
	}

	private void listFiles(String directoryName, List<Path> files, String pattern, boolean ignore) {
		File directory = new File(directoryName);

		File[] fileList = directory.listFiles();
		for (File file : fileList) {
			if (file.isFile() && (ignore && !file.getName().contains(pattern))) {
				files.add(file.toPath());
			} else if (file.isFile() && (!ignore && file.getName().contains(pattern))) {
				files.add(file.toPath());
			} else if (file.isDirectory()) {
				listFiles(file.getAbsolutePath(), files, pattern, ignore);
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
