package jp.ac.osaka_u.ist.sdl.scanalyzer.data.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
public class DBVersion implements IDBElement {

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
	 * The column name for cloneClasses
	 */
	public static final String CLONE_CLASSES_COLUMN_NAME = "CLONE_CLASSES";

	/**
	 * The column name for cloneClassMappings
	 */
	private static final String CLONE_CLASS_MAPPINGS_COLUMN_NAME = "CLONE_CLASS_MAPPINGS";

	/**
	 * The id of the version
	 */
	@DatabaseField(id = true, columnName = ID_COLUMN_NAME)
	private long id;

	/**
	 * The corresponding revision
	 */
	@DatabaseField(canBeNull = false, foreign = true, columnName = REVISION_COLUMN_NAME)
	private DBRevision revision;

	/**
	 * The collection of changes on source files between the last version and
	 * this version
	 */
	@ForeignCollectionField(eager = true, columnName = FILE_CHANGES_COLUMN_NAME)
	private Collection<DBFileChange> fileChanges;

	/**
	 * The collection of raw clone classes in this version
	 */
	@ForeignCollectionField(eager = true, columnName = RAW_CLONE_CLASSES_COLUMN_NAME)
	private Collection<DBRawCloneClass> rawCloneClasses;

	/**
	 * The collection of clone classes in this version
	 */
	@ForeignCollectionField(eager = true, columnName = CLONE_CLASSES_COLUMN_NAME)
	private Collection<DBCloneClass> cloneClasses;

	/**
	 * The collection of clone class mappings in this version
	 */
	@ForeignCollectionField(eager = true, columnName = CLONE_CLASS_MAPPINGS_COLUMN_NAME)
	private Collection<DBCloneClassMapping> cloneClassMappings;

	/**
	 * The collection of source files in this version. <br>
	 * NOTE: this field is NOT a column of database table. The relationship
	 * between versions and source files will be stored with
	 * {@link DBVersionSourceFile} since it is many-to-many.
	 */
	private Collection<DBSourceFile> sourceFiles;

