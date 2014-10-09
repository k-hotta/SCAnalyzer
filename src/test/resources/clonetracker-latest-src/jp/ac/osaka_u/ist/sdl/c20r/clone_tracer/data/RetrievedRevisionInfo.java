package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data;

/**
 * DBから取り出したリビジョン情報を表すクラス
 * 
 * @author k-hotta
 * 
 */
public class RetrievedRevisionInfo extends AbstractRetrievedElementInfo {

	/**
	 * リビジョン番号 (リビジョンIDとは別物)
	 */
	private final int revisionNum;

	/**
	 * ファイル数
	 */
	private final int filesCount;

	/**
	 * ブロック数
	 */
	private final int blocksCount;

	/**
	 * クローンセット数
	 */
	private final int cloneSetsCount;

	public RetrievedRevisionInfo(final long id, final int revisionNum,
			final int filesCount, final int blocksCount,
			final int cloneSetsCount) {
		super(id);
		this.revisionNum = revisionNum;
		this.filesCount = filesCount;
		this.blocksCount = blocksCount;
		this.cloneSetsCount = cloneSetsCount;
	}
	
	/**
	 * リビジョン番号を取得する
	 * @return
	 */
	public final int getRevisionNum() {
		return revisionNum;
	}

	/**
	 * ファイル数を取得する
	 * @return
	 */
	public final int getFilesCount() {
		return filesCount;
	}
	
	/**
	 * ブロック数を取得する
	 * @return
	 */
	public final int getBlocksCount() {
		return blocksCount;
	}
	
	/**
	 * クローンセット数を取得する
	 * @return
	 */
	public final int getCloneSetsCount() {
		return cloneSetsCount;
	}
	
}
