package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data;

import java.util.LinkedList;
import java.util.List;

/**
 * 各種マネージャーを管理するためのマネージャー
 * 
 * @author k-hotta
 * 
 */
public class DataManagerManager {

	/**
	 * シングルトンオブジェクト
	 */
	private static DataManagerManager SINGLETON = null;

	/**
	 * リビジョンマネージャー
	 */
	private final RevisionManager revisionManager = RevisionManager
			.getInstance();

	/**
	 * ファイルマネージャー
	 */
	private final FileManager fileManager = FileManager.getInstance();

	/**
	 * ユニットマネージャー
	 */
	private final UnitManager unitManager = UnitManager.getInstance();

	/**
	 * クローンセットマネージャー
	 */
	//private final CloneSetManager cloneManager = CloneSetManager.getInstance();
	private final CloneSetManager cloneManager = null;

	/**
	 * 全マネージャーを連結したリスト
	 */
	private final List<AbstractElementManager<?>> allManagers;

	private DataManagerManager() {
		this.allManagers = new LinkedList<AbstractElementManager<?>>();
		this.allManagers.add(revisionManager);
		this.allManagers.add(fileManager);
		this.allManagers.add(unitManager);
		//this.allManagers.add(cloneManager);
	}

	/**
	 * インスタンスを取得
	 * 
	 * @return
	 */
	public static DataManagerManager getInstance() {
		synchronized (DataManagerManager.class) {
			if (SINGLETON == null) {
				SINGLETON = new DataManagerManager();
			}
		}

		return SINGLETON;
	}

	/**
	 * リビジョンマネージャーを取得
	 * 
	 * @return
	 */
	public RevisionManager getRevisionManager() {
		return revisionManager;
	}

	/**
	 * ファイルマネージャーを取得
	 * 
	 * @return
	 */
	public FileManager getFileManager() {
		return fileManager;
	}

	/**
	 * ユニットマネージャーを取得
	 * 
	 * @return
	 */
	public UnitManager getUnitManager() {
		return unitManager;
	}

	/**
	 * クローンセットマネージャーを取得
	 * 
	 * @return
	 */
	public CloneSetManager getCloneSetManager() {
		return cloneManager;
	}

	/**
	 * 全マネージャーを初期化
	 */
	public void clearAll() {
		for (AbstractElementManager<?> manager : allManagers) {
			manager.clear();
		}
		SINGLETON = null;
	}

}
