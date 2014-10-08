package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import static org.junit.Assert.*;

import org.junit.Test;

public class DBUrlProviderTest {

	@Test
	public void testGetUrl1() {
		final DBMS dbms = DBMS.SQLITE;
		final String path = "C:/Users/k-hotta/git/SCAnalyzer/src/test/resources/test.db";

		try {
			final String url = DBUrlProvider.getUrl(dbms, path);
			assertTrue(url
					.equals("jdbc:sqlite:/C:/Users/k-hotta/git/SCAnalyzer/src/test/resources/test.db"));
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testGetUrl2() {
		final DBMS dbms = DBMS.SQLITE;
		final String path = "C:\\Users\\k-hotta\\git\\SCAnalyzer\\src\\test\\resources\\test.db";

		try {
			final String url = DBUrlProvider.getUrl(dbms, path);
			assertTrue(url
					.equals("jdbc:sqlite:/C:/Users/k-hotta/git/SCAnalyzer/src/test/resources/test.db"));
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testGetUrl3() {
		final DBMS dbms = DBMS.SQLITE;
		final String path = "src\\test\\resources\\test.db";

		try {
			final String url = DBUrlProvider.getUrl(dbms, path);
			assertTrue(url
					.equals("jdbc:sqlite:/C:/Users/k-hotta/git/SCAnalyzer/src/test/resources/test.db"));
		} catch (Exception e) {
			fail();
		}
	}

}
