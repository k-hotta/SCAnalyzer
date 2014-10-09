package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.AddedBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.DeletedBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.MatchedBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.MovedBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.RetrievedBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.manager.BlockPairManager;

public class BlockPairDetector {

	private final long beforeRevId;

	private final Map<Long, RetrievedBlockInfo> beforeBlocks;

	private final Map<Long, RetrievedBlockInfo> afterBlocks;

	private final BlockPairManager manager;

	private final CRDMode crdMode;

	public BlockPairDetector(long beforeRevId,
			Map<Long, RetrievedBlockInfo> beforeBlocks,
			Map<Long, RetrievedBlockInfo> afterBlocks,
			BlockPairManager manager, final CRDMode crdMode) {
		this.beforeRevId = beforeRevId;
		this.beforeBlocks = new TreeMap<Long, RetrievedBlockInfo>();
		this.beforeBlocks.putAll(beforeBlocks);
		this.afterBlocks = new TreeMap<Long, RetrievedBlockInfo>();
		this.afterBlocks.putAll(afterBlocks);
		this.manager = manager;
		this.crdMode = crdMode;
	}

	public void detect() {
		final Set<MatchedBlockPairInfo> matchedPairsInNotChangedFiles = processBlocksInNotChangedFiles();
		for (final MatchedBlockPairInfo matchedPair : matchedPairsInNotChangedFiles) {
			this.manager.add(matchedPair);
		}
		processBlocksInChangedFiles();
	}

	private Set<MatchedBlockPairInfo> processBlocksInNotChangedFiles() {
		final Set<MatchedBlockPairInfo> result = new HashSet<MatchedBlockPairInfo>();

		final Set<Long> toRemoveKeys = new HashSet<Long>();

		for (final Map.Entry<Long, RetrievedBlockInfo> entry : beforeBlocks
				.entrySet()) {
			final long beforeBlockId = entry.getKey();
			final RetrievedBlockInfo beforeBlock = entry.getValue();

			if (beforeBlock.isInChangedFile(beforeRevId)) {
				continue;
			}

			if (!afterBlocks.containsKey(beforeBlockId)) {
				assert false; // here shouldn't be reached!!
				System.err.println("ERROR: #" + beforeBlockId
						+ " block cannot be found!!");
				continue;
			}

			final RetrievedBlockInfo afterBlock = afterBlocks
					.get(beforeBlockId);

			final MatchedBlockPairInfo matchedPair = new MatchedBlockPairInfo(
					beforeBlock, afterBlock);
			result.add(matchedPair);
			toRemoveKeys.add(beforeBlockId);
		}

		for (final long toRemoveKey : toRemoveKeys) {
			beforeBlocks.remove(toRemoveKey);
			afterBlocks.remove(toRemoveKey);
		}

		return result;
	}

	public void processBlocksInChangedFiles() {
		Set<BlockPairCandidate> pairCandidates = detectPairCandidates();

		for (BlockPairCandidate candidate : pairCandidates) {
			candidate.finalize();
			for (MatchedBlockPairInfo matchedPair : candidate
					.getDetectedPairs()) {
				manager.add(matchedPair);
				beforeBlocks.remove(matchedPair.getBeforeBlock().getId());
				afterBlocks.remove(matchedPair.getAfterBlock().getId());
			}
		}

		// ブロックの移動追跡処理
		IBlockPairDetector moveDetector = null;
		if (crdMode == CRDMode.TRADITIONAL) {
			moveDetector = new TraditionalMovedBlockPairDetector(beforeBlocks,
					afterBlocks);
		} else if (crdMode == CRDMode.ENHANCED) {
			moveDetector = new MovedBlockPairDetector(beforeBlocks, afterBlocks);
		}

		final Set<MovedBlockPairInfo> movedBlockPairs = moveDetector
				.detectMovedBlockPairs();

		for (final MovedBlockPairInfo movedBlockPair : movedBlockPairs) {
			manager.add(movedBlockPair);
			beforeBlocks.remove(movedBlockPair.getBeforeBlock().getId());
			afterBlocks.remove(movedBlockPair.getAfterBlock().getId());
		}

		for (RetrievedBlockInfo remainingBeforeBlock : beforeBlocks.values()) {
			final DeletedBlockPairInfo deletedPair = new DeletedBlockPairInfo(
					remainingBeforeBlock);
			manager.add(deletedPair);
		}

		for (RetrievedBlockInfo remainingAfterBlock : afterBlocks.values()) {
			final AddedBlockPairInfo addedPair = new AddedBlockPairInfo(
					remainingAfterBlock);
			manager.add(addedPair);
		}

	}

	private Set<BlockPairCandidate> detectPairCandidates() {
		Set<RetrievedBlockInfo> processedBeforeBlocks = new HashSet<RetrievedBlockInfo>();
		Set<RetrievedBlockInfo> processedAfterBlocks = new HashSet<RetrievedBlockInfo>();
		Set<BlockPairCandidate> pairCandidates = new HashSet<BlockPairCandidate>();

		// 一致するブロックペアを特定
		for (RetrievedBlockInfo beforeBlock : beforeBlocks.values()) {
			for (RetrievedBlockInfo afterBlock : afterBlocks.values()) {
				if (beforeBlock.isMatchWithoutCm(afterBlock)) {
					if (processedBeforeBlocks.contains(beforeBlock)) {
						// 前ブロックとマッチする後ブロックが存在する時
						final long id = beforeBlock.getId();
						BlockPairCandidate correpondentPairCandidate = null;
						for (BlockPairCandidate candidate : pairCandidates) {
							if (candidate.containsBefore(id)) {
								correpondentPairCandidate = candidate;
								break;
							}
						}

						correpondentPairCandidate.addAfter(afterBlock);

					} else if (processedAfterBlocks.contains(afterBlock)) {
						// 後ブロックが複数の前ブロックとマッチしているとき
						final long id = afterBlock.getId();
						BlockPairCandidate correspondentPairCandidate = null;
						for (BlockPairCandidate candidate : pairCandidates) {
							if (candidate.containsAfter(id)) {
								correspondentPairCandidate = candidate;
								break;
							}
						}

						correspondentPairCandidate.addBefore(beforeBlock);

					} else {
						// 前ブロックも後ブロックも対応するものがはじめて見つかったとき
						final BlockPairCandidate candidate = new BlockPairCandidate(
								beforeBlock, afterBlock);
						pairCandidates.add(candidate);
					}

					processedBeforeBlocks.add(beforeBlock);
					processedAfterBlocks.add(afterBlock);
				}
			}
		}

		return pairCandidates;
	}

}
