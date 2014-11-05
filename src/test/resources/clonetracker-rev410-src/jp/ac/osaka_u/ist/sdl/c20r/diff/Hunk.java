package jp.ac.osaka_u.ist.sdl.c20r.diff;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * diffで検出される差分の塊1つを表すクラス
 * 
 * @author k-hotta
 * 
 */
public class Hunk {

	/**
	 * リビジョン番号
	 */
	private final long revisionNum;

	/**
	 * ファイルパス
	 */
	private final String filePath;

	/**
	 * 差分となっている行の行番号の集合
	 */
	private final SortedSet<Integer> lines;

	public Hunk(final long revisionNum, final String filePath,
			final Collection<Integer> lines) {
		this.revisionNum = revisionNum;
		this.filePath = filePath;
		this.lines = new TreeSet<Integer>();
		this.lines.addAll(lines);
	}
	
	public final long getRevisionNum() {
		return this.revisionNum;
	}
	
	public final String getFilePath() {
		return this.filePath;
	}
	
	public final Set<Integer> getLines() {
		return Collections.unmodifiableSortedSet(lines);
	}

}
