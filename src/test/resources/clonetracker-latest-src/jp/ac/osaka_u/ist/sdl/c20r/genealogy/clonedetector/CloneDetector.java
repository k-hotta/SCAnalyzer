package jp.ac.osaka_u.ist.sdl.c20r.genealogy.clonedetector;

import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.RevisionsDetector;
import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;

public class CloneDetector {

	private final int startRev;

	private final int endRev;

	private final int threadsCount;

	public CloneDetector(final int startRev, final int endRev,
			final int threadsCount, final String dbPath) {
		this.startRev = startRev;
		this.endRev = endRev;
		this.threadsCount = threadsCount;
		DBConnection.createInstance(dbPath);
	}

	public void run() {
		System.out.println("detecting target revisions ...");
		final RevisionsDetector revDetector = new RevisionsDetector(
				startRev, endRev);
		final int[] revisions = revDetector.getRevisionsArray();
		
		System.out.println("\tthe number of targer revisions is "
				+ revisions.length);
		
		Thread[] threads = new Thread[threadsCount];
		final AtomicInteger index = new AtomicInteger(0);
		for (int i = 0; i < threadsCount; i++) {
			threads[i] = new Thread(new CloneDetectThread(revisions, index));
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
