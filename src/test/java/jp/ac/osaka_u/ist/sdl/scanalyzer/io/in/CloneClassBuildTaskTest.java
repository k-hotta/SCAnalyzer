package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Token;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Type1TokenEqualizer;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.Language;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.svn.SVNFileContentProvider;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.svn.SVNRepositoryManager;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

import difflib.myers.Equalizer;

public class CloneClassBuildTaskTest {

	private static Method mTraceBack;

	private static Method mSearchPositionWithLine;

	private static Token mock1;

	private static Token mock2;

	private static Token mock3_1;

	private static Token mock3_2;

	private static Token mock3_3;

	private static Token mock4;

	private static Token mock5;

	private static Token mock6;

	private static Token mock7;

	private static Token mock8;

	private static Token mock9;

	private static Token mock10;

	private static SortedMap<Integer, Token> mockElements;

	private static final String PATH_OF_TEST_REPO = "src/test/resources/repository-clonetracker";

	private static final String RELATIVE_PATH_FOR_TEST = "/c20r_main/src";

	private static SVNRepositoryManager manager;

	private static SVNFileContentProvider<Token> provider;

	private static final TokenSourceFileParser parser = new TokenSourceFileParser(
			Language.JAVA);

	private static Version<Token> version419Mock;

	private static RawCloneClass<Token> rawCloneClassMock1;

	private static RawCloneClass<Token> rawCloneClassMock2;

	private static RawCloneClass<Token> rawCloneClassMock3;

	private static Equalizer<Token> equalizer;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		manager = new SVNRepositoryManager(PATH_OF_TEST_REPO,
				RELATIVE_PATH_FOR_TEST, Language.JAVA);
		provider = new SVNFileContentProvider<Token>(manager);

		mTraceBack = CloneClassBuildTask.class.getDeclaredMethod("traceBack",
				SortedMap.class, int.class);
		mTraceBack.setAccessible(true);

		mSearchPositionWithLine = CloneClassBuildTask.class.getDeclaredMethod(
				"searchStartPositionWithLine", SortedMap.class, int.class);
		mSearchPositionWithLine.setAccessible(true);

		mock1 = EasyMock.createMock(Token.class);
		EasyMock.expect(mock1.getLine()).andStubReturn(1);

		mock2 = EasyMock.createMock(Token.class);
		EasyMock.expect(mock2.getLine()).andStubReturn(2);

		mock3_1 = EasyMock.createMock(Token.class);
		EasyMock.expect(mock3_1.getLine()).andStubReturn(3);

		mock3_2 = EasyMock.createMock(Token.class);
		EasyMock.expect(mock3_2.getLine()).andStubReturn(3);

		mock3_3 = EasyMock.createMock(Token.class);
		EasyMock.expect(mock3_3.getLine()).andStubReturn(3);

		mock4 = EasyMock.createMock(Token.class);
		EasyMock.expect(mock4.getLine()).andStubReturn(4);

		mock5 = EasyMock.createMock(Token.class);
		EasyMock.expect(mock5.getLine()).andStubReturn(5);

		mock6 = EasyMock.createMock(Token.class);
		EasyMock.expect(mock6.getLine()).andStubReturn(6);

		mock7 = EasyMock.createMock(Token.class);
		EasyMock.expect(mock7.getLine()).andStubReturn(7);

		mock8 = EasyMock.createMock(Token.class);
		EasyMock.expect(mock8.getLine()).andStubReturn(8);

		mock9 = EasyMock.createMock(Token.class);
		EasyMock.expect(mock9.getLine()).andStubReturn(9);

		mock10 = EasyMock.createMock(Token.class);
		EasyMock.expect(mock10.getLine()).andStubReturn(10);

		EasyMock.replay(mock1, mock2, mock3_1, mock3_2, mock3_3, mock4, mock5,
				mock6, mock7, mock8, mock9, mock10);

		mockElements = new TreeMap<Integer, Token>();
		int count = 1;
		mockElements.put(count++, mock1);
		mockElements.put(count++, mock2);
		mockElements.put(count++, mock3_1);
		mockElements.put(count++, mock3_2);
		mockElements.put(count++, mock3_3);
		mockElements.put(count++, mock4);
		mockElements.put(count++, mock5);
		mockElements.put(count++, mock6);
		mockElements.put(count++, mock7);
		mockElements.put(count++, mock8);
		mockElements.put(count++, mock9);
		mockElements.put(count++, mock10);

		final DBVersion version419Db = new DBVersion((long) 419,
				new DBRevision((long) 419, "419", null),
				new HashSet<DBFileChange>(), new HashSet<DBRawCloneClass>(),
				new HashSet<DBCloneClass>(), new HashSet<DBSourceFile>());
		version419Mock = new Version<Token>(version419Db);
		version419Mock.setRevision(new Revision(version419Db.getRevision()));

