package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * A class that represents source files. <br>
 * 
 * @author k-hotta
 * 
 */
@DatabaseTable(tableName = "source_files")
public class SourceFile implements IDBElement {

	/**
	 * The id of this source file
	 */
	@DatabaseField(id = true, index = true)
	private long id;

	/**
	 * The path of this source file
	 */
	@DatabaseField(canBeNull = false)
	private String path;

	/**
	 * The version that has this source file
	 */
	@DatabaseField(canBeNull = false, foreign = true)
	private Version version;

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
	 * @param version
	 *            the version that has this source file
	 */
	public SourceFile(final long id, final String path, final Version version) {
		this.id = id;
		this.path = path;
		this.version = version;
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
	 * Get the version that has this source file
	 * 
	 * @return the owner version of this source file
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * Set the owner version of this source file
	 * 
	 * @param version
	 *            the owner version to be set
	 */
	public void setVersion(Version version) {
		this.version = version;
	}

}