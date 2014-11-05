package jp.ac.osaka_u.ist.sdl.scanalyzer.mapping;

import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;

/**
 * This interface represents how to detect mapping of code fragments between two
 * consecutive versions.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public interface ICodeFragmentMapper<E extends IProgramElement> {

	/**
	 * Detect mapping of code fragments between the given two versions.
	 * 
	 * @param previousVersion
	 *            the previous version
	 * @param nextVersion
	 *            the next version
	 * @return a collection contains all the detected mapping
	 */
	public Collection<CodeFragmentMapping<E>> detectMapping(
			final Version<E> previousVersion, final Version<E> nextVersion);

}
