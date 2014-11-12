package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.svn;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.Language;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.svn.SVNRepositoryManager;

import org.junit.Test;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

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
									"/src/test/resources/repository-clonetracker"));
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
									"/src/test/resources/repository-clonetracker"));
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
									"/src/test/resources/repository-clonetracker"));
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testGetListOfRelevantFiles1() {
		try {
			final SVNRepositoryManager manager = new SVNRepositoryManager(
					PATH_OF_TEST_REPO, null, Language.JAVA);
			final List<String> reference = readResultFile("src/test/resources/clonetracker-list-rev200.txt");
			final List<String> result = manager
					.getListOfRelevantFiles((long) 200);
			assertTrue(reference.size() == result.size());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testGetListOfRelevantFiles2() {
		try {
			final SVNRepositoryManager manager = new SVNRepositoryManager(
					PATH_OF_TEST_REPO, null, Language.JAVA);
			final List<String> reference = readResultFile("src/test/resources/clonetracker-list-rev300.txt");
			final List<String> result = manager
					.getListOfRelevantFiles((long) 300);
			assertTrue(reference.size() == result.size());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testGetListOfRelevantFiles3() {
		try {
			final SVNRepositoryManager manager = new SVNRepositoryManager(
					PATH_OF_TEST_REPO, null, Language.JAVA);
			manager.getListOfRelevantFiles((long) 1000);
			fail(); // here shouldn't be reached
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	@Test
	public void testGetListOfRelevantFiles4() {
		try {
			final SVNRepositoryManager manager = new SVNRepositoryManager(
					PATH_OF_TEST_REPO, RELATIVE_PATH_FOR_TEST, Language.JAVA);
			final List<String> reference = readResultFile("src/test/resources/clonetracker-list-rev300.txt");
			final List<String> result = manager
					.getListOfRelevantFiles((long) 300);
			// there exists a java file out of the relative path
			assertTrue(reference.size() - 1 == result.size());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testGetListOfRelevantFiles5() {
		try {
			final SVNRepositoryManager manager = new SVNRepositoryManager(
					PATH_OF_TEST_REPO, RELATIVE_PATH_FOR_TEST2, Language.JAVA);
			final List<String> result = manager
					.getListOfRelevantFiles((long) 300);
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
			if (Language.JAVA.isRelevantFile(line)) {
				result.add(line);
			}
		}

		br.close();
		return result;
	}

	@Test
	public void testGetLog1() {
		try {
			final SVNRepositoryManager manager = new SVNRepositoryManager(
					PATH_OF_TEST_REPO, RELATIVE_PATH_FOR_TEST, Language.JAVA);
			final Collection<SVNLogEntry> logEntries = manager.getLog(282);
			final Map<String, String> reference = readDiffFile("src/test/resources/clonetracker-diff-summarize-rev281-rev282.txt");

			for (final SVNLogEntry logEntry : logEntries) {
				for (final SVNLogEntryPath p : logEntry.getChangedPaths()
						.values()) {
					if (!reference.containsKey(p.getPath())) {
						fail();
					}
					if (!reference.get(p.getPath()).equals(
							String.valueOf(p.getType()))) {
						fail();
					}

					reference.remove(p.getPath());
				}
			}

			assertTrue(reference.isEmpty());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testGetLog2() {
		try {
			final SVNRepositoryManager manager = new SVNRepositoryManager(
					PATH_OF_TEST_REPO, RELATIVE_PATH_FOR_TEST, Language.JAVA);
			final Collection<SVNLogEntry> logEntries = manager.getLog(335);
			final Map<String, String> reference = readDiffFile("src/test/resources/clonetracker-diff-summarize-rev334-rev335.txt");

			for (final SVNLogEntry logEntry : logEntries) {
				for (final SVNLogEntryPath p : logEntry.getChangedPaths()
						.values()) {
					if (!reference.containsKey(p.getPath())) {
						fail();
					}
					if (!reference.get(p.getPath()).equals(
							String.valueOf(p.getType()))) {
						fail();
					}

					reference.remove(p.getPath());
				}
			}

			assertTrue(reference.isEmpty());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testGetLog3() {
		try {
			final SVNRepositoryManager manager = new SVNRepositoryManager(
					PATH_OF_TEST_REPO, RELATIVE_PATH_FOR_TEST, Language.JAVA);
			final Collection<SVNLogEntry> logEntries = manager.getLog(406);
			final Map<String, String> reference = readDiffFile("src/test/resources/clonetracker-diff-summarize-rev405-rev406.txt");

			for (final SVNLogEntry logEntry : logEntries) {
				for (final SVNLogEntryPath p : logEntry.getChangedPaths()
						.values()) {
					if (!reference.containsKey(p.getPath())) {
						fail();
					}
					if (!reference.get(p.getPath()).equals(
							String.valueOf(p.getType()))) {
						fail();
					}

					reference.remove(p.getPath());
				}
			}

			assertTrue(reference.isEmpty());
		} catch (Exception e) {
			fail();
		}
	}

	private Map<String, String> readDiffFile(final String path)
			throws Exception {
		final Map<String, String> result = new HashMap<String, String>();
		final BufferedReader br = new BufferedReader(new FileReader(new File(
				path)));

		String line;
		while ((line = br.readLine()) != null) {
			final String[] splitLine = line.split(" ");
			result.put(splitLine[1], splitLine[0]);
		}

		br.close();
		return result;
	}

}
