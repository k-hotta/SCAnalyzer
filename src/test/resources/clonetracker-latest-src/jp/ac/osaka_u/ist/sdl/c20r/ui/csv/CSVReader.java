package jp.ac.osaka_u.ist.sdl.c20r.ui.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.ui.data.CSVCloneElementInfo;
import jp.ac.osaka_u.ist.sdl.c20r.ui.data.CSVCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.c20r.ui.data.CSVRevisionInfo;
import jp.ac.osaka_u.ist.sdl.c20r.ui.settings.UISettings;

public class CSVReader {

	private static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	public static CSVRevisionInfo parse(final String csvFilePath,
			final boolean considerOnlyDisappearedClones) throws IOException,
			InvalidCSVFormatException {
		final File csvFile = new File(csvFilePath);
		final BufferedReader reader = new BufferedReader(
				new FileReader(csvFile));

		try {

			reader.readLine(); // 1çsñ⁄ÇÕãÛì«Ç›

			String line = reader.readLine();
			String[] splitedLine = line.split(",");

			final int revisionNum = Integer.parseInt(splitedLine[0]);
			final int nextRevisionNum = Integer.parseInt(splitedLine[1]);

			reader.readLine(); // 3çsñ⁄Ç‡ãÛì«Ç›

			long currentCloneId = -1;

			final Map<Long, CSVCloneSetInfo> cloneSets = new TreeMap<Long, CSVCloneSetInfo>();
			final Set<CSVCloneElementInfo> currentElements = new TreeSet<CSVCloneElementInfo>();

			while ((line = reader.readLine()) != null) {
				final CSVCloneElementInfo element = createElement(line,
						revisionNum);
				final long cloneId = element.getCloneId();

				if (currentCloneId != cloneId) {
					currentCloneId = processCloneSet(revisionNum,
							currentCloneId, cloneSets, currentElements,
							cloneId, considerOnlyDisappearedClones);
				}

				currentElements.add(element);
			}

			processCloneSet(revisionNum, currentCloneId, cloneSets,
					currentElements, currentCloneId,
					considerOnlyDisappearedClones);

			final CSVRevisionInfo revisionInfo = new CSVRevisionInfo(
					revisionNum, nextRevisionNum, cloneSets.values());

			reader.close();

			return revisionInfo;

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidCSVFormatException();
		}
	}

	private static long processCloneSet(final int revisionNum,
			long currentCloneId, final Map<Long, CSVCloneSetInfo> cloneSets,
			final Set<CSVCloneElementInfo> currentElements, final long cloneId,
			final boolean considerOnlyDisappearClones) {
		if (currentCloneId != -1) {
			final CSVCloneSetInfo cloneSet = new CSVCloneSetInfo(revisionNum,
					currentCloneId, currentElements);
			if (!considerOnlyDisappearClones || cloneSet.isContainsDisappear()) {
				if (cloneSets.containsKey(currentCloneId)) {
					System.err.println("duplicated IDs in revision " + revisionNum);
				}
				cloneSets.put(currentCloneId, cloneSet);
			}
			currentElements.clear();
		}
		currentCloneId = cloneId;
		return currentCloneId;
	}

	private static CSVCloneElementInfo createElement(final String line,
			final int revisionNum) throws Exception {
		String[] splitedLine = line.split(",");
		final long cloneId = Long.parseLong(splitedLine[0]);
		// final String path = UISettings.getInstance().getWorkingDir()
		// + splitedLine[1];
		final String path = splitedLine[1];
		final int startLine = Integer.parseInt(splitedLine[2]);
		final int endLine = Integer.parseInt(splitedLine[3]);
		final int length = Integer.parseInt(splitedLine[4]);
		final int cc = Integer.parseInt(splitedLine[5]);
		final int fo = Integer.parseInt(splitedLine[6]);
		final int disappear = Integer.parseInt(splitedLine[7]);
		final int inDeletedFile = Integer.parseInt(splitedLine[8]);
		final int afterStartLine = Integer.parseInt(splitedLine[9]);
		final int afterEndLine = Integer.parseInt(splitedLine[10]);
		final int moved = Integer.parseInt(splitedLine[12]);
		final String afterPath = splitedLine[13];
		final String beforeCrd = convertCrd(splitedLine[14]);
		final String afterCrd = convertCrd(splitedLine[15]);
		final double similarity = Double.parseDouble(splitedLine[16]);
		final int ld = Integer.parseInt(splitedLine[17]);

		final boolean isDisappear = (disappear == 1);
		final boolean isInDeletedFile = (inDeletedFile == 1);
		final boolean isMoved = (moved == 1);

		return new CSVCloneElementInfo(revisionNum, cloneId, path, startLine,
				endLine, isDisappear, afterStartLine, afterEndLine, afterPath,
				isInDeletedFile, length, cc, fo, isMoved, beforeCrd, afterCrd,
				similarity, ld);
	}

	private static String convertCrd(final String crdStr) {
		return crdStr.replaceAll("\\\\\\\\", LINE_SEPARATOR);
	}

}
