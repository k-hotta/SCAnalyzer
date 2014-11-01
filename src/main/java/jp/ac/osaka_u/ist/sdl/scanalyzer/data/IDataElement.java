package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.IDBElement;

/**
 * This interface describes data element. Each data element has its core, which
 * is an instance of {@link IDBElement}. The data elements have not only
 * persistent data, which are stored in database, but also volatile data. The
 * volatile data mean those cannot be stored in database due to space limits,
 * and they should include the information about source code etc.
 * 
 * @author k-hotta
 *
 * @param <D>
 *            the core database element, which has persist data
 */
public interface IDataElement<D extends IDBElement> {

	/**
	 * Get the id of this element.
	 * 
	 * @return the id of this element, which must be the same to the id of the
	 *         core db element.
	 */
	public long getId();

	/**
	 * Get the core of this element.
	 * 
	 * @return the core of this element
	 */
	public D getCore();

}
