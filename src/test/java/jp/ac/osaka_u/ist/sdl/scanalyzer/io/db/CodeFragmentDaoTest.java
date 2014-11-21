package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CodeFragmentDaoTest {

	private static final String TEST_DB_XML_PATH = "src/test/resources/test-db.xml";

	private static DBXmlParser parser;

	private static TestDBConnection connection;

	private static CodeFragmentDao dao;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		parser = new DBXmlParser(TEST_DB_XML_PATH);
		parser.parse();
		connection = TestDBConnection.create(parser);
		connection.initializeTables();
		connection.storeAll(parser);
		dao = DBManager.getInstance().getCodeFragmentDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		connection.close();
	}

	@Before
	public void setUp() throws Exception {
		connection.initializeTable(DBCodeFragment.class);
		for (final DBCodeFragment segment : parser.getCodeFragments().values()) {
			connection.storeCodeFragmentWithNativeWay(segment);
		}
	}

	@After
	public void tearDown() throws Exception {
		DBManager.getInstance().clearDaos();
	}

	private boolean check(final DBCodeFragment result,
			final DBCodeFragment reference) {
		if (result == null && reference == null) {
			return true;
		}

		if (result == null && reference != null) {
			return false;
		} else if (result != null && reference == null) {
			return false;
		}

		if (result.getCloneClass().getId() != reference.getCloneClass().getId()) {
			return false;
		}

		if (result.getSegments().size() != reference.getSegments().size()) {
			return false;
		}

		return true;
	}

	@Test
	public void testGet1() throws Exception {
		final long id = 1;
		final DBCodeFragment reference = parser.getCodeFragments().get(id);
		final DBCodeFragment result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet2() throws Exception {
		final long id = -1;
		final DBCodeFragment reference = parser.getCodeFragments().get(id);
		final DBCodeFragment result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet3() throws Exception {
		final long id1 = 1;
		final long id2 = 2;
		final List<DBCodeFragment> results = dao.get(id1, id2);

		assertTrue(results.size() == 2);
		for (final DBCodeFragment result : results) {
			final DBCodeFragment reference = parser.getCodeFragments().get(
					result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGet4() throws Exception {
		final long id1 = 1;
		final long id2 = -1;
		final List<DBCodeFragment> results = dao.get(id1, id2);

		assertTrue(results.size() == 1);
		for (final DBCodeFragment result : results) {
			final DBCodeFragment reference = parser.getCodeFragments().get(
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
		final List<DBCodeFragment> results = dao.get(ids);

		assertTrue(results.size() == 2);
		for (final DBCodeFragment result : results) {
			final DBCodeFragment reference = parser.getCodeFragments().get(
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
		final List<DBCodeFragment> results = dao.get(ids);

		assertTrue(results.size() == 1);
		for (final DBCodeFragment result : results) {
			final DBCodeFragment reference = parser.getCodeFragments().get(
					result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGetAll() throws Exception {
		final Map<Long, DBCodeFragment> references = parser.getCodeFragments();
		final Collection<DBCodeFragment> results = dao.getAll();

		assertTrue(results.size() == references.size());
		for (final DBCodeFragment result : results) {
			final DBCodeFragment reference = references.get(result.getId());
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
		connection.initializeTable(DBCodeFragment.class); // clear tables

		final Map<Long, DBCodeFragment> references = parser.getCodeFragments();
		final DBCodeFragment sg1 = references.get((long) 1);

		dao.register(sg1);
		final DBCodeFragment result = dao.get((long) 1);

		assertTrue(check(result, sg1));
	}

	@Test
	public void testRegister3() throws Exception {
		connection.initializeTable(DBCodeFragment.class); // clear tables

		final Map<Long, DBCodeFragment> references = parser.getCodeFragments();

		dao.registerAll(references.values());

		Collection<DBCodeFragment> results = dao.getAll();

		assertTrue(results.size() == references.size());
		for (final DBCodeFragment result : results) {
			final DBCodeFragment reference = references.get(result.getId());
			assertTrue(check(result, reference));
		}
	}

}
