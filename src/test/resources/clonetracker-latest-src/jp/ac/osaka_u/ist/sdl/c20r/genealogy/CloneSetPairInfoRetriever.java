package jp.ac.osaka_u.ist.sdl.c20r.genealogy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;

public class CloneSetPairInfoRetriever {

	private final DBConnection dbConnection;

	private final long beforeRevId;

	public CloneSetPairInfoRetriever(final long beforeRevId) {
		this.dbConnection = DBConnection.getInstance();
		this.beforeRevId = beforeRevId;
	}

	public SortedSet<RetrievedCloneSetPairInfo> retrieveAll() {
		final SortedSet<RetrievedCloneSetPairInfo> result = new TreeSet<RetrievedCloneSetPairInfo>();

		try {

			final Statement stmt = dbConnection.createStatement();
			final ResultSet rs = stmt.executeQuery(getSelectStatement());

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

	public SortedSet<RetrievedCloneSetPairInfo> retrieve(
			final Collection<Long> ids) {
		final SortedSet<RetrievedCloneSetPairInfo> result = new TreeSet<RetrievedCloneSetPairInfo>();

		try {

			final Statement stmt = dbConnection.createStatement();

			for (final long id : ids) {
				ResultSet rs = stmt
						.executeQuery("select * from CLONESETPAIR where CLONESETPAIR_ID = "
								+ ((Long) id).toString());
				while (rs.next()) {
					result.add(createElement(rs));
				}
				rs.close();
			}

			stmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return Collections.unmodifiableSortedSet(result);
	}

	private String getSelectStatement() {
		return "select * from CLONESETPAIR where CLONESETPAIR_BEFORE_REV_ID = "
				+ ((Long) beforeRevId).toString();
	}

	private RetrievedCloneSetPairInfo createElement(final ResultSet rs)
			throws SQLException {
		final long id = rs.getLong(1);
		final long beforeRevId = rs.getLong(2);
		final long afterRevId = rs.getLong(3);
		final long beforeCloneId = rs.getLong(4);
		final long afterCloneId = rs.getLong(5);
		final int hashChanged = rs.getInt(6);
		final int addedElements = rs.getInt(7);
		final int deletedElements = rs.getInt(8);
		final int deletedElementsInDeletedFiles = rs.getInt(9);

		return new RetrievedCloneSetPairInfo(id, beforeRevId, afterRevId,
				beforeCloneId, afterCloneId, (hashChanged == 1), addedElements,
				deletedElements, deletedElementsInDeletedFiles);
	}

}
