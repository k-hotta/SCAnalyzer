package jp.ac.osaka_u.ist.sdl.scanalyzer.mapping;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;

/**
 * This interface provides the protocol of how to map program elements between
 * two consecutive versions. This interface provides two methods,
 * {@link IProgramElementMapper#getNext(IProgramElement)} and
 * {@link IProgramElementMapper#clear()}. The mapping of program elements is
 * flexible for code move between files. That is, it is allowed for the mapping
 * algorithm to map two program elements that are in different source files.
 * 
 * @author k-hotta
 * 
 * @param <E>
 *            the type of program element under consideration
 *
 * @see jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version
 * @see jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement
 */
public interface IProgramElementMapper<E extends IProgramElement> {

	/**
	 * Get the corresponding element in the next version to the given element
	 * which is in the previous version.
	 * 
	 * @param previous
	 *            an element in the previous version
	 * @return an element in the next version that is mapped to the given
	 *         element, <code>null</code> if no such an element exists
	 */
	public E getNext(final E previous);

	/**
	 * Clear the mapper. This method is used to make the mapper reusable in
	 * processing another pair of consecutive versions. It is expected that this
	 * method clears all the internal data in the mapper, which is used in
	 * processing a pair of consecutive versions.
	 */
	public void clear();

}
