package jp.ac.osaka_u.ist.sdl.c20r.csv_analyzer;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CSVAnalyzer {

	private final String rootDir;

	private final int threadsCount;

	private final String outputFilePath;

	public CSVAnalyzer(final String rootDir, final int threadsCount,
			final String outputFilePath) {
		this.rootDir = rootDir;
		this.threadsCount = threadsCount;
		this.outputFilePath = outputFilePath;
	}

	public void analyze() {
		final String[] csvFiles = CSVFileDetector.detectCSVFiles(rootDir);
		final AtomicInteger index = new AtomicInteger(0);
		final ConcurrentMap<Integer, String> toPrint = new ConcurrentHashMap<Integer, String>();

		final Thread[] threads = new Thread[threadsCount];

		for (int i = 0; i < threadsCount; i++) {
			Thread thread = new Thread(new CSVReadThread(index, csvFiles,
					toPrint));
			threads[i] = thread;
			threads[i].start();
		}

		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		final Map<Integer, String> sortedToPrint = new TreeMap<Integer, String>();
		sortedToPrint.putAll(toPrint);
		
		CSVAnalyzerResultWriter writer = new CSVAnalyzerResultWriter(outputFilePath, sortedToPrint);
		writer.write();

	}

}
