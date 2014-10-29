package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.Collection;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class represents a clone class, which is a set of {@link CodeFragment}.
 * 
 * @author k-hotta
 * 
 */
@DatabaseTable(tableName = "CLONE_CLASSES")
public class CloneClass implements IDBElement {

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
	 * The id of this clone class
	 */
	@DatabaseField(id = true, columnName = ID_COLUMN_NAME)
	private long id;

	@DatabaseField(canBeNull = false, foreign = true, columnName = VERSION_COLUMN_NAME)
	private Version version;

	/**
	 * The code fragments
	 */
	@ForeignCollectionField(eager = true, columnName = CODE_FRAGMENTS_COLUMN_NAME)
	private Collection<CodeFragment> codeFragments;

	/**
	 * The default constructor
	 */
	public CloneClass() {

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
	 */
	public CloneClass(final long id, final Version version,
			final Collection<CodeFragment> codeFragments) {
		this.id = id;
		this.version = version;
		this.codeFragments = codeFragments;
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
	public Version getVersion() {
		return version;
	}

	/**
	 * Set the owner version of this clone class with the specified one.
	 * 
	 * @param version
	 *            the owner version to be set
	 */
	public void setVersion(final Version version) {
		this.version = version;
	}

	/**
	 * Get the code fragments in this clone class.
	 * 
	 * @return the code fragments in this clone class
	 */
	public Collection<CodeFragment> getCodeFragments() {
		return codeFragments;
	}

	/**
	 * Set the code fragments with the specified one.
	 * 
	 * @param codeFragments
	 *            the code fragments to be set
	 */
	public void setCodeFragments(final Collection<CodeFragment> codeFragments) {
		this.codeFragments = codeFragments;
	}

	/**
	 * Judge whether the given object equals to this object. <br>
	 * 
	 * @return <code>true</code> if the given object is an instance of
	 *         {@link CloneClass} and the id values of the two objects are the
	 *         same to each other, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CloneClass)) {
			return false;
		}

		final CloneClass another = (CloneClass) obj;

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

		for (final CodeFragment fragment : codeFragments) {
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
