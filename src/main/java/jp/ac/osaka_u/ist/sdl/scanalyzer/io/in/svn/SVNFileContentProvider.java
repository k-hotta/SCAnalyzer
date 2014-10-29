package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.svn;

import java.io.IOException;
import java.io.OutputStream;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.IFileContentProvider;

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
public class SVNFileContentProvider implements IFileContentProvider {

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
	public String getFileContent(final Version version,
			final SourceFile sourceFile) {
		if (version == null) {
			eLogger.fatal("cannot get file content: version must not be null");
			throw new IllegalArgumentException("version is null");
		}
		if (sourceFile == null) {
			eLogger.fatal("cannot get file content: sourceFile must not be null");
			throw new IllegalArgumentException("sourceFile is null");
		}

		final Revision revision = version.getRevision();
		if (revision == null) {
			eLogger.fatal("the given version has no valid revision");
			throw new IllegalArgumentException("revision in the version "
					+ version.getId() + " is null");
		}

		try {
			return getFileContent(Long.valueOf(revision.getIdentifier()),
					sourceFile.getPath());
		} catch (SVNException e) {
			eLogger.fatal("cannot get the content of " + sourceFile.getPath()
					+ " in revision " + revision.getIdentifier());
			throw new IllegalStateException("cannot get the file content");
		}
	}

	private String getFileContent(final long revisionNum, final String path)
			throws SVNException {
		final StringBuilder builder = new StringBuilder();

		final SVNURL target = repositoryManager.getUrl()
				.appendPath(path, false);
		final SVNClientManager clientManager = SVNClientManager.newInstance();

		try {
			final SVNWCClient wcClient = clientManager.getWCClient();
			wcClient.doGetFileContents(target, SVNRevision.create(revisionNum),
					SVNRevision.create(revisionNum), false, new OutputStream() {
						@Override
						public void write(int b) throws IOException {
							builder.append((char) b);
						}
					});

		} finally {
			if (clientManager != null) {
				clientManager.dispose();
			}
		}

		return builder.toString();
	}

}
