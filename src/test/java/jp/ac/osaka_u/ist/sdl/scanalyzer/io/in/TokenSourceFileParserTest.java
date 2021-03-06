package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.Language;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Token;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.svn.SVNFileContentProvider;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.svn.SVNRepositoryManager;

import org.junit.BeforeClass;
import org.junit.Test;

public class TokenSourceFileParserTest {

	private static final String PATH_OF_TEST_REPO = "src/test/resources/repository-clonetracker";

	private static final String RELATIVE_PATH_FOR_TEST = "/c20r_main/src";

	private static SVNRepositoryManager manager;

	private static SVNFileContentProvider<Token> provider;

	private static final TokenSourceFileParser parser = new TokenSourceFileParser(
			Language.JAVA);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		manager = SVNRepositoryManager.setup(PATH_OF_TEST_REPO,
				RELATIVE_PATH_FOR_TEST, Language.JAVA);
		provider = new SVNFileContentProvider<Token>(manager);
	}

	@Test
	public void testParse1() throws Exception {
		boolean caughtException = false;

		try {
			parser.parse(null, null);
		} catch (IllegalArgumentException e) {
			caughtException = true;
		}

		assertTrue(caughtException);
	}

	@Test
	public void testParse2() throws Exception {
		boolean caughtException = false;

		try {
			parser.parse(new SourceFile<Token>(new DBSourceFile()), null);
		} catch (IllegalArgumentException e) {
			caughtException = true;
		}

		assertTrue(caughtException);
	}

	@Test
	public void testParse3() throws Exception {
		boolean caughtException = false;

		try {
			DBSourceFile sourceFile = new DBSourceFile(1, "A.java",
					"A.java".hashCode());
			parser.parse(new SourceFile<Token>(sourceFile), null);
		} catch (IllegalArgumentException e) {
			caughtException = true;
		}

		assertTrue(caughtException);
	}

	@Test
	public void testParse4() throws Exception {
		DBSourceFile sourceFile = new DBSourceFile(1, "A.java",
				"A.java".hashCode());
		final Map<Integer, Token> result = parser.parse(new SourceFile<Token>(
				sourceFile), "int x = 0;");
		assertTrue(result.size() == 5);
	}

	@Test
	public void testParse5() throws Exception {
		final DBVersion version = new DBVersion();
		version.setRevision(new DBRevision(0, "419", null));
		final DBSourceFile sourceFile = new DBSourceFile(
				1,
				"/c20r_main/src/jp/ac/osaka_u/ist/sdl/c20r/rev_analyzer/BlockDetectThread.java",
				"/c20r_main/src/jp/ac/osaka_u/ist/sdl/c20r/rev_analyzer/BlockDetectThread.java"
						.hashCode());
		final Version<Token> volatileVersion = new Version<Token>(version);
		volatileVersion.setRevision(new Revision(version.getRevision()));
		final String content = provider.getFileContent(volatileVersion,
				new SourceFile<Token>(sourceFile));

		final Map<Integer, Token> result = parser.parse(new SourceFile<Token>(
				sourceFile), content);

		assertTrue(result.get(1).getValue().equals("package"));
	}
}
