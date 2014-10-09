package jp.ac.osaka_u.ist.sdl.c20r.csv_analyzer;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CSVReadThread implements Runnable {

	private final AtomicInteger index;

	private final String[] csvFiles;

	private final ConcurrentMap<Integer, String> toPrint;

	private final CSVReader reader;

	public CSVReadThread(final AtomicInteger index, final String[] csvFiles,
			final ConcurrentMap<Integer, String> toPrint) {
		this.index = index;
		this.csvFiles = csvFiles;
		this.toPrint = toPrint;
		this.reader = new CSVReader();
	}

	@Override
	public void run() {
		while (true) {
			final int currentIndex = index.getAndIncrement();

			if (currentIndex >= csvFiles.length) {
				break;
			}

			final String csvPath = csvFiles[currentIndex];

			System.out.println("now analyzing " + csvPath + " ["
					+ (currentIndex + 1) + "/" + csvFiles.length + "]");

			reader.readFile(csvPath);

			final String str = reader.getBeforeRevision() + ","
					+ reader.getCloneSetsCount() + ","
					+ reader.getCloneElementsCount() + ","
					+ reader.getDisappearedCloneSetsCount() + ","
					+ reader.getDisappearedCloneElementsCount() + ","
					+ reader.getCloneSetsContainsElementsInDeletedFiles() + ","
					+ reader.getCloneSetsContainsElementsNotInDeletedFiles()
					+ "," + reader.getCloneSetsContainsBoth() + ","
					+ reader.getMovedBlocks();

			toPrint.put(reader.getBeforeRevision(), str);

		}
	}

}
