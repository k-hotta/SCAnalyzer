package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

/**
 * A class represents revision
 * 
 * @author k-hotta
 * 
 */
public class Revision implements IDBElement {

	/**
	 * The id of the revision
	 */
	private Long id;

	/**
	 * The string of the revision that is used as identifiers in its version
	 * control system. <br>
	 * e.g. numerical sequential value in SVN, hash value in git.
	 */
	private String identifier;

	/**
	 * The default constructor
	 */
	public Revision() {

	}

	/**
	 * The constructor with all the values to be set
	 * 
	 * @param id
	 *            the id of the revision
	 * @param identifier
	 *            the identifier of this revision in its version control system
	 */
	public Revision(final Long id, final String identifier) {
		this.id = id;
		this.identifier = identifier;
	}

	/**
	 * Get the id of this revision
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Set the id of this revision with the specified value
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Get the identifier of this revision in version control system
	 * 
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Set the identifier of this revision in version control system with the
	 * specified value
	 * 
	 * @param identifier
	 *            the identifier to be set
	 */
	public void setIdentifier(final String identifier) {
		this.identifier = identifier;
	}

}
