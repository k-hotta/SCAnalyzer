package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFileWithContent;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Token;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBElementComparator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.Language;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.DBXmlParser;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class VersionProviderTest {

	private static final String TEST_DB_XML_PATH = "src/test/resources/test-db.xml";

	private static DBXmlParser parser;

	private static IRevisionProvider revisionProviderMock;

	private static IFileChangeEntryDetector fileChangeEntryDetectorMock;

	private static IRelocationFinder relocationFinderMock;

	private static ICloneDetector cloneDetectorMock;

	private static IFileContentProvider contentProviderMock;

	private static ISourceFileParser<Token> fileParser = new TokenSourceFileParser(
			Language.JAVA);

	private static Method mReady;

	private static Method mDetectNextRevision;

	private static Method mProcessFileChanges;

	private static Method mGetSourceFilesAsMap;

	private static VersionProvider provider;

	private static final DBVersion INITIAL_VERSION = new DBVersion((long) 0,
			new DBRevision(0, "pseudo-initial-revision", null),
			new HashSet<DBFileChange>(), new HashSet<DBRawCloneClass>(),
			new HashSet<DBCloneClass>(), new HashSet<DBSourceFile>(),
			new HashMap<Long, SourceFileWithContent<?>>());

	private static class TempFileContentProvider implements
			IFileContentProvider {

		private TempStringProvider stringProvider;

		public TempFileContentProvider(final TempStringProvider stringProvider) {
			this.stringProvider = stringProvider;
		}

		@Override
		public String getFileContent(DBVersion version, DBSourceFile sourceFile) {
			return stringProvider.provide(version, sourceFile.getPath());
		}

	}

	private interface TempStringProvider {

		public String provide(final DBVersion version, final String path);

	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// parse data
		parser = new DBXmlParser(TEST_DB_XML_PATH);
		parser.parse();

		// setup mock for IRevisionProvider
		revisionProviderMock = EasyMock.createMock(IRevisionProvider.class);
		EasyMock.expect(revisionProviderMock.getFirstRevision()).andStubReturn(
				parser.getRevisions().get((long) 1));
		EasyMock.expect(
				revisionProviderMock.getNextRevision(parser.getRevisions().get(
						(long) 1))).andStubReturn(
				parser.getRevisions().get((long) 2));
		EasyMock.expect(
				revisionProviderMock.getNextRevision(parser.getRevisions().get(
						(long) 2))).andStubReturn(
				parser.getRevisions().get((long) 3));
		EasyMock.expect(
				revisionProviderMock.getNextRevision(parser.getRevisions().get(
						(long) 3))).andStubReturn(
				parser.getRevisions().get((long) 4));
		EasyMock.expect(
				revisionProviderMock.getNextRevision(parser.getRevisions().get(
						(long) 4))).andStubReturn(null);

		// setup mock for IFileChangeEntryDetector
		fileChangeEntryDetectorMock = EasyMock
				.createMock(IFileChangeEntryDetector.class);
		EasyMock.expect(
				fileChangeEntryDetectorMock
						.detectFileChangeEntriesToRevision(parser
								.getRevisions().get((long) 1))).andStubReturn(
				getFileChangeEntries(parser.getVersions().get((long) 1)
						.getFileChanges()));
		EasyMock.expect(
				fileChangeEntryDetectorMock
						.detectFileChangeEntriesToRevision(parser
								.getRevisions().get((long) 2))).andStubReturn(
				getFileChangeEntries(parser.getVersions().get((long) 2)
						.getFileChanges()));
		EasyMock.expect(
				fileChangeEntryDetectorMock
						.detectFileChangeEntriesToRevision(parser
								.getRevisions().get((long) 3))).andStubReturn(
				getFileChangeEntries(parser.getVersions().get((long) 3)
						.getFileChanges()));
		EasyMock.expect(
				fileChangeEntryDetectorMock
						.detectFileChangeEntriesToRevision(parser
								.getRevisions().get((long) 4))).andStubReturn(
				getFileChangeEntries(parser.getVersions().get((long) 4)
						.getFileChanges()));

		// setup mock for IRelocationFinder
		relocationFinderMock = EasyMock.createMock(IRelocationFinder.class);

		// setup mock for ICloneDetector
		cloneDetectorMock = EasyMock.createMock(ICloneDetector.class);
		EasyMock.expect(
				cloneDetectorMock.detectClones(parser.getVersions().get(
						(long) 1))).andStubReturn(
				parser.getVersions().get((long) 1).getRawCloneClasses());
		EasyMock.expect(
				cloneDetectorMock.detectClones(parser.getVersions().get(
						(long) 2))).andStubReturn(
				parser.getVersions().get((long) 2).getRawCloneClasses());
		EasyMock.expect(
				cloneDetectorMock.detectClones(parser.getVersions().get(
						(long) 3))).andStubReturn(
				parser.getVersions().get((long) 3).getRawCloneClasses());
		EasyMock.expect(
				cloneDetectorMock.detectClones(parser.getVersions().get(
						(long) 4))).andStubReturn(
				parser.getVersions().get((long) 4).getRawCloneClasses());

		// setup mock for IFileContentProvider
		TempStringProvider stringProviderMock = EasyMock
				.createMock(TempStringProvider.class);
		EasyMock.expect(
				stringProviderMock.provide(parser.getVersions().get((long) 1),
						parser.getSourceFiles().get((long) 1).getPath()))
				.andStubReturn(parser.getFileContentsStr().get((long) 1));
		EasyMock.expect(
				stringProviderMock.provide(parser.getVersions().get((long) 2),
						parser.getSourceFiles().get((long) 1).getPath()))
				.andStubReturn(parser.getFileContentsStr().get((long) 1));
		EasyMock.expect(
				stringProviderMock.provide(parser.getVersions().get((long) 2),
						parser.getSourceFiles().get((long) 2).getPath()))
				.andStubReturn(parser.getFileContentsStr().get((long) 2));
		EasyMock.expect(
				stringProviderMock.provide(parser.getVersions().get((long) 3),
						parser.getSourceFiles().get((long) 2).getPath()))
				.andStubReturn(parser.getFileContentsStr().get((long) 2));
		EasyMock.expect(
				stringProviderMock.provide(parser.getVersions().get((long) 3),
						parser.getSourceFiles().get((long) 3).getPath()))
				.andStubReturn(parser.getFileContentsStr().get((long) 3));
		EasyMock.expect(
				stringProviderMock.provide(parser.getVersions().get((long) 3),
						parser.getSourceFiles().get((long) 4).getPath()))
				.andStubReturn(parser.getFileContentsStr().get((long) 4));
		EasyMock.expect(
				stringProviderMock.provide(parser.getVersions().get((long) 4),
						parser.getSourceFiles().get((long) 2).getPath()))
				.andStubReturn(parser.getFileContentsStr().get((long) 2));
		EasyMock.expect(
				stringProviderMock.provide(parser.getVersions().get((long) 4),
						parser.getSourceFiles().get((long) 3).getPath()))
				.andStubReturn(parser.getFileContentsStr().get((long) 3));
		EasyMock.replay(stringProviderMock);
		contentProviderMock = new TempFileContentProvider(stringProviderMock);

		// enable every mock
		EasyMock.replay(revisionProviderMock);
		EasyMock.replay(fileChangeEntryDetectorMock);
		EasyMock.replay(relocationFinderMock);
		EasyMock.replay(cloneDetectorMock);

		// set private method accessible
		mReady = VersionProvider.class.getDeclaredMethod("ready");
		mReady.setAccessible(true);

		mDetectNextRevision = VersionProvider.class.getDeclaredMethod(
				"detectNextRevision", DBVersion.class);
		mDetectNextRevision.setAccessible(true);

		mProcessFileChanges = VersionProvider.class.getDeclaredMethod(
				"processFileChanges", DBVersion.class, DBVersion.class,
				Collection.class);
		mProcessFileChanges.setAccessible(true);

		mGetSourceFilesAsMap = VersionProvider.class.getDeclaredMethod(
				"getSourceFilesAsMap", Collection.class);
		mGetSourceFilesAsMap.setAccessible(true);

		// setup the instance of VersionProvider
		provider = new VersionProvider();
		provider.setRevisionProvider(revisionProviderMock);
		provider.setFileChangeDetector(fileChangeEntryDetectorMock);
		// provider.setRelocationFinder(relocationFinderMock);
		provider.setCloneDetector(cloneDetectorMock);
		provider.setContentProvider(contentProviderMock);
		provider.setContentBuilder(new SourceFileContentBuilder<Token>(
				fileParser));
	}

	@Before
	public void setUp() throws Exception {
		IDGenerator.initialize(DBVersion.class, 0);
		IDGenerator.initialize(DBRevision.class, 0);
		IDGenerator.initialize(DBSourceFile.class, Long.MIN_VALUE);
		IDGenerator.initialize(DBFileChange.class, Long.MIN_VALUE);
		IDGenerator.initialize(DBRawCloneClass.class, Long.MIN_VALUE);
		IDGenerator.initialize(DBRawClonedFragment.class, Long.MIN_VALUE);
	}

	@Test
	public void testReady1() throws Exception {
		final VersionProvider provider = new VersionProvider();
		assertFalse((Boolean) mReady.invoke(provider));
	}

	@Test
	public void testReady2() throws Exception {
		final VersionProvider provider = new VersionProvider();
		provider.setRevisionProvider(revisionProviderMock);
		assertFalse((Boolean) mReady.invoke(provider));
	}

	@Test
	public void testReady3() throws Exception {
		final VersionProvider provider = new VersionProvider();
		provider.setRevisionProvider(revisionProviderMock);
		provider.setFileChangeDetector(fileChangeEntryDetectorMock);
		assertFalse((Boolean) mReady.invoke(provider));
	}

	@Test
	public void testReady4() throws Exception {
		final VersionProvider provider = new VersionProvider();
		provider.setRevisionProvider(revisionProviderMock);
		provider.setFileChangeDetector(fileChangeEntryDetectorMock);
		provider.setCloneDetector(cloneDetectorMock);
		assertFalse((Boolean) mReady.invoke(provider));
	}

	@Test
	public void testReady5() throws Exception {
		final VersionProvider provider = new VersionProvider();
		provider.setRevisionProvider(revisionProviderMock);
		provider.setFileChangeDetector(fileChangeEntryDetectorMock);
		provider.setCloneDetector(cloneDetectorMock);
		provider.setContentProvider(contentProviderMock);
		assertFalse((Boolean) mReady.invoke(provider));
	}

	@Test
	public void testReady6() throws Exception {
		final VersionProvider provider = new VersionProvider();
		provider.setRevisionProvider(revisionProviderMock);
		provider.setFileChangeDetector(fileChangeEntryDetectorMock);
		provider.setCloneDetector(cloneDetectorMock);
		provider.setContentProvider(contentProviderMock);
		provider.setContentBuilder(new SourceFileContentBuilder<Token>(null));
		assertTrue((Boolean) mReady.invoke(provider));
	}

	@Test
	public void testReady7() throws Exception {
		final VersionProvider provider = new VersionProvider();
		provider.setRevisionProvider(revisionProviderMock);
		provider.setFileChangeDetector(fileChangeEntryDetectorMock);
		provider.setCloneDetector(cloneDetectorMock);
		provider.setContentProvider(contentProviderMock);
		provider.setContentBuilder(new SourceFileContentBuilder<Token>(null));
		provider.setRelocationFinder(relocationFinderMock);
		assertTrue((Boolean) mReady.invoke(provider));
	}

	@Test
	public void testSetFileChangeDetector1() throws Exception {
		final VersionProvider provider = new VersionProvider();
		provider.setFileChangeDetector(fileChangeEntryDetectorMock);
		assertEquals(fileChangeEntryDetectorMock,
				provider.getFileChangeDetector());
	}

	@Test
	public void testSetFileChangeDetector2() throws Exception {
		final VersionProvider provider = new VersionProvider();
		boolean caughtException = false;
		try {
			provider.setFileChangeDetector(null);
		} catch (IllegalArgumentException e) {
			caughtException = true;
		}
		assertTrue(caughtException);
	}

	@Test
	public void testSetRevisionProvider1() throws Exception {
		final VersionProvider provider = new VersionProvider();
		provider.setRevisionProvider(revisionProviderMock);
		assertEquals(revisionProviderMock, provider.getRevisionProvider());
	}

	@Test
	public void testSetRevisionProvider2() throws Exception {
		final VersionProvider provider = new VersionProvider();
		boolean caughtException = false;
		try {
			provider.setRevisionProvider(null);
		} catch (IllegalArgumentException e) {
			caughtException = true;
		}
		assertTrue(caughtException);
	}

	@Test
	public void testSetRelocationFinder1() throws Exception {
		final VersionProvider provider = new VersionProvider();
		provider.setRelocationFinder(relocationFinderMock);
		assertEquals(relocationFinderMock, provider.getRelocationFinder());
	}

	@Test
	public void testSetRelocationFinder2() throws Exception {
		final VersionProvider provider = new VersionProvider();
		provider.setRelocationFinder(null);
		assertNull(provider.getRelocationFinder());
	}

	@Test
	public void testSetCloneDetector1() throws Exception {
		final VersionProvider provider = new VersionProvider();
		provider.setCloneDetector(cloneDetectorMock);
		assertEquals(cloneDetectorMock, provider.getCloneDetector());
	}

	@Test
	public void testSetCloneDetector2() throws Exception {
		final VersionProvider provider = new VersionProvider();
		boolean caughtException = false;
		try {
			provider.setCloneDetector(null);
		} catch (IllegalArgumentException e) {
			caughtException = true;
		}
		assertTrue(caughtException);
	}

	@Test
	public void testDetectNextRevision1() throws Exception {
		final DBRevision revision = (DBRevision) mDetectNextRevision.invoke(
				provider, INITIAL_VERSION);
		assertEquals(revision, parser.getRevisions().get((long) 1));
	}

	@Test
	public void testDetectNextRevision2() throws Exception {
		final DBRevision revision = (DBRevision) mDetectNextRevision.invoke(
				provider, parser.getVersions().get((long) 4));
		assertNull(revision);
	}

	@Test
	public void testGetSourceFilesAsMap() throws Exception {
		final Collection<DBSourceFile> references = parser.getVersions()
				.get((long) 2).getSourceFiles();
		@SuppressWarnings("unchecked")
		final Map<String, DBSourceFile> result = (Map<String, DBSourceFile>) mGetSourceFilesAsMap
				.invoke(provider, references);

		assertEquals(references.size(), result.size());
		for (final DBSourceFile reference : references) {
			assertTrue(result.containsValue(reference));
		}
	}

	@Test
	public void testProcessFileChanges1() throws Exception {
		final DBVersion ver0 = INITIAL_VERSION;
		final DBVersion ver1 = parser.getVersions().get((long) 1);
		final Collection<DBFileChange> fileChanges = ver1.getFileChanges();

		final Set<FileChangeEntry> fileChangeEntries = getFileChangeEntries(fileChanges);

		final DBVersion versionUnderConstructed = new DBVersion(ver1.getId(),
				ver1.getRevision(), new TreeSet<DBFileChange>(
						new DBElementComparator()), new TreeSet<DBRawCloneClass>(
						new DBElementComparator()), new TreeSet<DBCloneClass>(
						new DBElementComparator()), new TreeSet<DBSourceFile>(
						new DBElementComparator()),
				new TreeMap<Long, SourceFileWithContent<?>>());

		mProcessFileChanges.invoke(provider, ver0, versionUnderConstructed,
				fileChangeEntries);

		assertEquals(fileChanges.size(), versionUnderConstructed
				.getFileChanges().size());
		assertEquals(ver1.getSourceFiles().size(), versionUnderConstructed
				.getSourceFiles().size());
	}

	@Test
	public void testProcessFileChanges2() throws Exception {
		final DBVersion ver1 = parser.getVersions().get((long) 1);
		final DBVersion ver2 = parser.getVersions().get((long) 2);
		final Collection<DBFileChange> fileChanges = ver2.getFileChanges();

		final Set<FileChangeEntry> fileChangeEntries = getFileChangeEntries(fileChanges);

		final DBVersion versionUnderConstructed = new DBVersion(ver2.getId(),
				ver2.getRevision(), new TreeSet<DBFileChange>(
						new DBElementComparator()), new TreeSet<DBRawCloneClass>(
						new DBElementComparator()), new TreeSet<DBCloneClass>(
						new DBElementComparator()), new TreeSet<DBSourceFile>(
						new DBElementComparator()),
				new TreeMap<Long, SourceFileWithContent<?>>());

		mProcessFileChanges.invoke(provider, ver1, versionUnderConstructed,
				fileChangeEntries);

		assertEquals(fileChanges.size(), versionUnderConstructed
				.getFileChanges().size());
		assertEquals(ver2.getSourceFiles().size(), versionUnderConstructed
				.getSourceFiles().size());
	}

	@Test
	public void testProcessFileChanges3() throws Exception {
		final DBVersion ver2 = parser.getVersions().get((long) 2);
		final DBVersion ver3 = parser.getVersions().get((long) 3);
		final Collection<DBFileChange> fileChanges = ver3.getFileChanges();

		final Set<FileChangeEntry> fileChangeEntries = getFileChangeEntries(fileChanges);

		final DBVersion versionUnderConstructed = new DBVersion(ver3.getId(),
				ver3.getRevision(), new TreeSet<DBFileChange>(
						new DBElementComparator()), new TreeSet<DBRawCloneClass>(
						new DBElementComparator()), new TreeSet<DBCloneClass>(
						new DBElementComparator()), new TreeSet<DBSourceFile>(
						new DBElementComparator()),
				new TreeMap<Long, SourceFileWithContent<?>>());

		mProcessFileChanges.invoke(provider, ver2, versionUnderConstructed,
				fileChangeEntries);

		assertEquals(fileChanges.size(), versionUnderConstructed
				.getFileChanges().size());
		assertEquals(ver3.getSourceFiles().size(), versionUnderConstructed
				.getSourceFiles().size());
	}

	@Test
	public void testProcessFileChanges4() throws Exception {
		final DBVersion ver3 = parser.getVersions().get((long) 3);
		final DBVersion ver4 = parser.getVersions().get((long) 4);
		final Collection<DBFileChange> fileChanges = ver4.getFileChanges();

		final Set<FileChangeEntry> fileChangeEntries = getFileChangeEntries(fileChanges);

		final DBVersion versionUnderConstructed = new DBVersion(ver4.getId(),
				ver4.getRevision(), new TreeSet<DBFileChange>(
						new DBElementComparator()), new TreeSet<DBRawCloneClass>(
						new DBElementComparator()), new TreeSet<DBCloneClass>(
						new DBElementComparator()), new TreeSet<DBSourceFile>(
						new DBElementComparator()),
				new TreeMap<Long, SourceFileWithContent<?>>());

		mProcessFileChanges.invoke(provider, ver3, versionUnderConstructed,
				fileChangeEntries);

		assertEquals(fileChanges.size(), versionUnderConstructed
				.getFileChanges().size());
		assertEquals(ver4.getSourceFiles().size(), versionUnderConstructed
				.getSourceFiles().size());
	}

	@Test
	public void testProcessFileChanges5() throws Exception {
		final DBVersion ver3 = parser.getVersions().get((long) 3);
		final DBVersion ver4 = parser.getVersions().get((long) 4);
		final Collection<DBFileChange> fileChanges = ver4.getFileChanges();

		final Set<FileChangeEntry> fileChangeEntries = getFileChangeEntries(fileChanges);
		final FileChangeEntry dummy = new FileChangeEntry("Dummy.java", null,
				'D');
		fileChangeEntries.add(dummy);

		final DBVersion versionUnderConstructed = new DBVersion(ver4.getId(),
				ver4.getRevision(), new TreeSet<DBFileChange>(
						new DBElementComparator()), new TreeSet<DBRawCloneClass>(
						new DBElementComparator()), new TreeSet<DBCloneClass>(
						new DBElementComparator()), new TreeSet<DBSourceFile>(
						new DBElementComparator()),
				new TreeMap<Long, SourceFileWithContent<?>>());

		boolean caughtException = false;
		try {
			mProcessFileChanges.invoke(provider, ver3, versionUnderConstructed,
					fileChangeEntries);
		} catch (Exception e) {
			if (e.getCause() instanceof IllegalStateException) {
				caughtException = true;
			}
		}

		assertTrue(caughtException);
	}

	@Test
	public void testProcessFileChanges6() throws Exception {
		final DBVersion ver3 = parser.getVersions().get((long) 3);
		final DBVersion ver4 = parser.getVersions().get((long) 4);
		final Collection<DBFileChange> fileChanges = ver4.getFileChanges();

		final Set<FileChangeEntry> fileChangeEntries = getFileChangeEntries(fileChanges);
		final FileChangeEntry dummy = new FileChangeEntry("A.java", null, 'U');
		fileChangeEntries.add(dummy);

		final DBVersion versionUnderConstructed = new DBVersion(ver4.getId(),
				ver4.getRevision(), new TreeSet<DBFileChange>(
						new DBElementComparator()), new TreeSet<DBRawCloneClass>(
						new DBElementComparator()), new TreeSet<DBCloneClass>(
						new DBElementComparator()), new TreeSet<DBSourceFile>(
						new DBElementComparator()),
				new TreeMap<Long, SourceFileWithContent<?>>());

		boolean caughtException = false;
		try {
			mProcessFileChanges.invoke(provider, ver3, versionUnderConstructed,
					fileChangeEntries);
		} catch (Exception e) {
			if (e.getCause() instanceof IllegalStateException) {
				caughtException = true;
			}
		}

		assertTrue(caughtException);
	}

	@Test
	public void testProcessFileChanges7() throws Exception {
		final DBVersion ver3 = parser.getVersions().get((long) 3);
		final DBVersion ver4 = parser.getVersions().get((long) 4);
		final Collection<DBFileChange> fileChanges = ver4.getFileChanges();

		final Set<FileChangeEntry> fileChangeEntries = getFileChangeEntries(fileChanges);
		final FileChangeEntry dummy = new FileChangeEntry("Dummy.java", null,
				'A');
		fileChangeEntries.add(dummy);

		final DBVersion versionUnderConstructed = new DBVersion(ver4.getId(),
				ver4.getRevision(), new TreeSet<DBFileChange>(
						new DBElementComparator()), new TreeSet<DBRawCloneClass>(
						new DBElementComparator()), new TreeSet<DBCloneClass>(
						new DBElementComparator()), new TreeSet<DBSourceFile>(
						new DBElementComparator()),
				new TreeMap<Long, SourceFileWithContent<?>>());

		boolean caughtException = false;
		try {
			mProcessFileChanges.invoke(provider, ver3, versionUnderConstructed,
					fileChangeEntries);
		} catch (Exception e) {
			if (e.getCause() instanceof IllegalStateException) {
				caughtException = true;
			}
		}

		assertTrue(caughtException);
	}

	@Test
	public void testGetNextVersion1() throws Exception {
		VersionProvider provider = new VersionProvider();
		boolean caughtException = false;

		try {
			provider.getNextVersion(null);
		} catch (IllegalStateException e) {
			caughtException = true;
		}

		assertTrue(caughtException);
	}

	@Test
	public void testGetNextVersion2() throws Exception {
		DBVersion result = provider.getNextVersion(null);
		assertTrue(result.getId() == 0);
		assertEquals(result.getRevision().getIdentifier(),
				"pseudo-initial-revision");
	}

	@Test
	public void testGetNextVersion3() throws Exception {
		DBVersion current = INITIAL_VERSION;
		DBVersion reference = parser.getVersions().get((long) 1);

		IDGenerator.initialize(DBVersion.class, current.getId() + 1);
		IDGenerator.initialize(DBSourceFile.class, 1);
		DBVersion result = provider.getNextVersion(current);

		assertEquals(reference.getFileChanges().size(), result.getFileChanges()
				.size());
		assertEquals(reference.getRevision().getIdentifier(), result
				.getRevision().getIdentifier());
		assertEquals(reference.getSourceFiles().size(), result.getSourceFiles()
				.size());
		assertEquals(reference.getRawCloneClasses().size(), result
				.getRawCloneClasses().size());
	}

	@Test
	public void testGetNextVersion4() throws Exception {
		DBVersion current = parser.getVersions().get((long) 1);
		DBVersion reference = parser.getVersions().get((long) 2);

		IDGenerator.initialize(DBVersion.class, current.getId() + 1);
		IDGenerator.initialize(DBSourceFile.class, 2);
		DBVersion result = provider.getNextVersion(current);

		assertEquals(reference.getFileChanges().size(), result.getFileChanges()
				.size());
		assertEquals(reference.getRevision().getIdentifier(), result
				.getRevision().getIdentifier());
		assertEquals(reference.getSourceFiles().size(), result.getSourceFiles()
				.size());
		assertEquals(reference.getRawCloneClasses().size(), result
				.getRawCloneClasses().size());
	}

	@Test
	public void testGetNextVersion5() throws Exception {
		DBVersion current = parser.getVersions().get((long) 2);
		DBVersion reference = parser.getVersions().get((long) 3);

		IDGenerator.initialize(DBVersion.class, current.getId() + 1);
		IDGenerator.initialize(DBSourceFile.class, 3);
		DBVersion result = provider.getNextVersion(current);

		assertEquals(reference.getFileChanges().size(), result.getFileChanges()
				.size());
		assertEquals(reference.getRevision().getIdentifier(), result
				.getRevision().getIdentifier());
		assertEquals(reference.getSourceFiles().size(), result.getSourceFiles()
				.size());
		assertEquals(reference.getRawCloneClasses().size(), result
				.getRawCloneClasses().size());
	}

	@Test
	public void testGetNextVersion6() throws Exception {
		DBVersion current = parser.getVersions().get((long) 3);
		DBVersion reference = parser.getVersions().get((long) 4);

		IDGenerator.initialize(DBVersion.class, current.getId() + 1);
		DBVersion result = provider.getNextVersion(current);

		assertEquals(reference.getFileChanges().size(), result.getFileChanges()
				.size());
		assertEquals(reference.getRevision().getIdentifier(), result
				.getRevision().getIdentifier());
		assertEquals(reference.getSourceFiles().size(), result.getSourceFiles()
				.size());
		assertEquals(reference.getRawCloneClasses().size(), result
				.getRawCloneClasses().size());
	}

	@Test
	public void testGetNextVersion7() throws Exception {
		DBVersion current = parser.getVersions().get((long) 4);

		IDGenerator.initialize(DBVersion.class, current.getId() + 1);
		DBVersion result = provider.getNextVersion(current);

		assertNull(result);
	}

	private static Set<FileChangeEntry> getFileChangeEntries(
			final Collection<DBFileChange> fileChanges) {
		final Set<FileChangeEntry> fileChangeEntries = new HashSet<FileChangeEntry>();

		for (final DBFileChange fileChange : fileChanges) {
			final String oldPath = (fileChange.getOldSourceFile() == null) ? null
					: fileChange.getOldSourceFile().getPath();
			final String newPath = (fileChange.getNewSourceFile() == null) ? null
					: fileChange.getNewSourceFile().getPath();
			char type;
			switch (fileChange.getType()) {
			case ADD:
				type = 'A';
				break;
			case DELETE:
				type = 'D';
				break;
			case MODIFY:
				type = 'M';
				break;
			case RELOCATE:
				type = 'R';
				break;
			default:
				type = 'U';
				break;
			}

			fileChangeEntries.add(new FileChangeEntry(oldPath, newPath, type));
		}

		return fileChangeEntries;
	}

}
