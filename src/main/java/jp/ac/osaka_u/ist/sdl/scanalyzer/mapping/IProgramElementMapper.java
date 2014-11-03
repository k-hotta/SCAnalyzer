package jp.ac.osaka_u.ist.sdl.scanalyzer.mapping;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;

/**
 * This interface provides the protocol of how to map program elements between
 * two consecutive versions. This interface provides two methods,
 * {@link IProgramElementMapper#getNext(IProgramElement)} and
 * {@link IProgramElementMapper#prepare(Version, Version)}. The mapping of
 * program elements is flexible for code move between files. That is, it is
 * allowed for the mapping algorithm to map two program elements that are in
 * different source files.
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
	 * Set up the mapper to provide mapping of program elements between the two
	 * given versions.
	 * 
	 * @param previousVersion
	 *            the previous version
	 * @param nextVersion
	 *            the next version
	 * @return <code>true</code> if the mapper has been successfully prepared,
	 *         <code>false</code> otherwise.
	 */
	public boolean prepare(final Version<E> previousVersion,
			final Version<E> nextVersion);

}
