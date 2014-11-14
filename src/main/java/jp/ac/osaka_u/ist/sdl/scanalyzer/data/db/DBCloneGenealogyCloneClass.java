package jp.ac.osaka_u.ist.sdl.scanalyzer.data.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class is just for realizing many-to-many relationship between
 * {@link DBCloneGenealogy} and {@link DBCloneClass}. An instance of this class
 * represents a relationship between an instance of {@link DBCloneGenealogy} and
 * that of {@link DBCloneClass}.
 * 
 * @author k-hotta
 *
 * @see DBCloneGenealogy
 * @see DBCloneClass
 */
@DatabaseTable(tableName = "CLONE_GENEALOGY_CLONE_CLASS")
public class DBCloneGenealogyCloneClass implements IDBElement {

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
	public static final String CLONE_CLASS_COLUMN_NAME = "CLONE_CLASS";

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
	 * The corresponding clone class
	 */
	@DatabaseField(canBeNull = false, foreign = true, columnName = CLONE_CLASS_COLUMN_NAME)
	private DBCloneClass cloneClass;

	/**
	 * The default constructor
	 */
	public DBCloneGenealogyCloneClass() {

	}

	/**
	 * The constructor with all the values specified.
	 * 
	 * @param id
	 *            the id of this relationship
	 * @param cloneGenealogy
	 *            the corresponding clone genealogy
	 * @param cloneClass
	 *            the corresponding clone class
	 */
	public DBCloneGenealogyCloneClass(final long id,
			final DBCloneGenealogy cloneGenealogy, final DBCloneClass cloneClass) {
		this.id = id;
		this.cloneGenealogy = cloneGenealogy;
		this.cloneClass = cloneClass;
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
	 * Get the corresponding clone class.
	 * 
	 * @return the corresponding clone class
	 */
	public DBCloneClass getCloneClass() {
		return cloneClass;
	}

	/**
	 * Set the corresponding clone class with the specified one.
	 * 
	 * @param cloneClass
	 *            the clone class to be set
	 */
	public void setCloneClass(DBCloneClass cloneClass) {
		this.cloneClass = cloneClass;
	}

	/**
	 * Judge whether the given object equals to this object. <br>
	 * 
	 * @return <code>true</code> if the given object is an instance of
	 *         {@link DBCloneGenealogyCloneClass} and the id values of the two
	 *         objects are the same to each other, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DBCloneGenealogyCloneClass)) {
			return false;
		}
		final DBCloneGenealogyCloneClass another = (DBCloneGenealogyCloneClass) obj;

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
