package jp.ac.osaka_u.ist.sdl.scanalyzer.data.db;

/**
 * This interface represents any elements that should be stored in DB. <br>
 * That is, any objects of classes implementing this interface will be stored in
 * DB. <br>
 * 
 * @author k-hotta
 * 
 */
public interface IDBElement {

	/**
	 * Get the id of the element
	 * 
	 * @return the value of id
	 */
	public long getId();

	/**
	 * Set the id of the element with the given value
	 * 
	 * @param id
	 *            the value of id to be set
	 */
	public void setId(final long id);

}
