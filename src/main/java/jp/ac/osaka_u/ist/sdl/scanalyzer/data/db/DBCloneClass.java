package jp.ac.osaka_u.ist.sdl.scanalyzer.data.db;

import java.util.Collection;

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
@DatabaseTable(tableName = "CLONE_CLASSES")
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
	 * the column name for ghostFragments
	 */
	public static final String GHOST_FRAGMENTS_COLUMN_NAME = "GHOST_FRAGMENTS";

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
	 * The ghost fragments
	 */
	@ForeignCollectionField(eager = true, columnName = GHOST_FRAGMENTS_COLUMN_NAME)
	private Collection<DBCodeFragment> ghostFragments;

	/**
	 * The default constructor
	 */
	public DBCloneClass() {

	}

	/**
	 * The constructor with all the values specified.
	 * 
	 * @param id
	 *            the id of this clone class
	 * @param version
	 *            the owner version of this clone class
	 * @param codeFragments
	 *            the code fragments in this clone class
	 * @param ghostFragments
	 *            the ghost fragments in this clone class
	 */
	public DBCloneClass(final long id, final DBVersion version,
			final Collection<DBCodeFragment> codeFragments,
			final Collection<DBCodeFragment> ghostFragments) {
		this.id = id;
		this.version = version;
		this.codeFragments = codeFragments;
		this.ghostFragments = ghostFragments;
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
		return codeFragments;
	}

	/**
	 * Set the code fragments with the specified one.
	 * 
	 * @param codeFragments
	 *            the code fragments to be set
	 */
	public void setCodeFragments(final Collection<DBCodeFragment> codeFragments) {
		this.codeFragments = codeFragments;
	}

	/**
	 * Get the ghost fragments in this clone class.
	 * 
	 * @return the ghost fragments in this clone class
	 */
	public Collection<DBCodeFragment> getGhostFragments() {
		return ghostFragments;
	}

	/**
	 * Set the ghost fragments with the specified one.
	 * 
	 * @param ghostFragments
	 *            the ghost fragments to be set
	 */
	public void setGhostFragments(
			final Collection<DBCodeFragment> ghostFragments) {
		this.ghostFragments = ghostFragments;
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

		if (ghostFragments.size() > 0) {
			builder.append(" (");
			for (final DBCodeFragment ghost : ghostFragments) {
				builder.append(ghost.toString() + ", ");
			}
			builder.deleteCharAt(builder.length() - 1);
			builder.deleteCharAt(builder.length() - 1);
			builder.append(") ");
		}

		builder.append("}");

		return builder.toString();
	}

}
