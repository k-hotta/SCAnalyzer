package jp.ac.osaka_u.ist.sdl.c20r.ui.csv;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.c20r.ui.data.CSVRevisionInfo;

public class CSVDirectoryReader {

	public static Map<Integer, CSVRevisionInfo> parseDirectory(
			final String dirPath, final boolean considerOnlyDisappearedClones) {
		final List<String> csvFiles = detectCSVFiles(new File(dirPath));
		final Map<Integer, CSVRevisionInfo> result = new TreeMap<Integer, CSVRevisionInfo>();

		for (final String csvFile : csvFiles) {
			try {
				final CSVRevisionInfo revision = CSVReader.parse(csvFile,
						considerOnlyDisappearedClones);
				result.put(revision.getRevisionNum(), revision);
			} catch (Exception e) {
				System.err.println("cannot parse " + csvFile
						+ " : this file will be ignored.");
			}
		}

		return Collections.unmodifiableMap(result);
	}

	private static List<String> detectCSVFiles(final File target) {
		final List<String> result = new ArrayList<String>();
		if (target.isDirectory()) {
			for (final File file : target.listFiles()) {
				result.addAll(detectCSVFiles(file));
			}
		} else if (target.isFile()) {
			if (target.getName().endsWith(".csv")) {
				result.add(target.getAbsolutePath());
			}
		}
		return result;
	}

}
