package jp.ac.osaka_u.ist.sdl.scanalyzer.data.db;

import java.util.Collection;
import java.util.Collections;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class represents a clone class, which is a set of {@link DBCodeFragment}
 * .
 * 
 * @author k-hotta
 * 
 */
@DatabaseTable(tableName = TableName.CLONE_CLASS)
public class DBCloneClass implements IDBElement {

	/**
	 * The column name for id
	 */
	public static final String ID_COLUMN_NAME = "ID";

	/**
	 * The column name for version
	 */
	public static final String VERSION_COLUMN_NAME = "VERSION";

	/**
	 * The column name for codeFragments
	 */
	public static final String CODE_FRAGMENTS_COLUMN_NAME = "CODE_FRAGMENTS";

	/**
	 * The column name for numClonedFragments
	 */
	public static final String NUM_CLONED_FRAGMENTS_COLUMN_NAME = "NUM_CLONED_FRAGMENTS";

	/**
	 * The column name for numGhosts
	 */
	public static final String NUM_GHOST_FRAGMENTS_COLUMN_NAME = "NUM_GHOST_FRAGMENTS";

	/**
	 * The column name for numCommonClonedElements
	 */
	public static final String NUM_COMMON_CLONED_ELEMENTS_COLUMN_NAME = "NUM_COMMON_CLONED_ELEMENTS";

	/**
	 * The column name for numCommonAllElements
	 */
	public static final String NUM_COMMON_ALL_ELEMENTS_COLUMN_NAME = "NUM_COMMON_ALL_ELEMENTS";

	/**
	 * The id of this clone class
	 */
	@DatabaseField(id = true, columnName = ID_COLUMN_NAME)
	private long id;

	@DatabaseField(canBeNull = false, foreign = true, columnName = VERSION_COLUMN_NAME)
	private DBVersion version;

	/**
	 * The code fragments
	 */
	@ForeignCollectionField(eager = true, columnName = CODE_FRAGMENTS_COLUMN_NAME)
	private Collection<DBCodeFragment> codeFragments;

	/**
	 * The number of cloned fragments
	 */
	@DatabaseField(columnName = NUM_CLONED_FRAGMENTS_COLUMN_NAME)
	private int numClonedFragments;

	/**
	 * The number of ghost fragments
	 */
	@DatabaseField(columnName = NUM_GHOST_FRAGMENTS_COLUMN_NAME)
	private int numGhostFragments;

	/**
	 * The number of common program elements among cloned fragments
	 */
	@DatabaseField(columnName = NUM_COMMON_CLONED_ELEMENTS_COLUMN_NAME)
	private int numCommonClonedElements;

	/**
	 * The number of common program elements among all code fragments
	 */
	@DatabaseField(columnName = NUM_COMMON_ALL_ELEMENTS_COLUMN_NAME)
	private int numCommonAllElements;

	/**
	 * The default constructor
	 */
	public DBCloneClass() {

	}

	/**
	 * The constructor with all the values specified, without metrics.
	 * 
	 * @param id
	 *            the id of this clone class
	 * @param version
	 *            the owner version of this clone class
	 * @param codeFragments
	 *            the code fragments in this clone class
	 */
	public DBCloneClass(final long id, final DBVersion version,
			final Collection<DBCodeFragment> codeFragments) {
		this.id = id;
		this.version = version;
		this.codeFragments = codeFragments;
		this.numClonedFragments = 0;
		this.numGhostFragments = 0;
		for (final DBCodeFragment codeFragment : codeFragments) {
			if (codeFragment.isGhost()) {
				this.numGhostFragments++;
			} else {
				this.numClonedFragments++;
			}
		}
		this.numCommonClonedElements = 0;
		this.numCommonAllElements = 0;
	}

	/**
	 * The constructor with all the values specified, including metrics.
	 * 
	 * @param id
	 *            the id of this clone class
	 * @param version
	 *            the owner version of this clone class
	 * @param codeFragments
	 *            the code fragments in this clone class
	 * @param numCloneFragments
	 *            the number of cloned fragments
	 * @param numGhostFragments
	 *            the number of ghost fragments
	 * @param numCommonClonedElements
	 *            the number of common program elements among cloned fragments
	 * @param numCommonAllElements
	 *            the number of common program elements among all code fragments
	 */
	public DBCloneClass(final long id, final DBVersion version,
			final Collection<DBCodeFragment> codeFragments,
			final int numCloneFragments, final int numGhostFragments,
			final int numCommonClonedElements, final int numCommonAllElements) {
		this.id = id;
		this.version = version;
		this.codeFragments = codeFragments;
		this.numClonedFragments = numCloneFragments;
		this.numGhostFragments = numGhostFragments;
		this.numCommonClonedElements = numCommonClonedElements;
		this.numCommonAllElements = numCommonAllElements;
	}

