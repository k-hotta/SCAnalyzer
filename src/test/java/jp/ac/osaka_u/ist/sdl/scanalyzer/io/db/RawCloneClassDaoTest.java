package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawCloneClass;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class has test cases for {@link RawCloneClassDao}
 * 
 * @author k-hotta
 * 
 */
public class RawCloneClassDaoTest {

	private static final String TEST_DB_XML_PATH = "src/test/resources/test-db.xml";

	private static DBXmlParser parser;

	private static TestDBConnection connection;

	private static RawCloneClassDao dao;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		parser = new DBXmlParser(TEST_DB_XML_PATH);
		parser.parse();
		connection = TestDBConnection.create(parser);
		connection.initializeTables();
		connection.storeAll(parser);
		dao = DBManager.getInstance().getRawCloneClassDao();
	}

	@Before
	public void setUp() throws Exception {
		connection.initializeTable(DBRawCloneClass.class);
		for (final DBRawCloneClass rawCloneClass : parser.getRawCloneClasses()
				.values()) {
			connection.storeRawCloneClassWithNativeWay(rawCloneClass);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		connection.close();
	}

	@After
	public void tearDown() throws Exception {
		DBManager.getInstance().clearDaos();
	}

	private boolean check(final DBRawCloneClass result,
			final DBRawCloneClass reference) {
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

		if (result.getVersion().getId() != reference.getVersion().getId()) {
			return false;
		}

		if (result.getElements().size() != reference.getElements().size()) {
			return false;
		}

		return true;
	}

	@Test
	public void testGet1() throws Exception {
		final long id = 1;
		final DBRawCloneClass reference = parser.getRawCloneClasses().get(id);
		final DBRawCloneClass result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet2() throws Exception {
		final long id = -1;
		final DBRawCloneClass reference = parser.getRawCloneClasses().get(id);
		final DBRawCloneClass result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet3() throws Exception {
		final long id1 = 1;
		final long id2 = 2;
		final List<DBRawCloneClass> results = dao.get(id1, id2);

		assertTrue(results.size() == 2);
		for (final DBRawCloneClass result : results) {
			final DBRawCloneClass reference = parser.getRawCloneClasses().get(
					result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGet4() throws Exception {
		final long id1 = 1;
		final long id2 = -1;
		final List<DBRawCloneClass> results = dao.get(id1, id2);

		assertTrue(results.size() == 1);
		for (final DBRawCloneClass result : results) {
			final DBRawCloneClass reference = parser.getRawCloneClasses().get(
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
		final List<DBRawCloneClass> results = dao.get(ids);

		assertTrue(results.size() == 2);
		for (final DBRawCloneClass result : results) {
			final DBRawCloneClass reference = parser.getRawCloneClasses().get(
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
		final List<DBRawCloneClass> results = dao.get(ids);

		assertTrue(results.size() == 1);
		for (final DBRawCloneClass result : results) {
			final DBRawCloneClass reference = parser.getRawCloneClasses().get(
					result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGetAll1() throws Exception {
		final Map<Long, DBRawCloneClass> references = parser.getRawCloneClasses();
		final Collection<DBRawCloneClass> results = dao.getAll();

		assertTrue(results.size() == references.size());
		for (final DBRawCloneClass result : results) {
			final DBRawCloneClass reference = references.get(result.getId());
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
		connection.initializeTable(DBRawCloneClass.class); // clear tables

		final Map<Long, DBRawCloneClass> references = parser.getRawCloneClasses();
		final DBRawCloneClass reference = references.get((long) 1);

		dao.register(reference);
		final DBRawCloneClass result = dao.get((long) 1);

		assertTrue(check(result, reference));
	}

	@Test
	public void testRegister3() throws Exception {
		connection.initializeTable(DBRawCloneClass.class); // clear tables

		final Map<Long, DBRawCloneClass> references = parser.getRawCloneClasses();
		dao.registerAll(references.values());

		final List<DBRawCloneClass> results = dao.getAll();

		assertTrue(results.size() == references.size());
		for (final DBRawCloneClass result : results) {
			final DBRawCloneClass reference = references.get(result.getId());
			assertTrue(check(result, reference));
		}
	}

}
