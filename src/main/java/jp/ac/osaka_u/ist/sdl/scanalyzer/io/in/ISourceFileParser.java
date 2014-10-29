package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.util.Map;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IAtomicElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.Language;

/**
 * This interface describes how to parse each source file. <br>
 * That is, how to convert the string of each source file to a list of elements.
 * 
 * @author k-hotta
 * 
 * @param <E>
 *            the type of elements (e.g. Token)
 */
public interface ISourceFileParser<E extends IAtomicElement> {

	/**
	 * Parse the given string and detect elements in the string.
	 * 
	 * @param language
	 *            the programming language in which the contents are written
	 * @param sourceFile
	 *            the source file to be parsed
	 * @param contents
	 *            the contents of the source file to be parsed
	 * @return a map which contains all the elements in the given string
	 */
	public Map<Integer, E> parse(final Language language,
			final SourceFile sourceFile, final String contents);

}
