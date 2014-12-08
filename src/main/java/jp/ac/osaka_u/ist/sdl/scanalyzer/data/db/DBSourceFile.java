package jp.ac.osaka_u.ist.sdl.scanalyzer.data.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * A class that represents source files. <br>
 * Note that this class has just surface-level information.
 * 
 * @author k-hotta
 * 
 */
@DatabaseTable(tableName = TableName.SOURCE_FILE)
public class DBSourceFile implements IDBElement {

	/**
	 * The column name for id
	 */
	public static final String ID_COLUMN_NAME = "ID";

	/**
	 * The column name for path
	 */
	public static final String PATH_COLUMN_NAME = "PATH";

	/**
	 * The column name for hash
	 */
	public static final String HASH_OF_PATH_COLUMN_NAME = "HASH_OF_PATH";

	/**
	 * The id of this source file
	 */
	@DatabaseField(id = true, columnName = ID_COLUMN_NAME)
	private long id;

	/**
	 * The path of this source file
	 */
	@DatabaseField(canBeNull = false, columnName = PATH_COLUMN_NAME)
	private String path;

	/**
	 * The hash code of the path of this source file. Note that if this source
	 * file has been introduced by relocation, the hash code of path will equal
	 * to that in the origin file. This is for handling file relocation in
	 * mapping process.
	 */
	@DatabaseField(canBeNull = false, columnName = HASH_OF_PATH_COLUMN_NAME)
	private int hashOfPath;

	/**
	 * The default constructor
	 */
	public DBSourceFile() {

	}

	/**
	 * The constructor with all the values to be set
	 * 
	 * @param id
	 *            the id of this source file
	 * @param path
	 *            the path of this source file
	 * @param hashOfPath
	 *            the hash code of the path of this source file
	 */
	public DBSourceFile(final long id, final String path, final int hashOfPath) {
		this.id = id;
		this.path = path;
		this.hashOfPath = hashOfPath;
	}

	/**
	 * Get the id of this source file.
	 * 
	 * @return the id of this source file
	 */
	@Override
	public long getId() {
		return id;
	}

	/**
	 * Set the id of this source file with the specified value.
	 * 
	 * @param id
	 *            the id to be set
	 */
	@Override
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get the path of this source file.
	 * 
	 * @return the path of this source file
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Set the path of this source file with the specified value.
	 * 
	 * @param path
	 *            the path to be set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Get the hash code of the path of this source file. This hash value might
	 * be different from that calculated by {@link #hashCode()}. This hash value
	 * is expected to be used in mapping process. Please use {@link #hashCode()}
	 * instead in other cases.
	 * 
	 * @return the hash code of the path.
	 */
	public int getHashOfPath() {
		return hashOfPath;
	}

	/**
	 * Set the hash code of the path of this source file with the specified
	 * value.
	 * 
	 * @param hashOfPath
	 *            an integer value to be set
	 */
	public void setHashOfPath(final int hashOfPath) {
		this.hashOfPath = hashOfPath;
	}

	/**
	 * Judge whether the given object equals to this object. <br>
	 * 
	 * @return <code>true</code> if the given object is an instance of
	 *         {@link DBSourceFile} and the id values of the two objects are the
	 *         same to each other, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DBSourceFile)) {
			return false;
		}
		final DBSourceFile another = (DBSourceFile) obj;

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
		return this.id + " " + this.path;
	}

}
