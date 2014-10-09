package jp.ac.osaka_u.ist.sdl.c20r.genealogy.clonepairdetector;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone.AbstractCloneSetPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.settings.Settings;

public class ClonePairRegisterer {

	private final DBConnection dbConnection;

	public static ClonePairRegisterer SINGLETON = null;

	private ClonePairRegisterer() {
		this.dbConnection = DBConnection.getInstance();
	}

	public static ClonePairRegisterer getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new ClonePairRegisterer();
		}
		return SINGLETON;
	}

	public synchronized void registerAll(
			final Collection<AbstractCloneSetPairInfo> pairs,
			final long beforeRevId, final long afterRevId) {
		try {
			final PreparedStatement pstmt = dbConnection
					.createPreparedStatement(createPreparedStatementQueue());

			int count = 0;
			final int maxBatchCount = Settings.getIntsance().getMaxBatchCount();

			for (final AbstractCloneSetPairInfo pair : pairs) {
				setAttributes(pstmt, pair, beforeRevId, afterRevId);
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
		return "insert into CLONESETPAIR values (?,?,?,?,?,?,?,?,?)";
	}

	private void setAttributes(PreparedStatement pstmt,
			AbstractCloneSetPairInfo pair, long beforeRevId, long afterRevId)
			throws SQLException {
		pstmt.setLong(1, pair.getId());
		pstmt.setLong(2, beforeRevId);
		pstmt.setLong(3, afterRevId);
		pstmt.setLong(4, pair.getBeforeCloneSetId());
		pstmt.setLong(5, pair.getAfterCloneSetId());
		pstmt.setInt(6, (pair.containsHashChange()) ? 1 : 0);
		pstmt.setInt(7, pair.getAddedBlocks().size());
		pstmt.setInt(8, pair.getDeletedBlocks().size());
		pstmt.setInt(9, pair.getDeletedBlocksInDeletedFiles().size());
	}

}
