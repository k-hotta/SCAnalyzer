package jp.ac.osaka_u.ist.sdl.c20r.csv_analyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CSVFileDetector {

	public static String[] detectCSVFiles(final String rootDir) {
		final List<String> result = new ArrayList<String>();
		
		final File root = new File(rootDir);
		parseDir(root, result);
		
		final String[] array = new String[result.size()];
		
		for (int i = 0; i < result.size(); i++) {
			array[i] = result.get(i);
		}
		
		return array;
	}
	
	private static void parseDir(final File dir, final List<String> result) {
		for (File tmp : dir.listFiles()) {
			if (tmp.isDirectory()) {
				parseDir(tmp, result);
			} else {
				if (tmp.getAbsolutePath().endsWith(".csv")) {
					result.add(tmp.getAbsolutePath());
				}
			}
		}
	}
	
}
