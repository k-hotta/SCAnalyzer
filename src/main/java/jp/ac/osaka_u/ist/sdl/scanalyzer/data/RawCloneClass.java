package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.Collection;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * A class that represents <b>raw</b> clone class. <br>
 * Here, the term raw indicates that the data as they are reported by clone
 * detectors. <br>
 * SCAnalyzer has its own another definition of clone classes and cloned
 * fragments. <br>
 * This class is supposed to be used for reading and storing clone information
 * reported by any clone detectors as a pre-process of SCAnalyzer. <br>
 * 
 * @author k-hotta
 * 
 * @see RawClonedFragment
 * 
 */
@DatabaseTable(tableName = "raw_clone_classes")
public class RawCloneClass implements IDBElement {

	/**
	 * The id of this clone class
	 */
	@DatabaseField(id = true, index = true)
	private long id;

	/**
	 * The owner version of this raw clone class
	 */
	@DatabaseField(canBeNull = false, foreign = true)
	private Version version;

	/**
	 * A collection having all the members of this clone class
	 */
	@ForeignCollectionField(eager = false)
	private Collection<RawClonedFragment> elements;

	/**
	 * The default constructor
	 */
	public RawCloneClass() {

	}

	/**
	 * The constructor with all the values specified
	 * 
	 * @param id
	 *            the id of this clone class
	 * @param version
	 *            the owner version of this clone class
	 * @param elements
	 *            the collection that has all the elements of this clone class
	 */
	public RawCloneClass(final long id, final Version version,
			final Collection<RawClonedFragment> elements) {
		this.id = id;
		this.version = version;
		this.elements = elements;
	}

	/**
	 * Get the id of this clone class
	 * 
	 * @return the id of this clone class
	 */
	@Override
	public long getId() {
		return id;
	}

	/**
	 * Set the id of this clone class with the specified value
	 * 
	 * @param id
	 *            the id to be set
	 */
	@Override
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get the owner version of this raw clone class.
	 * 
	 * @return the owner version of this raw clone class
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * Set the owner version of this raw clone class with the specified one
	 * 
	 * @param version
	 *            the owner version of this raw clone class
	 */
	public void setVersion(Version version) {
		this.version = version;
	}

	/**
	 * Get all the elements of this clone class as a collection
	 * 
	 * @return a collection having all the elements of this clone class
	 */
	public Collection<RawClonedFragment> getElements() {
		return elements;
	}

	/**
	 * Set the collection of elements in this clone class
	 * 
	 * @param elements
	 *            the collection having all the elements in this clone class
	 */
	public void setElements(Collection<RawClonedFragment> elements) {
		this.elements = elements;
	}

}
