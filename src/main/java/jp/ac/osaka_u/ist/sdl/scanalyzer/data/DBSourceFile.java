package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * A class that represents source files. <br>
 * Note that this class has just surface-level information. The contents of the
 * file would be stored in {@link SourceFileWithContent}.
 * 
 * @author k-hotta
 * 
 * @see SourceFileWithContent
 */
@DatabaseTable(tableName = "SOURCE_FILES")
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
	 */
	public DBSourceFile(final long id, final String path) {
		this.id = id;
		this.path = path;
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
