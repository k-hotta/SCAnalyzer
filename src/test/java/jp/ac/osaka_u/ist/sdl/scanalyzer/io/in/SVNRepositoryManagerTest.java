package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.io.Language;

import org.junit.Test;

public class SVNRepositoryManagerTest {

	private static final String PATH_OF_TEST_REPO = "src/test/resources/repository-clonetracker";

	private static final String RELATIVE_PATH_FOR_TEST = "/c20r_main/src";

	private static final String RELATIVE_PATH_FOR_TEST2 = "c20r_main/src/jp/ac/osaka_u/ist/sdl/c20r/rev_analyzer/ast/";

	@Test
	public void test1() {
		final String path = "src\\test\\resources\\repository-clonetracker";
		final String relativePath = null;

		try {
			final SVNRepositoryManager manager = new SVNRepositoryManager(path,
					relativePath, Language.JAVA);
			assertTrue(manager.getUrl().toString().startsWith("file://")
					&& manager
							.getUrl()
							.toString()
							.endsWith(
									"/src/test/resources/repository-clonetracker"));
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test2() {
		final String path = "src\\test\\resources\\repository-clonetracker";
		final String relativePath = "trunk";

		try {
			final SVNRepositoryManager manager = new SVNRepositoryManager(path,
					relativePath, Language.JAVA);
			assertTrue(manager.getUrl().toString().startsWith("file://")
					&& manager
							.getUrl()
							.toString()
							.endsWith(
									"/src/test/resources/repository-clonetracker/trunk"));
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test3() {
		final String path = "src/test/resources/repository-clonetracker/";
		final String relativePath = "/trunk";

		try {
			final SVNRepositoryManager manager = new SVNRepositoryManager(path,
					relativePath, Language.JAVA);
			assertTrue(manager.getUrl().toString().startsWith("file://")
					&& manager
							.getUrl()
							.toString()
							.endsWith(
									"/src/test/resources/repository-clonetracker/trunk"));
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test4() {
		final String path = "file:///src/test/resources/repository-clonetracker/";
		final String relativePath = "trunk";

		try {
			final SVNRepositoryManager manager = new SVNRepositoryManager(path,
					relativePath, Language.JAVA);
			assertTrue(manager.getUrl().toString().startsWith("file://")
					&& manager
							.getUrl()
							.toString()
							.endsWith(
									"/src/test/resources/repository-clonetracker/trunk"));
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testGetListOfRelativeFiles1() {
		try {
			final SVNRepositoryManager manager = new SVNRepositoryManager(
					PATH_OF_TEST_REPO, null, Language.JAVA);
			final List<String> reference = readResultFile("src/test/resources/clonetracker-list-rev200.txt");
			final List<String> result = manager
					.getListOfRelativeFiles((long) 200);
			assertTrue(reference.size() == result.size());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testGetListOfRelativeFiles2() {
		try {
			final SVNRepositoryManager manager = new SVNRepositoryManager(
					PATH_OF_TEST_REPO, null, Language.JAVA);
			final List<String> reference = readResultFile("src/test/resources/clonetracker-list-rev300.txt");
			final List<String> result = manager
					.getListOfRelativeFiles((long) 300);
			assertTrue(reference.size() == result.size());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testGetListOfRelativeFiles3() {
		try {
			final SVNRepositoryManager manager = new SVNRepositoryManager(
					PATH_OF_TEST_REPO, null, Language.JAVA);
			final List<String> result = manager
					.getListOfRelativeFiles((long) 1000);
			fail(); // here shouldn't be reached
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	@Test
	public void testGetListOfRelativeFiles4() {
		try {
			final SVNRepositoryManager manager = new SVNRepositoryManager(
					PATH_OF_TEST_REPO, RELATIVE_PATH_FOR_TEST, Language.JAVA);
			final List<String> reference = readResultFile("src/test/resources/clonetracker-list-rev300.txt");
			final List<String> result = manager
					.getListOfRelativeFiles((long) 300);
			// there exists a java file out of the relative path
			assertTrue(reference.size() - 1 == result.size());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testGetListOfRelativeFiles5() {
		try {
			final SVNRepositoryManager manager = new SVNRepositoryManager(
					PATH_OF_TEST_REPO, RELATIVE_PATH_FOR_TEST2, Language.JAVA);
			final List<String> result = manager
					.getListOfRelativeFiles((long) 300);
			assertTrue(result.size() == 9);
		} catch (Exception e) {
			fail();
		}
	}

	private List<String> readResultFile(final String path) throws Exception {
		final List<String> result = new ArrayList<String>();
		final BufferedReader br = new BufferedReader(new FileReader(new File(
				path)));

		String line;
		while ((line = br.readLine()) != null) {
			if (Language.JAVA.isRelativeFile(line)) {
				result.add(line);
			}
		}

		br.close();
		return result;
	}

}