	/**
	 * Get the id of this clone class.
	 * 
	 * @return the id of this clone class
	 */
	@Override
	public long getId() {
		return id;
	}

	/**
	 * Set the id of this clone class.
	 * 
	 * @param id
	 *            the id to be set
	 */
	@Override
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get the owner version of this clone class.
	 * 
	 * @return the owner version of this clone class
	 */
	public DBVersion getVersion() {
		return version;
	}

	/**
	 * Set the owner version of this clone class with the specified one.
	 * 
	 * @param version
	 *            the owner version to be set
	 */
	public void setVersion(final DBVersion version) {
		this.version = version;
	}

	/**
	 * Get the code fragments in this clone class.
	 * 
	 * @return the code fragments in this clone class
	 */
	public Collection<DBCodeFragment> getCodeFragments() {
		return Collections.unmodifiableCollection(codeFragments);
	}

	/**
	 * Add the given code fragment into this clone class.
	 * 
	 * @param codeFragment
	 *            the code fragment to be added
	 */
	public void addCodeFragment(final DBCodeFragment codeFragment) {
		if (codeFragment == null) {
			return;
		}

		if (codeFragment.isGhost()) {
			this.numGhostFragments++;
		} else {
			this.numClonedFragments++;
		}

		codeFragments.add(codeFragment);
	}

	/**
	 * Add the given code fragments into this clone class.
	 * 
	 * @param codeFragments
	 *            a collection of code fragments to be added
	 */
	public void addCodeFragments(final Collection<DBCodeFragment> codeFragments) {
		for (final DBCodeFragment codeFragment : codeFragments) {
			addCodeFragment(codeFragment);
		}
	}

	/**
	 * Set the code fragments with the specified one.
	 * 
	 * @param codeFragments
	 *            the code fragments to be set
	 */
	public void setCodeFragments(final Collection<DBCodeFragment> codeFragments) {
		this.codeFragments = codeFragments;
		this.numClonedFragments = 0;
		this.numGhostFragments = 0;
		for (final DBCodeFragment codeFragment : codeFragments) {
			if (codeFragment.isGhost()) {
				this.numGhostFragments++;
			} else {
				this.numClonedFragments++;
			}
		}
	}

	/**
	 * Get the number of cloned fragments.
	 * 
	 * @return the number of cloned fragments
	 */
	public int getNumClonedFragments() {
		return numClonedFragments;
	}

	/**
	 * Get the number of ghost fragments.
	 * 
	 * @return
	 */
	public int getNumGhostFragments() {
		return numGhostFragments;
	}

	/**
	 * Get the number of common program elements among all the cloned fragments.
	 * 
	 * @return the number of common program elements among all the cloned
	 *         fragments
	 */
	public int getNumCommonClonedElements() {
		return numCommonClonedElements;
	}

	/**
	 * Set the number of common program elements among all the cloned fragments
	 * with the specified value.
	 * 
	 * @param numCommonClonedElements
	 *            an integer value to be set
	 */
	public void setNumCommonClonedElements(int numCommonClonedElements) {
		this.numCommonClonedElements = numCommonClonedElements;
	}

	/**
	 * Get the number of common program elements among all the code fragments
	 * (including ghosts).
	 * 
	 * @return the number of common program elements among all the code
	 *         fragments
	 */
	public int getNumCommonAllElements() {
		return numCommonAllElements;
	}

	/**
	 * Set the number of common program elements among all the code fragments
	 * (including ghosts) with the specified value.
	 * 
	 * @param numCommonAllElements
	 *            an integer value to be set
	 */
	public void setNumCommonAllElements(int numCommonAllElements) {
		this.numCommonAllElements = numCommonAllElements;
	}

	/**
	 * Judge whether the given object equals to this object. <br>
	 * 
	 * @return <code>true</code> if the given object is an instance of
	 *         {@link DBCloneClass} and the id values of the two objects are the
	 *         same to each other, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DBCloneClass)) {
			return false;
		}

		final DBCloneClass another = (DBCloneClass) obj;

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
		final StringBuilder builder = new StringBuilder();

		builder.append(id + " {");

		for (final DBCodeFragment fragment : codeFragments) {
			builder.append(fragment.toString() + ", ");
		}
		if (codeFragments.size() > 0) {
			builder.deleteCharAt(builder.length() - 1);
			builder.deleteCharAt(builder.length() - 1);
		}

		builder.append("}");

		return builder.toString();
	}

}
