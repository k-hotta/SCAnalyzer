package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.util.Map;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IAtomicElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFileContent;

/**
 * This class is for parsing source file and making instances of
 * {@link SourceFileContent}.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of elements (e.g. Token)
 */
public class SourceFileContentBuilder<E extends IAtomicElement> {

	/**
	 * The parser
	 */
	private final ISourceFileParser<E> parser;

	/**
	 * Construct the instance with the given parser
	 * 
	 * @param parser
	 *            the parser
	 */
	public SourceFileContentBuilder(final ISourceFileParser<E> parser) {
		this.parser = parser;
	}

	/**
	 * Build the instance of {@link SourceFileContent} with the specified
	 * values.
	 * 
	 * @param sourceFile
	 *            the source file
	 * @param contentsStr
	 *            the string representation of the contents of the source file
	 * @return the built instance
	 */
	public SourceFileContent<E> build(final SourceFile sourceFile,
			final String contentsStr) {
		final Map<Integer, E> contents = parser.parse(sourceFile, contentsStr);

		return new SourceFileContent<E>(sourceFile, contents);
	}

}
