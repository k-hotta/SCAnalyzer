package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;

/**
 * This interface represents the protocol of how versions are provided. <br>
 * 
 * @author k-hotta
 * 
 */
public interface IVersionProvider {

	/**
	 * Get the next version of the given version.
	 * 
	 * @param currentVersion
	 *            the current version
	 * @return the next version of the given current veresion
	 */
	public Version getNextVersion(final Version currentVersion);

}
