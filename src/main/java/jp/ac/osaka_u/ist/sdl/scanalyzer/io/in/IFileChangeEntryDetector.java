package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;

/**
 * This interface describes how to detect the entries of
 * added/deleted/modified/relocated files to a specified revision.
 * 
 * @author k-hotta
 * 
 */
public interface IFileChangeEntryDetector {

	/**
	 * Detect the entries of file changes to the given revision from its
	 * previous revision.
	 * <p>
	 * Note: this method should detect file changes <b>TO</b> the given
	 * revision, <b>not from</b> the given revision. In other words, the next
	 * revision will be given. </p>
	 * 
	 * @param revision
	 *            the target revision
	 * @return a collection of file change entries to the given revision from
	 *         its previous revision
	 * @throws Exception
	 *             If any errors occurred
	 */
	public Collection<FileChangeEntry> detectFileChangeEntriesToRevision(
			Revision revision) throws Exception;

}
