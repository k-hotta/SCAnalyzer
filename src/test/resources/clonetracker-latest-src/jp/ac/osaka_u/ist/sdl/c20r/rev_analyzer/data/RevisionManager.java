package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.revision.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.manager.AbstractDBElementManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.manager.DBRevisionManager;

/**
 * リビジョン情報を管理するクラス <br>
 * 単一リビジョンの解析時は，1つのリビジョン情報のみを管理し，複数のリビジョン情報の登録は許可しない
 * 
 * @author k-hotta
 * 
 */
public class RevisionManager extends AbstractElementManager<RevisionInfo> {

	/**
	 * シングルトンオブジェクト
	 */
	private static RevisionManager SINGLETON = null;

	private RevisionManager() {
		super();
	}

	/**
	 * インスタンスを取得する
	 * 
	 * @return
	 */
	public static RevisionManager getInstance() {
		synchronized (RevisionManager.class) {
			if (SINGLETON == null) {
				SINGLETON = new RevisionManager();
			}
		}

		return SINGLETON;
	}

	/**
	 * 複数の要素の追加を防ぐために親クラスのメソッドをオーバーライド
	 */
	@Override
	public void add(RevisionInfo element) {
		synchronized (RevisionManager.class) {
			if (count.get() == 0) {
				final long key = element.getId();
				elements.put(key, element);
				count.getAndIncrement(); // インクリメントしたいだけなのでgetした値は使わない
			}
		}
	}

	/**
	 * 現在のリビジョン情報を取得する <br>
	 * 未登録であれば null
	 * 
	 * @return
	 */
	public RevisionInfo getCurrentRevision() {
		for (RevisionInfo revision : elements.values()) {
			return revision;
		}
		return null;
	}

	@Override
	void clear() {
		SINGLETON = null;
	}

	@Override
	protected AbstractDBElementManager<RevisionInfo> getDbManager() {
		return DBRevisionManager.getInstance();
	}

}
