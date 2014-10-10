package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.util.HashSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;

/**
 * This class provides versions for repositories managed by Subversion.
 * 
 * @author k-hotta
 * 
 */
public class SVNVersionProvider implements IVersionProvider {

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
	public SVNVersionProvider(final SVNRepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
	}

	@Override
	public Version getNextVersion(Version currentVersion) {
		if (currentVersion == null) {
			// the first call
			// return the pseudo version with the pseudo initial revision
			return new Version(IDGenerator.generate(Version.class),
					new Revision(IDGenerator.generate(Revision.class),
							"init(pseudo)", null), new HashSet<SourceFile>(),
					new HashSet<FileChange>(), new HashSet<RawCloneClass>());
		} else {
			return null; //TODO implement
		}
	}
	
	
	
}
