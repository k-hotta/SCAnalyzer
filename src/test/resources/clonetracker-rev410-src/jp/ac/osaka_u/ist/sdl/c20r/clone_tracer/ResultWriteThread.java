package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer;

import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.writer.ResultWriter;

public class ResultWriteThread implements Runnable {

	private final ResultWriter writer;

	private final int toBeAnalyzedRevisionsCount;

	private final AtomicInteger index;

	public ResultWriteThread(final int toBeAnalyzedRevisionsCount,
			final AtomicInteger index) {
		this.toBeAnalyzedRevisionsCount = toBeAnalyzedRevisionsCount;
		this.index = index;
		this.writer = ResultWriter.getInstance();
	}

	@Override
	public void run() {
		while (true) {
			final int processedCount = index.get();

			if (processedCount >= toBeAnalyzedRevisionsCount) {
				break;
			}

			if (processRevision()) {
				System.out.println("\ta result was saved. ["
						+ (index.get() + 1) + "/" + toBeAnalyzedRevisionsCount
						+ "]");
				index.getAndIncrement();
			}

			// try {
			// Thread.sleep(100);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
		}
	}

	private boolean processRevision() {
		final int targetRevisionNum = writer.getTargetRevisionNum();
		if (targetRevisionNum < 0) {
			return false;
		}

		writer.write(targetRevisionNum);
		writer.remove(targetRevisionNum);

		return true;
	}

}
