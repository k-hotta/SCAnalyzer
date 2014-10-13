package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.Language;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

public class SVNVersionProviderTest {

	private static final String PATH_OF_TEST_REPO = "src/test/resources/repository-clonetracker";

	private static final String RELATIVE_PATH_FOR_TEST = "/c20r_main/src";

	private static final Version INITIAL = new Version(0, new Revision(0,
			"init(pseudo)", null), new HashSet<SourceFile>(),
			new HashSet<FileChange>(), new HashSet<RawCloneClass>());

	private static final Version VERSION_REV1 = new Version(1, new Revision(1,
			"1", new Date()), new HashSet<SourceFile>(),
			new HashSet<FileChange>(), new HashSet<RawCloneClass>());

	private static final Version VERSION_REV281 = new Version(281,
			new Revision(281, "281", new Date()), new HashSet<SourceFile>(),
			new HashSet<FileChange>(), new HashSet<RawCloneClass>());

	private static final Version VERSION_REV420 = new Version(420,
			new Revision(420, "420", new Date()), new HashSet<SourceFile>(),
			new HashSet<FileChange>(), new HashSet<RawCloneClass>());

	private SVNRepositoryManager manager;

	private SVNFileChangeEntryDetector provider;

	private SVNRepositoryManager managerMock;

	private SVNFileChangeEntryDetector providerWithMock;

	private Method mProcessFileChange;

	private Method mProcessDirectoryChange;

	private Method mDetectFileChangeEntries;

	@Before
	public void setUp() throws Exception {
		manager = new SVNRepositoryManager(PATH_OF_TEST_REPO,
				RELATIVE_PATH_FOR_TEST, Language.JAVA);
		provider = new SVNFileChangeEntryDetector(manager);

		managerMock = EasyMock.createMock(SVNRepositoryManager.class);
		EasyMock.expect(managerMock.getLanguage()).andReturn(Language.JAVA);
		EasyMock.expect(managerMock.getRelativePath()).andReturn("/");

		List<String> oldRelevantFiles = new ArrayList<String>();
		oldRelevantFiles.add("/before/Test1.java");
		oldRelevantFiles.add("/before/Test2.java");

		List<String> newRelevantFiles = new ArrayList<String>();
		newRelevantFiles.add("/test/Test1.java");
		newRelevantFiles.add("/test/Test2.java");

		EasyMock.expect(managerMock.getListOfRelevantFiles((long) 1, "/before"))
				.andReturn(oldRelevantFiles);
		EasyMock.expect(managerMock.getListOfRelevantFiles((long) 2, "/test"))
				.andReturn(newRelevantFiles);

		EasyMock.replay(managerMock);

		providerWithMock = new SVNFileChangeEntryDetector(managerMock);

		mProcessFileChange = SVNFileChangeEntryDetector.class.getDeclaredMethod(
				"processFileChange", Map.class, SVNLogEntryPath.class,
				SVNLogEntry.class);
		mProcessFileChange.setAccessible(true);

		mProcessDirectoryChange = SVNFileChangeEntryDetector.class.getDeclaredMethod(
				"processDirectoryChange", Map.class, SVNLogEntryPath.class,
				SVNLogEntry.class, Set.class);
		mProcessDirectoryChange.setAccessible(true);

		mDetectFileChangeEntries = SVNFileChangeEntryDetector.class.getDeclaredMethod(
				"detectFileChangeEntries", Collection.class);
		mDetectFileChangeEntries.setAccessible(true);
	}