		final DBSourceFile similarityCalculatorJavaDb = new DBSourceFile(1,
				"/c20r_main/src/jp/ac/osaka_u/ist/sdl/c20r/util/SimilarityCalculator.java");
		version419Db.getSourceFiles().add(similarityCalculatorJavaDb);

		final SourceFile<Token> similarityCalculatorJava = new SourceFile<Token>(
				similarityCalculatorJavaDb);
		version419Mock.addSourceFile(similarityCalculatorJava);

		final String similarityCalculatorJavaStr = provider.getFileContent(
				version419Mock, similarityCalculatorJava);
		final Map<Integer, Token> contentSimilarityCalulatorJava = parser
				.parse(similarityCalculatorJava, similarityCalculatorJavaStr);
		similarityCalculatorJava.setContents(contentSimilarityCalulatorJava
				.values());

		final DBSourceFile levenshteinDistanceCalculatorJavaDb = new DBSourceFile(
				2,
				"/c20r_main/src/jp/ac/osaka_u/ist/sdl/c20r/rev_analyzer/LevenshteinDistanceCalculator.java");
		version419Db.getSourceFiles().add(levenshteinDistanceCalculatorJavaDb);

		final SourceFile<Token> levenshteinDistanceCalculatorJava = new SourceFile<Token>(
				levenshteinDistanceCalculatorJavaDb);
		version419Mock.addSourceFile(levenshteinDistanceCalculatorJava);

		final String levenshteinDistanceCalculatorJavaStr = provider
				.getFileContent(version419Mock,
						levenshteinDistanceCalculatorJava);

		final Map<Integer, Token> contentLevenshteinDistanceCalculatorJava = parser
				.parse(levenshteinDistanceCalculatorJava,
						levenshteinDistanceCalculatorJavaStr);
		levenshteinDistanceCalculatorJava
				.setContents(contentLevenshteinDistanceCalculatorJava.values());

		final DBRawCloneClass rawCloneClassDb1 = new DBRawCloneClass(1,
				version419Mock.getCore(), new HashSet<DBRawClonedFragment>());
		final RawClonedFragment<Token> frag1_1 = new RawClonedFragment<Token>(
				new DBRawClonedFragment(1, version419Mock.getCore(),
						similarityCalculatorJavaDb, 12, 4, rawCloneClassDb1));
		final RawClonedFragment<Token> frag1_2 = new RawClonedFragment<Token>(
				new DBRawClonedFragment(2, version419Mock.getCore(),
						similarityCalculatorJavaDb, 19, 2, rawCloneClassDb1));
		rawCloneClassDb1.getElements().add(frag1_1.getCore());
		rawCloneClassDb1.getElements().add(frag1_2.getCore());

		frag1_1.setSourceFile(similarityCalculatorJava);
		frag1_2.setSourceFile(similarityCalculatorJava);

		rawCloneClassMock1 = new RawCloneClass<Token>(rawCloneClassDb1);
		rawCloneClassMock1.addRawClonedFragment(frag1_1);
		rawCloneClassMock1.addRawClonedFragment(frag1_2);
		frag1_1.setRawCloneClass(rawCloneClassMock1);
		frag1_2.setRawCloneClass(rawCloneClassMock1);

		final DBRawCloneClass rawCloneClassDb2 = new DBRawCloneClass(2,
				version419Mock.getCore(), new HashSet<DBRawClonedFragment>());
		final RawClonedFragment<Token> frag2_1 = new RawClonedFragment<Token>(
				new DBRawClonedFragment(3, version419Mock.getCore(),
						similarityCalculatorJavaDb, 10, 22, rawCloneClassDb2));
		final RawClonedFragment<Token> frag2_2 = new RawClonedFragment<Token>(
				new DBRawClonedFragment(4, version419Mock.getCore(),
						levenshteinDistanceCalculatorJavaDb, 24, 22,
						rawCloneClassDb2));
		rawCloneClassDb2.getElements().add(frag2_1.getCore());
		rawCloneClassDb2.getElements().add(frag2_2.getCore());

		frag2_1.setSourceFile(similarityCalculatorJava);
		frag2_2.setSourceFile(levenshteinDistanceCalculatorJava);

		rawCloneClassMock2 = new RawCloneClass<Token>(rawCloneClassDb2);
		rawCloneClassMock2.addRawClonedFragment(frag2_1);
		rawCloneClassMock2.addRawClonedFragment(frag2_2);
		frag2_1.setRawCloneClass(rawCloneClassMock2);
		frag2_2.setRawCloneClass(rawCloneClassMock2);

