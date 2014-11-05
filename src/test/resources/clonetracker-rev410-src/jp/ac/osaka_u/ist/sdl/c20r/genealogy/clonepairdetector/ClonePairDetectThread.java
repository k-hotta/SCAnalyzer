package jp.ac.osaka_u.ist.sdl.c20r.genealogy.clonepairdetector;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.manager.BlockPairManager;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.manager.CloneSetPairManager;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve.BlockPairDetector;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve.CloneSetPairDetector;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.DataRetriever;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.RetrievedDataContainer;

public class ClonePairDetectThread implements Runnable {

	private final int[] revisions;

	private final ConcurrentMap<Integer, Integer> nextRevisions;

	private final AtomicInteger index;

	private final ConcurrentMap<Integer, RetrievedDataContainer> containerContainer;

	public ClonePairDetectThread(
			final int[] revisions,
			final ConcurrentMap<Integer, Integer> nextRevisions,
			final AtomicInteger index,
			final ConcurrentMap<Integer, RetrievedDataContainer> containerContainer) {
		this.revisions = revisions;
		this.nextRevisions = nextRevisions;
		this.index = index;
		this.containerContainer = containerContainer;
	}

	@Override
	public void run() {
		while (true) {
			final int currentIndex = index.getAndIncrement();

			if (currentIndex >= revisions.length) {
				break;
			}

			final int beforeRevisionNum = revisions[currentIndex];
			final int afterRevisionNum = nextRevisions.get(beforeRevisionNum);

			System.out.println("\tnow analyzing revisions " + beforeRevisionNum
					+ " and " + afterRevisionNum + ". [" + (currentIndex + 1)
					+ "/" + revisions.length + "]");

			// 前リビジョンのデータを回収
			RetrievedDataContainer beforeContainer = containerContainer
					.get(beforeRevisionNum);
			if (beforeContainer == null) {
				beforeContainer = DataRetriever.retrieve(beforeRevisionNum);
			}

			// 後リビジョンのデータを回収
			RetrievedDataContainer afterContainer = containerContainer
					.get(afterRevisionNum);
			if (afterContainer == null) {
				afterContainer = DataRetriever.retrieve(afterRevisionNum);
			}

			final BlockPairManager blockPairManager = new BlockPairManager();
			final BlockPairDetector blockPairDetector = new BlockPairDetector(
					beforeContainer.getRevisionId(),
					beforeContainer.getBlocks(), afterContainer.getBlocks(),
					blockPairManager);
			blockPairDetector.detect();

			final CloneSetPairManager clonePairManager = new CloneSetPairManager();
			final CloneSetPairDetector clonePairDetector = new CloneSetPairDetector(
					blockPairManager.getAddedPairs(),
					blockPairManager.getDeletedPairs(),
					blockPairManager.getMatchedBlockPairInfo(),
					beforeContainer.getClones(), afterContainer.getClones(),
					clonePairManager);
			clonePairDetector.detect();

			ClonePairRegisterer.getInstance().registerAll(
					clonePairManager.getAllCloneSetPairs(),
					beforeContainer.getRevisionId(),
					afterContainer.getRevisionId());

			for (int i = 0; i <= currentIndex; i++) {
				containerContainer.remove(revisions[i]);
			}

			if (!containerContainer.containsKey(afterRevisionNum)) {
				containerContainer.put(afterRevisionNum, afterContainer);
			}
		}
	}
}
