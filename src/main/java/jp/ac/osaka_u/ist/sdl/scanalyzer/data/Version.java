package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;

/**
 * This class represents version.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class Version<E extends IProgramElement> implements
		IDataElement<DBVersion> {

	/**
	 * The id of this version
	 */
	private final long id;

	/**
	 * The core of this version
	 */
	private final DBVersion core;

	/**
	 * The corresponding revision
	 */
	private Revision revision;

	/**
	 * The file changes on source files between the last version and this
	 * version
	 */
	private final SortedMap<Long, FileChange<E>> fileChanges;

	/**
	 * The raw clone classes in this version
	 */
	private final SortedMap<Long, RawCloneClass<E>> rawCloneClasses;

	/**
	 * The clone classes in this version
	 */
	private final SortedMap<Long, CloneClass<E>> cloneClasses;

	/**
	 * The source files in this version
	 */
	private final SortedMap<Long, SourceFile<E>> sourceFiles;

	/**
	 * The mappings of clone classes in this version
	 */
	private final SortedMap<Long, CloneClassMapping<E>> cloneClassMappings;

	/**
	 * The constructor with core
	 * 
	 * @param core
	 *            the core
	 */
	public Version(final DBVersion core) {
		this.id = core.getId();
		this.core = core;
		this.revision = null;
		this.fileChanges = new TreeMap<>();
		this.rawCloneClasses = new TreeMap<>();
		this.cloneClasses = new TreeMap<>();
		this.sourceFiles = new TreeMap<>();
		this.cloneClassMappings = new TreeMap<>();
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public DBVersion getCore() {
		return core;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Version)) {
			return false;
		}

		final Version<?> another = (Version<?>) obj;

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
	 * Get the corresponding revision of this version.
	 * 
	 * @return the corresponding revision of this version
	 * 
	 * @throws IllegalStateException
	 *             if the revision has not been set
	 */
	public Revision getRevision() {
		if (revision == null) {
			throw new IllegalStateException("the revision has not been set");
		}
		return revision;
	}

	/**
	 * Set the corresponding revision of this version with the specified one.
	 * The core of the given revision must match to that in the core of this
	 * version.
	 * 
	 * @param revision
	 *            the revision to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if the given revision doesn't match to that in the core of
	 *             this version, or the given revision is <code>null</code>
	 */
	public void setRevision(final Revision revision) {
		if (revision == null) {
			throw new IllegalArgumentException("the given revision is null");
		}

		if (!this.core.getRevision().equals(revision.getCore())) {
			throw new IllegalArgumentException(
					"the given revision doesn't match to that in the core");
		}

		this.revision = revision;
	}

	/**
	 * Get the file changes between the last version and this version as an
	 * unmodifiable map.
	 * 
	 * @return the map of file changes, each of whose key is the id of a file
	 *         change, each of whose value is the file change
	 */
	public SortedMap<Long, FileChange<E>> getFileChanges() {
		return Collections.unmodifiableSortedMap(fileChanges);
	}

	/**
	 * Add the given file change to this version.
	 * 
	 * @param fileChange
	 *            the file change to be added
	 * @throws IllegalArgumentException
	 *             if the given file change is not included in the file changes
	 *             in the core, or the given file change is <code>null</code>
	 */
	public void addFileChange(final FileChange<E> fileChange) {
		if (fileChange == null) {
			throw new IllegalArgumentException("the given file change is null");
		}

		if (!this.core.getFileChanges().contains(fileChange.getCore())) {
			throw new IllegalArgumentException(
					"the given file change is not include in those in the core");
		}

		this.fileChanges.put(fileChange.getId(), fileChange);
	}

	/**
	 * Get the raw clone classes in this version as an unmodifiable map.
	 * 
	 * @return the map of raw clone classes, each of whose key is the id of a
	 *         raw clone class, each of whose value is the raw clone class
	 */
	public SortedMap<Long, RawCloneClass<E>> getRawCloneClasses() {
		return Collections.unmodifiableSortedMap(rawCloneClasses);
	}

	/**
	 * Add the given raw clone class to this version.
	 * 
	 * @param rawCloneClass
	 *            the raw clone class to be added
	 * @throws IllegalArgumentException
	 *             if the given raw clone class is not included in the raw clone
	 *             classes in the core, or the given raw clone class is
	 *             <code>null</code>
	 */
	public void addRawCloneClass(final RawCloneClass<E> rawCloneClass) {
		if (rawCloneClass == null) {
			throw new IllegalArgumentException(
					"the given raw clone class is null");
		}

		if (!this.core.getRawCloneClasses().contains(rawCloneClass.getCore())) {
			throw new IllegalArgumentException(
					"the given raw clone class is not included in those in the core");
		}

		this.rawCloneClasses.put(rawCloneClass.getId(), rawCloneClass);
	}

	/**
	 * Get the clone classes in this version as an unmodifiable map.
	 * 
	 * @return the map of clone classes, each of whose key is the id of a clone
	 *         class, each of whose value is the clone class
	 */
	public SortedMap<Long, CloneClass<E>> getCloneClasses() {
		return Collections.unmodifiableSortedMap(cloneClasses);
	}

	/**
	 * Add the given clone class to this version.
	 * 
	 * @param cloneClass
	 *            the clone class to be added
	 * @throws IllegalArgumentException
	 *             if the given clone class is not included in the clone classes
	 *             in the core, or the given clone class is <code>null</code>
	 */
	public void addCloneClass(final CloneClass<E> cloneClass) {
		if (cloneClass == null) {
			throw new IllegalArgumentException("the given clone class is null");
		}

		if (!this.core.getCloneClasses().contains(cloneClass.getCore())) {
			throw new IllegalArgumentException(
					"the given clone class is not included in those in the core");
		}

		this.cloneClasses.put(cloneClass.getId(), cloneClass);
	}

	/**
	 * Get the source files in this version as an unmodifiable map.
	 * 
	 * @return the map of source files, each of whose key is the id of a source
	 *         file, each of whose value is the source file
	 */
	public SortedMap<Long, SourceFile<E>> getSourceFiles() {
		return Collections.unmodifiableSortedMap(sourceFiles);
	}

	/**
	 * Add the given source file to this version.
	 * 
	 * @param sourceFile
	 *            the source file to be added
	 * @throws IllegalArgumentException
	 *             if the given source file is not included in the source files
	 *             in the core, or the given source file is <code>null</code>
	 */
	public void addSourceFile(final SourceFile<E> sourceFile) {
		if (sourceFile == null) {
			throw new IllegalArgumentException("the given source file is null");
		}

		if (!this.core.getSourceFiles().contains(sourceFile.getCore())) {
			throw new IllegalArgumentException(
					"the given source file is not included in those in the core");
		}

		this.sourceFiles.put(sourceFile.getId(), sourceFile);
	}

	/**
	 * Get the mappings of clone classes in this version as an unmodifiable map.
	 * 
	 * @return the map of clone class mappings, each of whose key is the id of a
	 *         mapping, each of whose value is the mapping
	 */
	public SortedMap<Long, CloneClassMapping<E>> getCloneClassMappings() {
		return Collections.unmodifiableSortedMap(cloneClassMappings);
	}

	/**
	 * Add the given clone class mapping to this version.
	 * 
	 * @param cloneClassMapping
	 *            the clone class mapping to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if the given clone class mapping is not included in the clone
	 *             class mappings in the core, or the given clone class mapping
	 *             is <code>null</code>
	 */
	public void addCloneClassMapping(
			final CloneClassMapping<E> cloneClassMapping) {
		if (cloneClassMapping == null) {
			throw new IllegalArgumentException(
					"the given clone class mapping is null");
		}

		if (!this.core.getCloneClassMappings().contains(
				cloneClassMapping.getCore())) {
			throw new IllegalArgumentException(
					"the given clone class mapping is not included in those in the core");
		}

		this.cloneClassMappings.put(cloneClassMapping.getId(),
				cloneClassMapping);
	}

}
