package at.ac.imp.util;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileCrawler{
	
	// Wrapper method around recursion
	public List<Path> readFilesFromDirectory(String dir) {
		List<Path> result = new ArrayList<Path>();
		
		listFiles(dir, result);
		
		return result;
	}
	
	private void listFiles(String directoryName, List<Path> files) {
	    File directory = new File(directoryName);

	    File[] fileList = directory.listFiles();
	    for (File file : fileList) {
	        if (file.isFile()) {
	            files.add(file.toPath());
	        } else if (file.isDirectory()) {
	        	listFiles(file.getAbsolutePath(), files);
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
