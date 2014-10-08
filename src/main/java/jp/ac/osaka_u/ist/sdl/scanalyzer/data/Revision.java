package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * A class represents revision
 * 
 * @author k-hotta
 * 
 */
@DatabaseTable(tableName = "revisions")
public class Revision implements IDBElement, Comparable<Revision> {

	/**
	 * The id of the revision
	 */
	@DatabaseField(id = true, index = true)
	private long id;

	/**
	 * The string of the revision that is used as identifiers in its version
	 * control system. <br>
	 * e.g. numerical sequential value in SVN, hash value in git.
	 */
	@DatabaseField(unique = true, index = true)
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
	public Revision(final long id, final String identifier) {
		this.id = id;
		this.identifier = identifier;
	}

	/**
	 * Get the id of this revision
	 */
	public long getId() {
		return id;
	}

	/**
	 * Set the id of this revision with the specified value
	 */
	public void setId(long id) {
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

	/**
	 * Judge whether the given object equals to this object. <br>
	 * 
	 * @return <code>true</code> if the given object is an instance of
	 *         {@link Revision Revision} and the id values of the two objects
	 *         are the same to each other; <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Revision)) {
			return false;
		}
		final Revision another = (Revision) obj;

		return this.id == another.getId();
	}

	/**
	 * Return a hash code value of this object. <br>
	 * The hash value of this object is just the value of the id. <br>
	 * 
	 * @return the hash value, which equals to the value of id of this object
	 */
	@Override
	public int hashCode() {
		return (int) this.id;
	}

	/**
	 * Compare this element to the given one based on their IDs.
	 */
	public int compareTo(final Revision another) {
		return ((Long) this.id).compareTo(another.getId());
	}

}
