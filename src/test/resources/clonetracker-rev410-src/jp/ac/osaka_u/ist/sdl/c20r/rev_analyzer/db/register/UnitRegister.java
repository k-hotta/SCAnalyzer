package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.register;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import jp.ac.osaka_u.ist.sdl.c20r.diff.DifferenceManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.DataManagerManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.FileManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block.UnitInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.settings.Settings;

public class UnitRegister extends AbstractElementRegister<UnitInfo> {

	private final long maxRevisionId;

	public UnitRegister(final long maxRevisionId) {
		this.maxRevisionId = maxRevisionId;
	}

	@Override
	protected String createPreparedStatementQueue() {
		return "insert into BLOCK values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	}

	@Override
	protected void setAttributesIntoPreparedStatement(PreparedStatement pstmt,
			UnitInfo element) throws SQLException {
		int column = 0;
		pstmt.setLong(++column, element.getId());
		// pstmt.setLong(2, element.getOwnerRevisionId());
		pstmt.setLong(++column, element.getOwnerFileId());
		pstmt.setLong(++column, element.getParentUnitId());
		// pstmt.setInt(3, -1); // CLONESET_ID ÇÕèâä˙ìoò^éûÇ…ÇÕÇÌÇ©ÇÁÇ»Ç¢ÇÃÇ≈ -1
		pstmt.setLong(++column, element.getOwnerRevisionId());
		pstmt.setLong(++column, maxRevisionId);
		pstmt.setString(++column, element.getUnitTypeString());
		pstmt.setInt(++column, element.getStartLine());
		pstmt.setInt(++column, element.getEndLine());
		pstmt.setInt(++column, element.getHash());
		pstmt.setString(++column, element.getCRD().toString());
		pstmt.setInt(++column, element.getCM().getCC()
				+ element.getCM().getFO());
		pstmt.setInt(++column, 0);
		pstmt.setInt(++column, element.getLength());
		pstmt.setInt(++column, element.getCM().getCC());
		pstmt.setInt(++column, element.getCM().getFO());
		// pstmt.setString(++column, element.getCRDElement().toString());
		pstmt.setString(++column, element.getDiscriminator());
		if (element.isWhollyAdded()) {
			pstmt.setInt(++column, 1);
		} else {
			pstmt.setInt(++column, 0);
		}
		pstmt.setInt(++column, 0);
		pstmt.setString(++column, element.getRootClassName());
		pstmt.setString(++column, element.getRootMethodName());
		pstmt.setString(++column, element.getRootMethodParametersAsString());
		pstmt.setString(++column, element.getCRDAfterRootMethod().toString());
	}

	public void updatePreviousRevisionBlocks(final long currentRevisionId) {
		try {
			final long start = System.currentTimeMillis();
			long lap = System.currentTimeMillis();

			dbConnection.setAutoCommit(true);
			final Statement stmt = dbConnection.createStatement();
			ResultSet rs = stmt
					.executeQuery("select * from BLOCK where START_REV_ID < "
							+ ((Long) currentRevisionId).toString()
							+ " AND END_REV_ID = "
							+ ((Long) maxRevisionId).toString());
			dbConnection.setAutoCommit(false);
			final long previousRevisionId = currentRevisionId - 1;

			PreparedStatement pstmt = dbConnection
					.createPreparedStatement("update BLOCK set END_REV_ID = "
							+ ((Long) previousRevisionId).toString()
							+ ", FILE_DELETE = ?, BLOCK_DELETED = ? where BLOCK_ID = ?");

			final FileManager fileManager = DataManagerManager.getInstance()
					.getFileManager();
			final DifferenceManager diffManager = DifferenceManager
					.getInstance();
			final Set<Long> deletedFileIds = fileManager.getDeletedFileIds();
			int count = 0;
			final int maxBatchCount = Settings.getIntsance().getMaxBatchCount();

			while (rs.next()) {
				final long fileId = rs.getLong(2);
				if (fileManager.contains(fileId)) {
					final String path = fileManager.getElement(fileId)
							.getPath();
					final long blockId = rs.getLong(1);
					pstmt.setInt(1, 0);

					final int startLine = rs.getInt(7);
					final int endLine = rs.getInt(8);
					if (diffManager.isContainedInBeforeDiff(path, startLine,
							endLine)) {
						pstmt.setInt(2, 1);
					} else {
						pstmt.setInt(2, 0);
					}

					pstmt.setLong(3, blockId);
					pstmt.addBatch();

					if ((++count % maxBatchCount) == 0) {
						pstmt.executeBatch();
						pstmt.clearBatch();
						// dbConnection.commit();
						final long now = System.currentTimeMillis();
						System.out.println("\t\t\t" + (count)
								+ " elements are updated (elapsed time "
								+ (now - lap) + "ms)");
						lap = now;
					}
				} else if (deletedFileIds.contains(fileId)) {
					final long blockId = rs.getLong(1);
					if (fileManager.isRenamed(fileId)) {
						pstmt.setInt(1, 0);
					} else {
						pstmt.setInt(1, 1);
					}
					pstmt.setInt(2, 1);
					pstmt.setLong(3, blockId);
					pstmt.addBatch();

					if ((++count % maxBatchCount) == 0) {
						pstmt.executeBatch();
						pstmt.clearBatch();
						// dbConnection.commit();
						final long now = System.currentTimeMillis();
						System.out.println("\t\t\t" + (count)
								+ " elements are updated (elapsed time "
								+ (now - lap) + "ms)");
						lap = now;

					}
				}
			}

			pstmt.executeBatch();
			dbConnection.commit();
			final long end = System.currentTimeMillis();
			System.out.println("\t\t\t" + (count)
					+ " elements are updated (elapsed time " + (end - lap)
					+ "ms)");

			System.out.println("\t\ttotal elapsed time is " + (end - start)
					+ "ms");

			rs.close();
			stmt.close();
			pstmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
