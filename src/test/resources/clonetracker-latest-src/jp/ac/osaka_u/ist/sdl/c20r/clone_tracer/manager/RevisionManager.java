package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.manager;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.RetrievedRevisionInfo;

/**
 * 前後のリビジョンとそれらのデータを管理するためのクラス
 * 
 * @author k-hotta
 * 
 */
public class RevisionManager {

	/**
	 * 前リビジョン
	 */
	private final RetrievedRevisionInfo beforeRevision;

	/**
	 * 前リビジョンのデータ
	 */
	private final RetrievedDataManager beforeDataManager;

	/**
	 * 後リビジョン
	 */
	private final RetrievedRevisionInfo afterRevision;

	/**
	 * 後リビジョンのデータ
	 */
	private final RetrievedDataManager afterDataManager;

	/**
	 * ブロックペアのマネージャー
	 */
	private final BlockPairManager blockPairManager;

	/**
	 * クローンセット対のマネージャー
	 */
	private final CloneSetPairManager cloneManager;

	/**
	 * コンストラクタ
	 * 
	 * @param beforeRevision
	 * @param afterRevision
	 */
	public RevisionManager(final RetrievedRevisionInfo beforeRevision,
			final RetrievedRevisionInfo afterRevision) {
		this.beforeRevision = beforeRevision;
		this.afterRevision = afterRevision;
		this.beforeDataManager = new RetrievedDataManager();
		this.afterDataManager = new RetrievedDataManager();
		this.blockPairManager = new BlockPairManager();
		this.cloneManager = new CloneSetPairManager();
	}

	/**
	 * 前リビジョンを取得
	 * 
	 * @return
	 */
	public RetrievedRevisionInfo getBeforeRevision() {
		return beforeRevision;
	}

	/**
	 * 前リビジョンのデータマネージャーを取得
	 * 
	 * @return
	 */
	public RetrievedDataManager getBeforeDataManager() {
		return beforeDataManager;
	}

	/**
	 * 後リビジョンを取得
	 * 
	 * @return
	 */
	public RetrievedRevisionInfo getAfterRevision() {
		return afterRevision;
	}

	/**
	 * 後リビジョンのデータマネージャーを取得
	 * 
	 * @return
	 */
	public RetrievedDataManager getAfterDataManager() {
		return afterDataManager;
	}

	/**
	 * ブロックペアのマネージャーを取得
	 * 
	 * @return
	 */
	public BlockPairManager getBlockPairManager() {
		return blockPairManager;
	}

	/**
	 * クローンセットペアのマネージャーを取得
	 * 
	 * @return
	 */
	public CloneSetPairManager getCloneManager() {
		return cloneManager;
	}

}
