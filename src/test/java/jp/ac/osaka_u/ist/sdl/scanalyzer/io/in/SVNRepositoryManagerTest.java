package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

public class SVNRepositoryManagerTest {

	@Test
	public void test1() {
		final String path = "src\\test\\resources\\repository-clonetracker";
		final String relativePath = null;

		try {
			final SVNRepositoryManager manager = new SVNRepositoryManager(path,
					relativePath);
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
					relativePath);
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
					relativePath);
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
					relativePath);
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
	
}
