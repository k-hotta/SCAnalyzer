package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.manager;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.file.FileInfo;

public class DBFileManager extends AbstractDBElementManager<FileInfo> {

	private static DBFileManager SINGLETON = null;
	
	private DBFileManager() {
		super();
	}
	
	public static DBFileManager getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new DBFileManager();
		}
		
		return SINGLETON;
	}
	
	@Override
	public String getMaxIdQuery() {
		return "select MAX(FILE_ID) from FILE";
	}
	
	public Map<String, Long> getRegisteredElements() {
		Map<String, Long> result = new TreeMap<String, Long>();
		
		try {
			connection.setAutoCommit(true);
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("select * from FILE");
			connection.setAutoCommit(false);
			
			while (rs.next()) {
				final String path = rs.getString(3);
				final long id = rs.getLong(1);
				result.put(path, id);
			}
			
			stmt.close();
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Collections.unmodifiableMap(result);
	}
	
}
