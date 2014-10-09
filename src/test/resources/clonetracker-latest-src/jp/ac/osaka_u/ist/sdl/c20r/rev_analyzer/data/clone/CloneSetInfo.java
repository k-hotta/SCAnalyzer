package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.clone;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.AbstractElementInfo;

public class CloneSetInfo extends AbstractElementInfo {

	private final int hash;
	
	public CloneSetInfo(long id, int hash) {
		super(id);
		this.hash = hash;
	}

	public final int getHash() {
		return hash;
	}
	
}
