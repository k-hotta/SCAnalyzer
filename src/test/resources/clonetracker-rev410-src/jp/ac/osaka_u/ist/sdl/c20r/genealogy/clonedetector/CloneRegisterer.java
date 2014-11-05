package jp.ac.osaka_u.ist.sdl.c20r.genealogy.clonedetector;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone.RetrievedCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.settings.Settings;

public class CloneRegisterer {

	private final DBConnection dbConnection;

	public static CloneRegisterer SINGLETON = null;

	private CloneRegisterer() {
		this.dbConnection = DBConnection.getInstance();
	}

	public static CloneRegisterer getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new CloneRegisterer();
		}
		return SINGLETON;
	}

	public synchronized void registerAll(
			final Collection<RetrievedCloneSetInfo> clones,
			final long revisionId) {
		try {
			final PreparedStatement pstmt = dbConnection
					.createPreparedStatement(createPreparedStatementQueue());

			int count = 0;
			final int maxBatchCount = Settings.getIntsance().getMaxBatchCount();

			for (RetrievedCloneSetInfo clone : clones) {
				setAttributes(pstmt, clone, revisionId);
				pstmt.addBatch();
				if ((++count % maxBatchCount) == 0) {
					pstmt.executeBatch();
					pstmt.clearBatch();
				}
			}

			pstmt.executeBatch();
			dbConnection.commit();

			pstmt.close();
			System.out.println("\t\t" + count + " elements are registered");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String createPreparedStatementQueue() {
		return "insert into CLONESET values (?,?,?,?,?)";
	}

	private void setAttributes(PreparedStatement pstmt,
			RetrievedCloneSetInfo clone, long revisionId) throws SQLException {
		pstmt.setLong(1, clone.getId());
		pstmt.setInt(2, clone.getCount());
		pstmt.setLong(3, revisionId);
		pstmt.setInt(4, clone.getHash());
		pstmt.setString(5, clone.convertElementsIntoString());
	}
}
