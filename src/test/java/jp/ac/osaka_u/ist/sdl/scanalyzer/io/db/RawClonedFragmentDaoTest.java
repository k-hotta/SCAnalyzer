package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawClonedFragment;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class has test cases for {@link RawClonedFragmentDao}
 * 
 * @author k-hotta
 * 
 */
public class RawClonedFragmentDaoTest {

	private static final String TEST_DB_XML_PATH = "src/test/resources/test-db.xml";

	private static DBXmlParser parser;

	private static TestDBConnection connection;

	private static RawClonedFragmentDao dao;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		parser = new DBXmlParser(TEST_DB_XML_PATH);
		parser.parse();
		connection = TestDBConnection.create(parser);
		connection.initializeTables();
		connection.storeAll(parser);
		dao = DBManager.getInstance().getRawClonedFragmentDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		connection.close();
	}

	@After
	public void tearDown() throws Exception {
		DBManager.getInstance().clearDaos();
	}

	@Before
	public void setUp() throws Exception {
		connection.initializeTable(DBRawClonedFragment.class);
		for (final DBRawClonedFragment rawClonedFragment : parser
				.getRawClonedFragments().values()) {
			connection.storeRawClonedFragmentWithNativeWay(rawClonedFragment);
		}
	}

	private boolean check(final DBRawClonedFragment result,
			final DBRawClonedFragment reference) {
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

		if (result.getCloneClass().getId() != reference.getCloneClass().getId()) {
			return false;
		}

		if (result.getSourceFile().getId() != reference.getSourceFile().getId()) {
			return false;
		}

		if (result.getVersion().getId() != reference.getVersion().getId()) {
			return false;
		}

		if (result.getStartLine() != reference.getStartLine()) {
			return false;
		}

		if (result.getLength() != reference.getLength()) {
			return false;
		}

		return true;
	}

	@Test
	public void testGet1() throws Exception {
		final long id = 1;
		final DBRawClonedFragment reference = parser.getRawClonedFragments()
				.get(id);
		final DBRawClonedFragment result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet2() throws Exception {
		final long id = -1;
		final DBRawClonedFragment reference = parser.getRawClonedFragments()
				.get(id);
		final DBRawClonedFragment result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet3() throws Exception {
		final long id1 = 1;
		final long id2 = 2;
		final Collection<DBRawClonedFragment> results = dao.get(id1, id2)
				.values();

		assertTrue(results.size() == 2);
		for (final DBRawClonedFragment result : results) {
			final DBRawClonedFragment reference = parser
					.getRawClonedFragments().get(result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGet4() throws Exception {
		final long id1 = 1;
		final long id2 = -1;
		final Collection<DBRawClonedFragment> results = dao.get(id1, id2)
				.values();

		assertTrue(results.size() == 1);
		for (final DBRawClonedFragment result : results) {
			final DBRawClonedFragment reference = parser
					.getRawClonedFragments().get(result.getId());
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
		final Collection<DBRawClonedFragment> results = dao.get(ids).values();

		assertTrue(results.size() == 2);
		for (final DBRawClonedFragment result : results) {
			final DBRawClonedFragment reference = parser
					.getRawClonedFragments().get(result.getId());
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
		final Collection<DBRawClonedFragment> results = dao.get(ids).values();

		assertTrue(results.size() == 1);
		for (final DBRawClonedFragment result : results) {
			final DBRawClonedFragment reference = parser
					.getRawClonedFragments().get(result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGetAll1() throws Exception {
		final Map<Long, DBRawClonedFragment> references = parser
				.getRawClonedFragments();
		final Collection<DBRawClonedFragment> results = dao.getAll().values();

		assertTrue(results.size() == references.size());
		for (final DBRawClonedFragment result : results) {
			final DBRawClonedFragment reference = references
					.get(result.getId());
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
		connection.initializeTable(DBRawClonedFragment.class); // clear tables

		final Map<Long, DBRawClonedFragment> references = parser
				.getRawClonedFragments();
		final DBRawClonedFragment reference = references.get((long) 1);

		dao.register(reference);
		final DBRawClonedFragment result = dao.get((long) 1);

		assertTrue(check(result, reference));
	}

	@Test
	public void testRegister3() throws Exception {
		connection.initializeTable(DBRawClonedFragment.class); // clear tables

		final Map<Long, DBRawClonedFragment> references = parser
				.getRawClonedFragments();
		dao.registerAll(references.values());

		final Collection<DBRawClonedFragment> results = dao.getAll().values();

		assertTrue(results.size() == references.size());
		for (final DBRawClonedFragment result : results) {
			final DBRawClonedFragment reference = references
					.get(result.getId());
			assertTrue(check(result, reference));
		}
	}

}
