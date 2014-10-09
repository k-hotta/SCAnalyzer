package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve.CRDMode;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.settings.TracerSettings;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.writer.ResultWriter;
import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;

public class Tracer {

	public Tracer(final String dbPath, final String outputDir,
			final String workingDir, final int threadsCount,
			final int startRev, final int endRev, final CRDMode crdMode) {
		final TracerSettings settings = TracerSettings.getInstance();
		settings.setDbPath(dbPath);
		settings.setResultDir(outputDir);
		settings.setWorkingDir(workingDir);
		settings.setThreadsCount(threadsCount);
		settings.setStartRev(startRev);
		settings.setEndRev(endRev);
		settings.setCrdMode(crdMode);
		DBConnection.createInstance(dbPath);
	}

	public void trace() {
		final int threadsCount = TracerSettings.getInstance().getThreadsCount();

		System.out.println("detecting target revisions ... ");
		final RevisionsDetector revDetector = new RevisionsDetector(
				TracerSettings.getInstance().getStartRev(), TracerSettings
						.getInstance().getEndRev());
		final int[] revisions = revDetector.getRevisionsArray();
		final ConcurrentMap<Integer, Integer> nextRevisionsMap = revDetector
				.getNextRevisions();
		System.out.println("\tthe number of targer revisions is "
				+ revisions.length);

		if (threadsCount == 1) {
			processWithSingleThread(revisions, nextRevisionsMap);
		} else {
			processWithMultipleThreads(threadsCount, revisions,
					nextRevisionsMap);
		}
	}

	private void processWithMultipleThreads(final int threadsCount,
			final int[] revisions,
			final ConcurrentMap<Integer, Integer> nextRevisionsMap) {
		System.out.println("tracing consecutive revisions ... ");
		traceAll(threadsCount, revisions, nextRevisionsMap);
		// System.out.println("recording all the tracing results ... ");
		// recordAll(threadsCount, revisions);
	}

	private void recordAll(final int threadsCount, final int[] revisions) {
		Thread[] threads = new Thread[threadsCount];
		final AtomicInteger index = new AtomicInteger(0);
		for (int i = 0; i < threadsCount; i++) {
			threads[i] = new Thread(new ResultWriteThread(revisions.length,
					index));
			threads[i].start();
		}

		for (final Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void traceAll(final int threadsCount, final int[] revisions,
			final ConcurrentMap<Integer, Integer> nextRevisionsMap) {
		Thread[] threads = new Thread[threadsCount];
		final AtomicInteger index = new AtomicInteger(0);
		for (int i = 0; i < threadsCount; i++) {
			threads[i] = new Thread(new TraceThread(revisions,
					nextRevisionsMap, index, TracerSettings.getInstance()
							.getCrdMode()));
			threads[i].start();
		}

		for (final Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void processWithSingleThread(int[] revisions,
			ConcurrentMap<Integer, Integer> nextRevisionsMap) {
		Thread thread = new Thread(
				new TraceThread(revisions, nextRevisionsMap, new AtomicInteger(
						0), TracerSettings.getInstance().getCrdMode()));
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ResultWriter.getInstance().writeAll();
	}

}
