package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.MovedBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.RetrievedBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.Table;
import jp.ac.osaka_u.ist.sdl.c20r.util.SimilarityCalculator;

public class MovedBlockPairDetector implements IBlockPairDetector {

	private final Map<Long, RetrievedBlockInfo> beforeBlocks;

	private final Map<Long, RetrievedBlockInfo> afterBlocks;

	// private static final String lineSeparator = System
	// .getProperty("line.separator");
	private static final String lineSeparator = "\\n";

	private final Table<Long, Long, CRDSimilarity> similarityTable;

	private final Map<Long, Queue<Long>> sortedAddedBlockIDs;

	public MovedBlockPairDetector(
			final Map<Long, RetrievedBlockInfo> beforeBlocks,
			final Map<Long, RetrievedBlockInfo> afterBlocks) {
		this.beforeBlocks = beforeBlocks;
		this.afterBlocks = afterBlocks;
		this.similarityTable = new Table<Long, Long, CRDSimilarity>();
		this.sortedAddedBlockIDs = new HashMap<Long, Queue<Long>>();
	}

	@Override
	public Set<MovedBlockPairInfo> detectMovedBlockPairs() {
		// final Map<Long, RetrievedBlockInfo> deletedBlocks =
		// detectDeletedBlocks(beforeBlocks);
		// final Map<Long, RetrievedBlockInfo> addedBlocks =
		// detectAddedBlocks(afterBlocks);

		final Map<Long, RetrievedBlockInfo> deletedBlocks = beforeBlocks;
		final Map<Long, RetrievedBlockInfo> addedBlocks = afterBlocks;

		resolveSameCrdBlock(deletedBlocks);
		resolveSameCrdBlock(addedBlocks);

		if (deletedBlocks.isEmpty() || addedBlocks.isEmpty()) {
			return new HashSet<MovedBlockPairInfo>();
		}

		fillSimilarityTable(deletedBlocks, addedBlocks);
		detectSortedAddedBlocksLists(deletedBlocks.keySet());

		final Map<Long, Long> detectedPairs = new TreeMap<Long, Long>();

		final List<Long> unmatchedDeletedElements = new ArrayList<Long>();
		unmatchedDeletedElements.addAll(deletedBlocks.keySet());

		final int minLength = Math
				.min(deletedBlocks.size(), addedBlocks.size());

		final List<Long> dummyList = new ArrayList<Long>();

		while (true) {
			if (unmatchedDeletedElements.isEmpty()
					|| detectedPairs.size() >= minLength) {
				break;
			}

			dummyList.clear();
			dummyList.addAll(unmatchedDeletedElements);
			for (final long deletedElement : dummyList) {
				final long mostSimilarAddedElement = this.sortedAddedBlockIDs
						.get(deletedElement).poll();
				if (mostSimilarAddedElement == -1) {
					continue;
				}

				if (detectedPairs.containsKey(mostSimilarAddedElement)) {
					final long rivalElement = detectedPairs
							.get(mostSimilarAddedElement);
					if (similarityTable.getValueAt(deletedElement,
							mostSimilarAddedElement).compareTo(
							similarityTable.getValueAt(rivalElement,
									mostSimilarAddedElement)) > 0) {
						detectedPairs.remove(mostSimilarAddedElement);
						detectedPairs.put(mostSimilarAddedElement,
								deletedElement);
						unmatchedDeletedElements.add(rivalElement);
						unmatchedDeletedElements.remove(deletedElement);
						break;
					}
				} else {
					detectedPairs.put(mostSimilarAddedElement, deletedElement);
					unmatchedDeletedElements.remove(deletedElement);
					break;
				}
			}
		}

		final Set<MovedBlockPairInfo> result = new HashSet<MovedBlockPairInfo>();

		for (final Map.Entry<Long, Long> entry : detectedPairs.entrySet()) {
			if (similarityTable.getValueAt(entry.getValue(), entry.getKey()).sameDiscriminator) {
				final RetrievedBlockInfo beforeBlock = deletedBlocks.get(entry
						.getValue());
				final RetrievedBlockInfo afterBlock = addedBlocks.get(entry
						.getKey());

				result.add(new MovedBlockPairInfo(beforeBlock, afterBlock));
			}
		}

		return result;
	}

