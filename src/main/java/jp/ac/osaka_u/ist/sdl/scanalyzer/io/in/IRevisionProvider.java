package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;

/**
 * This class represents how to provide the next revision of for each given
 * revision.
 * 
 * @author k-hotta
 * 
 */
public interface IRevisionProvider {

	/**
	 * Provide the next revision of the given current revision.
	 * 
	 * @param currentRevision
	 *            the current revision
	 * @return the next revision if detected, <code>null</code> if failed to
	 *         detect
	 */
	public Revision getNextRevision(Revision currentRevision);

	/**
	 * Provide the first revision to be analyzed.
	 * 
	 * @return the first revision to be analyzed if detected, <code>null</code>
	 *         if failed to detect
	 */
	public Revision getFirstRevision();

}
