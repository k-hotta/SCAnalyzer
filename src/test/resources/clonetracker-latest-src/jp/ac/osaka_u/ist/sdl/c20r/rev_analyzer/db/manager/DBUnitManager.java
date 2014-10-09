package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.manager;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block.UnitInfo;

public class DBUnitManager extends AbstractDBElementManager<UnitInfo> {

	private static DBUnitManager SINGLETON = null;
	
	private DBUnitManager() {
		super();
	}
	
	public static DBUnitManager getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new DBUnitManager();
		}
		
		return SINGLETON;
	}

	@Override
	public String getMaxIdQuery() {
		return "select MAX(BLOCK_ID) from BLOCK";
	}
	
}
