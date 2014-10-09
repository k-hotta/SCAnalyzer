package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.register;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.file.FileInfo;

public class FileRegister extends AbstractElementRegister<FileInfo> {
	
	@Override
	protected String createPreparedStatementQueue() {
		return "insert into FILE values (?,?,?)";
	}

	@Override
	protected void setAttributesIntoPreparedStatement(PreparedStatement pstmt,
			FileInfo element) throws SQLException {
		pstmt.setLong(1, element.getId());
		//pstmt.setLong(2, element.getOwnerRevisionId());
		pstmt.setString(2, element.getName());
		pstmt.setString(3, element.getPath());
		//pstmt.setInt(5, element.getBlocks());
	}

}
