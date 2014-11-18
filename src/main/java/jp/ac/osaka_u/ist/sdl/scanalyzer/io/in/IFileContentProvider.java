package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;

/**
 * This interface describes the protocol of how to get the contents of the given
 * files.
 * 
 * @author k-hotta
 * 
 * @param <E>
 *            the type of program element
 */
public interface IFileContentProvider<E extends IProgramElement> {

	/**
	 * Provide the content of the given source file as a string.
	 * 
	 * @param version
	 *            the version under consideration
	 * @param sourceFile
	 *            the target source file
	 * 
	 * @return a string that represents the content of the given file
	 */
	public String getFileContent(final Version<E> version,
			final SourceFile<E> sourceFile);

	/**
	 * Provide the content of the given source file as a string.
	 * 
	 * @param dbVersion
	 *            the version under consideration
	 * @param dbSourceFile
	 *            the target source file
	 * 
	 * @return a string that represents the content of the given file
	 */
	public String getFileContent(final DBVersion dbVersion,
			final DBSourceFile dbSourceFile);

}
