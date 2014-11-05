package jp.ac.osaka_u.ist.sdl.scanalyzer.data.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class represents mapping between two clone classes, one of which is in a
 * version and the other of which is in the next version.
 * 
 * @author k-hotta
 *
 */
@DatabaseTable(tableName = "CLONE_CLASS_MAPPING")
public class DBCloneClassMapping implements IDBElement {

	/**
	 * The column name for id
	 */
	public static final String ID_COLUMN_NAME = "ID";

	/**
	 * The column name for oldCloneClass
	 */
	public static final String OLD_CLONE_CLASS_COLUMN_NAME = "OLD_CLONE_CLASS";

	/**
	 * The column name for newCloneClass
	 */
	public static final String NEW_CLONE_CLASS_COLUMN_NAME = "NEW_CLONE_CLASS";

	/**
	 * The id of this mapping
	 */
	@DatabaseField(id = true, columnName = ID_COLUMN_NAME)
	private long id;

	/**
	 * The old clone class of this mapping, which is in the before version
	 */
	@DatabaseField(canBeNull = true, foreign = true, columnName = OLD_CLONE_CLASS_COLUMN_NAME)
	private DBCloneClass oldCloneClass;

	/**
	 * The new clone class of this mapping, which is in the after version
	 */
	@DatabaseField(canBeNull = true, foreign = true, columnName = NEW_CLONE_CLASS_COLUMN_NAME)
	private DBCloneClass newCloneClass;

	/**
	 * The default constructor, which does nothing
	 */
	public DBCloneClassMapping() {

	}

	/**
	 * The constructor with all the values specified
	 * 
	 * @param id
	 *            the id of this mapping
	 * @param oldCloneClass
	 *            the old clone class of this mapping
	 * @param newCloneClass
	 *            the new clone class of this mapping
	 */
	public DBCloneClassMapping(final long id, final DBCloneClass oldCloneClass,
			final DBCloneClass newCloneClass) {
		this.id = id;
		this.oldCloneClass = oldCloneClass;
		this.newCloneClass = newCloneClass;
	}

	/**
	 * Get the id of this mapping.
	 * 
	 * @return the id of this mapping
	 */
	@Override
	public long getId() {
		return id;
	}

	/**
	 * Set the id of this mapping with the specified value.
	 * 
	 * @param id
	 *            the id to be set
	 */
	@Override
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get the old clone class of this mapping, which is in the before version
	 * 
	 * @return the old clone class of this mapping
	 */
	public DBCloneClass getOldCloneClass() {
		return oldCloneClass;
	}

	/**
	 * Set the old clone class of this mapping with the specified one.
	 * 
	 * @param oldCloneClass
	 *            the old clone class to be set
	 */
	public void setOldCloneClass(DBCloneClass oldCloneClass) {
		this.oldCloneClass = oldCloneClass;
	}

	/**
	 * Get the new clone class of this mapping, which is in the after version
	 * 
	 * @return the new clone class of this mapping
	 */
	public DBCloneClass getNewCloneClass() {
		return newCloneClass;
	}

	/**
	 * Set the new clone class of this mapping with the specified one.
	 * 
	 * @param newCloneClass
	 *            the new clone class to be set
	 */
	public void setNewCloneClass(DBCloneClass newCloneClass) {
		this.newCloneClass = newCloneClass;
	}

	/**
	 * Judge whether the given object equals to this object. <br>
	 * 
	 * @return <code>true</code> if the given object is an instance of
	 *         {@link DBCloneClassMapping} and the id values of the two objects
	 *         are the same to each other, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DBCloneClassMapping)) {
			return false;
		}

		final DBCloneClassMapping another = (DBCloneClassMapping) obj;

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

	@Override
	public String toString() {
		if (oldCloneClass == null) {
			return "\t-\t" + newCloneClass.toString();
		} else if (newCloneClass == null) {
			return oldCloneClass.toString() + "\t-\t";
		} else {
			return oldCloneClass.toString() + "\t-\t"
					+ newCloneClass.toString();
		}
	}

}
