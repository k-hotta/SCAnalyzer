package jp.ac.osaka_u.ist.sdl.c20r.genealogy.genealogydetector;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.CloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.settings.Settings;

public class CloneGenealogyRegisterer {

	private final DBConnection dbConnection;

	public static CloneGenealogyRegisterer SINGLETON = null;

	private CloneGenealogyRegisterer() {
		this.dbConnection = DBConnection.getInstance();
	}

	public static CloneGenealogyRegisterer getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new CloneGenealogyRegisterer();
		}
		return SINGLETON;
	}

	public void registerAll(final Collection<CloneGenealogyInfo> genealogies) {
		try {
			final PreparedStatement pstmt = dbConnection
					.createPreparedStatement(createPreparedStatementQueue());

			int count = 0;
			final int maxBatchCount = Settings.getIntsance().getMaxBatchCount();

			for (final CloneGenealogyInfo genealogy : genealogies) {
				setAttributes(pstmt, genealogy);
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
		return "insert into CLONEGENEALOGY values (?,?,?,?,?,?,?,?,?,?,?)";
	}

	private void setAttributes(PreparedStatement pstmt,
			CloneGenealogyInfo genealogy) throws SQLException {
		int column = 0;
		pstmt.setLong(++column, genealogy.getId());
		pstmt.setLong(++column, genealogy.getStartRev());
		pstmt.setLong(++column, genealogy.getEndRev());
		pstmt.setString(++column, genealogy.getPairsAsString());
		pstmt.setInt(++column, genealogy.getHashChanged());
		pstmt.setInt(++column, genealogy.getAddedTotal());
		pstmt.setInt(++column, genealogy.getDeletedTotal());
		pstmt.setInt(++column, genealogy.getDeletedInFileDelTotal());
		pstmt.setInt(++column, genealogy.getAddedRevCount());
		pstmt.setInt(++column, genealogy.getDeletedRevCount());
		pstmt.setInt(++column, genealogy.getDeletedFileDelCount());
	}

}
