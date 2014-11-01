package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class has test cases for {@link FileChangeDao}
 * 
 * @author k-hotta
 * 
 */
public class FileChangeDaoTest {

	private static final String TEST_DB_XML_PATH = "src/test/resources/test-db.xml";

	private static DBXmlParser parser;

	private static TestDBConnection connection;

	private static FileChangeDao dao;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		parser = new DBXmlParser(TEST_DB_XML_PATH);
		parser.parse();
		connection = TestDBConnection.create(parser);
		connection.initializeTables();
		connection.storeAll(parser);
		dao = DBManager.getInstance().getFileChangeDao();
	}

	@Before
	public void setUp() throws Exception {
		connection.initializeTable(DBFileChange.class);
		for (final DBFileChange fileChange : parser.getFileChanges().values()) {
			connection.storeFileChangeWithNativeWay(fileChange);
		}
	}
	
	@After
	public void tearDown() throws Exception {
		DBManager.getInstance().clearDaos();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		connection.close();
	}

	private boolean check(final DBFileChange result, final DBFileChange reference) {
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

		if (result.getOldSourceFile() != null
				&& reference.getOldSourceFile() == null) {
			return false;
		} else if (result.getOldSourceFile() == null
				&& reference.getOldSourceFile() != null) {
			return false;
		} else if (result.getOldSourceFile() != null
				&& reference.getOldSourceFile() != null) {
			if (result.getOldSourceFile().getId() != reference
					.getOldSourceFile().getId()) {
				return false;
			}
		}

		if (result.getNewSourceFile() != null
				&& reference.getNewSourceFile() == null) {
			return false;
		} else if (result.getNewSourceFile() == null
				&& reference.getNewSourceFile() != null) {
			return false;
		} else if (result.getNewSourceFile() != null
				&& reference.getNewSourceFile() != null) {
			if (result.getNewSourceFile().getId() != reference
					.getNewSourceFile().getId()) {
				return false;
			}
		}

		if (result.getType() != reference.getType()) {
			return false;
		}

		if (result.getVersion().getId() != reference.getVersion().getId()) {
			return false;
		}

		return true;
	}

	@Test
	public void testGet1() throws Exception {
		final long id = 1;
		final DBFileChange reference = parser.getFileChanges().get(id);
		final DBFileChange result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet2() throws Exception {
		final long id = -1;
		final DBFileChange reference = parser.getFileChanges().get(id);
		final DBFileChange result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet3() throws Exception {
		final long id1 = 1;
		final long id2 = 2;
		final List<DBFileChange> results = dao.get(id1, id2);

		assertTrue(results.size() == 2);
		for (final DBFileChange result : results) {
			final DBFileChange reference = parser.getFileChanges().get(
					result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGet4() throws Exception {
		final long id1 = 1;
		final long id2 = -1;
		final List<DBFileChange> results = dao.get(id1, id2);

		assertTrue(results.size() == 1);
		for (final DBFileChange result : results) {
			final DBFileChange reference = parser.getFileChanges().get(
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
		final List<DBFileChange> results = dao.get(ids);

		assertTrue(results.size() == 2);
		for (final DBFileChange result : results) {
			final DBFileChange reference = parser.getFileChanges().get(
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
		final List<DBFileChange> results = dao.get(ids);

		assertTrue(results.size() == 1);
		for (final DBFileChange result : results) {
			final DBFileChange reference = parser.getFileChanges().get(
					result.getId());
			assertTrue(check(result, reference));
		}
	}

	@Test
	public void testGetAll1() throws Exception {
		final Map<Long, DBFileChange> references = parser.getFileChanges();
		final Collection<DBFileChange> results = dao.getAll();

		assertTrue(results.size() == references.size());
		for (final DBFileChange result : results) {
			final DBFileChange reference = references.get(result.getId());
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
		connection.initializeTable(DBFileChange.class); // clear tables

		final Map<Long, DBFileChange> fileChanges = parser.getFileChanges();
		final DBFileChange fc1 = fileChanges.get((long) 1);

		dao.register(fc1);
		final DBFileChange result = dao.get((long) 1);

		assertTrue(check(result, fc1));
	}

	@Test
	public void testRegisterAll1() throws Exception {
		connection.initializeTable(DBFileChange.class); // clear tables
		final Map<Long, DBFileChange> references = parser.getFileChanges();
		dao.registerAll(references.values());
		
		final List<DBFileChange> results = dao.getAll();
		
		assertTrue(results.size() == references.size());
		for (final DBFileChange result : results) {
			final DBFileChange reference = references.get(result.getId());
			assertTrue(check(result, reference));
		}
	}

}
