package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class represents a change to an individual file from one version to the
 * next. <br>
 * There are four types of changes that are supported: ADD, DELETE, MODIFY, and
 * RELOCATE.
 * 
 * @author k-hotta
 * 
 */
@DatabaseTable(tableName = "file_changes")
public class FileChange implements IDBElement {

	/**
	 * Enumerates types of file changes
	 * 
	 * @author k-hotta
	 * 
	 */
	public enum Type {
		ADD, DELETE, MODIFY, RELOCATE
	}

	/**
	 * The id of this file change
	 */
	@DatabaseField(id = true)
	private long id;

	/**
	 * The source file before changed, <code>null</code> if the type of this
	 * file change is {@link Type#ADD}
	 */
	@DatabaseField(canBeNull = true, foreign = true)
	private SourceFile oldSourceFile;

	/**
	 * The source file after changed, <code>null</code> if the type of this file
	 * change is {@link Type#DELETE}
	 */
	@DatabaseField(canBeNull = true, foreign = true)
	private SourceFile newSourceFile;

	/**
	 * The type of this file change
	 */
	@DatabaseField(canBeNull = false)
	private Type type;

	/**
	 * The owner version of this file change. This file change occurred between
	 * the owner version and its last (previous) version.
	 */
	@DatabaseField(canBeNull = false, foreign = true)
	private Version version;

	/**
	 * The default constructor
	 */
	public FileChange() {

	}

	/**
	 * The constructor with all values specified
	 * 
	 * @param id
	 *            the id
	 * @param oldSourceFile
	 *            the source file before changed, must be <code>null</code> if
	 *            the type is {@link Type#ADD}, otherwise must not be
	 *            <code>null</code>
	 * @param newSourceFile
	 *            the source file after changed, must be <code>null</code> if
	 *            the type is {@link Type#DELETE}, otherwise must not be
	 *            <code>null</code>
	 * @param type
	 *            the type of this file change
	 * @param version
	 *            the owner version of this file change, that is, this file
	 *            change occurred between the owner version and its last
	 *            (previous) one
	 */
	public FileChange(final long id, final SourceFile oldSourceFile,
			final SourceFile newSourceFile, final Type type,
			final Version version) {
		assert ((type == Type.ADD && oldSourceFile == null && newSourceFile != null)
				|| (type == Type.DELETE && oldSourceFile != null && newSourceFile == null) || (oldSourceFile != null && newSourceFile != null));

		this.id = id;
		this.oldSourceFile = oldSourceFile;
		this.newSourceFile = newSourceFile;
		this.type = type;
		this.version = version;
	}

	/**
	 * Get the id of this file change.
	 * 
	 * @return the id of this file change
	 */
	@Override
	public long getId() {
		return id;
	}

	/**
	 * Set the id of this file change with the specified value.
	 * 
	 * @param id
	 *            the id to be set
	 */
	@Override
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get the source file before changed.
	 * 
	 * @return the source file before changed, the returned value is expected to
	 *         be <code>null</code> if the type of this file change is
	 *         {@link Type#ADD}.
	 */
	public SourceFile getOldSourceFile() {
		return oldSourceFile;
	}

	/**
	 * Set the source file before changed with the specified one.
	 * 
	 * @param oldSourceFile
	 *            the source file before changed to be set
	 */
	public void setOldSourceFile(SourceFile oldSourceFile) {
		this.oldSourceFile = oldSourceFile;
	}

	/**
	 * Get the source file after changed.
	 * 
	 * @return the source file after changed, the returned value is expected to
	 *         be <code>null</code> if the type of this file change is
	 *         {@link Type#DELETE}.
	 */
	public SourceFile getNewSourceFile() {
		return newSourceFile;
	}

	/**
	 * Set the source file after changed with the specified one.
	 * 
	 * @param newSourceFile
	 *            the source file after changed to be set
	 */
	public void setNewSourceFile(SourceFile newSourceFile) {
		this.newSourceFile = newSourceFile;
	}

	/**
	 * Get the type of this file change
	 * 
	 * @return the type of this file change
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Set the type of this file change
	 * 
	 * @param type
	 *            the type to be set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * Get the owner version of this file change
	 * 
	 * @return the owner version of this file change
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * Set the owner version of this file change with the specified one
	 * 
	 * @param version
	 *            the owner version of this file change
	 */
	public void setVersion(Version version) {
		this.version = version;
	}

}
