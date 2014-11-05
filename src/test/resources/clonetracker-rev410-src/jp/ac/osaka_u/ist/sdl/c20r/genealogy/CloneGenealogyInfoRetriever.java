package jp.ac.osaka_u.ist.sdl.c20r.genealogy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;

public class CloneGenealogyInfoRetriever {

	private final DBConnection connection;

	public CloneGenealogyInfoRetriever() {
		this.connection = DBConnection.getInstance();
	}

	public SortedSet<RetrievedCloneGenealogyInfo> retrieveAll() {
		final SortedSet<RetrievedCloneGenealogyInfo> result = new TreeSet<RetrievedCloneGenealogyInfo>();

		try {

			final Statement stmt = connection.createStatement();
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

	private String getSelectStatement() {
		return "select * from CLONEGENEALOGY";
	}

	private RetrievedCloneGenealogyInfo createElement(ResultSet rs)
			throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final long startRev = rs.getLong(++column);
		final long endRev = rs.getLong(++column);
		final String pairsStr = rs.getString(++column);
		final int hashChanged = rs.getInt(++column);
		final int addedElements = rs.getInt(++column);
		final int deletedElements = rs.getInt(++column);
		final int deletedElementsInFileDel = rs.getInt(++column);
		final int addedRevs = rs.getInt(++column);
		final int deletedRevs = rs.getInt(++column);
		final int deletedFileDelRevs = rs.getInt(++column);

		return new RetrievedCloneGenealogyInfo(id, startRev, endRev, pairsStr,
				hashChanged, addedElements, deletedElements,
				deletedElementsInFileDel, addedRevs, deletedRevs,
				deletedFileDelRevs);
	}

}
