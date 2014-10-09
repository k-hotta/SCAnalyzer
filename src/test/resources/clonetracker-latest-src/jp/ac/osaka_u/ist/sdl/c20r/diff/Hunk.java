package jp.ac.osaka_u.ist.sdl.c20r.diff;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * diff�Ō��o����鍷���̉�1��\���N���X
 * 
 * @author k-hotta
 * 
 */
public class Hunk {

	/**
	 * ���r�W�����ԍ�
	 */
	private final long revisionNum;

	/**
	 * �t�@�C���p�X
	 */
	private final String filePath;

	/**
	 * �����ƂȂ��Ă���s�̍s�ԍ��̏W��
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
