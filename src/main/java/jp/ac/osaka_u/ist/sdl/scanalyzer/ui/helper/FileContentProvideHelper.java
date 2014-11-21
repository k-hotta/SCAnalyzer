package jp.ac.osaka_u.ist.sdl.scanalyzer.ui.helper;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.IFileContentProvider;

/**
 * This class supports to get file contents.
 * 
 * @author k-hotta
 *
 */
public class FileContentProvideHelper {

	/**
	 * The file content provider
	 */
	private static IFileContentProvider<?> provider = null;

	/**
	 * Set the file content provider. This method must be called before calling
	 * any methods in this class.
	 * 
	 * @param provider
	 *            the provider to be set
	 */
	public static void setProvider(final IFileContentProvider<?> provider) {
		if (provider == null) {
			throw new IllegalArgumentException("the given provider is null");
		}

		FileContentProvideHelper.provider = provider;
	}

	/**
	 * Get the content of the owner file of the given segment as a string.
	 * 
	 * @param segment
	 *            the segment under consideration
	 * 
	 * @return a string represents the content of the whole of the owner file
	 */
	public static String getFileContent(final Segment<?> segment) {
		try {
			final SourceFile<?> sourceFile = segment.getSourceFile();
			final Version<?> version = segment.getCodeFragment()
					.getCloneClass().getVersion();

			return provider.getFileContent(version.getCore(),
					sourceFile.getCore());
		} catch (Exception e) {
			throw new IllegalStateException("cannot get the file content", e);
		}
	}

	/**
	 * Get the content of the owner file of the given db segment as a string.
	 * 
	 * @param dbSegment
	 *            the db segment under consideration
	 * 
	 * @return a string represents the content of the whole of the owner file
	 */
	public static String getFileContent(final DBSegment dbSegment) {
		try {
			return provider.getFileContent(dbSegment.getCodeFragment()
					.getCloneClass().getVersion(), dbSegment.getSourceFile());
		} catch (Exception e) {
			throw new IllegalStateException("cannot get the file content", e);
		}
	}
}
