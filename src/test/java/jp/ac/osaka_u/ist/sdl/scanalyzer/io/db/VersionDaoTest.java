package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.VersionSourceFile;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class VersionDaoTest {

	private static final String TEST_DB_XML_PATH = "src/test/resources/test-db.xml";

	private static DBXmlParser parser;

	private static TestDBConnection connection;

	private static VersionDao dao;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		parser = new DBXmlParser(TEST_DB_XML_PATH);
		parser.parse();
		connection = TestDBConnection.create(parser);
		connection.initializeTables();
		connection.storeAll(parser);
		dao = DBManager.getInstance().getVersionDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		connection.close();
	}

	@Before
	public void setUp() throws Exception {
		connection.initializeTable(Version.class);
		connection.initializeTable(VersionSourceFile.class);
		for (final Version version : parser.getVersions().values()) {
			connection.storeVersionWithNativeWay(version);
		}
		for (final VersionSourceFile vsf : parser.getVersionSourceFiles()
				.values()) {
			connection.storeVersionSourceFileWithNativeWay(vsf);
		}
	}

	@After
	public void tearDown() throws Exception {
		DBManager.getInstance().clearDaos();
	}

	private boolean check(final Version result, final Version reference) {
		if (result == null && reference == null) {
			return true;
		}

		if (result == null && reference != null) {
			return false;
		} else if (result != null && reference == null) {
			return false;
		}

		if (result.getId() != reference.getId()) {
			return false;
		}

		if (result.getRevision().getId() != reference.getRevision().getId()) {
			return false;
		}

		if (result.getFileChanges().size() != reference.getFileChanges().size()) {
			return false;
		}

		if (!result.getFileChanges().containsAll(reference.getFileChanges())) {
			return false;
		}

		if (!reference.getFileChanges().containsAll(result.getFileChanges())) {
			return false;
		}

		if (result.getRawCloneClasses().size() != reference
				.getRawCloneClasses().size()) {
			return false;
		}

		if (!result.getRawCloneClasses().containsAll(
				reference.getRawCloneClasses())) {
			return false;
		}

		if (!reference.getRawCloneClasses().containsAll(
				result.getRawCloneClasses())) {
			return false;
		}

		if (result.getSourceFiles().size() != reference.getSourceFiles().size()) {
			return false;
		}

		if (!result.getSourceFiles().containsAll(reference.getSourceFiles())) {
			return false;
		}

		if (!reference.getSourceFiles().containsAll(result.getSourceFiles())) {
			return false;
		}

		return true;
	}

	@Test
	public void testGet1() throws Exception {
		final long id = 1;
		final Version reference = parser.getVersions().get(id);
		final Version result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet2() throws Exception {
		final long id = 3;
		final Version reference = parser.getVersions().get(id);
		final Version result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet3() throws Exception {
		final long id = -1;
		final Version reference = parser.getVersions().get(id);
		final Version result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet4() throws Exception {
		final long id1 = 1;
		final long id2 = 2;
		final List<Version> results = dao.get(id1, id2);

		assertTrue(results.size() == 2);
		for (final Version result : results) {
			final Version reference = parser.getVersions().get(result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGet5() throws Exception {
		final long id1 = 1;
		final long id2 = -1;
		final List<Version> results = dao.get(id1, id2);

		assertTrue(results.size() == 1);
		for (final Version result : results) {
			final Version reference = parser.getVersions().get(result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGet6() throws Exception {
		final long id1 = 2;
		final long id2 = 4;
		final List<Long> ids = new ArrayList<Long>();
		ids.add(id1);
		ids.add(id2);
		final List<Version> results = dao.get(ids);

		assertTrue(results.size() == 2);
		for (final Version result : results) {
			final Version reference = parser.getVersions().get(result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGet7() throws Exception {
		final long id1 = -1;
		final long id2 = 3;
		final List<Long> ids = new ArrayList<Long>();
		ids.add(id1);
		ids.add(id2);
		final List<Version> results = dao.get(ids);

		assertTrue(results.size() == 1);
		for (final Version result : results) {
			final Version reference = parser.getVersions().get(result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGetAll() throws Exception {
		final Map<Long, Version> references = parser.getVersions();
		final Collection<Version> results = dao.getAll();

		assertTrue(results.size() == references.size());
		for (final Version result : results) {
			final Version reference = references.get(result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testRegister1() throws Exception {
		boolean caughtException = false;

		try {
			dao.register(null);
		} catch (IllegalStateException e) {
			caughtException = true;
		}

		assertTrue(caughtException);
	}

	@Test
	public void testRegister2() throws Exception {
		connection.initializeTable(Version.class); // clear table
		connection.initializeTable(VersionSourceFile.class);

		final Map<Long, Version> references = parser.getVersions();
		final Version v1 = references.get((long) 1);

		dao.register(v1);
		final Version result = dao.get((long) 1);

		assertTrue(check(result, v1));
	}

	@Test
	public void testRegisterAll1() throws Exception {
		connection.initializeTable(Version.class); // clear table
		connection.initializeTable(VersionSourceFile.class);

		final Map<Long, Version> references = parser.getVersions();
		dao.registerAll(references.values());

		final List<Version> results = dao.getAll();

		assertTrue(results.size() == references.size());
		for (final Version result : results) {
			final Version reference = references.get(result.getId());
			assertTrue(check(result, reference));
		}
	}

}
