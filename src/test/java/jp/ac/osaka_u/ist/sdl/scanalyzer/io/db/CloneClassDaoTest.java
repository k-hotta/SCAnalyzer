package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CloneClassDaoTest {

	private static final String TEST_DB_XML_PATH = "src/test/resources/test-db.xml";

	private static DBXmlParser parser;

	private static TestDBConnection connection;

	private static CloneClassDao dao;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		parser = new DBXmlParser(TEST_DB_XML_PATH);
		parser.parse();
		connection = TestDBConnection.create(parser);
		connection.initializeTables();
		connection.storeAll(parser);
		dao = DBManager.getInstance().getCloneClassDao();
	}

	@Before
	public void setUp() throws Exception {
		connection.initializeTable(CloneClass.class);
		for (final CloneClass cloneClass : parser.getCloneClasses().values()) {
			connection.storeCloneClassWithNativeWay(cloneClass);
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
	
	private boolean check(final CloneClass result,
			final CloneClass reference) {
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

		if (result.getCodeFragments().size() != reference.getCodeFragments().size()) {
			return false;
		}

		return true;
	}

	@Test
	public void testGet1() throws Exception {
		final long id = 1;
		final CloneClass reference = parser.getCloneClasses().get(id);
		final CloneClass result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet2() throws Exception {
		final long id = -1;
		final CloneClass reference = parser.getCloneClasses().get(id);
		final CloneClass result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet3() throws Exception {
		final long id1 = 1;
		final long id2 = 2;
		final List<CloneClass> results = dao.get(id1, id2);

		assertTrue(results.size() == 2);
		for (final CloneClass result : results) {
			final CloneClass reference = parser.getCloneClasses().get(
					result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGet4() throws Exception {
		final long id1 = 1;
		final long id2 = -1;
		final List<CloneClass> results = dao.get(id1, id2);

		assertTrue(results.size() == 1);
		for (final CloneClass result : results) {
			final CloneClass reference = parser.getCloneClasses().get(
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
		final List<CloneClass> results = dao.get(ids);

		assertTrue(results.size() == 2);
		for (final CloneClass result : results) {
			final CloneClass reference = parser.getCloneClasses().get(
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
		final List<CloneClass> results = dao.get(ids);

		assertTrue(results.size() == 1);
		for (final CloneClass result : results) {
			final CloneClass reference = parser.getCloneClasses().get(
					result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGetAll1() throws Exception {
		final Map<Long, CloneClass> references = parser.getCloneClasses();
		final Collection<CloneClass> results = dao.getAll();

		assertTrue(results.size() == references.size());
		for (final CloneClass result : results) {
			final CloneClass reference = references.get(result.getId());
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
		connection.initializeTable(CloneClass.class); // clear tables

		final Map<Long, CloneClass> references = parser.getCloneClasses();
		final CloneClass reference = references.get((long) 1);

		dao.register(reference);
		final CloneClass result = dao.get((long) 1);

		assertTrue(check(result, reference));
	}

	@Test
	public void testRegister3() throws Exception {
		connection.initializeTable(CloneClass.class); // clear tables

		final Map<Long, CloneClass> references = parser.getCloneClasses();
		dao.registerAll(references.values());

		final List<CloneClass> results = dao.getAll();

		assertTrue(results.size() == references.size());
		for (final CloneClass result : results) {
			final CloneClass reference = references.get(result.getId());
			assertTrue(check(result, reference));
		}
	}

}
