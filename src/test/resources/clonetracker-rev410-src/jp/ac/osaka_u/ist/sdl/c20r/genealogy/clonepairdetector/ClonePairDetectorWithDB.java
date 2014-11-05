package jp.ac.osaka_u.ist.sdl.c20r.genealogy.clonepairdetector;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.RevisionsDetector;
import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.RetrievedDataContainer;

public class ClonePairDetectorWithDB {

	private final int startRev;

	private final int endRev;

	private final int threadsCount;

	public ClonePairDetectorWithDB(final int startRev, final int endRev,
			final int threadsCount, final String dbPath) {
		this.startRev = startRev;
		this.endRev = endRev;
		this.threadsCount = threadsCount;
		DBConnection.createInstance(dbPath);
	}

	public void run() {
		System.out.println("detecting target revisions ...");
		final RevisionsDetector revDetector = new RevisionsDetector(startRev,
				endRev);
		final int[] revisions = revDetector.getRevisionsArray();

		System.out.println("\tthe number of target revisions is "
				+ revisions.length);

		final ConcurrentMap<Integer, Integer> nextRevisions = revDetector
				.getNextRevisions();
		final ConcurrentMap<Integer, RetrievedDataContainer> containerContainer = new ConcurrentHashMap<Integer, RetrievedDataContainer>();

		Thread[] threads = new Thread[threadsCount];
		final AtomicInteger index = new AtomicInteger(0);
		for (int i = 0; i < threadsCount; i++) {
			threads[i] = new Thread(new ClonePairDetectThread(revisions,
					nextRevisions, index, containerContainer));
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
}
