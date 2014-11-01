package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.util.SortedMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFileWithContent;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;

/**
 * This class is for parsing source file and making instances of
 * {@link SourceFileWithContent}.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of elements (e.g. Token)
 */
public class SourceFileContentBuilder<E extends IProgramElement> {

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
	 * Build the instance of {@link SourceFileWithContent} with the specified
	 * values.
	 * 
	 * @param sourceFile
	 *            the source file
	 * @param contentsStr
	 *            the string representation of the contents of the source file
	 * @return the built instance
	 */
	public SourceFileWithContent<E> build(final DBSourceFile sourceFile,
			final String contentsStr) {
		final SortedMap<Integer, E> contents = parser.parse(sourceFile, contentsStr);

		return new SourceFileWithContent<E>(sourceFile, contents);
	}

}
