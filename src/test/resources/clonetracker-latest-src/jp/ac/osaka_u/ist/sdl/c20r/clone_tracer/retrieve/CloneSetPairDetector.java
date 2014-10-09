package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.AbstractBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.AddedBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.DeletedBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.MatchedBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone.AbstractCloneSetPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone.RetrievedCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.manager.CloneSetPairManager;

public class CloneSetPairDetector {

	private final Set<AddedBlockPairInfo> addedBlockPairs;

	private final Set<DeletedBlockPairInfo> deletedBlockPairs;

	private final Set<MatchedBlockPairInfo> matchedBlockPairs;

	private final Map<Long, RetrievedCloneSetInfo> beforeCloneSets;

	private final Map<Long, RetrievedCloneSetInfo> afterCloneSets;

	private final CloneSetPairManager manager;

	public CloneSetPairDetector(final Set<AddedBlockPairInfo> addedBlockPairs,
			final Set<DeletedBlockPairInfo> deletedBlockPairs,
			final Set<MatchedBlockPairInfo> matchedBlockPairs,
			final Map<Long, RetrievedCloneSetInfo> beforeCloneSets,
			final Map<Long, RetrievedCloneSetInfo> afterCloneSets,
			final CloneSetPairManager manager) {
		this.addedBlockPairs = addedBlockPairs;
		this.deletedBlockPairs = deletedBlockPairs;
		this.matchedBlockPairs = matchedBlockPairs;
		this.beforeCloneSets = beforeCloneSets;
		this.afterCloneSets = afterCloneSets;
		this.manager = manager;
	}

	public void detect() {
		final ArrayList<AbstractCloneSetPairInfo> cloneSetPairs = new ArrayList<AbstractCloneSetPairInfo>();

		for (final MatchedBlockPairInfo matchedBlockPair : matchedBlockPairs) {
			final long beforeCloneId = matchedBlockPair.getBeforeBlock()
					.getCloneSetId();
			final long afterCloneId = matchedBlockPair.getAfterBlock()
					.getCloneSetId();

			if ((beforeCloneId == -1) && (afterCloneId == -1)) {
				continue;
			}

			processBlockPair(cloneSetPairs, matchedBlockPair, beforeCloneId,
					afterCloneId);
		}

		for (final AddedBlockPairInfo addedBlockPair : addedBlockPairs) {
			final long beforeCloneId = -1;
			final long afterCloneId = addedBlockPair.getAfterBlock()
					.getCloneSetId();

			if (afterCloneId == -1) {
				continue;
			}

			processBlockPair(cloneSetPairs, addedBlockPair, beforeCloneId,
					afterCloneId);
		}

		for (final DeletedBlockPairInfo deletedBlockPair : deletedBlockPairs) {
			final long beforeCloneId = deletedBlockPair.getBeforeBlock()
					.getCloneSetId();
			final long afterCloneId = -1;

			if (beforeCloneId == -1) {
				continue;
			}

			processBlockPair(cloneSetPairs, deletedBlockPair, beforeCloneId,
					afterCloneId);
		}

		for (AbstractCloneSetPairInfo pair : cloneSetPairs) {
			pair.finalize();
			manager.add(pair);
		}

	}

	private void processBlockPair(
			final ArrayList<AbstractCloneSetPairInfo> cloneSetPairs,
			final AbstractBlockPairInfo blockPair, final long beforeCloneId,
			final long afterCloneId) {

		boolean isRegistered = false;
		for (final AbstractCloneSetPairInfo registeredPair : cloneSetPairs) {
			if (registeredPair.isCorrespondent(beforeCloneId, afterCloneId)) {
				registeredPair.addBlockPair(blockPair);
				isRegistered = true;
				break;
			}
		}
		if (!isRegistered) {
			final AbstractCloneSetPairInfo newPair = AbstractCloneSetPairInfo
					.createInstance(beforeCloneId, beforeCloneSets,
							afterCloneId, afterCloneSets);
			if (newPair != null) {
				newPair.addBlockPair(blockPair);
				cloneSetPairs.add(newPair);
			}
		}

	}
}
