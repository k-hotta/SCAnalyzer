package jp.ac.osaka_u.ist.sdl.scanalyzer.data.db;

import java.util.Collection;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class represents a clone genealogy.
 * 
 * @author k-hotta
 *
 */
@DatabaseTable(tableName = "CLONE_GENEALOGIES")
public class DBCloneGenealogy implements IDBElement {

	/**
	 * The column name for id
	 */
	public static final String ID_COLUMN_NAME = "ID";

	/**
	 * The column name for startVersion
	 */
	public static final String START_VERSION_COLUMN_NAME = "START_VERSION";

	/**
	 * The column name for endVersion
	 */
	public static final String END_VERSION_COLUMN_NAME = "END_VERSION";

	/**
	 * The id of this genealogy
	 */
	@DatabaseField(id = true, columnName = ID_COLUMN_NAME)
	private long id;

	/**
	 * The start version of this genealogy
	 */
	@DatabaseField(canBeNull = false, foreign = true, columnName = START_VERSION_COLUMN_NAME)
	private DBVersion startVersion;

	/**
	 * The end version of this genealogy
	 */
	@DatabaseField(canBeNull = false, foreign = true, columnName = END_VERSION_COLUMN_NAME)
	private DBVersion endVersion;

	/**
	 * The clone classes. <br>
	 * NOTE: this field is NOT a column of database table. The relationship
	 * between genealogies and clone classes will be stored via
	 * {@link DBCloneGenealogyCloneClass} since it is many-to-many.
	 */
	private Collection<DBCloneClass> cloneClasses;

	/**
	 * The mappings of clone classes. <br>
	 * NOTE: this field is NOT a column of database table. The relationship
	 * between genealogies and clone class mappings will be stored via
	 * {@link DBCloneGenealogyCloneClassMapping} since it is many-to-many.
	 */
	private Collection<DBCloneClassMapping> cloneClassMappings;

	/**
	 * The default constructor
	 */
	public DBCloneGenealogy() {

	}

	/**
	 * The constructor with all the values specified.
	 * 
	 * @param id
	 *            the id of this genealogy
	 * @param startVersion
	 *            the start version of this genealogy
	 * @param endVersion
	 *            the end version of this genealogy
	 * @param cloneClasses
	 *            the clone classes included in this genealogy
	 * @param cloneClassMappings
	 *            the clone class mappings related to this genealogy
	 */
	public DBCloneGenealogy(final long id, final DBVersion startVersion,
			final DBVersion endVersion,
			final Collection<DBCloneClass> cloneClasses,
			final Collection<DBCloneClassMapping> cloneClassMappings) {
		this.id = id;
		this.startVersion = startVersion;
		this.endVersion = endVersion;
		this.cloneClasses = cloneClasses;
		this.cloneClassMappings = cloneClassMappings;
	}

	/**
	 * Get the id of this genealogy
	 * 
	 * @return the id of this genealogy
	 */
	@Override
	public long getId() {
		return id;
	}

	/**
	 * Set the id of this genealogy with the specified value.
	 * 
	 * @param id
	 *            the id to be set
	 */
	@Override
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get the start version of this genealogy.
	 * 
	 * @return the start version of this genealogy
	 */
	public DBVersion getStartVersion() {
		return startVersion;
	}

	/**
	 * Set the start version of this genealogy with the specified one.
	 * 
	 * @param startVersion
	 *            the start version to be set
	 */
	public void setStartVersion(final DBVersion startVersion) {
		this.startVersion = startVersion;
	}

	/**
	 * Get the end version of this genealogy.
	 * 
	 * @return the end version of this genealogy
	 */
	public DBVersion getEndVersion() {
		return endVersion;
	}

	/**
	 * Set the end version of this genealogy.
	 * 
	 * @param endVersion
	 *            the end version to be set
	 */
	public void setEndVersion(final DBVersion endVersion) {
		this.endVersion = endVersion;
	}

	/**
	 * Get the clone classes in this genealogy.
	 * 
	 * @return the clone classes in this genealogy
	 */
	public Collection<DBCloneClass> getCloneClasses() {
		return cloneClasses;
	}

	/**
	 * Set the clone classes in this genealogy.
	 * 
	 * @param cloneClasses
	 *            the clone classes to be set
	 */
	public void setCloneClasses(final Collection<DBCloneClass> cloneClasses) {
		this.cloneClasses = cloneClasses;
	}

	/**
	 * Get the clone class mappings in this genealogy.
	 * 
	 * @return the clone class mappings in this genealogy
	 */
	public Collection<DBCloneClassMapping> getCloneClassMappings() {
		return cloneClassMappings;
	}

	/**
	 * Set the clone class mappings in this genealogy.
	 * 
	 * @param cloneClassMappings
	 *            the clone class mappings to be set
	 */
	public void setCloneClassMappings(
			final Collection<DBCloneClassMapping> cloneClassMappings) {
		this.cloneClassMappings = cloneClassMappings;
	}

}
