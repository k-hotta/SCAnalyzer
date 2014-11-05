package jp.ac.osaka_u.ist.sdl.c20r.genealogy;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.RetrievedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.RetrievedBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone.RetrievedCloneSetInfo;

public class RetrievedDataContainer {

	private final long revisionId;

	private final RetrievedRevisionInfo revisionInfo;

	private final SortedMap<Long, RetrievedBlockInfo> blocks;

	private final SortedMap<Long, RetrievedCloneSetInfo> clones;

	public RetrievedDataContainer(final long revisionId,
			final RetrievedRevisionInfo revisionInfo,
			final Collection<RetrievedBlockInfo> blocks,
			final Collection<RetrievedCloneSetInfo> clones) {
		this.revisionId = revisionId;
		this.revisionInfo = revisionInfo;
		this.blocks = new TreeMap<Long, RetrievedBlockInfo>();
		for (final RetrievedBlockInfo block : blocks) {
			this.blocks.put(block.getId(), block);
		}
		this.clones = new TreeMap<Long, RetrievedCloneSetInfo>();
		for (final RetrievedCloneSetInfo clone : clones) {
			this.clones.put(clone.getId(), clone);
		}
	}

	public final long getRevisionId() {
		return revisionId;
	}

	public final RetrievedRevisionInfo getRevisionInfo() {
		return revisionInfo;
	}

	public final Map<Long, RetrievedBlockInfo> getBlocks() {
		return Collections.unmodifiableMap(blocks);
	}

	public final Map<Long, RetrievedCloneSetInfo> getClones() {
		return Collections.unmodifiableMap(clones);
	}

}
