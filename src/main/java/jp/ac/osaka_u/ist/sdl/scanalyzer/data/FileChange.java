package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange.Type;

/**
 * This class represents file change.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class FileChange<E extends IProgramElement> implements
		IDataElement<DBFileChange> {

	/**
	 * The id of this file change
	 */
	private final long id;

	/**
	 * The core of this file change
	 */
	private final DBFileChange core;

	/**
	 * The source file before changed
	 */
	private SourceFile<E> oldSourceFile;

	/**
	 * The source file after changed
	 */
	private SourceFile<E> newSourceFile;

	/**
	 * The type of this change
	 */
	private final DBFileChange.Type type;

	public FileChange(final DBFileChange core) {
		this.id = core.getId();
		this.core = core;
		this.oldSourceFile = null;
		this.newSourceFile = null;
		this.type = core.getType();
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public DBFileChange getCore() {
		return core;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof FileChange)) {
			return false;
		}

		final FileChange<?> another = (FileChange<?>) obj;

		return this.core.equals(another.getCore());
	}

	@Override
	public int hashCode() {
		return this.core.hashCode();
	}

	@Override
	public String toString() {
		return this.core.toString();
	}

	/**
	 * Get the old source file of this file change.
	 * 
	 * @return the old source file of this file change
	 * 
	 * @throws IllegalStateException
	 *             if the type of this file change is NOT addition, and the old
	 *             source file is <code>null</code>
	 */
	public SourceFile<E> getOldSourceFile() {
		if (type != Type.ADD && oldSourceFile == null) {
			throw new IllegalStateException(
					"the old source file has not been set");
		}

		return oldSourceFile;
	}

	/**
	 * Set the old source file of this file change with the specified one.
	 * 
	 * @param oldSourceFile
	 *            the old source file to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if one or more of the followings statements hold: (1) the
	 *             given old source file is <code>null</code> but the type of
	 *             the file change is not addition, (2) the old source file of
	 *             the core is not <code>null</code> but the given old source
	 *             file is <code>null</code>, (3) the old source file of the
	 *             core is <code>null</code>, but the given old source file is
	 *             not <code>null</code>, (4) the given old source file doesn't
	 *             match to that in the core
	 */
	public void setOldSourceFile(final SourceFile<E> oldSourceFile) {
		if (type != Type.ADD && oldSourceFile == null) {
			throw new IllegalArgumentException(
					"the given old source file is null though the type of change is not addition");
		}

		if (this.core.getOldSourceFile() != null) {
			if (oldSourceFile == null) {
				throw new IllegalArgumentException(
						"the given old source file is null, but that in the core is not");
			} else if (!this.core.getOldSourceFile().equals(
					oldSourceFile.getCore())) {
				throw new IllegalArgumentException(
						"the given old source file doesn't match to that in the core");
			}
		} else if (oldSourceFile != null) {
			throw new IllegalArgumentException(
					"the given old source file is not null, but that in the core is null");
		}

		this.oldSourceFile = oldSourceFile;
	}

	/**
	 * Get the new source file of this file change.
	 * 
	 * @return the new source file of this file change
	 * 
	 * @throws IllegalStateException
	 *             if the type of this file change is NOT deletion and the new
	 *             source file is <code>null</code>, or the type of this file
	 *             change is deletion and the new source file is NOT
	 *             <code>null</code>
	 */
	public SourceFile<E> getNewSourceFile() {
		if (type != Type.DELETE && newSourceFile == null) {
			throw new IllegalStateException(
					"the new source file has not been set");
		}

		if (type == Type.DELETE && newSourceFile != null) {
			throw new IllegalStateException(
					"the new source file is not null even though the type of the change is deletion");
		}

		return newSourceFile;
	}

	/**
	 * Set the new source file of this file change with the specified one.
	 * 
	 * @param newSourceFile
	 *            the new source file to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if one or more of the followings statements hold: (1) the
	 *             given new source file is <code>null</code> but the type of
	 *             the file change is not deletion, (2) the new source file of
	 *             the core is not <code>null</code> but the given new source
	 *             file is <code>null</code>, (3) the new source file of the
	 *             core is <code>null</code>, but the given new source file is
	 *             not <code>null</code>, (4) the given new source file doesn't
	 *             match to that in the core
	 */
	public void setNewSourceFile(final SourceFile<E> newSourceFile) {
		if (type != Type.DELETE && newSourceFile == null) {
			throw new IllegalArgumentException(
					"the given new source file is null though the type of change is not deletion");
		}

		if (this.core.getNewSourceFile() != null) {
			if (newSourceFile == null) {
				throw new IllegalArgumentException(
						"the given new source file is null, but that in the core is not");
			} else if (!this.core.getNewSourceFile().equals(
					newSourceFile.getCore())) {
				throw new IllegalArgumentException(
						"the given new source file doesn't match to that in the core");
			}
		} else if (newSourceFile != null) {
			throw new IllegalArgumentException(
					"the given new source file is not null, but that in the core is null");
		}

		this.newSourceFile = newSourceFile;
	}

	/**
	 * Get the type of this file change.
	 * 
	 * @return the type of this file change
	 */
	public Type getType() {
		return type;
	}

}
