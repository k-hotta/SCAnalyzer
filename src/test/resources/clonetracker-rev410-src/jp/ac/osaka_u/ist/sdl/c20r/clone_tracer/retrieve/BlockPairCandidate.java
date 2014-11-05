package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.MatchedBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.RetrievedBlockInfo;

/**
 * ブロックペア形成時の候補となるブロック同士の関係を保持するクラス
 * 
 * @author k-hotta
 * 
 */
public class BlockPairCandidate {

	private final Map<Long, RetrievedBlockInfo> beforeBlocks;

	private final Map<Long, RetrievedBlockInfo> afterBlocks;

	private final Set<MatchedBlockPairInfo> detectedPairs;

	public BlockPairCandidate(final RetrievedBlockInfo beforeBlock,
			final RetrievedBlockInfo afterBlock) {
		this.beforeBlocks = new TreeMap<Long, RetrievedBlockInfo>();
		this.beforeBlocks.put(beforeBlock.getId(), beforeBlock);
		this.afterBlocks = new TreeMap<Long, RetrievedBlockInfo>();
		this.afterBlocks.put(afterBlock.getId(), afterBlock);
		this.detectedPairs = new HashSet<MatchedBlockPairInfo>();
	}
	
	public Map<Long, RetrievedBlockInfo> getBeforeBlocksMap() {
		return Collections.unmodifiableMap(beforeBlocks);
	}
	
	public Map<Long, RetrievedBlockInfo> getAfterBlocksMap() {
		return Collections.unmodifiableMap(afterBlocks);
	}
	
	public Set<MatchedBlockPairInfo> getDetectedPairs() {
		return Collections.unmodifiableSet(detectedPairs);
	}

	public boolean containsBefore(final long key) {
		return beforeBlocks.containsKey(key);
	}

	public boolean containsAfter(final long key) {
		return afterBlocks.containsKey(key);
	}

	public void addBefore(final RetrievedBlockInfo beforeBlock) {
		this.beforeBlocks.put(beforeBlock.getId(), beforeBlock);
	}

	public void addAfter(final RetrievedBlockInfo afterBlock) {
		this.afterBlocks.put(afterBlock.getId(), afterBlock);
	}

	public void finalize() {
		Set<Long> processedBeforeBlocks = new HashSet<Long>();
		Set<Long> processedAfterBlocks = new HashSet<Long>();
		for (Map.Entry<Long, RetrievedBlockInfo> entryBefore : beforeBlocks
				.entrySet()) {
			final RetrievedBlockInfo beforeBlock = entryBefore.getValue();
			RetrievedBlockInfo closestAfterBlock = null;
			int minDistance = Integer.MAX_VALUE;
			final int cmBefore = beforeBlock.getCm();
			boolean isMatch = false;

			for (Map.Entry<Long, RetrievedBlockInfo> entryAfter : afterBlocks
					.entrySet()) {
				final RetrievedBlockInfo afterBlock = entryAfter.getValue();

				if (processedAfterBlocks.contains(afterBlock.getId())) {
					continue;
				}

				final int cmAfter = afterBlock.getCm();

				final int distance = (cmBefore >= cmAfter) ? cmBefore - cmAfter
						: cmAfter - cmBefore;

				if (distance == 0) {
					closestAfterBlock = afterBlock;
					isMatch = true;
					break;
				}

				if (distance < minDistance) {
					minDistance = distance;
					closestAfterBlock = afterBlock;
					isMatch = true;
				}
			}

			if (isMatch) {
				final MatchedBlockPairInfo pair = new MatchedBlockPairInfo(
						beforeBlock, closestAfterBlock);
				processedBeforeBlocks.add(beforeBlock.getId());
				processedAfterBlocks.add(closestAfterBlock.getId());

				detectedPairs.add(pair);
			}
		}

		for (Long id : processedBeforeBlocks) {
			beforeBlocks.remove(id);
		}

		for (Long id : processedAfterBlocks) {
			afterBlocks.remove(id);
		}
	}

}
