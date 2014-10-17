package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawClonedFragment;

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
		dao = new RawClonedFragmentDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		connection.close();
	}

	@Before
	public void setUp() throws Exception {
		connection.initializeTables();
		connection.storeAll(parser);
	}

	private boolean check(final RawClonedFragment result,
			final RawClonedFragment reference) {
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
		final RawClonedFragment reference = parser.getRawClonedFragments().get(
				id);
		final RawClonedFragment result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet2() throws Exception {
		final long id = -1;
		final RawClonedFragment reference = parser.getRawClonedFragments().get(
				id);
		final RawClonedFragment result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet3() throws Exception {
		final long id1 = 1;
		final long id2 = 2;
		final List<RawClonedFragment> results = dao.get(id1, id2);

		assertTrue(results.size() == 2);
		for (final RawClonedFragment result : results) {
			final RawClonedFragment reference = parser.getRawClonedFragments()
					.get(result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGet4() throws Exception {
		final long id1 = 1;
		final long id2 = -1;
		final List<RawClonedFragment> results = dao.get(id1, id2);

		assertTrue(results.size() == 1);
		for (final RawClonedFragment result : results) {
			final RawClonedFragment reference = parser.getRawClonedFragments()
					.get(result.getId());
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
		final List<RawClonedFragment> results = dao.get(ids);

		assertTrue(results.size() == 2);
		for (final RawClonedFragment result : results) {
			final RawClonedFragment reference = parser.getRawClonedFragments()
					.get(result.getId());
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
		final List<RawClonedFragment> results = dao.get(ids);

		assertTrue(results.size() == 1);
		for (final RawClonedFragment result : results) {
			final RawClonedFragment reference = parser.getRawClonedFragments()
					.get(result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGetAll1() throws Exception {
		final Map<Long, RawClonedFragment> references = parser
				.getRawClonedFragments();
		final Collection<RawClonedFragment> results = dao.getAll();

		assertTrue(results.size() == references.size());
		for (final RawClonedFragment result : results) {
			final RawClonedFragment reference = references.get(result.getId());
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
		connection.initializeTable(RawClonedFragment.class); // clear tables

		final Map<Long, RawClonedFragment> references = parser
				.getRawClonedFragments();
		final RawClonedFragment reference = references.get((long) 1);

		dao.register(reference);
		final RawClonedFragment result = dao.get((long) 1);

		assertTrue(check(result, reference));
	}

	@Test
	public void testRegister3() throws Exception {
		connection.initializeTable(RawClonedFragment.class); // clear tables

		final Map<Long, RawClonedFragment> references = parser
				.getRawClonedFragments();
		dao.registerAll(references.values());

		final List<RawClonedFragment> results = dao.getAll();

		assertTrue(results.size() == references.size());
		for (final RawClonedFragment result : results) {
			final RawClonedFragment reference = references.get(result.getId());
			assertTrue(check(result, reference));
		}
	}

}