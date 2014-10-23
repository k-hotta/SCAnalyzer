package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;

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
		connection.initializeTable(SourceFile.class);
		for (final SourceFile sourceFile : parser.getSourceFiles().values()) {
			connection.storeSourceFileWithNativeWay(sourceFile);
		}
	}
	
	@After
	public void tearDown() throws Exception {
		DBManager.getInstance().clearDaos();
	}

	private boolean check(final SourceFile result, final SourceFile reference) {
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
		final SourceFile reference = parser.getSourceFiles().get(id);
		final SourceFile result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet2() throws Exception {
		final long id = -1;
		final SourceFile reference = parser.getSourceFiles().get(id);
		final SourceFile result = dao.get(id);

		assertTrue(check(result, reference));
	}

	@Test
	public void testGet3() throws Exception {
		final long id1 = 1;
		final long id2 = 2;
		final List<SourceFile> results = dao.get(id1, id2);

		assertTrue(results.size() == 2);
		for (final SourceFile result : results) {
			final SourceFile reference = parser.getSourceFiles().get(
					result.getId());
			assertTrue(check(result, reference));
		}
	}
	
	@Test
	public void testGet4() throws Exception {
		final long id1 = 1;
		final long id2 = -1;
		final List<SourceFile> results = dao.get(id1, id2);

		assertTrue(results.size() == 1);
		for (final SourceFile result : results) {
			final SourceFile reference = parser.getSourceFiles().get(
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
		final List<SourceFile> results = dao.get(ids);

		assertTrue(results.size() == 2);
		for (final SourceFile result : results) {
			final SourceFile reference = parser.getSourceFiles().get(
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
		final List<SourceFile> results = dao.get(ids);

		assertTrue(results.size() == 1);
		for (final SourceFile result : results) {
			final SourceFile reference = parser.getSourceFiles().get(
					result.getId());
			assertTrue(check(result, reference));
		}
	}
	
	@Test
	public void testGetAll() throws Exception {
		final Map<Long, SourceFile> references = parser.getSourceFiles();
		final Collection<SourceFile> results = dao.getAll();
		
		assertTrue(results.size() == references.size());
		for (final SourceFile result : results) {
			final SourceFile reference = references.get(result.getId());
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
		connection.initializeTable(SourceFile.class); // clear tables

		final Map<Long, SourceFile> sourceFiles = parser.getSourceFiles();
		final SourceFile sf1 = sourceFiles.get((long) 1);

		dao.register(sf1);
		final SourceFile result = dao.get((long) 1);

		assertTrue(check(result, sf1));
	}
	
	@Test
	public void testRegisterAll1() throws Exception {
		connection.initializeTable(SourceFile.class); // clear tables

		final Map<Long, SourceFile> references = parser.getSourceFiles();
		dao.registerAll(references.values());
		
		List<SourceFile> results = dao.getAll();

		assertTrue(results.size() == references.size());
		for (final SourceFile result : results) {
			final SourceFile reference = references.get(result.getId());
			assertTrue(check(result, reference));
		}
	}

}
