package jp.ac.osaka_u.ist.sdl.scanalyzer.data.db;

import com.j256.ormlite.field.DatabaseField;

/**
 * This class represents mapping between two code fragments, one of which is in
 * a version and the other of which is in the next version.
 * 
 * @author k-hotta
 *
 * @see DBCodeFragment
 */
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
	 */
	public DBCodeFragmentMapping(final long id,
			final DBCodeFragment oldCodeFragment,
			final DBCodeFragment newCodeFragment) {
		this.id = id;
		this.oldCodeFragment = oldCodeFragment;
		this.newCodeFragment = newCodeFragment;
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

}
