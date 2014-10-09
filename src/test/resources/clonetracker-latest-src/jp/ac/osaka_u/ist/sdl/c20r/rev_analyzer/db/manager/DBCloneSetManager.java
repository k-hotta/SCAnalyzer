package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.manager;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.clone.CloneSetInfo;

public class DBCloneSetManager extends AbstractDBElementManager<CloneSetInfo> {

	private static DBCloneSetManager SINGLETON = null;
	
	private DBCloneSetManager() {
		super();
	}
	
	public static DBCloneSetManager getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new DBCloneSetManager();
		}
		
		return SINGLETON;
	}
	
	@Override
	public String getMaxIdQuery() {
		return "select MAX(CLONESET_ID) from CLONESET";
	}
	
	public Set<CloneSetInfo> getRegisteredElements() {
		Set<CloneSetInfo> result = new TreeSet<CloneSetInfo>();
		
		try {
			connection.setAutoCommit(true);
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("select * from CLONESET");
			connection.setAutoCommit(false);
			
			while (rs.next()) {
				final int hash = rs.getInt(2);
				final long id = rs.getLong(1);
				result.add(new CloneSetInfo(id, hash));
			}
			
			stmt.close();
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Collections.unmodifiableSet(result);
	}

}
