package jp.ac.osaka_u.ist.sdl.c20r.ui.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class CSVProjectInfo {

	private final Map<Integer, CSVRevisionInfo> revisions;

	public CSVProjectInfo(Collection<CSVRevisionInfo> revisions) {
		this.revisions = new TreeMap<Integer, CSVRevisionInfo>();
		for (CSVRevisionInfo revision : revisions) {
			this.revisions.put(revision.getRevisionNum(), revision);
		}
	}

	public final Map<Integer, CSVRevisionInfo> getAllRevisions() {
		return Collections.unmodifiableMap(this.revisions);
	}

}
