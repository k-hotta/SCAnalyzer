package jp.ac.osaka_u.ist.sdl.scanalyzer.data.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class is just for realizing many-to-many relationship between
 * {@link DBCloneGenealogy} and {@link DBCloneClassMapping}. An instance of this
 * class represents a relationship between an instance of
 * {@link DBCloneGenealogy} and that of {@link DBCloneClassMapping}.
 * 
 * @author k-hotta
 *
 * @see DBCloneGenealogy
 * @see DBCloneClassMapping
 */
@DatabaseTable(tableName = TableName.CLONE_GENEALOGY_CLONE_CLASS)
public class DBCloneGenealogyCloneClassMapping implements IDBElement {

	/**
	 * The column name for id
	 */
	public static final String ID_COLUMN_NAME = "ID";

	/**
	 * The column name for cloneGenealogy
	 */
	public static final String CLONE_GENEALOGY_COLUMN_NAME = "CLONE_GENEALOGY";

	/**
	 * The column name for cloneClass
	 */
	public static final String CLONE_CLASS_MAPPING_COLUMN_NAME = "CLONE_CLASS_MAPPING";

	/**
	 * The id of this relationship
	 */
	@DatabaseField(id = true, columnName = ID_COLUMN_NAME)
	private long id;

	/**
	 * The corresponding clone genealogy
	 */
	@DatabaseField(canBeNull = false, foreign = true, columnName = CLONE_GENEALOGY_COLUMN_NAME)
	private DBCloneGenealogy cloneGenealogy;

	/**
	 * The corresponding clone class mapping
	 */
	@DatabaseField(canBeNull = false, foreign = true, columnName = CLONE_CLASS_MAPPING_COLUMN_NAME)
	private DBCloneClassMapping cloneClassMapping;

	/**
	 * The default constructor
	 */
	public DBCloneGenealogyCloneClassMapping() {

	}

	/**
	 * The constructor with all the values specified.
	 * 
	 * @param id
	 *            the id of this relationship
	 * @param cloneGenealogy
	 *            the corresponding clone genealogy
	 * @param cloneClassMapping
	 *            the corresponding clone class mapping
	 */
	public DBCloneGenealogyCloneClassMapping(final long id,
			final DBCloneGenealogy cloneGenealogy,
			final DBCloneClassMapping cloneClassMapping) {
		this.id = id;
		this.cloneGenealogy = cloneGenealogy;
		this.cloneClassMapping = cloneClassMapping;
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
	 * Get the corresponding clone genealogy.
	 * 
	 * @return the corresponding clone genealogy
	 */
	public DBCloneGenealogy getCloneGenealogy() {
		return cloneGenealogy;
	}

	/**
	 * Set the corresponding clone genealogy with the specified one.
	 * 
	 * @param cloneGenealogy
	 *            the clone genealogy to be set
	 */
	public void setCloneGenealogy(DBCloneGenealogy cloneGenealogy) {
		this.cloneGenealogy = cloneGenealogy;
	}

	/**
	 * Get the corresponding clone class mapping.
	 * 
	 * @return the corresponding clone class mapping
	 */
	public DBCloneClassMapping getCloneClassMapping() {
		return cloneClassMapping;
	}

	/**
	 * Set the corresponding clone class mapping with the specified one.
	 * 
	 * @param cloneClassMapping
	 *            the clone class to be set
	 */
	public void setCloneClassMapping(DBCloneClassMapping cloneClassMapping) {
		this.cloneClassMapping = cloneClassMapping;
	}

	/**
	 * Judge whether the given object equals to this object. <br>
	 * 
	 * @return <code>true</code> if the given object is an instance of
	 *         {@link DBCloneGenealogyCloneClassMapping} and the id values of
	 *         the two objects are the same to each other, <code>false</code>
	 *         otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DBCloneGenealogyCloneClassMapping)) {
			return false;
		}
		final DBCloneGenealogyCloneClassMapping another = (DBCloneGenealogyCloneClassMapping) obj;

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

}
