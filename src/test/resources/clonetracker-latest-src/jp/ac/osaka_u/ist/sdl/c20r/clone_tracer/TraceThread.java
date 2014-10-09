package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve.CRDMode;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve.Retriever;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.writer.ResultStringCreator;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.writer.ResultWriter;

public class TraceThread implements Runnable {

	private final int[] revisions;

	private final ConcurrentMap<Integer, Integer> nextRevisions;

	private final AtomicInteger index;

	private final CRDMode crdMode;

	public TraceThread(final int[] revisions,
			final ConcurrentMap<Integer, Integer> nextRevisions,
			final AtomicInteger index, final CRDMode crdMode) {
		this.revisions = revisions;
		this.nextRevisions = nextRevisions;
		this.index = index;
		this.crdMode = crdMode;
	}

	@Override
	public void run() {
		while (true) {
			final int currentIndex = index.getAndIncrement();

			if (currentIndex >= revisions.length) {
				break;
			}

			final long t1 = System.nanoTime();
			final int beforeRevisionNum = revisions[currentIndex];
			final int afterRevisionNum = nextRevisions.get(beforeRevisionNum);

			System.out.println("\tnow analyzing revisions " + beforeRevisionNum
					+ " and " + afterRevisionNum + ". [" + (currentIndex + 1)
					+ "/" + revisions.length + "]");

			final Retriever retriever = new Retriever(beforeRevisionNum,
					afterRevisionNum, crdMode);
			retriever.retrieve();
			final long t2 = System.nanoTime();

			final String resultStr = ResultStringCreator.create(
					retriever.getRevisionManager(), (t2 - t1) / 1000 / 1000);

			final long t3 = System.nanoTime();

			// ResultWriter.getInstance().pool(beforeRevisionNum,
			// afterRevisionNum, resultStr);
			ResultWriter.getInstance().write(beforeRevisionNum,
					afterRevisionNum, resultStr);

			final long t4 = System.nanoTime();

			// System.out.println("\t\tretrieve : " + (t2-t1) / 1000000);
			// System.out.println("\t\tcreate string : " + (t3-t2) / 1000000);
			// System.out.println("\t\twrite : " + (t4-t3) / 1000000);
		}
	}

}
