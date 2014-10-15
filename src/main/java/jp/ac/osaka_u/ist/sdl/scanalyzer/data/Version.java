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
@DatabaseTable(tableName = "VERSIONS")
public class Version implements IDBElement {

	/**
	 * The column name for id
	 */
	public static final String ID_COLUMN_NAME = "ID";

	/**
	 * The column name for revision
	 */
	public static final String REVISION_COLUMN_NAME = "REVISION";

	/**
	 * The column name for fileChanges
	 */
	public static final String FILE_CHANGES_COLUMN_NAME = "FILE_CHANGES";

	/**
	 * The column name for rawCloneClasses
	 */
	public static final String RAW_CLONE_CLASSES_COLUMN_NAME = "RAW_CLONE_CLASSES";

	/**
	 * The id of the version
	 */
	@DatabaseField(id = true, columnName = ID_COLUMN_NAME)
	private long id;

	/**
	 * The corresponding revision
	 */
	@DatabaseField(canBeNull = false, foreign = true, columnName = REVISION_COLUMN_NAME)
	private Revision revision;

	/**
	 * The collection of changes on source files between the last version and
	 * this version
	 */
	@ForeignCollectionField(eager = false, columnName = FILE_CHANGES_COLUMN_NAME)
	private Collection<FileChange> fileChanges;

	/**
	 * The collection of raw clone classes in this version
	 */
	@ForeignCollectionField(eager = false, columnName = RAW_CLONE_CLASSES_COLUMN_NAME)
	private Collection<RawCloneClass> rawCloneClasses;

	/**
	 * The collection of source files in this version. <br>
	 * NOTE: this field is NOT a column of database table. The relationship
	 * between versions and source files will be stored with
	 * {@link VersionSourceFile} since it is many-to-many.
	 */
	private Collection<SourceFile> sourceFiles;

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
	 * @param sourceFiles
	 *            the collection that contains all the source files in this
	 *            version
	 */
	public Version(final long id, final Revision revision,
			final Collection<FileChange> fileChanges,
			final Collection<RawCloneClass> rawCloneClasses,
			final Collection<SourceFile> sourceFiles) {
		this.id = id;
		this.revision = revision;
		this.fileChanges = fileChanges;
		this.rawCloneClasses = rawCloneClasses;
		this.sourceFiles = sourceFiles;
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

	/**
	 * Get the collection of all the source files in this version.
	 * 
	 * @return the collection of all the source files in this version
	 */
	public Collection<SourceFile> getSourceFiles() {
		return sourceFiles;
	}

	/**
	 * Set a collection that has all the source files in this version
	 * 
	 * @param sourceFiles
	 *            the collection to be set
	 */
	public void setSourceFiles(Collection<SourceFile> sourceFiles) {
		this.sourceFiles = sourceFiles;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();

		builder.append("Version " + id + "\n\n");
		builder.append("revision\n\t" + revision.toString() + "\n\n");

		builder.append("file changes = \n");
		for (final FileChange fileChange : fileChanges) {
			builder.append("\t" + fileChange.toString() + "\n");
		}
		builder.append("\n");

		builder.append("raw clone classes = \n");
		for (final RawCloneClass rawCloneClass : rawCloneClasses) {
			builder.append("\t" + rawCloneClass.toString() + "\n");
		}
		builder.append("\n");

		return builder.toString();
	}

}
