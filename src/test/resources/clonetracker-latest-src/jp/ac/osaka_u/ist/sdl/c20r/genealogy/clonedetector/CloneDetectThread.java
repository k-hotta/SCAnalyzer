package jp.ac.osaka_u.ist.sdl.c20r.genealogy.clonedetector;

import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.RetrievedFileInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.RetrievedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.RetrievedBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone.RetrievedCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve.BlockInfoRetriever;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve.CloneSetInfoRetriever;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve.FileInfoRetriever;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve.RevisionInfoRetriever;

public class CloneDetectThread implements Runnable {

	private final int[] revisions;

	private final AtomicInteger index;

	public CloneDetectThread(final int[] revisions, final AtomicInteger index) {
		this.revisions = revisions;
		this.index = index;
	}

	@Override
	public void run() {
		while (true) {
			final int currentIndex = index.getAndIncrement();

			if (currentIndex >= revisions.length) {
				break;
			}

			final int revisionNum = revisions[currentIndex];

			System.out
					.println("\tnow analyzing revision " + revisionNum + ". ["
							+ (currentIndex + 1) + "/" + revisions.length + "]");

			final RetrievedRevisionInfo revisionInfo = new RevisionInfoRetriever(
					0, revisionNum).retrieveAll().first();
			final long revisionId = revisionInfo.getId();

			final BlockInfoRetriever blockRetriever = new BlockInfoRetriever(
					revisionId);
			final SortedSet<RetrievedBlockInfo> retrievedBlocks = blockRetriever
					.retrieveAll();

			final FileInfoRetriever fileRetriever = new FileInfoRetriever(
					revisionId, blockRetriever.getToRetrieveFileIds());
			final SortedSet<RetrievedFileInfo> retrievedFiles = fileRetriever
					.retrieveAll();

			final CloneSetInfoRetriever cloneRetriever = new CloneSetInfoRetriever(
					retrievedBlocks, true);
			final SortedSet<RetrievedCloneSetInfo> retrievedClones = cloneRetriever
					.retrieveAll();

			CloneRegisterer.getInstance().registerAll(retrievedClones,
					revisionInfo.getId());
		}
	}
}
