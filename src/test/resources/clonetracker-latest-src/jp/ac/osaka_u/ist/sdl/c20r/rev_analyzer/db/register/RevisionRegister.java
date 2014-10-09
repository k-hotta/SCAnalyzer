package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.register;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.revision.RevisionInfo;

public class RevisionRegister extends AbstractElementRegister<RevisionInfo> {

	@Override
	protected String createPreparedStatementQueue() {
		return "insert into REVISION values (?,?,?,?,?)";
	}

	@Override
	protected void setAttributesIntoPreparedStatement(PreparedStatement pstmt,
			RevisionInfo element) throws SQLException {
		pstmt.setLong(1, element.getId());
		pstmt.setInt(2, element.getRevisionNum());
		pstmt.setInt(3, element.getFilesCount());
		pstmt.setInt(4, element.getBlocksCount());
		pstmt.setInt(5, element.getCloneSetsCount());
	}

	public void regist(RevisionInfo element) {
		try {
			Statement stmt = dbConnection.createStatement();
			StringBuilder queryBuilder = new StringBuilder();
			queryBuilder
					.append("insert into REVISION values (");
			queryBuilder.append(element.getId() + ",");
			queryBuilder.append(element.getRevisionNum() + ",");
			queryBuilder.append(element.getFilesCount() + ",");
			queryBuilder.append(element.getBlocksCount() + ",");
			queryBuilder.append(element.getCloneSetsCount() + ")");
			stmt.executeUpdate(queryBuilder.toString());
			dbConnection.commit();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
