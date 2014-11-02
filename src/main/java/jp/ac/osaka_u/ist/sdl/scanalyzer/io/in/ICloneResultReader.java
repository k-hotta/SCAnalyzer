package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.exception.IllegalCloneResultFileFormatException;

/**
 * This interface represents the protocol of how to get clone classes from the
 * result file reported by a clone detector.<br>
 * 
 * @author k-hotta
 * 
 * @param <E>
 *            the type of program element
 * 
 */
public interface ICloneResultReader<E extends IProgramElement> extends
		ICloneDetector<E> {

	/**
	 * Read the given file to get raw clone classes.
	 * 
	 * @param file
	 *            the file in which raw clone classes
	 * @param version
	 *            the version under consideration
	 * @return a collection that has all the raw clone classes reported in the
	 *         given file
	 * @throws IOException
	 *             If an error occurred in reading the given file
	 * @throws IllegalCloneResultFileFormatException
	 *             If the format of the given file is invalid
	 */
	public Collection<RawCloneClass<E>> read(final File file,
			final Version<E> version) throws IOException,
			IllegalCloneResultFileFormatException;

}
