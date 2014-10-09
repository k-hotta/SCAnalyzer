package jp.ac.osaka_u.ist.sdl.c20r.genealogy;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.RetrievedFileInfo;
import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;

public class GenealogyFileInfoRetriever {

	private final DBConnection connection;

	public GenealogyFileInfoRetriever() {
		this.connection = DBConnection.getInstance();
	}

	public SortedSet<RetrievedFileInfo> retrieveAll() {
		SortedSet<RetrievedFileInfo> result = new TreeSet<RetrievedFileInfo>();

		try {
			final Statement stmt = connection.createStatement();
			final ResultSet rs = stmt.executeQuery("select * from FILE");

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

	private final RetrievedFileInfo createElement(ResultSet rs)
			throws Exception {
		final long fileId = rs.getLong(1);
		final String fileName = rs.getString(2);
		final String filePath = rs.getString(3);

		return new RetrievedFileInfo(fileId, fileName, filePath);
	}

}
