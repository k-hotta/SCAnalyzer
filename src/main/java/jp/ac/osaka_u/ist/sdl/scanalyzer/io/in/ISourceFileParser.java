package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.util.Map;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IAtomicElement;

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
	 * @param contentAsStr
	 *            the string to be parsed
	 * @return a map which contains all the elements in the given string
	 */
	public Map<Integer, E> parse(final String contentAsStr);

}
