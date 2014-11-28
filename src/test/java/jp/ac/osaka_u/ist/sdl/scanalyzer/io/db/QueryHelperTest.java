package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class QueryHelperTest {

	@Test
	public void testquerySelectIdIn1() {
		String query = QueryHelper.querySelectIdIn("Table", "ID", null);
		assertEquals(query, "");
	}

	@Test
	public void testquerySelectIdIn2() {
		String query = QueryHelper.querySelectIdIn("Table", "ID",
				new ArrayList<Long>());
		assertEquals(query, "");
	}

	@Test
	public void testquerySelectIdIn3() {
		final List<Long> ids = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		for (long i = 1; i < 10; i++) {
			ids.add(i);
			builder.append(i + ",");
		}
		builder.deleteCharAt(builder.length() - 1);
		String query = QueryHelper.querySelectIdIn("Table", "ID", ids);
		assertEquals("select * from Table where ID in (1,2,3,4,5,6,7,8,9)", query);
	}
	
	@Test
	public void testquerySelectIdIn4() {
		final List<Long> ids = new ArrayList<>();
		QueryHelper.setMaximumOfIn(5);
		StringBuilder builder = new StringBuilder();
		for (long i = 1; i < 10; i++) {
			ids.add(i);
			builder.append(i + ",");
		}
		builder.deleteCharAt(builder.length() - 1);
		String query = QueryHelper.querySelectIdIn("Table", "ID", ids);
		assertEquals("select * from Table where ID in (1,2,3,4,5) or ID in (6,7,8,9)", query);
	}

}
