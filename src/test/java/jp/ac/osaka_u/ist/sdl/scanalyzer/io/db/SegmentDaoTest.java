package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBSegment;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SegmentDaoTest {

	private static final String TEST_DB_XML_PATH = "src/test/resources/test-db.xml";

	private static DBXmlParser parser;

	private static TestDBConnection connection;

	private static SegmentDao dao;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		parser = new DBXmlParser(TEST_DB_XML_PATH);
		parser.parse();
		connection = TestDBConnection.create(parser);
		connection.initializeTables();
		connection.storeAll(parser);
		dao = DBManager.getInstance().getSegmentDao();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		connection.close();
	}

	@Before
	public void setUp() throws Exception {
		connection.initializeTable(DBSegment.class);
		for (final DBSegment segment : parser.getSegments().values()) {
			connection.storeSegmentWithNativeWay(segment);
		}
	}

	@After
	public void tearDown() throws Exception {
		DBManager.getInstance().clearDaos();
	}

	private boolean check(final DBSegment result, final DBSegment reference) {
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

		if (result.getCodeFragment().getId() != reference.getCodeFragment()
				.getId()) {
			return false;
		}

		if (result.getSourceFile().getId() != reference.getSourceFile().getId()) {
			return false;
		}

		if (result.getStartPosition() != reference.getStartPosition()) {
			return false;
		}

		if (result.getEndPosition() != reference.getEndPosition()) {
			return false;
		}

		return true;
	}

	@Test
	public void testGet1() throws Exception {
		final long id = 1;
		final DBSegment reference = parser.getSegments().get(id);
		final DBSegment result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet2() throws Exception {
		final long id = -1;
		final DBSegment reference = parser.getSegments().get(id);
		final DBSegment result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet3() throws Exception {
		final long id1 = 1;
		final long id2 = 2;
		final List<DBSegment> results = dao.get(id1, id2);

		assertTrue(results.size() == 2);
		for (final DBSegment result : results) {
			final DBSegment reference = parser.getSegments().get(result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGet4() throws Exception {
		final long id1 = 1;
		final long id2 = -1;
		final List<DBSegment> results = dao.get(id1, id2);

		assertTrue(results.size() == 1);
		for (final DBSegment result : results) {
			final DBSegment reference = parser.getSegments().get(result.getId());
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
		final List<DBSegment> results = dao.get(ids);

		assertTrue(results.size() == 2);
		for (final DBSegment result : results) {
			final DBSegment reference = parser.getSegments().get(result.getId());
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
		final List<DBSegment> results = dao.get(ids);

		assertTrue(results.size() == 1);
		for (final DBSegment result : results) {
			final DBSegment reference = parser.getSegments().get(result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGetAll() throws Exception {
		final Map<Long, DBSegment> references = parser.getSegments();
		final Collection<DBSegment> results = dao.getAll();

		assertTrue(results.size() == references.size());
		for (final DBSegment result : results) {
			final DBSegment reference = references.get(result.getId());
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
		connection.initializeTable(DBSegment.class); // clear tables

		final Map<Long, DBSegment> references = parser.getSegments();
		final DBSegment sg1 = references.get((long) 1);

		dao.register(sg1);
		final DBSegment result = dao.get((long) 1);

		assertTrue(check(result, sg1));
	}

	@Test
	public void testRegister3() throws Exception {
		connection.initializeTable(DBSegment.class); // clear tables

		final Map<Long, DBSegment> references = parser.getSegments();

		dao.registerAll(references.values());
		
		List<DBSegment> results = dao.getAll();

		assertTrue(results.size() == references.size());
		for (final DBSegment result : results) {
			final DBSegment reference = references.get(result.getId());
			assertTrue(check(result, reference));
		}
	}
	
}
