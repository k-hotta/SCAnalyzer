package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.IFileContentProvider;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.ISourceFileParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is a retriever for {@link SourceFile} with volatile information. Note
 * that it is impossible to retrieve file contents only from an instance of
 * {@link DBSourceFile} because it has no information about in which version the
 * file exists. Hence {@link this#retrieveElement(DBSourceFile)} does not work.
 * This class therefore provides another method {@link
 * this#retrieveElement(DBSourceFile, DBVersion)} to completely retrieve file
 * contents.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class VolatileSourceFileRetriever<E extends IProgramElement> implements
		IRetriever<E, DBSourceFile, SourceFile<E>> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(VolatileSourceFileRetriever.class);

	/**
	 * The manager for retrieved objects
	 */
	private final RetrievedObjectManager<E> manager;

	/**
	 * The file content provider
	 */
	private final IFileContentProvider<E> fileContentProvider;

	/**
	 * 
	 */
	private final ISourceFileParser<E> parser;

	public VolatileSourceFileRetriever(final RetrievedObjectManager<E> manager,
			final IFileContentProvider<E> fileContentProvider,
			final ISourceFileParser<E> parser) {
		this.manager = manager;
		this.fileContentProvider = fileContentProvider;
		this.parser = parser;
	}

	/**
	 * @deprecated This method produces an instance of {@link SourceFile} with
	 *             no contents. Use
	 *             {@link VolatileSourceFileRetriever#retrieveElement(DBSourceFile, DBVersion)}
	 *             instead.
	 */
	@Override
	public SourceFile<E> retrieveElement(DBSourceFile dbElement) {
		return retrieveElement(dbElement, null);
	}

	public SourceFile<E> retrieveElement(DBSourceFile dbElement,
			DBVersion dbVersion) {
		logger.debug("start retrieving " + dbElement.getId());
		final SourceFile<E> sourceFile = new SourceFile<E>(dbElement);

		if (dbVersion != null) {
			final String fileContentsStr = fileContentProvider.getFileContent(
					dbVersion, dbElement);
			sourceFile.setContents(parser.parse(sourceFile, fileContentsStr)
					.values());
		}

		manager.add(sourceFile);

		return sourceFile;
	}
}
