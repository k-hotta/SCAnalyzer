package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.svn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.Language;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;

public class SVNRevisionProviderTest {

	private SVNRepositoryManager mockManager;

	private SVNRepository mockRepository;

	private SVNRevisionProvider providerWithMock;

	private Field startRevisionNumField;

	private Field endRevisionNumField;

	@Before
	public void setUp() throws Exception {
		mockRepository = EasyMock.createMock(SVNRepository.class);
		EasyMock.expect(mockRepository.getLatestRevision()).andStubReturn(
				(long) 100);

		mockManager = EasyMock.createMock(SVNRepositoryManager.class);
		EasyMock.expect(mockManager.getRepository()).andStubReturn(
				mockRepository);
		EasyMock.expect(mockManager.getLanguage()).andStubReturn(Language.JAVA);

		// create mocks for revision 1
		final Calendar calendar = new GregorianCalendar(2012, 9, 13, 8, 0, 0);
		SVNLogEntryPath mockPath1InRev1 = createMockPath("README", null, 'A',
				SVNNodeKind.FILE);
		SVNLogEntry mockEntryInRev1 = createMockEntry(1, calendar.getTime(),
				mockPath1InRev1);
		final Collection<SVNLogEntry> logEntriesInRev1 = new HashSet<SVNLogEntry>();
		logEntriesInRev1.add(mockEntryInRev1);
		EasyMock.expect(mockManager.getLog((long) 1)).andStubReturn(
				logEntriesInRev1);

		// create mocks for revision 2
		calendar.set(2012, 9, 13, 12, 0);
		SVNLogEntryPath mockPath1InRev2 = createMockPath("/src/main", null,
				'A', SVNNodeKind.DIR);
		SVNLogEntryPath mockPath2InRev2 = createMockPath(
				"/src/main/Hoge1.java", null, 'A', SVNNodeKind.FILE);
		SVNLogEntry mockEntryInRev2 = createMockEntry(2, calendar.getTime(),
				mockPath1InRev2, mockPath2InRev2);
		final Collection<SVNLogEntry> logEntriesInRev2 = new HashSet<SVNLogEntry>();
		logEntriesInRev2.add(mockEntryInRev2);
		EasyMock.expect(mockManager.getLog((long) 2)).andStubReturn(
				logEntriesInRev2);

		// create mocks for revision 3
		calendar.set(2012, 9, 13, 15, 0);
		SVNLogEntryPath mockPath1InRev3 = createMockPath("/src/main/test",
				null, 'A', SVNNodeKind.DIR);
		SVNLogEntryPath mockPath2InRev3 = createMockPath(
				"/src/main/test/test.txt", null, 'A', SVNNodeKind.FILE);
		SVNLogEntry mockEntryInRev3 = createMockEntry(3, calendar.getTime(),
				mockPath1InRev3, mockPath2InRev3);
		final Collection<SVNLogEntry> logEntriesInRev3 = new HashSet<SVNLogEntry>();
		logEntriesInRev3.add(mockEntryInRev3);
		EasyMock.expect(mockManager.getLog((long) 3)).andStubReturn(
				logEntriesInRev3);

		// create mocks for revision 4
		calendar.set(2012, 9, 13, 17, 0);
		SVNLogEntryPath mockPath1InRev4 = createMockPath(
				"/src/main/test/Test.java", null, 'A', SVNNodeKind.FILE);
		SVNLogEntry mockEntryInRev4 = createMockEntry(4, calendar.getTime(),
				mockPath1InRev4);
		final Collection<SVNLogEntry> logEntriesInRev4 = new HashSet<SVNLogEntry>();
		logEntriesInRev4.add(mockEntryInRev4);
		EasyMock.expect(mockManager.getLog((long) 4)).andStubReturn(
				logEntriesInRev4);

		// create mocks for revision 5
		calendar.set(2012, 9, 13, 19, 0);
		SVNLogEntryPath mockPath1InRev5 = createMockPath("/src/main/test",
				null, 'D', SVNNodeKind.DIR);
		SVNLogEntry mockEntryInRev5 = createMockEntry(5, calendar.getTime(),
				mockPath1InRev5);
		final Collection<SVNLogEntry> logEntriesInRev5 = new HashSet<SVNLogEntry>();
		logEntriesInRev5.add(mockEntryInRev5);
		EasyMock.expect(mockManager.getLog((long) 5)).andStubReturn(
				logEntriesInRev5);
		final List<String> relativeFilesInDeletedDirInRev5 = new ArrayList<String>();
		relativeFilesInDeletedDirInRev5.add("/src/main/test/Test.java");
		EasyMock.expect(mockManager.getListOfRelevantFiles(4, "/src/main/test"))
				.andStubReturn(relativeFilesInDeletedDirInRev5);

		EasyMock.replay(mockRepository);
		EasyMock.replay(mockManager);

		providerWithMock = new SVNRevisionProvider(mockManager, (long) 1,
				(long) 5);
		startRevisionNumField = SVNRevisionProvider.class
				.getDeclaredField("startRevisionNum");
		startRevisionNumField.setAccessible(true);
		endRevisionNumField = SVNRevisionProvider.class
				.getDeclaredField("endRevisionNum");
		endRevisionNumField.setAccessible(true);
	}

	private SVNLogEntryPath createMockPath(final String path,
			final String copyPath, final char type, final SVNNodeKind kind) {
		SVNLogEntryPath mockPath = EasyMock.createMock(SVNLogEntryPath.class);
		EasyMock.expect(mockPath.getPath()).andStubReturn(path);
		EasyMock.expect(mockPath.getCopyPath()).andStubReturn(copyPath);
		EasyMock.expect(mockPath.getType()).andStubReturn(type);
		EasyMock.expect(mockPath.getKind()).andStubReturn(kind);
		EasyMock.replay(mockPath);

		return mockPath;
	}

