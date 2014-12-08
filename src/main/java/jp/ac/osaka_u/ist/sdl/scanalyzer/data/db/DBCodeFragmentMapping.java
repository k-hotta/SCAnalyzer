package jp.ac.osaka_u.ist.sdl.scanalyzer.data.db;

import java.util.Collection;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class represents mapping between two code fragments, one of which is in
 * a version and the other of which is in the next version.
 * 
 * @author k-hotta
 *
 * @see DBCodeFragment
 */
@DatabaseTable(tableName = TableName.CODE_FRAGMENT_MAPPING)
public class DBCodeFragmentMapping implements IDBElement {

	/**
	 * The column name for id
	 */
	public static final String ID_COLUMN_NAME = "ID";

	/**
	 * The column name for oldCloneClass
	 */
	public static final String OLD_CODE_FRAGMENT_COLUMN_NAME = "OLD_CODE_FRAGMENT";

	/**
	 * The column name for newCloneClass
	 */
	public static final String NEW_CODE_FRAGMENT_COLUMN_NAME = "NEW_CODE_FRAGMENT";

	/**
	 * The column name for modifications
	 */
	public static final String MODIFICATIONS_COLUMN_NAME = "MODIFICATIONS";

	/**
	 * The column name for cloneClassMapping
	 */
	public static final String CLONE_CLASS_MAPPING_COLUMN_NAME = "CLONE_CLASS_MAPPING";

	/**
	 * The id of this mapping
	 */
	@DatabaseField(id = true, columnName = ID_COLUMN_NAME)
	private long id;

	/**
	 * The old code fragment of this mapping, which is in the before version
	 */
	@DatabaseField(canBeNull = true, foreign = true, columnName = OLD_CODE_FRAGMENT_COLUMN_NAME)
	private DBCodeFragment oldCodeFragment;

	/**
	 * The new code fragment of this mapping, which is in the after version
	 */
	@DatabaseField(canBeNull = true, foreign = true, columnName = NEW_CODE_FRAGMENT_COLUMN_NAME)
	private DBCodeFragment newCodeFragment;

	/**
	 * The modifications on this code fragment mapping
	 */
	@ForeignCollectionField(eager = true, columnName = MODIFICATIONS_COLUMN_NAME)
	private Collection<DBCloneModification> modifications;

	/**
	 * The owner clone class mapping of this mapping
	 */
	@DatabaseField(canBeNull = false, foreign = true, columnName = CLONE_CLASS_MAPPING_COLUMN_NAME)
	private DBCloneClassMapping cloneClassMapping;

	/**
	 * The default constructor, which does nothing
	 */
	public DBCodeFragmentMapping() {

	}

	/**
	 * The constructor with all the values specified.
	 * 
	 * @param id
	 *            the id of this mapping
	 * @param oldCodeFragment
	 *            the old code fragment of this mapping
	 * @param newCodeFragment
	 *            the new code fragment of this mapping
	 * @param modifications
	 *            the modifications on this mapping
	 * @param cloneClassMapping
	 *            the owner clone class mapping of this mapping
	 */
	public DBCodeFragmentMapping(final long id,
			final DBCodeFragment oldCodeFragment,
			final DBCodeFragment newCodeFragment,
			final Collection<DBCloneModification> modifications,
			final DBCloneClassMapping cloneClassMapping) {
		this.id = id;
		this.oldCodeFragment = oldCodeFragment;
		this.newCodeFragment = newCodeFragment;
		this.modifications = modifications;
		this.cloneClassMapping = cloneClassMapping;
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
	 * Get the old code fragment of this mapping, which is in the before version
	 * 
	 * @return the old code fragment of this mapping
	 */
	public DBCodeFragment getOldCodeFragment() {
		return oldCodeFragment;
	}

	/**
	 * Set the old code fragment of this mapping with the specified one.
	 * 
	 * @param oldCodeFragment
	 *            the old code fragment to be set
	 */
	public void setOldCodeFragment(DBCodeFragment oldCodeFragment) {
		this.oldCodeFragment = oldCodeFragment;
	}

	/**
	 * Get the new code fragment of this mapping, which is in the after version
	 * 
	 * @return the new code fragment of this mapping
	 */
	public DBCodeFragment getNewCodeFragment() {
		return newCodeFragment;
	}

	/**
	 * Set the new code fragment of this mapping with the specified one.
	 * 
	 * @param newCodeFragment
	 *            the new code fragment to be set
	 */
	public void setNewCodeFragment(DBCodeFragment newCodeFragment) {
		this.newCodeFragment = newCodeFragment;
	}

	/**
	 * Get the modifications on this mapping.
	 * 
	 * @return a collection of modifications
	 */
	public Collection<DBCloneModification> getModifications() {
		return modifications;
	}

	/**
	 * Set the modifications on this mapping with the specified one.
	 * 
	 * @param modifications
	 *            a collection of modifications to be set
	 */
	public void setModifications(
			final Collection<DBCloneModification> modifications) {
		this.modifications = modifications;
	}

	/**
	 * Get the owner clone class mapping of this mapping.
	 * 
	 * @return the owner clone class mapping of this mapping
	 */
	public DBCloneClassMapping getCloneClassMapping() {
		return cloneClassMapping;
	}

	/**
	 * Set the owner clone class mapping of this mapping with the specified one.
	 * 
	 * @param cloneClassMapping
	 *            the owner clone class mapping to be set
	 */
	public void setCloneClassMapping(final DBCloneClassMapping cloneClassMapping) {
		this.cloneClassMapping = cloneClassMapping;
	}

	/**
	 * Judge whether the given object equals to this object. <br>
	 * 
	 * @return <code>true</code> if the given object is an instance of
	 *         {@link DBCodeFragmentMapping} and the id values of the two
	 *         objects are the same to each other, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DBCodeFragmentMapping)) {
			return false;
		}

		final DBCodeFragmentMapping another = (DBCodeFragmentMapping) obj;

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
		if (oldCodeFragment == null) {
			return "\t-\t" + newCodeFragment.toString();
		} else if (newCodeFragment == null) {
			return oldCodeFragment.toString() + "\t-\t";
		} else {
			return oldCodeFragment.toString() + "\t-\t"
					+ newCodeFragment.toString();
		}
	}

}
