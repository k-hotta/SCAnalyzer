package jp.ac.osaka_u.ist.sdl.c20r.genealogy;

import java.util.SortedSet;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.RetrievedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.RetrievedBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone.RetrievedCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve.BlockInfoRetriever;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve.RevisionInfoRetriever;

public class DataRetriever {

	public static RetrievedDataContainer retrieve(final int revisionNum) {
		final RevisionInfoRetriever revisionRetriever = new RevisionInfoRetriever(
				0, revisionNum);
		final RetrievedRevisionInfo revisionInfo = revisionRetriever
				.retrieveAll().first();

		final BlockInfoRetriever blockRetriever = new BlockInfoRetriever(
				revisionInfo.getId());
		final SortedSet<RetrievedBlockInfo> blocks = blockRetriever
				.retrieveAll();

		final CloneSetInfoRetriever cloneRetriever = new CloneSetInfoRetriever(
				revisionInfo.getId(), blocks);
		final SortedSet<RetrievedCloneSetInfo> clones = cloneRetriever
				.retrieveAll();

		return new RetrievedDataContainer(revisionInfo.getId(), revisionInfo,
				blocks, clones);
	}
}