		final DBRawCloneClass rawCloneClassDb3 = new DBRawCloneClass(3,
				version419Mock.getCore(), new HashSet<DBRawClonedFragment>());
		final RawClonedFragment<Token> frag3_1 = new RawClonedFragment<Token>(
				new DBRawClonedFragment(5, version419Mock.getCore(),
						similarityCalculatorJavaDb, 12, 4, rawCloneClassDb3));
		final RawClonedFragment<Token> frag3_2 = new RawClonedFragment<Token>(
				new DBRawClonedFragment(6, version419Mock.getCore(),
						similarityCalculatorJavaDb, 19, 2, rawCloneClassDb3));
		final RawClonedFragment<Token> frag3_3 = new RawClonedFragment<Token>(
				new DBRawClonedFragment(7, version419Mock.getCore(),
						similarityCalculatorJavaDb, 26, 4, rawCloneClassDb3));
		final RawClonedFragment<Token> frag3_4 = new RawClonedFragment<Token>(
				new DBRawClonedFragment(8, version419Mock.getCore(),
						levenshteinDistanceCalculatorJavaDb, 33, 2,
						rawCloneClassDb3));
		rawCloneClassDb3.getElements().add(frag3_1.getCore());
		rawCloneClassDb3.getElements().add(frag3_2.getCore());
		rawCloneClassDb3.getElements().add(frag3_3.getCore());
		rawCloneClassDb3.getElements().add(frag3_4.getCore());

		frag3_1.setSourceFile(similarityCalculatorJava);
		frag3_2.setSourceFile(similarityCalculatorJava);
		frag3_3.setSourceFile(similarityCalculatorJava);
		frag3_4.setSourceFile(levenshteinDistanceCalculatorJava);

		rawCloneClassMock3 = new RawCloneClass<Token>(rawCloneClassDb3);
		rawCloneClassMock3.addRawClonedFragment(frag3_1);
		rawCloneClassMock3.addRawClonedFragment(frag3_2);
		rawCloneClassMock3.addRawClonedFragment(frag3_3);
		rawCloneClassMock3.addRawClonedFragment(frag3_4);
		frag3_1.setRawCloneClass(rawCloneClassMock3);
		frag3_2.setRawCloneClass(rawCloneClassMock3);
		frag3_3.setRawCloneClass(rawCloneClassMock3);
		frag3_4.setRawCloneClass(rawCloneClassMock3);

