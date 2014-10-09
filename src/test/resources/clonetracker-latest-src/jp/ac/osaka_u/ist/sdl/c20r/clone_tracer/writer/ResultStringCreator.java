package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.writer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.RetrievedFileInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.AbstractBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.MovedBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.RetrievedBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone.AbstractCloneSetPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone.RetrievedCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.manager.RevisionManager;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.settings.TracerSettings;
import jp.ac.osaka_u.ist.sdl.c20r.util.SimilarityCalculator;

/**
 * 解析が終了したデータについて，ファイルに出力する用の文字列を生成するためのクラス
 * 
 * @author k-hotta
 * 
 */
public class ResultStringCreator {

	private final static String LINE_SEPARATOR = System
			.getProperty("line.separator");

	private final static int workingDirPathLength = TracerSettings
			.getInstance().getWorkingDir().length();

	public static String create(final RevisionManager manager,
			final long timeElapsed) {
		final StringBuilder builder = new StringBuilder();

		final Map<Long, RetrievedFileInfo> beforeFiles = manager
				.getBeforeDataManager().getFileManager().getMap();
		final Map<Long, RetrievedFileInfo> afterFiles = manager
				.getAfterDataManager().getFileManager().getMap();

		final int beforeRevNum = manager.getBeforeRevision().getRevisionNum();
		final int afterRevNum = manager.getAfterRevision().getRevisionNum();
		final int beforeCloneSetCount = manager.getBeforeDataManager()
				.getCloneManager().size();
		final int afterCloneSetCount = manager.getAfterDataManager()
				.getCloneManager().size();

		builder.append("BEFORE,AFTER,CLONES_IN_BEFORE,CLONES_IN_AFTER,TIME_ELAPSED"
				+ LINE_SEPARATOR);
		builder.append(beforeRevNum + "," + afterRevNum + ","
				+ beforeCloneSetCount + "," + afterCloneSetCount + ","
				+ timeElapsed + " ms" + LINE_SEPARATOR);

		builder.append("CLONE_ID,PATH,START_LINE,END_LINE,LENGTH,CC,FO,DISAPPEAR,FILE_DELETION,AFTER_START_LINE,AFTER_END_LINE,#_CLONE_ELEMENTS,MOVED,AFTER_PATH,BEFORE_CRD,AFTER_CRD,SIMILARITY,LD"
				+ LINE_SEPARATOR);

		final Map<Long, CloneSetAsResultString> lines = detectLines(manager,
				beforeFiles, afterFiles);

		for (Map.Entry<Long, CloneSetAsResultString> entry : lines.entrySet()) {
			for (final String line : entry.getValue().getLines().values()) {
				builder.append(line);
			}
		}

		return builder.toString();
	}

	private static Map<Long, CloneSetAsResultString> detectLines(
			final RevisionManager manager,
			final Map<Long, RetrievedFileInfo> beforeFiles,
			final Map<Long, RetrievedFileInfo> afterFiles) {
		// final Map<Long, AbstractBlockPairInfo> blockPairs = manager
		// .getBlockPairManager().getAllPairsAsMap();
		final Map<Long, CloneSetAsResultString> lines = new TreeMap<Long, CloneSetAsResultString>();
		final Set<Long> alreadyWritten = new HashSet<Long>();

		for (final AbstractCloneSetPairInfo pair : manager.getCloneManager()
				.getAllCloneSetPairs()) {

			final RetrievedCloneSetInfo beforeCloneSet = pair
					.getBeforeCloneSet();

			if (beforeCloneSet == null) {
				continue;
			}

			final long beforeCloneSetId = beforeCloneSet.getId();

			final Map<Long, AbstractBlockPairInfo> correspondingBlockPairs = pair
					.getBlockPairMap();
			for (Map.Entry<Long, AbstractBlockPairInfo> tmpPair : correspondingBlockPairs
					.entrySet()) {
				final long pairId = tmpPair.getKey();
				final AbstractBlockPairInfo blockPair = tmpPair.getValue();
				if (alreadyWritten.contains(pairId)
						|| blockPair.getBeforeBlock() == null) {
					continue;
				}

				final String line = convertBlockPairIntoString(beforeFiles,
						afterFiles, beforeCloneSet, blockPair);

				if (lines.containsKey(beforeCloneSetId)) {
					lines.get(beforeCloneSetId).addLine(line);
				} else {
					lines.put(beforeCloneSetId, new CloneSetAsResultString(
							beforeCloneSetId, line));
				}

				alreadyWritten.add(pairId);
			}

			// final RetrievedCloneSetInfo beforeCloneSet = pair
			// .getBeforeCloneSet();
			//
			// if (beforeCloneSet == null) {
			// continue;
			// }
			//
			// final Set<RetrievedBlockInfo> disappearedElements = pair
			// .getDeletedBlocks();
			//
			// for (final RetrievedBlockInfo block : beforeCloneSet
			// .getAllElements()) {
			// final AbstractBlockPairInfo blockPair = blockPairs.get(block
			// .getBlockPairId());
			// writeCloneSetElement(builder, beforeFiles, afterFiles,
			// beforeCloneSet, disappearedElements, block,
			// blockPair.getAfterBlock(),
			// (blockPair instanceof MovedBlockPairInfo));
			// }
		}
		return lines;
	}

