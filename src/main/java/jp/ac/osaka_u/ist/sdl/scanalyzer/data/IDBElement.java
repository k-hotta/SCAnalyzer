package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

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
	 * @return
	 */
	public Long getId();

	/**
	 * Set the id of the element with the given value
	 * 
	 * @param id
	 */
	public void setId(final Long id);

}
