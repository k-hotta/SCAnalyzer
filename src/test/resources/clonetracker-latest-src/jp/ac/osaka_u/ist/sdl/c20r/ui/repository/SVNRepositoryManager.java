package jp.ac.osaka_u.ist.sdl.c20r.ui.repository;

import java.io.IOException;
import java.io.OutputStream;

import jp.ac.osaka_u.ist.sdl.c20r.ui.settings.UISettings;

import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;

public class SVNRepositoryManager {

	private static SVNRepositoryManager SINGLETON;

	private final String repositoryPath;

	private final SVNURL url;

	private final SVNUpdateClient updateClient;

	private final SVNWCClient wcClient;

	private SVNRepositoryManager() throws Exception {
		this.repositoryPath = UISettings.getInstance().getRepository();
		this.url = SVNURL.parseURIDecoded("file:///" + repositoryPath);
		this.updateClient = SVNClientManager.newInstance().getUpdateClient();
		this.wcClient = SVNClientManager.newInstance().getWCClient();
		updateClient.setIgnoreExternals(false);
	}

	public static SVNRepositoryManager getInstance() throws Exception {
		if (SINGLETON == null) {
			SINGLETON = new SVNRepositoryManager();
		}

		return SINGLETON;
	}

	public String getFileContent(final int revisionNum, final String path)
			throws Exception {
		final StringBuilder builder = new StringBuilder();
		String normalizedPath = path;
		if (path.contains("\\")) {
			normalizedPath = path.replaceAll("\\\\", "/");
		}
		final SVNURL target = url.appendPath(normalizedPath, false);
		wcClient.doGetFileContents(target, SVNRevision.create(revisionNum),
				SVNRevision.create(revisionNum), false, new OutputStream() {

					@Override
					public void write(int b) throws IOException {
						builder.append((char) b);
					}

				});
		
		return builder.toString();
	}

}
