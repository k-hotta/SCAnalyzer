package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.io.File;
import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;

/**
 * This interface represents the protocol of how to get clone classes from the
 * result file reported by a clone detector.<br>
 * 
 * @author k-hotta
 * 
 */
public interface ICloneResultReader {

	/**
	 * Read the given file to get raw clone classes.
	 * 
	 * @param file
	 *            the file in which raw clone classes
	 * @return a collection that has all the raw clone classes reported in the
	 *         given file
	 */
	public Collection<RawCloneClass> read(final File file);

}
