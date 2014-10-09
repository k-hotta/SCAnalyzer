package jp.ac.osaka_u.ist.sdl.c20r.ui.data;

/**
 * CSVファイルから読み取った，各クローンセットを構成する要素
 * 
 * @author k-hotta
 * 
 */
public class CSVCloneElementInfo implements Comparable<CSVCloneElementInfo> {

	private final int revisionNum;

	private final long cloneId;

	private final String path;

	private final int startLine;

	private final int endLine;

	private final boolean isDisappear;

	private final int afterStartLine;

	private final int afterEndLine;

	private final String afterPath;

	private final boolean isInDeletedFile;

	private final int length;

	private final int cc;

	private final int fo;

	private final boolean isMoved;

	private final String beforeCrd;

	private final String afterCrd;

	private final double similarity;

	private final int ld;

	public CSVCloneElementInfo(final int revisionNum, final long cloneId,
			final String path, final int startLine, final int endLine,
			final boolean isDisappear, final int afterStartLine,
			final int afterEndLine, final String afterPath,
			final boolean isInDeletedFile, final int length, final int cc,
			final int fo, final boolean isMoved, final String beforeCrd,
			final String afterCrd, final double similarity, final int ld) {
		this.revisionNum = revisionNum;
		this.cloneId = cloneId;
		this.path = path;
		this.startLine = startLine;
		this.endLine = endLine;
		this.isDisappear = isDisappear;
		this.afterStartLine = afterStartLine;
		this.afterEndLine = afterEndLine;
		this.afterPath = afterPath;
		this.isInDeletedFile = isInDeletedFile;
		this.length = length;
		this.cc = cc;
		this.fo = fo;
		this.isMoved = isMoved;
		this.beforeCrd = beforeCrd;
		this.afterCrd = afterCrd;
		this.similarity = similarity;
		this.ld = ld;
	}

	public int getRevisionNum() {
		return revisionNum;
	}

	public long getCloneId() {
		return cloneId;
	}

	public String getPath() {
		return path;
	}

	public int getStartLine() {
		return startLine;
	}

	public int getEndLine() {
		return endLine;
	}

	public boolean isDisappear() {
		return isDisappear;
	}

	public int getAfterStartLine() {
		return afterStartLine;
	}

	public int getAfterEndLine() {
		return afterEndLine;
	}

	public String getAfterPath() {
		return afterPath;
	}

	public boolean isInDeletedFile() {
		return isInDeletedFile;
	}

	public int getLength() {
		return length;
	}

	public int getCC() {
		return cc;
	}

	public int getFO() {
		return fo;
	}

	public boolean isMoved() {
		return isMoved;
	}

	public String getBeforeCrd() {
		return beforeCrd;
	}

	public String getAfterCrd() {
		return afterCrd;
	}

	public double getSimilarity() {
		return similarity;
	}

	public int getLd() {
		return ld;
	}

	@Override
	public int compareTo(CSVCloneElementInfo anotherElement) {
		final int comparedWithCloneId = ((Long) cloneId)
				.compareTo(anotherElement.getCloneId());
		if (comparedWithCloneId != 0) {
			return comparedWithCloneId;
		}

		final int comparedWithPath = this.path.compareTo(anotherElement
				.getPath());
		if (comparedWithPath != 0) {
			return comparedWithPath;
		}

		return ((Integer) startLine).compareTo(anotherElement.getStartLine());
	}

}
