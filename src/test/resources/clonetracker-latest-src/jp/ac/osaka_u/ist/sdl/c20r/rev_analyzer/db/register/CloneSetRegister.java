package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.register;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.CloneSetManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.DataManagerManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.clone.CloneSetInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.settings.Settings;

/**
 * クローンセット情報を特定してDBに登録するクラス <br>
 * UnitInfo の情報がDBに登録されている前提で動作する
 * 
 * @author k-hotta
 * 
 */
public class CloneSetRegister extends AbstractElementRegister<CloneSetInfo> {

	private final long currentRevisionId;

	private final CloneSetManager cloneManager;

	public CloneSetRegister(long currentRevisionId) {
		this.currentRevisionId = currentRevisionId;
		this.cloneManager = DataManagerManager.getInstance()
				.getCloneSetManager();
	}

	public void detectCloneSets() {
		try {
			final long start = System.currentTimeMillis();
			long lap = start;

			dbConnection.setAutoCommit(true);
			final Statement stmt = dbConnection.createStatement();
			// final ResultSet rs = stmt
			// .executeQuery("select BLOCK_HASH from BLOCK where REVISION_ID = "
			// + currentRevisionId
			// + " group by BLOCK_HASH having count(BLOCK_ID) > 1");
			final ResultSet rs = stmt
					.executeQuery("select BLOCK_HASH from BLOCK where START_REV_ID = "
							+ currentRevisionId
							+ " group by BLOCK_HASH having count(BLOCK_ID) > 1");
			dbConnection.setAutoCommit(false);

			int count = 0;
			final int maxBatchCount = Settings.getIntsance().getMaxBatchCount();

			PreparedStatement pstmt = dbConnection
					.createPreparedStatement("update BLOCK set CLONESET_ID = ? where START_REV_ID = "
							+ currentRevisionId + " AND BLOCK_HASH = ?");

			while (rs.next()) {
				final int hashValue = rs.getInt(1);
				pstmt.setLong(1, cloneManager.getElement(hashValue).getId());
				pstmt.setInt(2, hashValue);
				pstmt.addBatch();
				if ((++count % maxBatchCount) == 0) {
					pstmt.executeBatch();
					dbConnection.commit();
					final long now = System.currentTimeMillis();
					System.out.println("\t\t\t" + (count)
							+ " elements are registered (elapsed time "
							+ (now - lap) + "ms)");
					lap = now;
				}
			}

			pstmt.executeBatch();
			dbConnection.commit();
			final long end = System.currentTimeMillis();
			System.out.println("\t\t\t" + (count)
					+ " elements are registered (elapsed time " + (end - lap)
					+ "ms)");

			System.out.println("\t\ttotal elapsed time is " + (end - start)
					+ "ms");

			rs.close();
			stmt.close();
			pstmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertCloneSets() {
		try {
			final long start = System.currentTimeMillis();
			long lap = start;

			PreparedStatement pstmt = dbConnection
					.createPreparedStatement("insert into CLONESET values (?,?)");

			int count = 0;
			final int maxBatchCount = Settings.getIntsance().getMaxBatchCount();

			for (CloneSetInfo element : cloneManager.getNewlyAddedElements()) {
				pstmt.setLong(1, element.getId());
				pstmt.setInt(2, element.getHash());
				pstmt.addBatch();
				if ((++count % maxBatchCount) == 0) {
					pstmt.executeBatch();
					dbConnection.commit();
					final long now = System.currentTimeMillis();
					System.out.println("\t\t\t" + (count)
							+ " elements are registered (elapsed time "
							+ (now - lap) + "ms)");
					lap = now;
				}
			}

			pstmt.executeBatch();
			dbConnection.commit();
			final long end = System.currentTimeMillis();
			System.out.println("\t\t\t" + (count)
					+ " elements are registered (elapsed time " + (end - lap)
					+ "ms)");

			System.out.println("\t\ttotal elapsed time is " + (end - start)
					+ "ms");

			// リビジョンテーブルのリビジョン数情報をアップデート
			Statement stmt = dbConnection.createStatement();
			stmt.executeUpdate("update REVISION set CLONESETS_IN_REVISION = "
					+ count + " where REVISION_ID = " + currentRevisionId);
			dbConnection.commit();

			pstmt.close();
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String createPreparedStatementQueue() {
		return "insert into CLONESET values (?,?)";
	}

	@Override
	protected void setAttributesIntoPreparedStatement(PreparedStatement pstmt,
			CloneSetInfo element) throws SQLException {
		pstmt.setLong(1, element.getId());
		pstmt.setInt(2, element.getHash());
	}

}
