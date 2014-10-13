package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.util.Collection;

/**
 * This interface represents how to detect file relocations.
 * 
 * @author k-hotta
 * 
 */
public interface IRelocationFinder {

	/**
	 * Find file relocations in the given set of file change entries.
	 * 
	 * @param fileChangeEntries
	 *            the set having file change entries
	 * @return a set of file change entries after relocations are detected
	 */
	public Collection<FileChangeEntry> fildRelocations(
			final Collection<FileChangeEntry> fileChangeEntries);

}
