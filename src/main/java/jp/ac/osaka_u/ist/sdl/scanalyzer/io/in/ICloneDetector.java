package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;

/**
 * This interface represents the protocol of how to find clones in a specified
 * version.
 * 
 * @author k-hotta
 * 
 * @param <E>
 *            the type of program element
 */
public interface ICloneDetector<E extends IProgramElement> {

	/**
	 * Detect all the clones in the specified version. <br>
	 * The given version should be under construction but it must have at least
	 * the information about the revision and the source files.
	 * 
	 * @param version
	 *            the version to be investigated
	 * @return a collection having all the clone classes in the specified
	 *         version
	 */
	public Collection<RawCloneClass<E>> detectClones(final Version<E> version);

}