		equalizer = new Type1TokenEqualizer();
	}

	@Test
	public void testTraceBack1() throws Exception {
		int result = (int) mTraceBack.invoke(new CloneClassBuildTask<Token>(
				null, null, equalizer), mockElements, 5);
		assertTrue(result == 3);
	}

	@Test
	public void testSearchPositionWithLine1() throws Exception {
		final SortedMap<Integer, Token> elements = new TreeMap<Integer, Token>(
				mockElements);
		elements.remove(6);
		elements.put(6, mock5);
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask<Token>(null, null, equalizer),
				elements, 3);
		assertTrue(result == 3);
	}

	@Test
	public void testSearchPositionWithLine2() throws Exception {
		final SortedMap<Integer, Token> elements = new TreeMap<Integer, Token>(
				mockElements);
		elements.remove(6);
		elements.put(6, mock5);
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask<Token>(null, null, equalizer),
				elements, 8);
		assertTrue(result == 10);
	}

	@Test
	public void testSearchPositionWithLine3() throws Exception {
		final SortedMap<Integer, Token> elements = new TreeMap<Integer, Token>(
				mockElements);
		elements.remove(6);
		elements.put(6, mock5);
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask<Token>(null, null, equalizer),
				elements, 4);
		assertTrue(result == 6);
	}

	@Test
	public void testSearchPositionWithLine4() throws Exception {
		final SortedMap<Integer, Token> elements = new TreeMap<Integer, Token>(
				mockElements);
		elements.remove(6);
		elements.put(6, mock3_3);
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask<Token>(null, null, equalizer),
				elements, 4);
		assertTrue(result == 7);
	}

	@Test
	public void testSearchPositionWithLine5() throws Exception {
		final SortedMap<Integer, Token> elements = new TreeMap<Integer, Token>(
				mockElements);
		elements.remove(9);
		elements.put(9, mock6);
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask<Token>(null, null, equalizer),
				elements, 7);
		assertTrue(result == 10);
	}

	@Test
	public void testSearchPositionWithLine6() throws Exception {
		final SortedMap<Integer, Token> elements = new TreeMap<Integer, Token>(
				mockElements);
		elements.remove(9);
		elements.put(9, mock8);
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask<Token>(null, null, equalizer),
				elements, 7);
		assertTrue(result == 9);
	}

	@Test
	public void testSearchPositionWithLine7() throws Exception {
		final SortedMap<Integer, Token> elements = new TreeMap<Integer, Token>(
				mockElements);
		for (int i = 4; i <= 12; i++) {
			elements.remove(i);
		}
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask<Token>(null, null, equalizer),
				elements, 7);
		assertTrue(result == -1);
	}

	@Test
	public void testSearchPositionWithLine8() throws Exception {
		final SortedMap<Integer, Token> elements = new TreeMap<Integer, Token>(
				mockElements);
		for (int i = 4; i <= 12; i++) {
			elements.remove(i);
		}
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask<Token>(null, null, equalizer),
				elements, -1);
		assertTrue(result == -1);
	}

	@Test
	public void testCall1() throws Exception {
		final CloneClassBuildTask<Token> task = new CloneClassBuildTask<Token>(
				rawCloneClassMock1, version419Mock, equalizer);
		final CloneClass<Token> result = task.call();

		for (final CodeFragment<Token> frag1 : result.getCodeFragments()
				.values()) {
			final List<Token> contents1 = new ArrayList<Token>();
			for (final Segment<Token> seg1 : frag1.getSegments().values()) {
				contents1.addAll(seg1.getContents().values());
			}

			for (final CodeFragment<Token> frag2 : result.getCodeFragments()
					.values()) {
				final List<Token> contents2 = new ArrayList<Token>();
				for (final Segment<Token> seg2 : frag2.getSegments().values()) {
					contents2.addAll(seg2.getContents().values());
				}

				assertTrue(contents1.size() == contents2.size());
			}
		}
	}

	@Test
	public void testCall2() throws Exception {
		final CloneClassBuildTask<Token> task = new CloneClassBuildTask<Token>(
				rawCloneClassMock2, version419Mock, equalizer);
		final CloneClass<Token> result = task.call();

		for (final CodeFragment<Token> frag1 : result.getCodeFragments()
				.values()) {
			final List<Token> contents1 = new ArrayList<Token>();
			for (final Segment<Token> seg1 : frag1.getSegments().values()) {
				contents1.addAll(seg1.getContents().values());
			}

			for (final CodeFragment<Token> frag2 : result.getCodeFragments()
					.values()) {
				final List<Token> contents2 = new ArrayList<Token>();
				for (final Segment<Token> seg2 : frag2.getSegments().values()) {
					contents2.addAll(seg2.getContents().values());
				}

				assertTrue(contents1.size() == contents2.size());
			}
		}
	}

	@Test
	public void testCall3() throws Exception {
		final CloneClassBuildTask<Token> task = new CloneClassBuildTask<Token>(
				rawCloneClassMock3, version419Mock, equalizer);
		final CloneClass<Token> result = task.call();

		for (final CodeFragment<Token> frag1 : result.getCodeFragments()
				.values()) {
			final List<Token> contents1 = new ArrayList<Token>();
			for (final Segment<Token> seg1 : frag1.getSegments().values()) {
				contents1.addAll(seg1.getContents().values());
			}

			for (final CodeFragment<Token> frag2 : result.getCodeFragments()
					.values()) {
				final List<Token> contents2 = new ArrayList<Token>();
				for (final Segment<Token> seg2 : frag2.getSegments().values()) {
					contents2.addAll(seg2.getContents().values());
				}

				assertTrue(contents1.size() == contents2.size());
			}
		}
	}

	@Test
	public void testCall4() throws Exception {
		ExecutorService pool = Executors.newCachedThreadPool();

		try {
			final CloneClassBuildTask<Token> task1 = new CloneClassBuildTask<Token>(
					rawCloneClassMock1, version419Mock, equalizer);
			final CloneClassBuildTask<Token> task2 = new CloneClassBuildTask<Token>(
					rawCloneClassMock2, version419Mock, equalizer);
			final CloneClassBuildTask<Token> task3 = new CloneClassBuildTask<Token>(
					rawCloneClassMock1, version419Mock, equalizer);

			final List<Future<CloneClass<Token>>> futures = new ArrayList<>();
			futures.add(pool.submit(task1));
			futures.add(pool.submit(task2));
			futures.add(pool.submit(task3));

			final List<CloneClass<Token>> results = new ArrayList<>();

			for (Future<CloneClass<Token>> future : futures) {
				try {
					results.add(future.get());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			for (final CloneClass<Token> result : results) {
				for (final CodeFragment<Token> frag1 : result
						.getCodeFragments().values()) {
					final List<Token> contents1 = new ArrayList<Token>();
					for (final Segment<Token> seg1 : frag1.getSegments()
							.values()) {
						contents1.addAll(seg1.getContents().values());
					}

					for (final CodeFragment<Token> frag2 : result
							.getCodeFragments().values()) {
						final List<Token> contents2 = new ArrayList<Token>();
						for (final Segment<Token> seg2 : frag2.getSegments()
								.values()) {
							contents2.addAll(seg2.getContents().values());
						}

						assertTrue(contents1.size() == contents2.size());
					}
				}
			}
		} finally {
			pool.shutdown();
		}
	}
}
