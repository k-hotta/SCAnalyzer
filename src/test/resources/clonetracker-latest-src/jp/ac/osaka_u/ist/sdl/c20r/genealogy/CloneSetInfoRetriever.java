package jp.ac.osaka_u.ist.sdl.c20r.genealogy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.RetrievedBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone.RetrievedCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;

public class CloneSetInfoRetriever {

	private final DBConnection connection;

	private final long revisionId;

	private final Map<Long, RetrievedBlockInfo> blocks;

	public CloneSetInfoRetriever(final long revisionId,
			final Collection<RetrievedBlockInfo> blocks) {
		this.connection = DBConnection.getInstance();
		this.revisionId = revisionId;
		this.blocks = new TreeMap<Long, RetrievedBlockInfo>();
		for (final RetrievedBlockInfo block : blocks) {
			this.blocks.put(block.getId(), block);
		}
	}

	public SortedSet<RetrievedCloneSetInfo> retrieveAll() {
		final SortedSet<RetrievedCloneSetInfo> result = new TreeSet<RetrievedCloneSetInfo>();

		try {

			final Statement stmt = connection.createStatement();
			final ResultSet rs = stmt
					.executeQuery(getSelectStatement(revisionId));

			while (rs.next()) {
				result.add(createElement(rs));
			}

			rs.close();
			stmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return Collections.unmodifiableSortedSet(result);
	}

	private String getSelectStatement(final long revisionId) {
		return "select * from CLONESET where CLONESET_REV_ID = "
				+ ((Long) revisionId).toString();
	}

	private RetrievedCloneSetInfo createElement(final ResultSet rs)
			throws SQLException {
		final long id = rs.getLong(1);
		final int hash = rs.getInt(4);
		final String elementsStr = rs.getString(5);

		final RetrievedCloneSetInfo result = new RetrievedCloneSetInfo(id, hash);

		for (final long blockId : detectBlockIds(elementsStr)) {
			final RetrievedBlockInfo block = blocks.get(blockId);
			block.setCloneSetId(id);
			result.addElement(block);
		}

		return result;
	}

	private List<Long> detectBlockIds(final String elementsStr) {
		final List<Long> result = new ArrayList<Long>();
		final String[] splited = elementsStr.split(",");

		for (String splitedElement : splited) {
			result.add(Long.parseLong(splitedElement));
		}

		return result;
	}

}
