package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

/**
 * An abstract class represents the base unit of any code fragments. <br>
 * This class implements {@link IDBElement IDBElement} so the objects should be
 * persistent and stored in DB. <br>
 * 
 * @author k-hotta
 * 
 */
public abstract class BaseElement implements IDBElement {

	/**
	 * The id of the element. This is the primary key of the element.
	 */
	protected Long id;

	/**
	 * The default constructor, which is required for this class to be a POJO.
	 */
	public BaseElement() {
		this(null);
	}

	/**
	 * Constructor with ID specified
	 * 
	 * @param id
	 */
	public BaseElement(final Long id) {
		this.id = id;
	}

	/**
	 * Get the id of the element
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Set the id of the element with the specified value
	 */
	public void setId(Long id) {
		this.id = id;
	}

}
