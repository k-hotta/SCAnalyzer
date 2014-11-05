package jp.ac.osaka_u.ist.sdl.scanalyzer.mapping;

import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;

/**
 * This interface represents the protocol of how to map clone classes between
 * two versions. The only method provided by this interface is
 * {@link ICloneClassMapper#detectMapping(Version, Version)}, which is expected
 * to provide all the mapping of clone classes between the given two versions.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public interface ICloneClassMapper<E extends IProgramElement> {

	/**
	 * Detect mapping of clone classes between the given two versions.
	 * 
	 * @param previousVersion
	 *            the previous version
	 * @param nextVersion
	 *            the next version
	 * @return a collection contains all the detected mapping
	 */
	public Collection<CloneClassMapping<E>> detectMapping(
			final Version<E> previousVersion, final Version<E> nextVersion);

}
