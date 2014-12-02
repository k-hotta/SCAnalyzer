package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class has test cases for {@link DBRevision}.
 * 
 * @author k-hotta
 * 
 */
public class RevisionDaoTest {

	private static final String TEST_DB_XML_PATH = "src/test/resources/test-db.xml";

	private static DBXmlParser parser;

	private static TestDBConnection connection;

	private static RevisionDao dao;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		parser = new DBXmlParser(TEST_DB_XML_PATH);
		parser.parse();
		connection = TestDBConnection.create(parser);
		connection.initializeTables();
		connection.storeAll(parser);
		dao = DBManager.getInstance().getRevisionDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		connection.close();
	}

	@Before
	public void setUp() throws Exception {
		connection.initializeTable(DBRevision.class);
		for (final DBRevision revision : parser.getRevisions().values()) {
			connection.storeRevisionWithNativeWay(revision);
		}
	}
	
	@After
	public void tearDown() throws Exception {
		DBManager.getInstance().clearDaos();
	}

	private boolean check(final DBRevision result, final DBRevision reference) {
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

		if (!result.getIdentifier().equals(reference.getIdentifier())) {
			return false;
		}

		if (reference.getDate() != null) {
			if (result.getDate() == null) {
				return false;
			}

			if (!result.getDate().equals(reference.getDate())) {
				return false;
			}
		}

		return true;
	}

	@Test
	public void testGet1() throws Exception {
		final long id = 1;
		final DBRevision reference = parser.getRevisions().get(id);
		final DBRevision result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet2() throws Exception {
		final long id = -1;
		final DBRevision reference = parser.getRevisions().get(id);
		final DBRevision result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet3() throws Exception {
		final long id1 = 1;
		final long id2 = 2;
		final Collection<DBRevision> results = dao.get(id1, id2).values();

		assertTrue(results.size() == 2);
		for (final DBRevision result : results) {
			final DBRevision reference = parser.getRevisions()
					.get(result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGet4() throws Exception {
		final long id1 = 1;
		final long id2 = -1;
		final Collection<DBRevision> results = dao.get(id1, id2).values();

		assertTrue(results.size() == 1);
		for (final DBRevision result : results) {
			final DBRevision reference = parser.getRevisions()
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
		final Collection<DBRevision> results = dao.get(ids).values();

		assertTrue(results.size() == 2);
		for (final DBRevision result : results) {
			final DBRevision reference = parser.getRevisions()
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
		final Collection<DBRevision> results = dao.get(ids).values();

		assertTrue(results.size() == 1);
		for (final DBRevision result : results) {
			final DBRevision reference = parser.getRevisions()
					.get(result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGetAll1() throws Exception {
		final Map<Long, DBRevision> references = parser.getRevisions();
		final Collection<DBRevision> results = dao.getAll();

		assertTrue(results.size() == references.size());
		for (final DBRevision result : results) {
			final DBRevision reference = references.get(result.getId());
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
		connection.initializeTable(DBRevision.class); // clear tables

		final Map<Long, DBRevision> references = parser.getRevisions();
		final DBRevision reference = references.get((long) 1);

		dao.register(reference);
		final DBRevision result = dao.get((long) 1);

		assertTrue(check(result, reference));
	}
	
	@Test
	public void testRegister3() throws Exception {
		connection.initializeTable(DBRevision.class); // clear tables

		final Map<Long, DBRevision> references = parser.getRevisions();
		dao.registerAll(references.values());

		final Collection<DBRevision> results = dao.getAll();

		assertTrue(results.size() == references.size());
		for (final DBRevision result : results) {
			final DBRevision reference = references.get(result.getId());
			assertTrue(check(result, reference));
		}
	}
	
}
