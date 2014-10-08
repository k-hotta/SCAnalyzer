package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * A class that represents version of source code. <br>
 * A version contains all the source files at a particular revision. <br>
 * 
 * @author k-hotta
 * 
 */
@DatabaseTable(tableName = "versions")
public class Version implements IDBElement {

	/**
	 * The id of the version
	 */
	@DatabaseField(id = true, index = true)
	private long id;

	/**
	 * The corresponding revision
	 */
	private Revision revision;

	/**
	 * The default constructor
	 */
	public Version() {

	}

	/**
	 * The constructor with all the values to be set
	 * 
	 * @param id
	 *            the id
	 * @param revision
	 *            the corresponding revision
	 */
	public Version(final long id, final Revision revision) {
		this.id = id;
		this.revision = revision;
	}

	/**
	 * Get the id of this version.
	 * 
	 * @return the id of this version
	 */
	@Override
	public long getId() {
		return id;
	}

	/**
	 * Set the id of this version with the specified value.
	 * 
	 * @param id
	 *            the id of this version to be set
	 */
	@Override
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get the corresponding revision of this version
	 * 
	 * @return the corresponding revision of this version
	 */
	public Revision getRevision() {
		return revision;
	}

	/**
	 * Set the corresponding revision of this version with the specified value.
	 * 
	 * @param revision
	 *            the corresponding revision of this version to be set
	 */
	public void setRevision(Revision revision) {
		this.revision = revision;
	}

}
