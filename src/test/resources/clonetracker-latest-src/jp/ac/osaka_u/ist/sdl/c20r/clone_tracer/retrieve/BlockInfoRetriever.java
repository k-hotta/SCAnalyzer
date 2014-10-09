package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.RetrievedBlockInfo;

public class BlockInfoRetriever extends
		AbstractElementRetriever<RetrievedBlockInfo> {

	private final SortedSet<Long> toRetrieveFileIds;

	public BlockInfoRetriever(final long revisionId) {
		super(revisionId);
		toRetrieveFileIds = new TreeSet<Long>();
	}

	@Override
	protected String getSelectStatement(long revisionId) {
		return "select * from BLOCK where START_REV_ID <= " + revisionId
				+ " AND END_REV_ID >= " + revisionId;
	}

	@Override
	protected RetrievedBlockInfo createElement(ResultSet rs) throws Exception {
		final long blockId = rs.getLong(1);
		final long fileId = rs.getLong(2);
		final long parentUnitId = rs.getLong(3);
		final long startRevId = rs.getLong(4);
		final long endRevId = rs.getLong(5);
		// final long cloneSetId = rs.getLong(3);
		final String blockType = rs.getString(6);
		final int startLine = rs.getInt(7);
		final int endLine = rs.getInt(8);
		final int hash = rs.getInt(9);
		final String crdStr = rs.getString(10);
		final int cm = rs.getInt(11);
		final int isInDeletedFile = rs.getInt(12);
		final int length = rs.getInt(13);
		final int cc = rs.getInt(14);
		final int fo = rs.getInt(15);
		final String discriminator = rs.getString(16);
		final int isAdded = rs.getInt(17);
		final int isDeleted = rs.getInt(18);
		final String rootClassName = rs.getString(19);
		final String rootMethodName = rs.getString(20);
		final String rootMethodParams = rs.getString(21);
		final String crdAfterRootMethod = rs.getString(22);

		toRetrieveFileIds.add(fileId);

		// strict
		return new RetrievedBlockInfo(blockId, fileId, parentUnitId,
				startRevId, endRevId, startLine, endLine, hash, crdStr, cm,
				(isInDeletedFile == 1 && endRevId == revisionId), length, cc,
				fo, discriminator, (isAdded == 1 && startRevId == revisionId),
				(isDeleted == 1) && endRevId == revisionId, rootClassName,
				rootMethodName, rootMethodParams, crdAfterRootMethod, blockType);

		// normal
		// return new RetrievedBlockInfo(blockId, fileId, parentUnitId,
		// startRevId, endRevId, startLine, endLine, hash, crdStr, cm,
		// (isInDeletedFile == 1 && endRevId == revisionId), length, cc,
		// fo, blockType, (isAdded == 1 && startRevId == revisionId),
		// (isDeleted == 1) && endRevId == revisionId);
	}

	public SortedSet<Long> getToRetrieveFileIds() {
		return Collections.unmodifiableSortedSet(toRetrieveFileIds);
	}

}
