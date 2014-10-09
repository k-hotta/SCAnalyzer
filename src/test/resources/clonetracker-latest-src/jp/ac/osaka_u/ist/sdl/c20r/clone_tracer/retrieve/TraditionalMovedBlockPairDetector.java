package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.MovedBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.RetrievedBlockInfo;

public class TraditionalMovedBlockPairDetector implements IBlockPairDetector {

	private final Map<Long, RetrievedBlockInfo> beforeBlocks;

	private final Map<Long, RetrievedBlockInfo> afterBlocks;

	// private static final String lineSeparator = System
	// .getProperty("line.separator");
	private static final String lineSeparator = "\\n";

	public TraditionalMovedBlockPairDetector(
			final Map<Long, RetrievedBlockInfo> beforeBlocks,
			final Map<Long, RetrievedBlockInfo> afterBlocks) {
		this.beforeBlocks = beforeBlocks;
		this.afterBlocks = afterBlocks;
	}

	@Override
	public Set<MovedBlockPairInfo> detectMovedBlockPairs() {
		// final Map<Long, RetrievedBlockInfo> deletedBlocks =
		// detectDeletedBlocks(beforeBlocks);
		// final Map<Long, RetrievedBlockInfo> addedBlocks =
		// detectAddedBlocks(afterBlocks);

		final Map<Long, RetrievedBlockInfo> deletedBlocks = beforeBlocks;
		final Map<Long, RetrievedBlockInfo> addedBlocks = afterBlocks;

		if (deletedBlocks.isEmpty() || addedBlocks.isEmpty()) {
			return new HashSet<MovedBlockPairInfo>();
		}

		final Set<MovedBlockPairInfo> result = new HashSet<MovedBlockPairInfo>();

		for (final RetrievedBlockInfo deletedBlock : deletedBlocks.values()) {

			if (deletedBlock.getRootMethodName().equals("N/A")) {
				continue;
			}

			final String beforeCrdAfterRootMethod = deletedBlock
					.getCrdAfterRootMethod();

			RetrievedBlockInfo matchedAfterBlock = null;
			for (final RetrievedBlockInfo addedBlock : addedBlocks.values()) {

				if (addedBlock.getRootMethodName().equals("N/A")) {
					continue;
				}

				final String afterCrdAfterRootMethod = addedBlock
						.getCrdAfterRootMethod();

				if (beforeCrdAfterRootMethod.isEmpty()) {
					if (afterCrdAfterRootMethod.isEmpty()) {
						// ここに来たときは両方がメソッド
						if (satisfyConditions(deletedBlock, addedBlock)) {
							result.add(new MovedBlockPairInfo(deletedBlock,
									addedBlock));
							matchedAfterBlock = addedBlock;
							break;
						}
					}
				} else if (deletedBlock.getCrdAfterRootMethod().equals(
						addedBlock.getCrdAfterRootMethod())) {
					// ここに来たときは両方ブロックでかつメソッド以下のCRDは一致
					if (satisfyConditions(deletedBlock, addedBlock)) {
						result.add(new MovedBlockPairInfo(deletedBlock,
								addedBlock));
						matchedAfterBlock = addedBlock;
						break;
					}
				}
			}

			if (matchedAfterBlock != null) {
				addedBlocks.remove(matchedAfterBlock.getId());
			}
		}

		return result;
	}

	private boolean satisfyConditions(final RetrievedBlockInfo before,
			final RetrievedBlockInfo after) {
		if (before.getRootClassName().equals(after.getRootClassName())) {
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
			
		} else {
			
			if (before.getRootMethodName().equals(after.getRootMethodName())) {
				if (!before.getRootMethodName().equals("N/A")) {
					if (before.getRootMethodParams().size() > 0
							&& before.getRootMethodParams().equals(
									after.getRootMethodParams())) {
						return true;
					}
				}
			}
		}

		return false;
	}

}
