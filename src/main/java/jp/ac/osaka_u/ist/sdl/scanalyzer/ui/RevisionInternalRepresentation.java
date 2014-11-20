package jp.ac.osaka_u.ist.sdl.scanalyzer.ui;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;

/**
 * This is a representation of {@link Revision}. This is just for the internal
 * use in the ui package.
 * 
 * @author k-hotta
 *
 */
public class RevisionInternalRepresentation {

	private long id;

	private String identifier;

	private int numOfClones;

	private int numOfClonesWithGhosts;

	private int numOfClonesCompletelyGhost;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public int getNumOfClones() {
		return numOfClones;
	}

	public void setNumOfClones(int numOfClones) {
		this.numOfClones = numOfClones;
	}

	public int getNumOfClonesWithGhosts() {
		return numOfClonesWithGhosts;
	}

	public void setNumOfClonesWithGhosts(int numOfClonesWithGhosts) {
		this.numOfClonesWithGhosts = numOfClonesWithGhosts;
	}

	public int getNumOfClonesCompletelyGhost() {
		return numOfClonesCompletelyGhost;
	}

	public void setNumOfClonesCompletelyGhost(int numOfClonesCompletelyGhost) {
		this.numOfClonesCompletelyGhost = numOfClonesCompletelyGhost;
	}
}
