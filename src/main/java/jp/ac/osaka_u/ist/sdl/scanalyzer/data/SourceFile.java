package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * A class that represents source files. <br>
 * 
 * @author k-hotta
 * 
 */
@DatabaseTable(tableName = "SOURCE_FILES")
public class SourceFile implements IDBElement {

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
	public SourceFile() {

	}

	/**
	 * The constructor with all the values to be set
	 * 
	 * @param id
	 *            the id of this source file
	 * @param path
	 *            the path of this source file
	 */
	public SourceFile(final long id, final String path) {
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

}
