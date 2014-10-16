package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * A class represents revision
 * 
 * @author k-hotta
 * 
 */
@DatabaseTable(tableName = "REVISIONS")
public class Revision implements IDBElement, Comparable<Revision> {

	/**
	 * The column name for id
	 */
	public static final String ID_COLUMN_NAME = "ID";

	/**
	 * The column name for identifier
	 */
	public static final String IDENTIFIER_COLUMN_NAME = "IDENTIFIER";

	/**
	 * The column name for date
	 */
	public static final String DATE_COLUMN_NAME = "DATE";

	/**
	 * The id of the revision
	 */
	@DatabaseField(id = true, columnName = ID_COLUMN_NAME)
	private long id;

	/**
	 * The string of the revision that is used as identifiers in its version
	 * control system. <br>
	 * e.g. numerical sequential value in SVN, hash value in git.
	 */
	@DatabaseField(canBeNull = false, unique = true, index = true, columnName = IDENTIFIER_COLUMN_NAME)
	private String identifier;

	/**
	 * The date when this revision was committed.
	 */
	@DatabaseField(canBeNull = true, index = true, columnName = DATE_COLUMN_NAME)
	private Date date;

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
	 * @param date
	 *            the date when this revision was committed
	 */
	public Revision(final long id, final String identifier, final Date date) {
		this.id = id;
		this.identifier = identifier;
		this.date = date;
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
	 * Get the date when this revision was committed
	 * 
	 * @return the date when this revision was committed
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Set the date when this revision was committed
	 * 
	 * @param date
	 *            the date to be set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Judge whether the given object equals to this object. <br>
	 * 
	 * @return <code>true</code> if the given object is an instance of
	 *         {@link Revision} and the id values of the two objects
	 *         are the same to each other, <code>false</code> otherwise.
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

	@Override
	public String toString() {
		return id + " [identifier:" + identifier + "] at " + date;
	}
	
}
