package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.Language;

import org.junit.Ignore;
import org.junit.Test;

public class SVNVersionProviderTest {

	private static final String PATH_OF_TEST_REPO = "src/test/resources/repository-clonetracker";

	private static final String RELATIVE_PATH_FOR_TEST = "/c20r_main/src";

	private static final Version INITIAL = new Version(0, new Revision(0,
			"init(pseudo)", null), new HashSet<SourceFile>(),
			new HashSet<FileChange>(), new HashSet<RawCloneClass>());
	
	private static final Version VERSION_REV1 = new Version(1, new Revision(1,
			"1", new Date()), new HashSet<SourceFile>(),
			new HashSet<FileChange>(), new HashSet<RawCloneClass>());
	
	private static final Version VERSION_REV281 = new Version(281, new Revision(281,
			"281", new Date()), new HashSet<SourceFile>(),
			new HashSet<FileChange>(), new HashSet<RawCloneClass>());
	
	private static final Version VERSION_REV420 = new Version(420, new Revision(420,
			"420", new Date()), new HashSet<SourceFile>(),
			new HashSet<FileChange>(), new HashSet<RawCloneClass>());

	@Ignore
	public void testGetVersion1() {
		try {
			final SVNRepositoryManager manager = new SVNRepositoryManager(
					PATH_OF_TEST_REPO, RELATIVE_PATH_FOR_TEST, Language.JAVA);
			final SVNVersionProvider provider = new SVNVersionProvider(manager);

			final Version version = provider.getNextVersion(null);
			final boolean compare = (version.getId() == INITIAL.getId()
					&& version.getRevision().getIdentifier()
							.equals(INITIAL.getRevision().getIdentifier())
					&& version.getFileChanges().isEmpty()
					&& version.getSourceFiles().isEmpty() && version
					.getRawCloneClasses().isEmpty());
			
			assertTrue(compare);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testGetVersion2() {
		try {
			final SVNRepositoryManager manager = new SVNRepositoryManager(
					PATH_OF_TEST_REPO, RELATIVE_PATH_FOR_TEST, Language.JAVA);
			final SVNVersionProvider provider = new SVNVersionProvider(manager);

			final Version version = provider.getNextVersion(VERSION_REV420);

			assertTrue(true);
		} catch (Exception e) {
			fail();
		}
	}

}
