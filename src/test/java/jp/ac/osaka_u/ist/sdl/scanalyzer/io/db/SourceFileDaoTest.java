package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SourceFileDaoTest {

	private static final String TEST_DB_XML_PATH = "src/test/resources/test-db.xml";

	private static DBXmlParser parser;

	private static TestDBConnection connection;

	private static SourceFileDao dao;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		parser = new DBXmlParser(TEST_DB_XML_PATH);
		parser.parse();
		connection = TestDBConnection.create(parser);
		connection.initializeTables();
		connection.storeAll(parser);
		dao = DBManager.getInstance().getSourceFileDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		connection.close();
	}

	@Before
	public void setUp() throws Exception {
		connection.initializeTable(DBSourceFile.class);
		for (final DBSourceFile sourceFile : parser.getSourceFiles().values()) {
			connection.storeSourceFileWithNativeWay(sourceFile);
		}
	}

	@After
	public void tearDown() throws Exception {
		DBManager.getInstance().clearDaos();
	}

	private boolean check(final DBSourceFile result,
			final DBSourceFile reference) {
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

		if (!result.getPath().equals(reference.getPath())) {
			return false;
		}

		return true;
	}

	@Test
	public void testGet1() throws Exception {
		final long id = 1;
		final DBSourceFile reference = parser.getSourceFiles().get(id);
		final DBSourceFile result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet2() throws Exception {
		final long id = -1;
		final DBSourceFile reference = parser.getSourceFiles().get(id);
		final DBSourceFile result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet3() throws Exception {
		final long id1 = 1;
		final long id2 = 2;
		final Collection<DBSourceFile> results = dao.get(id1, id2).values();

		assertTrue(results.size() == 2);
		for (final DBSourceFile result : results) {
			final DBSourceFile reference = parser.getSourceFiles().get(
					result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGet4() throws Exception {
		final long id1 = 1;
		final long id2 = -1;
		final Collection<DBSourceFile> results = dao.get(id1, id2).values();

		assertTrue(results.size() == 1);
		for (final DBSourceFile result : results) {
			final DBSourceFile reference = parser.getSourceFiles().get(
					result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGet5() throws Exception {
		final long id1 = 1;
		final long id2 = 2;
		final List<Long> ids = new ArrayList<Long>();
		ids.add(id1);
		ids.add(id2);
		final Collection<DBSourceFile> results = dao.get(ids).values();

		assertTrue(results.size() == 2);
		for (final DBSourceFile result : results) {
			final DBSourceFile reference = parser.getSourceFiles().get(
					result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGet6() throws Exception {
		final long id1 = 1;
		final long id2 = -1;
		final List<Long> ids = new ArrayList<Long>();
		ids.add(id1);
		ids.add(id2);
		final Collection<DBSourceFile> results = dao.get(ids).values();

		assertTrue(results.size() == 1);
		for (final DBSourceFile result : results) {
			final DBSourceFile reference = parser.getSourceFiles().get(
					result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGetAll() throws Exception {
		final Map<Long, DBSourceFile> references = parser.getSourceFiles();
		final Collection<DBSourceFile> results = dao.getAll().values();

		assertTrue(results.size() == references.size());
		for (final DBSourceFile result : results) {
			final DBSourceFile reference = references.get(result.getId());
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
		connection.initializeTable(DBSourceFile.class); // clear tables

		final Map<Long, DBSourceFile> sourceFiles = parser.getSourceFiles();
		final DBSourceFile sf1 = sourceFiles.get((long) 1);

		dao.register(sf1);
		final DBSourceFile result = dao.get((long) 1);

		assertTrue(check(result, sf1));
	}

	@Test
	public void testRegisterAll1() throws Exception {
		connection.initializeTable(DBSourceFile.class); // clear tables

		final Map<Long, DBSourceFile> references = parser.getSourceFiles();
		dao.registerAll(references.values());

		final Collection<DBSourceFile> results = dao.getAll().values();

		assertTrue(results.size() == references.size());
		for (final DBSourceFile result : results) {
			final DBSourceFile reference = references.get(result.getId());
			assertTrue(check(result, reference));
		}
	}

}
