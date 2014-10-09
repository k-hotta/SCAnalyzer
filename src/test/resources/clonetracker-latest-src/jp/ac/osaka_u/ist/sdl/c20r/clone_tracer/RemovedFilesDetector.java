package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;

public class RemovedFilesDetector {

	public static void main(String[] args) throws Exception {
		final SVNURL url = SVNURL.parseURIDecoded("file:///F:/repositories/repository-ant");
		FSRepositoryFactory.setup();
		final SVNRepository repository = FSRepositoryFactory.create(url);

		repository.log(null, 268239, 274485, true, true,
				new ISVNLogEntryHandler() {
					public void handleLogEntry(SVNLogEntry logEntry)
							throws SVNException {
						for (final Object key : logEntry.getChangedPaths()
								.keySet()) {
							final String path = (String) key;
							if (path.endsWith("/ant/core/trunk/proposal/antfarm")) {
								final long revision = logEntry
										.getRevision();
								System.out.println(path);
								System.out.println(Long.toString(revision));
							}
							if (path.endsWith("/tools/ant/Import.java")) {
								final long revision = logEntry
										.getRevision();
								//System.out.println(path);
								//System.out.println(Long.toString(revision));
								//break;
							}
						}
					}
					
				});
	}
	
}
