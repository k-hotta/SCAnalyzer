package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block.UnitInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.manager.AbstractDBElementManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.manager.DBUnitManager;

/**
 * 各種Unit情報を保持するマネージャー
 * 
 * @author k-hotta
 * 
 */
public class UnitManager extends AbstractElementManager<UnitInfo> {

	/**
	 * シングルトンオブジェクト
	 */
	private static UnitManager SINGLETON = null;

	private UnitManager() {
		super();
	}

	/**
	 * インスタンスを取得
	 * 
	 * @return
	 */
	static UnitManager getInstance() {
		synchronized (UnitManager.class) {
			if (SINGLETON == null) {
				SINGLETON = new UnitManager();
			}
		}

		return SINGLETON;
	}
	
	void clear() {
		SINGLETON = null;
	}

	@Override
	protected AbstractDBElementManager<UnitInfo> getDbManager() {
		return DBUnitManager.getInstance();
	}

}
