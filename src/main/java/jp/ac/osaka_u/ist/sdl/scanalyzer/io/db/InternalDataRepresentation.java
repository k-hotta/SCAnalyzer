package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.IDBElement;

/**
 * This interface describes an internal representation of the data type
 * specified as its type argument. An instance of classes implementing this
 * interface is expected to represent a row of a database table. This interface
 * provides only a single method {@link #getId()} because the ID field is the
 * only column shared among all the database tables.
 * 
 * @author k-hotta
 *
 * @param <D>
 *            the type of data to be represented
 */
interface InternalDataRepresentation<D extends IDBElement> {

	/**
	 * Get the id of the element.
	 * 
	 * @return the id of the element
	 */
	public Long getId();

}
