package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class is just for realizing many-to-many relationship between
 * {@link Version} and {@link SourceFile}. An instance of this class represents
 * a relationship between an instance of {@link Version} and an instance of
 * {@link SourceFile}.
 * 
 * @author k-hotta
 * @see Version
 * @see SourceFile
 */
@DatabaseTable(tableName = "version_source_file")
public class VersionSourceFile {

	/**
	 * The id of this relationship
	 */
	@DatabaseField(id = true, index = true)
	private long id;

	/**
	 * The corresponding version
	 */
	@DatabaseField(canBeNull = false, foreign = true)
	private Version version;

	/**
	 * The corresponding source file
	 */
	@DatabaseField(canBeNull = false, foreign = true)
	private SourceFile sourceFile;

	/**
	 * The default constructor for ORMLite.
	 */
	public VersionSourceFile() {

	}

	/**
	 * The constructor with all the values specified
	 * 
	 * @param id
	 *            the id of this relationship
	 * @param version
	 *            the corresponding version
	 * @param sourceFile
	 *            the corresponding source file
	 */
	public VersionSourceFile(final long id, final Version version,
			final SourceFile sourceFile) {
		this.id = id;
		this.version = version;
		this.sourceFile = sourceFile;
	}

	/**
	 * Get the id of this relationship.
	 * 
	 * @return the id of this relationship
	 */
	public long getId() {
		return id;
	}

	/**
	 * Set the id of this relationship with the specified value.
	 * 
	 * @param id
	 *            the id of this relationship to be set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get the corresponding version.
	 * 
	 * @return the corresponding version of this relationship
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * Set the corresponding version with the specified one.
	 * 
	 * @param version
	 *            the corresponding version to be set
	 */
	public void setVersion(Version version) {
		this.version = version;
	}

	/**
	 * Get the corresponding source file.
	 * 
	 * @return the corresponding source file of this relationship
	 */
	public SourceFile getSourceFile() {
		return sourceFile;
	}

	/**
	 * Set the corresponding source file with the specified one.
	 * 
	 * @param sourceFile
	 *            the corresponding source file to be set
	 */
	public void setSourceFile(SourceFile sourceFile) {
		this.sourceFile = sourceFile;
	}

}
