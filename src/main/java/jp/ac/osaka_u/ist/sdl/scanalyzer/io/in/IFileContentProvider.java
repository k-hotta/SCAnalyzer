package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBVersion;

/**
 * This interface describes the protocol of how to get the contents of the given
 * files.
 * 
 * @author k-hotta
 * 
 */
public interface IFileContentProvider {

	/**
	 * Provide the content of the given source file as a string.
	 * 
	 * @param version
	 *            the version under consideration
	 * @param sourceFile
	 *            the target source file
	 * @return a string that represents the content of the given file
	 */
	public String getFileContent(final DBVersion version,
			final DBSourceFile sourceFile);

}
