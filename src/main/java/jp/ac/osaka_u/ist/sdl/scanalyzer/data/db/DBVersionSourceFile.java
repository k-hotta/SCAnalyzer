package jp.ac.osaka_u.ist.sdl.scanalyzer.data.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class is just for realizing many-to-many relationship between
 * {@link DBVersion} and {@link DBSourceFile}. An instance of this class
 * represents a relationship between an instance of {@link DBVersion} and an
 * instance of {@link DBSourceFile}.
 * 
 * @author k-hotta
 * @see DBVersion
 * @see DBSourceFile
 */
@DatabaseTable(tableName = TableName.VERSION_SOURCE_FILE)
public class DBVersionSourceFile implements IDBElement {

	/**
	 * The column name for id
	 */
	public static final String ID_COLUMN_NAME = "ID";

	/**
	 * The column name for version
	 */
	public static final String VERSION_COLUMN_NAME = "VERSION";

	/**
	 * The column name for sourceFile
	 */
	public static final String SOURCE_FILE_COLUMN_NAME = "SOURCE_FILE";

	/**
	 * The id of this relationship
	 */
	@DatabaseField(id = true, columnName = ID_COLUMN_NAME)
	private long id;

	/**
	 * The corresponding version
	 */
	@DatabaseField(canBeNull = false, foreign = true, columnName = VERSION_COLUMN_NAME)
	private DBVersion version;

	/**
	 * The corresponding source file
	 */
	@DatabaseField(canBeNull = false, foreign = true, columnName = SOURCE_FILE_COLUMN_NAME)
	private DBSourceFile sourceFile;

	/**
	 * The default constructor for ORMLite.
	 */
	public DBVersionSourceFile() {

	}

	/**
	 * The constructor with all the values specified
	 * 
	 * @param id
	 *            the id of this relationship
	 * @param version
	 *            the corresponding version
	 * @param sourceFile
	 *            the corresponding source file
	 */
	public DBVersionSourceFile(final long id, final DBVersion version,
			final DBSourceFile sourceFile) {
		this.id = id;
		this.version = version;
		this.sourceFile = sourceFile;
	}

	/**
	 * Get the id of this relationship.
	 * 
	 * @return the id of this relationship
	 */
	public long getId() {
		return id;
	}

	/**
	 * Set the id of this relationship with the specified value.
	 * 
	 * @param id
	 *            the id of this relationship to be set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get the corresponding version.
	 * 
	 * @return the corresponding version of this relationship
	 */
	public DBVersion getVersion() {
		return version;
	}

	/**
	 * Set the corresponding version with the specified one.
	 * 
	 * @param version
	 *            the corresponding version to be set
	 */
	public void setVersion(DBVersion version) {
		this.version = version;
	}

	/**
	 * Get the corresponding source file.
	 * 
	 * @return the corresponding source file of this relationship
	 */
	public DBSourceFile getSourceFile() {
		return sourceFile;
	}

	/**
	 * Set the corresponding source file with the specified one.
	 * 
	 * @param sourceFile
	 *            the corresponding source file to be set
	 */
	public void setSourceFile(DBSourceFile sourceFile) {
		this.sourceFile = sourceFile;
	}

	/**
	 * Judge whether the given object equals to this object. <br>
	 * 
	 * @return <code>true</code> if the given object is an instance of
	 *         {@link DBVersionSourceFile} and the id values of the two objects
	 *         are the same to each other, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DBVersionSourceFile)) {
			return false;
		}
		final DBVersionSourceFile another = (DBVersionSourceFile) obj;

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

}