	private void resolveSameCrdBlock(final Map<Long, RetrievedBlockInfo> target) {
		final Map<String, Set<RetrievedBlockInfo>> sameCrdBlocks = new HashMap<String, Set<RetrievedBlockInfo>>();
		for (final RetrievedBlockInfo tmp : target.values()) {
			if (sameCrdBlocks.containsKey(tmp.getCrdStr())) {
				sameCrdBlocks.get(tmp.getCrdStr()).add(tmp);
			} else {
				sameCrdBlocks.put(tmp.getCrdStr(),
						new HashSet<RetrievedBlockInfo>());
				sameCrdBlocks.get(tmp.getCrdStr()).add(tmp);
			}
		}

		for (final Map.Entry<String, Set<RetrievedBlockInfo>> entry : sameCrdBlocks
				.entrySet()) {
			final Set<RetrievedBlockInfo> tmpSet = entry.getValue();
			if (tmpSet.size() > 1) {
				final SortedMap<Integer, RetrievedBlockInfo> sortedSameCrdBlocks = new TreeMap<Integer, RetrievedBlockInfo>();
				for (final RetrievedBlockInfo tmpBlock : tmpSet) {
					sortedSameCrdBlocks.put(tmpBlock.getStartLine(), tmpBlock);
				}
				int count = 1;
				for (final Map.Entry<Integer, RetrievedBlockInfo> sameBlockEntry : sortedSameCrdBlocks
						.entrySet()) {
					final RetrievedBlockInfo block = sameBlockEntry.getValue();
					block.setCrdStr(block.getCrdStr() + (count++));
				}
			}
		}
	}

