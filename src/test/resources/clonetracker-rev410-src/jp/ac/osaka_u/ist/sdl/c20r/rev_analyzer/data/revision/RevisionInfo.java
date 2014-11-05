package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.revision;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.AbstractElementInfo;

/**
 * リビジョン情報を表すクラス
 * 
 * @author k-hotta
 * 
 */
public class RevisionInfo extends AbstractElementInfo {
	
	private final int revisionNum;

	private final int filesCount;

	private final int blocksCount;

	private int cloneSetsCount;

	public RevisionInfo(long id, int revisionNum, int filesCount, int blocksCount) {
		super(id);
		this.revisionNum = revisionNum;
		this.filesCount = filesCount;
		this.blocksCount = blocksCount;
		this.cloneSetsCount = 0;
	}

	public void setCloneSetsCount(int cloneSetsCount) {
		this.cloneSetsCount = cloneSetsCount;
	}

	public int getRevisionNum() {
		return revisionNum;
	}

	public int getFilesCount() {
		return filesCount;
	}

	public int getBlocksCount() {
		return blocksCount;
	}

	public int getCloneSetsCount() {
		return cloneSetsCount;
	}

}
