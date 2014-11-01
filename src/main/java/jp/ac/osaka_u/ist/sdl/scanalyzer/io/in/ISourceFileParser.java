package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.util.SortedMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;

/**
 * This interface describes how to parse each source file. <br>
 * That is, how to convert the string of each source file to a list of elements.
 * 
 * @author k-hotta
 * 
 * @param <E>
 *            the type of elements (e.g. Token)
 */
public interface ISourceFileParser<E extends IProgramElement> {

	/**
	 * Parse the given string and detect elements in the string.
	 * 
	 * @param sourceFile
	 *            the source file to be parsed
	 * @param contentsStr
	 *            the contents of the source file to be parsed
	 * @return a map which contains all the elements in the given string
	 */
	public SortedMap<Integer, E> parse(final SourceFile sourceFile,
			final String contentsStr);

}