	private SVNLogEntry createMockEntry(final long revision, final Date date,
			final SVNLogEntryPath... paths) {
		SVNLogEntry mockEntry = EasyMock.createMock(SVNLogEntry.class);
		EasyMock.expect(mockEntry.getRevision()).andStubReturn(revision);
		EasyMock.expect(mockEntry.getDate()).andStubReturn(date);

		final Map<String, SVNLogEntryPath> pathsMap = new HashMap<String, SVNLogEntryPath>();
		for (final SVNLogEntryPath path : paths) {
			pathsMap.put(path.getPath(), path);
		}

		EasyMock.expect(mockEntry.getChangedPaths()).andStubReturn(pathsMap);
		EasyMock.replay(mockEntry);

		return mockEntry;
	}

	@Test
	public void testSVNRevisionProvider1() throws Exception {
		boolean caughtExcption = false;
		try {
			new SVNRevisionProvider(mockManager, (long) 50, (long) 20);
		} catch (IllegalArgumentException e) {
			caughtExcption = true;
		}

		assertTrue(caughtExcption);
	}

	@Test
	public void testSVNRevisionProvider2() throws Exception {
		try {
			SVNRevisionProvider anotherProvider = new SVNRevisionProvider(
					mockManager, null, null);
			long startRevisionNum = (long) startRevisionNumField
					.get(anotherProvider);
			long endRevisionNum = (long) endRevisionNumField
					.get(anotherProvider);

			assertTrue(startRevisionNum == 1 && endRevisionNum == 100);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testSVNRevisionProvider3() throws Exception {
		SVNRevisionProvider anotherProvider = new SVNRevisionProvider(
				mockManager, (long) -1, null);
		long startRevisionNum = (long) startRevisionNumField
				.get(anotherProvider);
		long endRevisionNum = (long) endRevisionNumField.get(anotherProvider);

		assertTrue(startRevisionNum == 1 && endRevisionNum == 100);
	}

	@Test
	public void testSVNRevisionProvider4() throws Exception {
		boolean caughtExcption = false;
		try {
			new SVNRevisionProvider(mockManager, (long) 101, (long) 120);
		} catch (IllegalArgumentException e) {
			caughtExcption = true;
		}

		assertTrue(caughtExcption);
	}

	@Test
	public void testSVNRevisionProvider5() throws Exception {
		SVNRevisionProvider anotherProvider = new SVNRevisionProvider(
				mockManager, (long) 2, (long) 5);
		long startRevisionNum = (long) startRevisionNumField
				.get(anotherProvider);
		long endRevisionNum = (long) endRevisionNumField.get(anotherProvider);

		assertTrue(startRevisionNum == 2 && endRevisionNum == 5);
	}

	@Test
	public void testSVNRevisionProvider6() throws Exception {
		SVNRevisionProvider anotherProvider = new SVNRevisionProvider(
				mockManager, (long) 2, (long) 101);
		long startRevisionNum = (long) startRevisionNumField
				.get(anotherProvider);
		long endRevisionNum = (long) endRevisionNumField.get(anotherProvider);

		assertTrue(startRevisionNum == 2 && endRevisionNum == 100);
	}

	@Test
	public void testGetFirstRevision1() throws Exception {
		final Revision first = providerWithMock.getFirstRevision();

		assertEquals(first.getIdentifier(), "2");
	}

	@Test
	public void testGetNextRevision1() throws Exception {
		final DBRevision current = new DBRevision(-1, "1", null);
		final Revision next = providerWithMock.getNextRevision(new Revision(
				current));
		final Calendar cal = Calendar.getInstance();
		cal.setTime(next.getDate());

		assertTrue(next.getIdentifier().equals("2")
				&& cal.get(Calendar.HOUR_OF_DAY) == 12);
	}

	@Test
	public void testGetNextRevision2() throws Exception {
		final DBRevision current = new DBRevision(-1, "2", null);
		final Revision next = providerWithMock.getNextRevision(new Revision(
				current));
		final Calendar cal = Calendar.getInstance();
		cal.setTime(next.getDate());

		assertTrue(next.getIdentifier().equals("4")
				&& cal.get(Calendar.HOUR_OF_DAY) == 17);
	}

	@Test
	public void testGetNextRevision3() throws Exception {
		final DBRevision current = new DBRevision(-1, "3", null);
		final Revision next = providerWithMock.getNextRevision(new Revision(
				current));
		final Calendar cal = Calendar.getInstance();
		cal.setTime(next.getDate());

		assertTrue(next.getIdentifier().equals("4")
				&& cal.get(Calendar.HOUR_OF_DAY) == 17);
	}

	@Test
	public void testGetNextRevision4() throws Exception {
		final DBRevision current = new DBRevision(-1, "4", null);
		final Revision next = providerWithMock.getNextRevision(new Revision(
				current));
		final Calendar cal = Calendar.getInstance();
		cal.setTime(next.getDate());

		assertTrue(next.getIdentifier().equals("5")
				&& cal.get(Calendar.HOUR_OF_DAY) == 19);
	}

	@Test
	public void testGetNextRevision5() throws Exception {
		final DBRevision current = new DBRevision(-1, "5", null);
		final Revision next = providerWithMock.getNextRevision(new Revision(
				current));
		assertNull(next);
	}

}
