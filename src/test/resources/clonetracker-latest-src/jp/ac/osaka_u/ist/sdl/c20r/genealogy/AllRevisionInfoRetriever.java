package jp.ac.osaka_u.ist.sdl.c20r.genealogy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.RetrievedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;

public class AllRevisionInfoRetriever {

	private final DBConnection connection;
	
	public AllRevisionInfoRetriever() {
		this.connection = DBConnection.getInstance();
	}
	
	public SortedSet<RetrievedRevisionInfo> retrieveAll() {
		final SortedSet<RetrievedRevisionInfo> result = new TreeSet<RetrievedRevisionInfo>();

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
		return "select * from REVISION";
	}

	private RetrievedRevisionInfo createElement(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		final long revisionId = rs.getLong(1);
		final int revisionNum = rs.getInt(2);
		final int files = rs.getInt(3);
		final int blocks = rs.getInt(4);
		final int cloneSets = rs.getInt(5);

		return new RetrievedRevisionInfo(revisionId, revisionNum, files,
				blocks, cloneSets);
	}

	
}
