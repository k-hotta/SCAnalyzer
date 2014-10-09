package jp.ac.osaka_u.ist.sdl.c20r.genealogy;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.RetrievedBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;

public class GenealogyBlockInfoRetriever {

	private final DBConnection connection;

	public GenealogyBlockInfoRetriever() {
		this.connection = DBConnection.getInstance();
	}

	public SortedSet<RetrievedBlockInfo> retrieveBlocksInSpecifiedClone(
			final long cloneId, final long revisionId) {
		final SortedSet<RetrievedBlockInfo> result = new TreeSet<RetrievedBlockInfo>();

		try {
			final List<Long> blockIds = detectToRetrievedBlockIds(cloneId);

			final Statement stmt = connection.createStatement();
			final ResultSet rs = stmt
					.executeQuery("select * from BLOCK where BLOCK_ID "
							+ createConditionalString(blockIds));

			while (rs.next()) {
				result.add(createElement(rs, revisionId));
			}

			rs.close();
			stmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return Collections.unmodifiableSortedSet(result);
	}

	protected RetrievedBlockInfo createElement(ResultSet rs, long revisionId)
			throws Exception {
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

	private List<Long> detectToRetrievedBlockIds(final long cloneId)
			throws Exception {
		final Statement stmt = connection.createStatement();
		final ResultSet rs = stmt
				.executeQuery("select * from CLONESET where CLONESET_ID = "
						+ cloneId);

		rs.next();
		final String elementsStr = rs.getString(5);

		rs.close();
		stmt.close();

		return detectBlockIds(elementsStr);
	}

	private List<Long> detectBlockIds(final String elementsStr) {
		final List<Long> result = new ArrayList<Long>();
		final String[] splited = elementsStr.split(",");

		for (String splitedElement : splited) {
			result.add(Long.parseLong(splitedElement));
		}

		return result;
	}

	private String createConditionalString(final List<Long> ids) {
		final StringBuilder builder = new StringBuilder();

		builder.append("in(");

		for (final long id : ids) {
			builder.append(id + ",");
		}

		builder.deleteCharAt(builder.length() - 1);

		builder.append(")");

		return builder.toString();
	}

}