	@Test
	public void testGetVersion1() {
		try {
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

	@Ignore
	public void testGetVersion2() {
		try {
			final Version version = provider.getNextVersion(VERSION_REV281);

			assertTrue(true);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testProcessFileChange1() throws Exception {
		SVNLogEntryPath pathMock = EasyMock.createMock(SVNLogEntryPath.class);
		EasyMock.expect(pathMock.getPath()).andReturn("/Test.java");
		EasyMock.expect(pathMock.getType()).andReturn('A');
		EasyMock.expect(pathMock.getCopyPath()).andReturn(null);
		EasyMock.replay(pathMock);

		SVNLogEntry logMock = EasyMock.createMock(SVNLogEntry.class);
		EasyMock.expect(logMock.getRevision()).andReturn((long) 2);
		EasyMock.replay(logMock);

		Map<String, FileChangeEntry> result = new HashMap<String, FileChangeEntry>();
		mProcessFileChange.invoke(providerWithMock, result, pathMock, logMock);

		assertTrue(result.containsKey("/Test.java"));
	}

	@Test
	public void testProcessFileChange2() throws Exception {
		SVNLogEntryPath pathMock = EasyMock.createMock(SVNLogEntryPath.class);
		EasyMock.expect(pathMock.getPath()).andReturn("/Test.java");
		EasyMock.expect(pathMock.getType()).andReturn('A');
		EasyMock.expect(pathMock.getCopyPath()).andReturn(null);
		EasyMock.replay(pathMock);

		SVNLogEntry logMock = EasyMock.createMock(SVNLogEntry.class);
		EasyMock.expect(logMock.getRevision()).andReturn((long) 2);
		EasyMock.replay(logMock);

		Map<String, FileChangeEntry> result = new HashMap<String, FileChangeEntry>();
		mProcessFileChange.invoke(providerWithMock, result, pathMock, logMock);

		final FileChangeEntry entry = result.get("/Test.java");
		assertTrue(entry.getBeforePath() == null
				&& entry.getAfterPath().endsWith("Test.java")
				&& entry.getType() == 'A');
	}

	@Test
	public void testProcessFileChange3() throws Exception {
		SVNLogEntryPath pathMock = EasyMock.createMock(SVNLogEntryPath.class);
		EasyMock.expect(pathMock.getPath()).andReturn("/Test.c");
		EasyMock.expect(pathMock.getType()).andReturn('A');
		EasyMock.expect(pathMock.getCopyPath()).andReturn(null);
		EasyMock.replay(pathMock);

		SVNLogEntry logMock = EasyMock.createMock(SVNLogEntry.class);
		EasyMock.expect(logMock.getRevision()).andReturn((long) 2);
		EasyMock.replay(logMock);

		Map<String, FileChangeEntry> result = new HashMap<String, FileChangeEntry>();
		mProcessFileChange.invoke(providerWithMock, result, pathMock, logMock);

		assertTrue(result.isEmpty());
	}

	@Test
	public void testProcessFileChange4() throws Exception {
		SVNLogEntryPath pathMock = EasyMock.createMock(SVNLogEntryPath.class);
		EasyMock.expect(pathMock.getPath()).andReturn("Test.java");
		EasyMock.expect(pathMock.getType()).andReturn('A');
		EasyMock.expect(pathMock.getCopyPath()).andReturn(null);
		EasyMock.replay(pathMock);

		SVNLogEntry logMock = EasyMock.createMock(SVNLogEntry.class);
		EasyMock.expect(logMock.getRevision()).andReturn((long) 2);
		EasyMock.replay(logMock);

		Map<String, FileChangeEntry> result = new HashMap<String, FileChangeEntry>();
		mProcessFileChange.invoke(providerWithMock, result, pathMock, logMock);

		assertTrue(result.isEmpty());
	}

	@Test
	public void testProcessFileChange5() throws Exception {
		SVNLogEntryPath pathMock = EasyMock.createMock(SVNLogEntryPath.class);
		EasyMock.expect(pathMock.getPath()).andReturn("/Test.java");
		EasyMock.expect(pathMock.getType()).andReturn('A');
		EasyMock.expect(pathMock.getCopyPath()).andReturn(null);
		EasyMock.replay(pathMock);

		SVNLogEntry logMock = EasyMock.createMock(SVNLogEntry.class);
		EasyMock.expect(logMock.getRevision()).andReturn((long) 2);
		EasyMock.replay(logMock);

		Map<String, FileChangeEntry> result = new HashMap<String, FileChangeEntry>();
		result.put("/Test.java", new FileChangeEntry("test", "/Test.java", 'A'));
		mProcessFileChange.invoke(providerWithMock, result, pathMock, logMock);

		final FileChangeEntry entry = result.get("/Test.java");
		assertTrue(entry.getBeforePath().equals("test"));
	}

	@Test
	public void testProcessFileChange6() throws Exception {
		SVNLogEntryPath pathMock = EasyMock.createMock(SVNLogEntryPath.class);
		EasyMock.expect(pathMock.getPath()).andReturn("/Test.java");
		EasyMock.expect(pathMock.getType()).andReturn('D');
		EasyMock.expect(pathMock.getCopyPath()).andReturn(null);
		EasyMock.replay(pathMock);

		SVNLogEntry logMock = EasyMock.createMock(SVNLogEntry.class);
		EasyMock.expect(logMock.getRevision()).andReturn((long) 2);
		EasyMock.replay(logMock);

		Map<String, FileChangeEntry> result = new HashMap<String, FileChangeEntry>();
		mProcessFileChange.invoke(providerWithMock, result, pathMock, logMock);

		final FileChangeEntry entry = result.get("/Test.java");
		assertTrue(entry.getBeforePath().endsWith("Test.java")
				&& entry.getAfterPath() == null && entry.getType() == 'D');
	}

	@Test
	public void testProcessFileChange7() throws Exception {
		SVNLogEntryPath pathMock = EasyMock.createMock(SVNLogEntryPath.class);
		EasyMock.expect(pathMock.getPath()).andReturn("/Test.java");
		EasyMock.expect(pathMock.getType()).andReturn('M');
		EasyMock.expect(pathMock.getCopyPath()).andReturn(null);
		EasyMock.replay(pathMock);

		SVNLogEntry logMock = EasyMock.createMock(SVNLogEntry.class);
		EasyMock.expect(logMock.getRevision()).andReturn((long) 2);
		EasyMock.replay(logMock);

		Map<String, FileChangeEntry> result = new HashMap<String, FileChangeEntry>();
		mProcessFileChange.invoke(providerWithMock, result, pathMock, logMock);

		final FileChangeEntry entry = result.get("/Test.java");
		assertTrue(entry.getBeforePath().endsWith("Test.java")
				&& entry.getAfterPath().endsWith("Test.java")
				&& entry.getType() == 'M');
	}

	@Test
	public void testProcessFileChange8() throws Exception {
		SVNLogEntryPath pathMock = EasyMock.createMock(SVNLogEntryPath.class);
		EasyMock.expect(pathMock.getPath()).andReturn("/Test.java");
		EasyMock.expect(pathMock.getType()).andReturn('R');
		EasyMock.expect(pathMock.getCopyPath()).andReturn("/before/Test.java");
		EasyMock.replay(pathMock);

		SVNLogEntry logMock = EasyMock.createMock(SVNLogEntry.class);
		EasyMock.expect(logMock.getRevision()).andReturn((long) 2);
		EasyMock.replay(logMock);

		Map<String, FileChangeEntry> result = new HashMap<String, FileChangeEntry>();
		mProcessFileChange.invoke(providerWithMock, result, pathMock, logMock);

		final FileChangeEntry entry = result.get("/Test.java");
		assertTrue(entry.getBeforePath().endsWith("/before/Test.java")
				&& entry.getAfterPath().endsWith("Test.java")
				&& entry.getType() == 'R');
	}

	@Test
	public void testProcessFileChange9() throws Exception {
		SVNLogEntryPath pathMock = EasyMock.createMock(SVNLogEntryPath.class);
		EasyMock.expect(pathMock.getPath()).andReturn("/Test.java");
		EasyMock.expect(pathMock.getType()).andReturn('C');
		EasyMock.expect(pathMock.getCopyPath()).andReturn(null);
		EasyMock.replay(pathMock);

		SVNLogEntry logMock = EasyMock.createMock(SVNLogEntry.class);
		EasyMock.expect(logMock.getRevision()).andReturn((long) 2);
		EasyMock.replay(logMock);

		Map<String, FileChangeEntry> result = new HashMap<String, FileChangeEntry>();

		boolean check = false;
		try {
			mProcessFileChange.invoke(providerWithMock, result, pathMock,
					logMock);
		} catch (InvocationTargetException e) {
			final Throwable cause = e.getTargetException();
			check = cause instanceof IllegalStateException;
		}

		assertTrue(check);
	}

	@Test
	public void testProcessFileChange10() throws Exception {
		SVNLogEntryPath pathMock = EasyMock.createMock(SVNLogEntryPath.class);
		EasyMock.expect(pathMock.getPath()).andReturn("/after/Test.java");
		EasyMock.expect(pathMock.getType()).andReturn('A');
		EasyMock.expect(pathMock.getCopyPath()).andReturn("/before/Test.java");
		EasyMock.replay(pathMock);

		SVNLogEntry logMock = EasyMock.createMock(SVNLogEntry.class);
		EasyMock.expect(logMock.getRevision()).andReturn((long) 2);
		EasyMock.replay(logMock);

		Map<String, FileChangeEntry> result = new HashMap<String, FileChangeEntry>();
		result.put("/before/Test.java", new FileChangeEntry(
				"/before/Test.java", null, 'D'));
		mProcessFileChange.invoke(providerWithMock, result, pathMock, logMock);

		final FileChangeEntry entry = result.get("/after/Test.java");
		assertTrue(result.size() == 1 && entry.getType() == 'R');
	}

	@Test
	public void testProcessDirectoryChange1() throws Exception {
		SVNLogEntryPath pathMock = EasyMock.createMock(SVNLogEntryPath.class);
		EasyMock.expect(pathMock.getPath()).andReturn("/test");
		EasyMock.expect(pathMock.getType()).andReturn('M');
		EasyMock.expect(pathMock.getCopyPath()).andReturn(null);
		EasyMock.replay(pathMock);

		SVNLogEntry logMock = EasyMock.createMock(SVNLogEntry.class);
		EasyMock.expect(logMock.getRevision()).andReturn((long) 2);
		EasyMock.replay(logMock);

		Map<String, FileChangeEntry> result = new HashMap<String, FileChangeEntry>();
		Set<String> deletedDirs = new HashSet<String>();
		mProcessDirectoryChange.invoke(providerWithMock, result, pathMock,
				logMock, deletedDirs);

		assertTrue(result.isEmpty());
	}

	@Test
	public void testProcessDirectoryChange2() throws Exception {
		SVNLogEntryPath pathMock = EasyMock.createMock(SVNLogEntryPath.class);
		EasyMock.expect(pathMock.getPath()).andReturn("/test");
		EasyMock.expect(pathMock.getType()).andReturn('R');
		EasyMock.expect(pathMock.getCopyPath()).andReturn("/testbefore");
		EasyMock.replay(pathMock);

		SVNLogEntry logMock = EasyMock.createMock(SVNLogEntry.class);
		EasyMock.expect(logMock.getRevision()).andReturn((long) 2);
		EasyMock.replay(logMock);

		Map<String, FileChangeEntry> result = new HashMap<String, FileChangeEntry>();
		Set<String> deletedDirs = new HashSet<String>();
		mProcessDirectoryChange.invoke(providerWithMock, result, pathMock,
				logMock, deletedDirs);

		assertTrue(result.isEmpty());
	}

	@Test
	public void testProcessDirectoryChange3() throws Exception {
		SVNLogEntryPath pathMock = EasyMock.createMock(SVNLogEntryPath.class);
		EasyMock.expect(pathMock.getPath()).andReturn("/test");
		EasyMock.expect(pathMock.getType()).andReturn('A');
		EasyMock.expect(pathMock.getCopyPath()).andReturn(null);
		EasyMock.replay(pathMock);

		SVNLogEntry logMock = EasyMock.createMock(SVNLogEntry.class);
		EasyMock.expect(logMock.getRevision()).andReturn((long) 2);
		EasyMock.replay(logMock);

		Map<String, FileChangeEntry> result = new HashMap<String, FileChangeEntry>();
		Set<String> deletedDirs = new HashSet<String>();
		mProcessDirectoryChange.invoke(providerWithMock, result, pathMock,
				logMock, deletedDirs);

		assertTrue(result.isEmpty());
	}

	@Test
	public void testProcessDirectoryChange4() throws Exception {
		SVNLogEntryPath pathMock = EasyMock.createMock(SVNLogEntryPath.class);
		EasyMock.expect(pathMock.getPath()).andReturn("/test");
		EasyMock.expect(pathMock.getType()).andReturn('A');
		EasyMock.expect(pathMock.getCopyPath()).andReturn("/before");
		EasyMock.replay(pathMock);

		SVNLogEntry logMock = EasyMock.createMock(SVNLogEntry.class);
		EasyMock.expect(logMock.getRevision()).andReturn((long) 2);
		EasyMock.replay(logMock);

		Map<String, FileChangeEntry> result = new HashMap<String, FileChangeEntry>();
		Set<String> deletedDirs = new HashSet<String>();
		mProcessDirectoryChange.invoke(providerWithMock, result, pathMock,
				logMock, deletedDirs);

		FileChangeEntry entry1 = result.get("/test/Test1.java");
		FileChangeEntry entry2 = result.get("/test/Test2.java");

		assertTrue(entry1 != null && entry2 != null
				&& entry1.getBeforePath().equals("/before/Test1.java")
				&& entry2.getBeforePath().equals("/before/Test2.java"));
	}

	@Test
	public void testProcessDirectoryChange5() throws Exception {
		SVNLogEntryPath pathMock = EasyMock.createMock(SVNLogEntryPath.class);
		EasyMock.expect(pathMock.getPath()).andReturn("/test");
		EasyMock.expect(pathMock.getType()).andReturn('A');
		EasyMock.expect(pathMock.getCopyPath()).andReturn("outside");
		EasyMock.replay(pathMock);

		SVNLogEntry logMock = EasyMock.createMock(SVNLogEntry.class);
		EasyMock.expect(logMock.getRevision()).andReturn((long) 2);
		EasyMock.replay(logMock);

		Map<String, FileChangeEntry> result = new HashMap<String, FileChangeEntry>();
		Set<String> deletedDirs = new HashSet<String>();
		mProcessDirectoryChange.invoke(providerWithMock, result, pathMock,
				logMock, deletedDirs);

		FileChangeEntry entry1 = result.get("/test/Test1.java");
		FileChangeEntry entry2 = result.get("/test/Test2.java");

		assertTrue(entry1 != null && entry2 != null
				&& entry1.getBeforePath() == null
				&& entry2.getBeforePath() == null);
	}

	@Test
	public void testProcessDirectoryChange6() throws Exception {
		SVNLogEntryPath pathMock = EasyMock.createMock(SVNLogEntryPath.class);
		EasyMock.expect(pathMock.getPath()).andReturn("/before");
		EasyMock.expect(pathMock.getType()).andReturn('D');
		EasyMock.expect(pathMock.getCopyPath()).andReturn(null);
		EasyMock.replay(pathMock);

		SVNLogEntry logMock = EasyMock.createMock(SVNLogEntry.class);
		EasyMock.expect(logMock.getRevision()).andReturn((long) 2);
		EasyMock.replay(logMock);

		Map<String, FileChangeEntry> result = new HashMap<String, FileChangeEntry>();
		Set<String> deletedDirs = new HashSet<String>();
		mProcessDirectoryChange.invoke(providerWithMock, result, pathMock,
				logMock, deletedDirs);

		FileChangeEntry entry1 = result.get("/before/Test1.java");
		FileChangeEntry entry2 = result.get("/before/Test2.java");

		assertTrue(entry1 != null && entry2 != null
				&& entry1.getBeforePath().startsWith("/before")
				&& entry2.getBeforePath().startsWith("/before")
				&& entry1.getAfterPath() == null
				&& entry2.getAfterPath() == null);
	}

	@Test
	public void testDetectFileChangeEntries1() throws Exception {
		final Collection<SVNLogEntry> logEntries = this.manager
				.getLog((long) 421);
		@SuppressWarnings("unchecked")
		final Collection<FileChangeEntry> result = (Collection<FileChangeEntry>) mDetectFileChangeEntries
				.invoke(provider, logEntries);

		final Map<String, String> reference = readDiffFile("src/test/resources/clonetracker-diff-summarize-rev420-rev421.txt");

		// this case corresponds to directory moving
		assertTrue(result.size() * 2 == reference.size());
	}

	@Test
	public void testDetectFileChangeEntries2() throws Exception {
		final Collection<SVNLogEntry> logEntries = this.manager
				.getLog((long) 335);
		@SuppressWarnings("unchecked")
		final Collection<FileChangeEntry> result = (Collection<FileChangeEntry>) mDetectFileChangeEntries
				.invoke(provider, logEntries);

		final Map<String, String> reference = readDiffFile("src/test/resources/clonetracker-diff-summarize-rev334-rev335.txt");

		assertTrue(result.size() == reference.size());
	}

	@Test
	public void testDetectFileChangeEntries3() throws Exception {
		final Collection<SVNLogEntry> logEntries = this.manager
				.getLog((long) 406);
		@SuppressWarnings("unchecked")
		final Collection<FileChangeEntry> result = (Collection<FileChangeEntry>) mDetectFileChangeEntries
				.invoke(provider, logEntries);

		assertTrue(result.size() == 9);
	}

	private Map<String, String> readDiffFile(final String path)
			throws Exception {
		final Map<String, String> result = new HashMap<String, String>();
		final BufferedReader br = new BufferedReader(new FileReader(new File(
				path)));

		String line;
		while ((line = br.readLine()) != null) {
			final String[] splitLine = line.split(" ");
			if (Language.JAVA.isRelevantFile(splitLine[1])) {
				result.put(splitLine[1], splitLine[0]);
			}
		}

		br.close();
		return result;
	}

}
