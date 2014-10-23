package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;

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
		connection.initializeTable(RawCloneClass.class);
		for (final RawCloneClass rawCloneClass : parser.getRawCloneClasses().values()) {
			connection.storeRawCloneClassWithNativeWay(rawCloneClass);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		connection.close();
	}

	private boolean check(final RawCloneClass result,
			final RawCloneClass reference) {
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

		if (!result.getElements().equals(result.getElements())) {
			return false;
		}

		return true;
	}

	@Test
	public void testGet1() throws Exception {
		final long id = 1;
		final RawCloneClass reference = parser.getRawCloneClasses().get(id);
		final RawCloneClass result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet2() throws Exception {
		final long id = -1;
		final RawCloneClass reference = parser.getRawCloneClasses().get(id);
		final RawCloneClass result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet3() throws Exception {
		final long id1 = 1;
		final long id2 = 2;
		final List<RawCloneClass> results = dao.get(id1, id2);

		assertTrue(results.size() == 2);
		for (final RawCloneClass result : results) {
			final RawCloneClass reference = parser.getRawCloneClasses().get(
					result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGet4() throws Exception {
		final long id1 = 1;
		final long id2 = -1;
		final List<RawCloneClass> results = dao.get(id1, id2);

		assertTrue(results.size() == 1);
		for (final RawCloneClass result : results) {
			final RawCloneClass reference = parser.getRawCloneClasses().get(
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
		final List<RawCloneClass> results = dao.get(ids);

		assertTrue(results.size() == 2);
		for (final RawCloneClass result : results) {
			final RawCloneClass reference = parser.getRawCloneClasses().get(
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
		final List<RawCloneClass> results = dao.get(ids);

		assertTrue(results.size() == 1);
		for (final RawCloneClass result : results) {
			final RawCloneClass reference = parser.getRawCloneClasses().get(
					result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGetAll1() throws Exception {
		final Map<Long, RawCloneClass> references = parser.getRawCloneClasses();
		final Collection<RawCloneClass> results = dao.getAll();

		assertTrue(results.size() == references.size());
		for (final RawCloneClass result : results) {
			final RawCloneClass reference = references.get(result.getId());
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
		connection.initializeTable(RawCloneClass.class); // clear tables

		final Map<Long, RawCloneClass> references = parser.getRawCloneClasses();
		final RawCloneClass reference = references.get((long) 1);

		dao.register(reference);
		final RawCloneClass result = dao.get((long) 1);

		assertTrue(check(result, reference));
	}

	@Test
	public void testRegister3() throws Exception {
		connection.initializeTable(RawCloneClass.class); // clear tables

		final Map<Long, RawCloneClass> references = parser.getRawCloneClasses();
		dao.registerAll(references.values());

		final List<RawCloneClass> results = dao.getAll();

		assertTrue(results.size() == references.size());
		for (final RawCloneClass result : results) {
			final RawCloneClass reference = references.get(result.getId());
			assertTrue(check(result, reference));
		}
	}

}
