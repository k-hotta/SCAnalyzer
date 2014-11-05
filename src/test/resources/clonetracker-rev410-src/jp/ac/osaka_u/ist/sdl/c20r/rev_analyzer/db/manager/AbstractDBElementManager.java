package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.manager;

import java.sql.ResultSet;
import java.sql.Statement;

import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.AbstractElementInfo;

public abstract class AbstractDBElementManager<T extends AbstractElementInfo> {

	protected final DBConnection connection;

	protected AbstractDBElementManager() {
		this.connection = DBConnection.getInstance();
	}

	/**
	 * ����DB�ɓo�^����Ă���v�f�̒��� ID ���ő�̂��̂���肵�C����ID���擾����
	 * 
	 * @return
	 */
	public long getMaxId() {
		try {
			connection.setAutoCommit(true);
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(getMaxIdQuery());
			connection.setAutoCommit(false);
			
			long result = 0;
			while (rs.next()) {
				result = rs.getLong(1);
				break;
			}
			
			stmt.close();
			rs.close();
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public abstract String getMaxIdQuery();

}