	/**
	 * The default constructor
	 */
	public DBVersion() {

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
	 * @param cloneClasses
	 *            the collection that contains all the clone classes in this
	 *            version
	 * @param sourceFiles
	 *            the collection that contains all the source files in this
	 *            version
	 * @param cloneClassMappings
	 *            the collection that contains all the clone class mappings in
	 *            this version
	 */
	public DBVersion(final long id, final DBRevision revision,
			final Collection<DBFileChange> fileChanges,
			final Collection<DBRawCloneClass> rawCloneClasses,
			final Collection<DBCloneClass> cloneClasses,
			final Collection<DBSourceFile> sourceFiles,
			final Collection<DBCloneClassMapping> cloneClassMappings) {
		this.id = id;
		this.revision = revision;
		this.fileChanges = fileChanges;
		this.rawCloneClasses = rawCloneClasses;
		this.cloneClasses = cloneClasses;
		this.sourceFiles = sourceFiles;
		this.cloneClassMappings = cloneClassMappings;
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
	public DBRevision getRevision() {
		return revision;
	}

	/**
	 * Set the corresponding revision of this version with the specified value.
	 * 
	 * @param revision
	 *            the corresponding revision of this version to be set
	 */
	public void setRevision(DBRevision revision) {
		this.revision = revision;
	}

	/**
	 * Get the collection of all the file changes between the last version and
	 * this version.
	 * 
	 * @return the collection of all the file changes between the last version
	 *         and this version
	 */
	public Collection<DBFileChange> getFileChanges() {
		return fileChanges;
	}

	/**
	 * Set a collection that has all the file changes between the last version
	 * and this version
	 * 
	 * @param fileChanges
	 *            the collection to be set
	 */
	public void setFileChanges(Collection<DBFileChange> fileChanges) {
		this.fileChanges = fileChanges;
	}

	/**
	 * Get the collection of all the raw clone classes in this version.
	 * 
	 * @return the collection of all the raw clone classes in this version
	 */
	public Collection<DBRawCloneClass> getRawCloneClasses() {
		return rawCloneClasses;
	}

	/**
	 * Set a collection that has all the raw clone classes in this version
	 * 
	 * @param rawCloneClasses
	 *            the collection to be set
	 */
	public void setRawCloneClasses(Collection<DBRawCloneClass> rawCloneClasses) {
		this.rawCloneClasses = rawCloneClasses;
	}

	/**
	 * Get the collection of all the clone classes in this version.
	 * 
	 * @return the collection of all the clone classes
	 */
	public Collection<DBCloneClass> getCloneClasses() {
		return cloneClasses;
	}

	/**
	 * Set a collection that has all the clone classes in this version.
	 * 
	 * @param cloneClasses
	 *            the collection to be set
	 */
	public void setCloneClasses(Collection<DBCloneClass> cloneClasses) {
		this.cloneClasses = cloneClasses;
	}

	/**
	 * Get the collection of all the source files in this version.
	 * 
	 * @return the collection of all the source files in this version
	 */
	public Collection<DBSourceFile> getSourceFiles() {
		return sourceFiles;
	}

	/**
	 * Set a collection that has all the source files in this version
	 * 
	 * @param sourceFiles
	 *            the collection to be set
	 */
	public void setSourceFiles(Collection<DBSourceFile> sourceFiles) {
		this.sourceFiles = sourceFiles;
	}

	/**
	 * Get the collection of all the clone class mappings in this version.
	 * 
	 * @return the collection of all the clone class mappings in this version
	 */
	public Collection<DBCloneClassMapping> getCloneClassMappings() {
		return cloneClassMappings;
	}

	/**
	 * Set a collection that has all the clone class mappings in this version
	 * 
	 * @param cloneClassMappings
	 *            the collection to be set
	 */
	public void setCloneClassMappings(
			final Collection<DBCloneClassMapping> cloneClassMappings) {
		this.cloneClassMappings = cloneClassMappings;
	}

	/**
	 * Judge whether the given object equals to this object. <br>
	 * 
	 * @return <code>true</code> if the given object is an instance of
	 *         {@link DBVersion} and the id values of the two objects are the
	 *         same to each other, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DBVersion)) {
			return false;
		}
		final DBVersion another = (DBVersion) obj;

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
		final StringBuilder builder = new StringBuilder();

		builder.append("Version " + id + "\n\n");
		builder.append("revision\n\t" + revision.toString() + "\n\n");

		builder.append("source files\n");
		final List<DBSourceFile> sortedSourceFile = new ArrayList<DBSourceFile>();
		sortedSourceFile.addAll(sourceFiles);
		Collections.sort(sortedSourceFile, new DBElementComparator());
		for (final DBSourceFile sourceFile : sortedSourceFile) {
			builder.append("\t" + sourceFile.toString() + "\n");
		}
		builder.append("\n");

		builder.append("file changes\n");
		final List<DBFileChange> sortedFileChanges = new ArrayList<DBFileChange>();
		sortedFileChanges.addAll(fileChanges);
		Collections.sort(sortedFileChanges, new DBElementComparator());
		for (final DBFileChange fileChange : sortedFileChanges) {
			builder.append("\t" + fileChange.toString() + "\n");
		}
		builder.append("\n");

		builder.append("raw clone classes\n");
		final List<DBRawCloneClass> sortedRawCloneClasses = new ArrayList<DBRawCloneClass>();
		sortedRawCloneClasses.addAll(rawCloneClasses);
		Collections.sort(sortedRawCloneClasses, new DBElementComparator());
		for (final DBRawCloneClass rawCloneClass : sortedRawCloneClasses) {
			builder.append("\t" + rawCloneClass.toString() + "\n");
		}
		builder.append("\n");

		return builder.toString();
	}

}
