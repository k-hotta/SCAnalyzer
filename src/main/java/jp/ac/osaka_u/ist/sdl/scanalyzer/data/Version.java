package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.Collection;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
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
	@DatabaseField(id = true)
	private long id;

	/**
	 * The corresponding revision
	 */
	@DatabaseField(canBeNull = false, foreign = true)
	private Revision revision;

	/**
	 * The collection of changes on source files between the last version and
	 * this version
	 */
	@ForeignCollectionField(eager = false)
	private Collection<FileChange> fileChanges;

	/**
	 * The collection of raw clone classes in this version
	 */
	@ForeignCollectionField(eager = false)
	private Collection<RawCloneClass> rawCloneClasses;

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
	 * @param fileChanges
	 *            the collection that contains all the changes on source files
	 *            between the last version and this version
	 * @param rawCloneClasses
	 *            the collection that contains all the raw clone classes in this
	 *            version
	 */
	public Version(final long id, final Revision revision,
			final Collection<FileChange> fileChanges,
			final Collection<RawCloneClass> rawCloneClasses) {
		this.id = id;
		this.revision = revision;
		this.fileChanges = fileChanges;
		this.rawCloneClasses = rawCloneClasses;
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

	/**
	 * Get the collection of all the file changes between the last version and
	 * this version.
	 * 
	 * @return the collection of all the file changes between the last version
	 *         and this version
	 */
	public Collection<FileChange> getFileChanges() {
		return fileChanges;
	}

	/**
	 * Set a collection that has all the file changes between the last version
	 * and this version
	 * 
	 * @param fileChanges
	 *            the collection to be set
	 */
	public void setFileChanges(Collection<FileChange> fileChanges) {
		this.fileChanges = fileChanges;
	}

	/**
	 * Get the collection of all the raw clone classes in this version.
	 * 
	 * @return the collection of all the raw clone classes in this version
	 */
	public Collection<RawCloneClass> getRawCloneClasses() {
		return rawCloneClasses;
	}

	/**
	 * Set a collection that has all the raw clone classes in this version
	 * 
	 * @param rawCloneClasses
	 *            the collection to be set
	 */
	public void setRawCloneClasses(Collection<RawCloneClass> rawCloneClasses) {
		this.rawCloneClasses = rawCloneClasses;
	}

}
