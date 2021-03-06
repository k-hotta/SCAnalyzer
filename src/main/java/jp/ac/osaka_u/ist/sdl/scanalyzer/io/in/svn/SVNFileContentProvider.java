package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.svn;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.IFileContentProvider;
import jp.ac.osaka_u.ist.sdl.scanalyzer.util.StringUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;

/**
 * This class provides contents of files managed by Subversion.
 * 
 * @author k-hotta
 * 
 */
public class SVNFileContentProvider<E extends IProgramElement> implements
		IFileContentProvider<E> {

	/**
	 * The logger for errors
	 */
	private static final Logger eLogger = LogManager.getLogger("error");

	/**
	 * The repository manager
	 */
	private final SVNRepositoryManager repositoryManager;

	/**
	 * The constructor with the instance of SVN repository manager
	 * 
	 * @param repositoryManager
	 *            the repository manager
	 */
	public SVNFileContentProvider(final SVNRepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
	}

	@Override
	public String getFileContent(final Version<E> version,
			final SourceFile<E> sourceFile) {
		if (version == null) {
			eLogger.fatal("cannot get file content: version must not be null");
			throw new IllegalArgumentException("version is null");
		}
		if (sourceFile == null) {
			eLogger.fatal("cannot get file content: sourceFile must not be null");
			throw new IllegalArgumentException("sourceFile is null");
		}

		return getFileContent(version.getCore(), sourceFile.getCore());
	}

	@Override
	public String getFileContent(final DBVersion dbVersion,
			final DBSourceFile dbSourceFile) {
		if (dbVersion == null) {
			eLogger.fatal("cannot get file content: version must not be null");
			throw new IllegalArgumentException("version is null");
		}
		if (dbSourceFile == null) {
			eLogger.fatal("cannot get file content: sourceFile must not be null");
			throw new IllegalArgumentException("sourceFile is null");
		}

		final DBRevision revision = dbVersion.getRevision();
		if (revision == null) {
			eLogger.fatal("the given version has no valid revision");
			throw new IllegalArgumentException("revision in the version "
					+ dbVersion.getId() + " is null");
		}

		try {
			return getFileContent(Long.valueOf(revision.getIdentifier()),
					dbSourceFile.getPath());
		} catch (SVNException e) {
			eLogger.fatal("cannot get the content of " + dbSourceFile.getPath()
					+ " in revision " + revision.getIdentifier());
			throw new IllegalStateException("cannot get the file content");
		}
	}

	private String getFileContent(final long revisionNum, final String path)
			throws SVNException {
		String result = null;

		synchronized (SVNFileContentProvider.class) {
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			final SVNClientManager clientManager = SVNClientManager
					.newInstance();

			try {
				final SVNURL target = repositoryManager.getUrl().appendPath(
						path, false);

				final SVNWCClient wcClient = clientManager.getWCClient();
				wcClient.doGetFileContents(target,
						SVNRevision.create(revisionNum),
						SVNRevision.create(revisionNum), false,
						new OutputStream() {
							@Override
							public void write(int b) throws IOException {
								os.write(b);
							}
						});

				final byte[] bytes = os.toByteArray();
				final Charset guessEncoding = StringUtil.guessEncoding(bytes);

				result = new String(bytes, guessEncoding);

			} finally {
				if (clientManager != null) {
					clientManager.dispose();
				}
				if (os != null) {
					try {
						os.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return result;
	}

}