	private Map<Long, RetrievedBlockInfo> detectAddedBlocks(
			final Map<Long, RetrievedBlockInfo> blocks) {
		final Map<Long, RetrievedBlockInfo> result = new TreeMap<Long, RetrievedBlockInfo>();
		for (final Map.Entry<Long, RetrievedBlockInfo> entry : blocks
				.entrySet()) {
			final RetrievedBlockInfo block = entry.getValue();
			if (block.isAdded()) {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	private Map<Long, RetrievedBlockInfo> detectDeletedBlocks(
			final Map<Long, RetrievedBlockInfo> blocks) {
		final Map<Long, RetrievedBlockInfo> result = new TreeMap<Long, RetrievedBlockInfo>();
		for (final Map.Entry<Long, RetrievedBlockInfo> entry : blocks
				.entrySet()) {
			final RetrievedBlockInfo block = entry.getValue();
			if (block.isDeleted()) {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	private void fillSimilarityTable(
			final Map<Long, RetrievedBlockInfo> before,
			final Map<Long, RetrievedBlockInfo> after) {
		for (final Map.Entry<Long, RetrievedBlockInfo> beforeEntry : before
				.entrySet()) {
			for (final Map.Entry<Long, RetrievedBlockInfo> afterEntry : after
					.entrySet()) {
				final CRDSimilarity sim = new CRDSimilarity(
						beforeEntry.getValue(), afterEntry.getValue());
				similarityTable.changeValueAt(beforeEntry.getKey(),
						afterEntry.getKey(), sim);
			}
		}
	}

	private void detectSortedAddedBlocksLists(
			final Collection<Long> deletedBlocks) {
		for (final long deletedBlock : deletedBlocks) {
			this.sortedAddedBlockIDs.put(deletedBlock,
					detectSortedAddedBlocksList(deletedBlock));
		}
	}

	private Queue<Long> detectSortedAddedBlocksList(final long deletedElementId) {
		final Queue<Long> result = new LinkedBlockingQueue<Long>();
		final Map<Long, CRDSimilarity> addedBlocksWithSimilarity = new HashMap<Long, CRDSimilarity>();
		try {
			addedBlocksWithSimilarity.putAll(this.similarityTable
					.getValuesAt(deletedElementId));
		} catch (Exception e) {
			e.printStackTrace();
		}

		while (!addedBlocksWithSimilarity.isEmpty()) {
			final Long mostSimilarBlock = getMostSimilarElementId(addedBlocksWithSimilarity);
			result.offer(mostSimilarBlock);
			addedBlocksWithSimilarity.remove(mostSimilarBlock);
		}

		return result;
	}

	private long getMostSimilarElementId(Map<Long, CRDSimilarity> target) {
		long result = -1;
		for (final Map.Entry<Long, CRDSimilarity> entry : target.entrySet()) {
			if (result == -1) {
				result = entry.getKey();
			} else {
				if (entry.getValue().isSameDiscriminator()) {
					if (entry.getValue().compareTo(target.get(result)) > 0) {
						result = entry.getKey();
					}
				}
			}
		}
		return result;
	}

	private class CRDSimilarity implements Comparable<CRDSimilarity> {

		private final String beforeCrd;

		private final String afterCrd;

		/**
		 * 2つのCRDがどの深さまで一致しているか
		 */
		private final int matchingCRDDepth;

		/**
		 * 2つのCRDのパッケージがどの深さまで一致しているか
		 */
		private final int matchingPackageDepth;

		private final boolean sameDiscriminator;

		/**
		 * 2つのCRDの文字列表現のテキスト上の類似度
		 */
		private final double normalizedStringSimilarity;

		private final MetricsValues beforeMetrics;

		private final MetricsValues afterMetrics;

		private final boolean sameMetricsValues;

		private CRDSimilarity(final RetrievedBlockInfo beforeBlock,
				final RetrievedBlockInfo afterBlock) {
			this.beforeCrd = beforeBlock.getCrdStr();
			this.afterCrd = afterBlock.getCrdStr();
			this.matchingCRDDepth = detectMatchingCRDDepth(beforeCrd, afterCrd);
			this.matchingPackageDepth = detectMatchingPackageDepth(
					beforeCrd.split(lineSeparator)[0],
					afterCrd.split(lineSeparator)[0]);
			if (beforeBlock.getDiscriminator().equals(
					afterBlock.getDiscriminator())) {
				this.sameDiscriminator = true;
			} else if (beforeBlock.getBlockType().equals("METHOD")
					&& afterBlock.getBlockType().equals("METHOD")) {
				this.sameDiscriminator = satisfyConditions(beforeBlock,
						afterBlock);
			} else {
				this.sameDiscriminator = false;
			}
			final int ld = SimilarityCalculator.calcLevenshteinDistance(
					beforeCrd, afterCrd);
			final int maxLength = Math.max(beforeCrd.length(),
					afterCrd.length());
			this.normalizedStringSimilarity = 1.0 - ((double) ld / (double) maxLength);
			this.beforeMetrics = new MetricsValues(beforeBlock.getCC(),
					beforeBlock.getFO(), beforeBlock.getEndLine()
							- beforeBlock.getStartLine() + 1);
			this.afterMetrics = new MetricsValues(afterBlock.getCC(),
					afterBlock.getFO(), afterBlock.getEndLine()
							- afterBlock.getStartLine() + 1);
			this.sameMetricsValues = this.beforeMetrics
					.isSameValue(afterMetrics);
		}

		private boolean satisfyConditions(final RetrievedBlockInfo before,
				final RetrievedBlockInfo after) {

			if (before.getRootMethodName().equals(after.getRootMethodName())) {
				if (!before.getRootMethodName().equals("N/A")) {
					return true;
				}
			}

			if (before.getRootMethodParams().size() > 0
					&& before.getRootMethodParams().equals(
							after.getRootMethodParams())) {
				return true;
			}

			return false;
		}

		private int detectMatchingCRDDepth(final String beforeCrd,
				final String afterCrd) {
			final String[] before = beforeCrd.split(lineSeparator);
			final String[] after = afterCrd.split(lineSeparator);
			return detectMatchingDepth(before, after);
		}

		private int detectMatchingPackageDepth(final String beforePackage,
				final String afterPackage) {
			final String[] before = beforePackage.split("\\.");
			final String[] after = afterPackage.split("\\.");
			return detectMatchingDepth(before, after);
		}

		private int detectMatchingDepth(final String[] before,
				final String[] after) {
			int result = 0;

			final int minLength = Math.min(before.length, after.length);
			for (int i = 0; i < minLength; i++) {
				final String currentBefore = before[i];
				final String currentAfter = after[i];
				if (currentBefore.equals(currentAfter)) {
					result++;
				} else {
					break;
				}
			}

			return result;
		}

		@Override
		public int compareTo(CRDSimilarity another) {
			// 片方が同じdiscriminatorでもう片方が違うdiscriminatorなら，その時点で同じdiscriminatorの方が一致度が高い
			if (this.sameDiscriminator && !another.sameDiscriminator) {
				return 1;
			} else if (!this.sameDiscriminator && another.sameDiscriminator) {
				return -1;
			}

			/*
			 * if (this.matchingCRDDepth != another.getMatchingCRDDepth()) {
			 * return ((Integer) this.matchingCRDDepth).compareTo(another
			 * .getMatchingCRDDepth()); }
			 * 
			 * return ((Integer) this.matchingPackageDepth).compareTo(another
			 * .getMatchingPackageDepth());
			 */

			// CRDの類似度で比較
			final int similarityCompared = ((Double) this.normalizedStringSimilarity)
					.compareTo(another.getNormalizedStringSimilarity());

			if (similarityCompared != 0) {
				return similarityCompared;
			}

			// CRDの類似度も同じなら，メトリクス値が同じほうを優先
			if (this.isSameMetricsValues() && !another.isSameMetricsValues()) {
				return 1;
			} else if (!this.isSameMetricsValues()
					&& another.isSameMetricsValues()) {
				return -1;
			}

			// それでも同じならあきらめる
			return 0;
		}

		public int getMatchingCRDDepth() {
			return this.matchingCRDDepth;
		}

		public int getMatchingPackageDepth() {
			return this.matchingPackageDepth;
		}

		public boolean isSameDiscriminator() {
			return this.sameDiscriminator;
		}

		public double getNormalizedStringSimilarity() {
			return this.normalizedStringSimilarity;
		}

		public boolean isSameMetricsValues() {
			return this.sameMetricsValues;
		}

		private class MetricsValues {

			private final int cyclomaticComplexity;

			private final int fanOut;

			private final double decisionDensity;

			public MetricsValues(final int cyclomaticComplexity,
					final int fanOut, final int loc) {
				this.cyclomaticComplexity = cyclomaticComplexity;
				this.fanOut = fanOut;
				this.decisionDensity = (double) cyclomaticComplexity
						/ (double) loc;
			}

			public double sum() {
				return (double) cyclomaticComplexity + (double) fanOut
						+ decisionDensity;
			}

			public boolean isSameValue(final MetricsValues another) {
				if (this.sum() == another.sum()) {
					return true;
				} else {
					return false;
				}
			}

		}

	}

}
