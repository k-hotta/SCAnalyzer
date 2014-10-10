package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import static org.junit.Assert.*;

import org.junit.Test;

public class DBUrlProviderTest {
	
	@Test
	public void testGetUrl() {
		final DBMS dbms = DBMS.SQLITE;
		final String path = "src\\test\\resources\\test.db";

		try {
			final String url = DBUrlProvider.getUrl(dbms, path);
			assertTrue(url
					.startsWith("jdbc:sqlite:") && url.endsWith("/src/test/resources/test.db"));
		} catch (Exception e) {
			fail();
		}
	}

}
