package jp.ac.osaka_u.ist.sdl.c20r.ui.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class CSVRevisionInfo implements Comparable<CSVRevisionInfo> {

	private final int revisionNum;

	private final int nextRevisionNum;

	private final Map<Long, CSVCloneSetInfo> cloneSets;

	private int disappeared;

	private int moved;

	private boolean containsFileDeletion;

	public CSVRevisionInfo(final int revisionNum, final int nextRevisionNum,
			final Collection<CSVCloneSetInfo> cloneSets) {
		this.revisionNum = revisionNum;
		this.nextRevisionNum = nextRevisionNum;
		this.cloneSets = new TreeMap<Long, CSVCloneSetInfo>();
		this.disappeared = 0;
		this.moved = 0;
		this.containsFileDeletion = false;
		for (CSVCloneSetInfo cloneSet : cloneSets) {
			this.cloneSets.put(cloneSet.getId(), cloneSet);
			if (cloneSet.isContainsDisappear()) {
				this.disappeared++;
			}
			if (cloneSet.isContainsMoved()) {
				this.moved += cloneSet.getMovedCount();
			}
			if (cloneSet.isInDeletedFile()) {
				this.containsFileDeletion = true;
			}
		}
	}

	public Map<Long, CSVCloneSetInfo> getCloneSetsMap() {
		return Collections.unmodifiableMap(cloneSets);
	}

	public int getRevisionNum() {
		return revisionNum;
	}

	public int getNextRevisionNum() {
		return nextRevisionNum;
	}

	public Collection<CSVCloneSetInfo> getCloneSets() {
		return Collections.unmodifiableCollection(this.cloneSets.values());
	}

	public boolean containsDisappeared() {
		return this.disappeared != 0;
	}

	public int getDisappeared() {
		return this.disappeared;
	}

	public boolean containsMoved() {
		return this.moved != 0;
	}

	public int getMoved() {
		return this.moved;
	}

	public boolean containsFileDeletion() {
		return this.containsFileDeletion;
	}

	@Override
	public int compareTo(CSVRevisionInfo anotherRevision) {
		return ((Integer) revisionNum).compareTo(anotherRevision
				.getRevisionNum());
	}

}
