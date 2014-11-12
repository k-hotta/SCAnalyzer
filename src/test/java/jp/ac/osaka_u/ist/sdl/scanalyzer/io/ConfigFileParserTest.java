package jp.ac.osaka_u.ist.sdl.scanalyzer.io;

import static org.junit.Assert.*;
import jp.ac.osaka_u.ist.sdl.scanalyzer.config.ConfigFileParser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigFileParserTest {

	private static final String TARGET = "src/main/resources/scanalyzer-config.xml";
	
	private ConfigFileParser parser;
	
	@Before
	public void setUp() {
		parser = new ConfigFileParser();
	}
	
	@After
	public void tearDown() {
		parser = null;
	}
	
	@Test
	public void testParse1() throws Exception {
		parser.parse(TARGET);
		
		assertEquals(parser.getValue("dbms"), "SQLITE");
		assertEquals(parser.getValue("language"), "JAVA");
		assertEquals(parser.getValue("repository"), "src/test/resources/repository-clonetracker");
		assertEquals(parser.getValue("version-control"), "SVN");
		assertEquals(parser.getValue("database"), "src/test/resources/test.db");
		assertEquals(parser.getValue("detector"), "Scorpio");
		assertEquals(parser.getValue("result-directory"), "src/test/resources/scorpio/");
		assertEquals(parser.getValue("filename-format"), "clonetracker-scorpio-rev%s.txt");
		assertEquals(parser.getValue("relocation"), "null");
		assertEquals(parser.getValue("equalizer"), "near-miss");
		assertEquals(parser.getValue("algorithm"), "ICLONES");
	}
	
}
