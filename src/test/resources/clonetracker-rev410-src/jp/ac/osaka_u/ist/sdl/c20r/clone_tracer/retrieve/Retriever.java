package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.RetrievedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.manager.RetrievedDataManager;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.manager.RevisionManager;

public class Retriever {

	private final int beforeRevisionNum;

	private final int afterRevisionNum;

	private RevisionManager manager;

	public Retriever(final int beforeRevisionNum, final int afterRevisionNum) {
		this.beforeRevisionNum = beforeRevisionNum;
		this.afterRevisionNum = afterRevisionNum;
	}

	public RevisionManager getRevisionManager() {
		return manager;
	}

	public void retrieve() {
		final long t1 = System.nanoTime();
		retrieveRevisions();
		final long t2 = System.nanoTime();
		retrieveOthers(manager.getBeforeRevision().getId(),
				manager.getBeforeDataManager(), true);
		final long t3 = System.nanoTime();
		retrieveOthers(manager.getAfterRevision().getId(),
				manager.getAfterDataManager(), false);

		final long t4 = System.nanoTime();
		detectBlockPairs();
		final long t5 = System.nanoTime();
		detectCloneSetPairs();
		final long t6 = System.nanoTime();
		// System.out.println("\t\t\tretrieve revision : " + (t2 - t1) / 1000000
		// + " ms");
		// System.out.println("\t\t\tretrieve others before : " + (t3 - t2)
		// / 1000000 + " ms");
		// System.out.println("\t\t\tretrieve others after : " + (t4 - t3)
		// / 1000000 + " ms");
		// System.out.println("\t\t\tdetect block pairs : " + (t5 - t4) /
		// 1000000
		// + " ms");
		// System.out.println("\t\t\tdetect clone sets : " + (t6 - t5) / 1000000
		// + " ms");
	}

	private void retrieveRevisions() {
		RevisionInfoRetriever beforeRetriever = new RevisionInfoRetriever(0,
				beforeRevisionNum);
		RetrievedRevisionInfo beforeRevision = beforeRetriever.retrieveAll()
				.first();

		RevisionInfoRetriever afterRetriever = new RevisionInfoRetriever(0,
				afterRevisionNum);
		RetrievedRevisionInfo afterRevision = afterRetriever.retrieveAll()
				.first();

		manager = new RevisionManager(beforeRevision, afterRevision);
	}

	private void retrieveOthers(final long revisionId,
			final RetrievedDataManager dataManager,
			final boolean isBeforeRevision) {
		final long t1 = System.nanoTime();
		final BlockInfoRetriever blockRetriever = new BlockInfoRetriever(
				revisionId);
		dataManager.getBlockManager().addAll(blockRetriever.retrieveAll());
		final long t2 = System.nanoTime();

		final FileInfoRetriever fileRetriever = new FileInfoRetriever(
				revisionId, blockRetriever.getToRetrieveFileIds());
		dataManager.getFileManager().addAll(fileRetriever.retrieveAll());
		final long t3 = System.nanoTime();

		final CloneSetInfoRetriever cloneRetriever = new CloneSetInfoRetriever(
				dataManager.getBlockManager().getAllElements(),
				isBeforeRevision);
		dataManager.getCloneManager().addAll(cloneRetriever.retrieveAll());
		final long t4 = System.nanoTime();

		// System.out.println("\t\t\t\tblock: " + (t2 - t1) / 1000000 + " ms");
		// System.out.println("\t\t\t\tfile: " + (t3 - t2) / 1000000 + " ms");
		// System.out.println("\t\t\t\tcloneset: " + (t4 - t3) / 1000000 +
		// " ms");
	}

	private void detectBlockPairs() {
		BlockPairDetector detector = new BlockPairDetector(manager
				.getBeforeRevision().getId(), manager.getBeforeDataManager()
				.getBlockManager().getMap(), manager.getAfterDataManager()
				.getBlockManager().getMap(), manager.getBlockPairManager());
		detector.detect();
	}

	private void detectCloneSetPairs() {
		CloneSetPairDetector detector = new CloneSetPairDetector(manager
				.getBlockPairManager().getAddedPairs(), manager
				.getBlockPairManager().getDeletedPairs(), manager
				.getBlockPairManager().getMatchedBlockPairInfo(), manager
				.getBeforeDataManager().getCloneManager().getMap(), manager
				.getAfterDataManager().getCloneManager().getMap(),
				manager.getCloneManager());
		detector.detect();
	}

}
