package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDataElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.IDBElement;

/**
 * This class represents the protocol of how to retrieve information of objects.
 * 
 * @author k-hotta
 *
 * @param E
 *            the type of program element
 */
public interface IRetriever<E extends IProgramElement, D extends IDBElement, T extends IDataElement<D>> {

	/**
	 * Retrieve the given database element.
	 * 
	 * @param dbElement
	 *            the database element, which will be the core of the retrieved
	 *            element
	 * @return the retrieved element
	 */
	public T retrieveElement(final D dbElement);

}
