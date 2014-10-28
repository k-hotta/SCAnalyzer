package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class is just for realizing many-to-many relationship between
 * {@link CloneClass} and {@link CodeFragment}. An instance of this class
 * represents a relationship between an instance of {@link CloneClass} and that
 * of {@link CodeFragment}.
 * 
 * @author k-hotta
 * 
 * @see CloneClass
 * @see CodeFragment
 */
@DatabaseTable(tableName = "CLONE_CLASS_CODE_FRAGMENT")
public class CloneClassCodeFragment implements IDBElement {

	/**
	 * The column name for id
	 */
	public static final String ID_COLUMN_NAME = "ID";

	/**
	 * The column name for cloneClass
	 */
	public static final String CLONE_CLASS_COLUMN_NAME = "CLONE_CLASS";

	/**
	 * The column name for codeFragment
	 */
	public static final String CODE_FRAGMENT_COLUMN_NAME = "CODE_FRAGMENT";

	/**
	 * The id of this relationship
	 */
	@DatabaseField(id = true, columnName = ID_COLUMN_NAME)
	private long id;

	/**
	 * The corresponding clone class
	 */
	@DatabaseField(canBeNull = false, foreign = true, columnName = CLONE_CLASS_COLUMN_NAME)
	private CloneClass cloneClass;

	/**
	 * The corresponding code fragment
	 */
	@DatabaseField(canBeNull = false, foreign = true, columnName = CODE_FRAGMENT_COLUMN_NAME)
	private CodeFragment codeFragment;

	/**
	 * The default constructor
	 */
	public CloneClassCodeFragment() {

	}

	/**
	 * The constructor with all the values specified
	 * 
	 * @param id
	 *            the id of this relationship
	 * @param cloneClass
	 *            the corresponding clone class
	 * @param codeFragment
	 *            the corresponding code fragment
	 */
	public CloneClassCodeFragment(final long id, final CloneClass cloneClass,
			final CodeFragment codeFragment) {
		this.id = id;
		this.cloneClass = cloneClass;
		this.codeFragment = codeFragment;
	}

	/**
	 * Get the id of this relationship.
	 * 
	 * @return the id of this relationship
	 */
	@Override
	public long getId() {
		return id;
	}

	/**
	 * Set the id of this relationship.
	 * 
	 * @param id
	 *            the id to be set
	 */
	@Override
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get the corresponding clone class of this relationship.
	 * 
	 * @return the corresponding clone class
	 */
	public CloneClass getCloneClass() {
		return cloneClass;
	}

	/**
	 * Set the corresponding clone class of this relationship with the specified
	 * one.
	 * 
	 * @param cloneClass
	 *            the corresponding clone class to be set
	 */
	public void setCloneClass(final CloneClass cloneClass) {
		this.cloneClass = cloneClass;
	}

	/**
	 * Get the corresponding code fragment of this relationship with the
	 * specified one.
	 * 
	 * @return the corresponding code fragment of this relationship
	 */
	public CodeFragment getCodeFragment() {
		return codeFragment;
	}

	/**
	 * Set the corresponding code fragment of this relationship with the
	 * specified one.
	 * 
	 * @param codeFragment
	 *            the corresponding code fragment to be set
	 */
	public void setCodeFragment(CodeFragment codeFragment) {
		this.codeFragment = codeFragment;
	}

	/**
	 * Judge whether the given object equals to this object. <br>
	 * 
	 * @return <code>true</code> if the given object is an instance of
	 *         {@link CloneClassCodeFragment} and the id values of the two
	 *         objects are the same to each other, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CloneClassCodeFragment)) {
			return false;
		}

		final CloneClassCodeFragment another = (CloneClassCodeFragment) obj;

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
		return "clone:" + this.cloneClass.getId() + " - fragment:"
				+ this.codeFragment.getId();
	}

}
