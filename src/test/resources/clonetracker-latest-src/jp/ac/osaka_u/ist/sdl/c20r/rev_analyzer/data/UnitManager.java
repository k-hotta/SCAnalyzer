package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block.UnitInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.manager.AbstractDBElementManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.manager.DBUnitManager;

/**
 * �e��Unit����ێ�����}�l�[�W���[
 * 
 * @author k-hotta
 * 
 */
public class UnitManager extends AbstractElementManager<UnitInfo> {

	/**
	 * �V���O���g���I�u�W�F�N�g
	 */
	private static UnitManager SINGLETON = null;

	private UnitManager() {
		super();
	}

	/**
	 * �C���X�^���X���擾
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