	private static String convertBlockPairIntoString(
			final Map<Long, RetrievedFileInfo> beforeFiles,
			final Map<Long, RetrievedFileInfo> afterFiles,
			final RetrievedCloneSetInfo beforeCloneSet,
			final AbstractBlockPairInfo blockPair) {
		final StringBuilder builder = new StringBuilder();

		final RetrievedBlockInfo beforeBlock = blockPair.getBeforeBlock();
		final RetrievedBlockInfo afterBlock = blockPair.getAfterBlock();
		final boolean moved = blockPair instanceof MovedBlockPairInfo;

		builder.append(beforeCloneSet.getId() + ",");
		builder.append(beforeFiles.get(beforeBlock.getFileId()).getPath()
				.substring(workingDirPathLength)
				+ ",");
		builder.append(beforeBlock.getStartLine() + ",");
		builder.append(beforeBlock.getEndLine() + ",");
		builder.append(beforeBlock.getLength() + ",");
		builder.append(beforeBlock.getCC() + ",");
		builder.append(beforeBlock.getFO() + ",");

		if (blockPair.isDeleted()) {
			builder.append("1");
		} else {
			builder.append("0");
		}
		builder.append(",");
		if (beforeBlock.isInDeletedFile()) {
			builder.append("1");
		} else {
			builder.append("0");
		}
		builder.append(",");

		final int afterStart = (afterBlock == null) ? -1 : afterBlock
				.getStartLine();
		final int afterEnd = (afterBlock == null) ? -1 : afterBlock
				.getEndLine();
		builder.append(afterStart + ",");
		builder.append(afterEnd + ",");
		builder.append(beforeCloneSet.getCount() + ",");
		if (moved) {
			builder.append("1");
		} else {
			builder.append("0");
		}
		builder.append(",");
		if (afterBlock == null) {
			builder.append("N/A" + ",");
		} else {
			builder.append(afterFiles.get(afterBlock.getFileId()).getPath()
					.substring(workingDirPathLength)
					+ ",");
		}

		String beforeCrd = beforeBlock.getCrdStr();
		String afterCrd = (afterBlock == null) ? "N/A" : afterBlock.getCrdStr();

		builder.append(getCrdForPrint(beforeCrd) + ",");
		builder.append(getCrdForPrint(afterCrd) + ",");

		if (afterBlock != null) {
			final int ld = SimilarityCalculator.calcLevenshteinDistance(
					beforeCrd, afterCrd);
			final int maxLength = Math.max(beforeCrd.length(),
					afterCrd.length());
			final double normalizedStringSimilarity = 1.0 - ((double) ld / (double) maxLength);

			builder.append(((Double) normalizedStringSimilarity).toString()
					+ ",");
			builder.append(ld);
		} else {
			builder.append("-1.0,");
			builder.append("-1");
		}

		builder.append(LINE_SEPARATOR);

		return builder.toString();
	}

	private static void writeCloneSetElement(StringBuilder builder,
			final Map<Long, RetrievedFileInfo> beforeFiles,
			final Map<Long, RetrievedFileInfo> afterFiles,
			final RetrievedCloneSetInfo beforeCloneSet,
			final Set<RetrievedBlockInfo> disappearedElements,
			final RetrievedBlockInfo beforeBlock,
			final RetrievedBlockInfo afterBlock, final boolean moved) {
		builder.append(beforeCloneSet.getId() + ",");
		builder.append(beforeFiles.get(beforeBlock.getFileId()).getPath()
				.substring(workingDirPathLength)
				+ ",");
		builder.append(beforeBlock.getStartLine() + ",");
		builder.append(beforeBlock.getEndLine() + ",");
		builder.append(beforeBlock.getLength() + ",");
		builder.append(beforeBlock.getCC() + ",");
		builder.append(beforeBlock.getFO() + ",");
		if (disappearedElements.contains(beforeBlock)) {
			builder.append("1");
		} else {
			builder.append("0");
		}
		builder.append(",");
		if (beforeBlock.isInDeletedFile()) {
			builder.append("1");
		} else {
			builder.append("0");
		}
		builder.append(",");

		final int afterStart = (afterBlock == null) ? -1 : afterBlock
				.getStartLine();
		final int afterEnd = (afterBlock == null) ? -1 : afterBlock
				.getEndLine();
		builder.append(afterStart + ",");
		builder.append(afterEnd + ",");
		builder.append(beforeCloneSet.getCount() + ",");
		if (moved) {
			builder.append("1");
		} else {
			builder.append("0");
		}
		builder.append(",");
		if (afterBlock == null) {
			builder.append("N/A" + ",");
		} else {
			builder.append(afterFiles.get(afterBlock.getFileId()).getPath()
					.substring(workingDirPathLength)
					+ ",");
		}

		String beforeCrd = beforeBlock.getCrdStr();
		String afterCrd = (afterBlock == null) ? "N/A" : afterBlock.getCrdStr();

		builder.append(getCrdForPrint(beforeCrd) + ",");
		builder.append(getCrdForPrint(afterCrd) + ",");

		if (afterBlock != null) {
			final int ld = SimilarityCalculator.calcLevenshteinDistance(
					beforeCrd, afterCrd);
			final int maxLength = Math.max(beforeCrd.length(),
					afterCrd.length());
			final double normalizedStringSimilarity = 1.0 - ((double) ld / (double) maxLength);

			builder.append(((Double) normalizedStringSimilarity).toString()
					+ ",");
			builder.append(ld);
		} else {
			builder.append("-1.0,");
			builder.append("-1");
		}

		builder.append(LINE_SEPARATOR);
	}

	private static String getCrdForPrint(final String crdStr) {
		return crdStr.replaceAll("\\n", "\\\\\\\\").replaceAll(",", ":");
	}

}
