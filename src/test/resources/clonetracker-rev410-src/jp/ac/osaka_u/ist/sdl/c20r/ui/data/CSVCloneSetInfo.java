package jp.ac.osaka_u.ist.sdl.c20r.ui.data;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * CSVから読み取ったクローンセットを表すクラス
 * 
 * @author k-hotta
 * 
 */
public class CSVCloneSetInfo implements Comparable<CSVCloneSetInfo> {

	private final int revisionNum;

	private final long id;

	private final Set<CSVCloneElementInfo> elements;

	private final int count;

	private boolean containsDisappear;

	private boolean containsInDeletedFile;

	private int moved;

	public CSVCloneSetInfo(final int revisionNum, final long id,
			final Set<CSVCloneElementInfo> elements) {
		this.revisionNum = revisionNum;
		this.id = id;
		this.elements = new TreeSet<CSVCloneElementInfo>();
		this.elements.addAll(elements);
		this.count = elements.size();
		this.containsDisappear = false;
		this.containsInDeletedFile = false;
		this.moved = 0;
		for (CSVCloneElementInfo element : elements) {
			if (element.isDisappear()) {
				this.containsDisappear = true;
			}
			if (element.isInDeletedFile()) {
				this.containsInDeletedFile = true;
			}
			if (element.isMoved()) {
				this.moved++;
			}
		}
	}

	public int getRevisionNum() {
		return revisionNum;
	}

	public long getId() {
		return id;
	}

	public Set<CSVCloneElementInfo> getElements() {
		return Collections.unmodifiableSet(elements);
	}

	public int getCount() {
		return count;
	}

	public boolean isContainsDisappear() {
		return containsDisappear;
	}

	public boolean isInDeletedFile() {
		return containsInDeletedFile;
	}

	public boolean isContainsMoved() {
		return moved != 0;
	}

	public int getMovedCount() {
		return moved;
	}

	@Override
	public int compareTo(CSVCloneSetInfo anotherCloneSet) {
		return ((Long) id).compareTo(anotherCloneSet.getId());
	}

}
