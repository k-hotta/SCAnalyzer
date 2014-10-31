package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IAtomicElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SegmentComparator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFileContent;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Token;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.Language;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.svn.SVNFileContentProvider;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.svn.SVNRepositoryManager;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

public class CloneClassBuildTaskTest {

	private static Method mTraceBack;

	private static Method mSearchPositionWithLine;

	private static IAtomicElement mock1;

	private static IAtomicElement mock2;

	private static IAtomicElement mock3_1;

	private static IAtomicElement mock3_2;

	private static IAtomicElement mock3_3;

	private static IAtomicElement mock4;

	private static IAtomicElement mock5;

	private static IAtomicElement mock6;

	private static IAtomicElement mock7;

	private static IAtomicElement mock8;

	private static IAtomicElement mock9;

	private static IAtomicElement mock10;

	private static SortedMap<Integer, IAtomicElement> mockElements;

	private static final String PATH_OF_TEST_REPO = "src/test/resources/repository-clonetracker";

	private static final String RELATIVE_PATH_FOR_TEST = "/c20r_main/src";

	private static SVNRepositoryManager manager;

	private static SVNFileContentProvider provider;

	private static final TokenSourceFileParser parser = new TokenSourceFileParser(
			Language.JAVA);

	private static Version version419Mock;

	private static RawCloneClass rawCloneClassMock1;

	private static RawCloneClass rawCloneClassMock2;

	private static RawCloneClass rawCloneClassMock3;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		manager = new SVNRepositoryManager(PATH_OF_TEST_REPO,
				RELATIVE_PATH_FOR_TEST, Language.JAVA);
		provider = new SVNFileContentProvider(manager);

		mTraceBack = CloneClassBuildTask.class.getDeclaredMethod("traceBack",
				SortedMap.class, int.class);
		mTraceBack.setAccessible(true);

		mSearchPositionWithLine = CloneClassBuildTask.class.getDeclaredMethod(
				"searchPositionWithLine", SortedMap.class, int.class);
		mSearchPositionWithLine.setAccessible(true);

		mock1 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock1.getLine()).andStubReturn(1);

		mock2 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock2.getLine()).andStubReturn(2);

		mock3_1 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock3_1.getLine()).andStubReturn(3);

		mock3_2 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock3_2.getLine()).andStubReturn(3);

		mock3_3 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock3_3.getLine()).andStubReturn(3);

		mock4 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock4.getLine()).andStubReturn(4);

		mock5 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock5.getLine()).andStubReturn(5);

		mock6 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock6.getLine()).andStubReturn(6);

		mock7 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock7.getLine()).andStubReturn(7);

		mock8 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock8.getLine()).andStubReturn(8);

		mock9 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock9.getLine()).andStubReturn(9);

		mock10 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock10.getLine()).andStubReturn(10);

		EasyMock.replay(mock1, mock2, mock3_1, mock3_2, mock3_3, mock4, mock5,
				mock6, mock7, mock8, mock9, mock10);

		mockElements = new TreeMap<Integer, IAtomicElement>();
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

		version419Mock = new Version((long) 419, new Revision((long) 419,
				"419", null), new HashSet<FileChange>(),
				new HashSet<RawCloneClass>(), new HashSet<CloneClass>(),
				new HashSet<SourceFile>(),
				new TreeMap<Long, SourceFileContent<?>>());

		final SourceFile similarityCalculatorJava = new SourceFile(1,
				"/c20r_main/src/jp/ac/osaka_u/ist/sdl/c20r/util/SimilarityCalculator.java");
		version419Mock.getSourceFiles().add(similarityCalculatorJava);
		final String similarityCalculatorJavaStr = provider.getFileContent(
				version419Mock, similarityCalculatorJava);

		final SourceFile levenshteinDistanceCalculatorJava = new SourceFile(
				2,
				"/c20r_main/src/jp/ac/osaka_u/ist/sdl/c20r/rev_analyzer/LevenshteinDistanceCalculator.java");
		version419Mock.getSourceFiles().add(levenshteinDistanceCalculatorJava);
		final String levenshteinDistanceCalculatorJavaStr = provider
				.getFileContent(version419Mock,
						levenshteinDistanceCalculatorJava);

		final SourceFileContentBuilder<Token> builder = new SourceFileContentBuilder<>(
				parser);

		final SourceFileContent<Token> contentSimilarityCalulatorJava = builder
				.build(similarityCalculatorJava, similarityCalculatorJavaStr);
		version419Mock.getSourceFileContents().put(
				similarityCalculatorJava.getId(),
				contentSimilarityCalulatorJava);

		final SourceFileContent<Token> contentLevenshteinDistanceCalculatorJava = builder
				.build(levenshteinDistanceCalculatorJava,
						levenshteinDistanceCalculatorJavaStr);
		version419Mock.getSourceFileContents().put(
				levenshteinDistanceCalculatorJava.getId(),
				contentLevenshteinDistanceCalculatorJava);

		rawCloneClassMock1 = new RawCloneClass(1, version419Mock,
				new HashSet<RawClonedFragment>());
		final RawClonedFragment frag1_1 = new RawClonedFragment(1,
				version419Mock, similarityCalculatorJava, 12, 4,
				rawCloneClassMock1);
		final RawClonedFragment frag1_2 = new RawClonedFragment(2,
				version419Mock, similarityCalculatorJava, 19, 2,
				rawCloneClassMock1);
		rawCloneClassMock1.getElements().add(frag1_1);
		rawCloneClassMock1.getElements().add(frag1_2);

		rawCloneClassMock2 = new RawCloneClass(2, version419Mock,
				new HashSet<RawClonedFragment>());
		final RawClonedFragment frag2_1 = new RawClonedFragment(3,
				version419Mock, similarityCalculatorJava, 10, 22,
				rawCloneClassMock2);
		final RawClonedFragment frag2_2 = new RawClonedFragment(4,
				version419Mock, levenshteinDistanceCalculatorJava, 24, 22,
				rawCloneClassMock2);
		rawCloneClassMock2.getElements().add(frag2_1);
		rawCloneClassMock2.getElements().add(frag2_2);

		rawCloneClassMock3 = new RawCloneClass(3, version419Mock,
				new HashSet<RawClonedFragment>());
		final RawClonedFragment frag3_1 = new RawClonedFragment(5,
				version419Mock, similarityCalculatorJava, 12, 4,
				rawCloneClassMock3);
		final RawClonedFragment frag3_2 = new RawClonedFragment(6,
				version419Mock, similarityCalculatorJava, 19, 2,
				rawCloneClassMock3);
		final RawClonedFragment frag3_3 = new RawClonedFragment(7,
				version419Mock, levenshteinDistanceCalculatorJava, 26, 4,
				rawCloneClassMock3);
		final RawClonedFragment frag3_4 = new RawClonedFragment(8,
				version419Mock, levenshteinDistanceCalculatorJava, 33, 2,
				rawCloneClassMock3);
		rawCloneClassMock3.getElements().add(frag3_1);
		rawCloneClassMock3.getElements().add(frag3_2);
		rawCloneClassMock3.getElements().add(frag3_3);
		rawCloneClassMock3.getElements().add(frag3_4);
	}

	@Test
	public void testTraceBack1() throws Exception {
		int result = (int) mTraceBack.invoke(new CloneClassBuildTask(null,
				null, null), mockElements, 5);
		assertTrue(result == 3);
	}

	@Test
	public void testSearchPositionWithLine1() throws Exception {
		final SortedMap<Integer, IAtomicElement> elements = new TreeMap<Integer, IAtomicElement>(
				mockElements);
		elements.remove(6);
		elements.put(6, mock5);
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask(null, null, null), elements, 3);
		assertTrue(result == 3);
	}

	@Test
	public void testSearchPositionWithLine2() throws Exception {
		final SortedMap<Integer, IAtomicElement> elements = new TreeMap<Integer, IAtomicElement>(
				mockElements);
		elements.remove(6);
		elements.put(6, mock5);
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask(null, null, null), elements, 8);
		assertTrue(result == 10);
	}

	@Test
	public void testSearchPositionWithLine3() throws Exception {
		final SortedMap<Integer, IAtomicElement> elements = new TreeMap<Integer, IAtomicElement>(
				mockElements);
		elements.remove(6);
		elements.put(6, mock5);
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask(null, null, null), elements, 4);
		assertTrue(result == 6);
	}

	@Test
	public void testSearchPositionWithLine4() throws Exception {
		final SortedMap<Integer, IAtomicElement> elements = new TreeMap<Integer, IAtomicElement>(
				mockElements);
		elements.remove(6);
		elements.put(6, mock3_3);
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask(null, null, null), elements, 4);
		assertTrue(result == 7);
	}

	@Test
	public void testSearchPositionWithLine5() throws Exception {
		final SortedMap<Integer, IAtomicElement> elements = new TreeMap<Integer, IAtomicElement>(
				mockElements);
		elements.remove(9);
		elements.put(9, mock6);
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask(null, null, null), elements, 7);
		assertTrue(result == 10);
	}

	@Test
	public void testSearchPositionWithLine6() throws Exception {
		final SortedMap<Integer, IAtomicElement> elements = new TreeMap<Integer, IAtomicElement>(
				mockElements);
		elements.remove(9);
		elements.put(9, mock8);
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask(null, null, null), elements, 7);
		assertTrue(result == 9);
	}

	@Test
	public void testSearchPositionWithLine7() throws Exception {
		final SortedMap<Integer, IAtomicElement> elements = new TreeMap<Integer, IAtomicElement>(
				mockElements);
		for (int i = 4; i <= 12; i++) {
			elements.remove(i);
		}
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask(null, null, null), elements, 7);
		assertTrue(result == -1);
	}

	@Test
	public void testSearchPositionWithLine8() throws Exception {
		final SortedMap<Integer, IAtomicElement> elements = new TreeMap<Integer, IAtomicElement>(
				mockElements);
		for (int i = 4; i <= 12; i++) {
			elements.remove(i);
		}
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask(null, null, null), elements, -1);
		assertTrue(result == -1);
	}

	private boolean compareFragments(
			final CodeFragment frag1,
			final CodeFragment frag2,
			final Map<Long, SourceFileContent<? extends IAtomicElement>> fileContents) {
		final List<Segment> segments1 = new ArrayList<Segment>();
		segments1.addAll(frag1.getSegments());
		Collections.sort(segments1, new SegmentComparator());

		final List<Segment> segments2 = new ArrayList<Segment>();
		segments2.addAll(frag2.getSegments());
		Collections.sort(segments2, new SegmentComparator());

		final List<IAtomicElement> segContent1 = new ArrayList<IAtomicElement>();
		final List<IAtomicElement> segContent2 = new ArrayList<IAtomicElement>();

		final SourceFileContent<? extends IAtomicElement> content1 = fileContents
				.get(segments1.get(0).getSourceFile().getId());
		for (final Segment segment : segments1) {
			segContent1.addAll(content1.getContentsIn(
					segment.getStartPosition(), segment.getEndPosition()));
		}

		final SourceFileContent<? extends IAtomicElement> content2 = fileContents
				.get(segments2.get(0).getSourceFile().getId());
		for (final Segment segment : segments2) {
			segContent2.addAll(content2.getContentsIn(
					segment.getStartPosition(), segment.getEndPosition()));
		}

		return segContent1.equals(segContent2);
	}

	@Test
	public void testCall1() throws Exception {
		final ConcurrentMap<Long, SourceFileContent<? extends IAtomicElement>> contents = new ConcurrentHashMap<Long, SourceFileContent<? extends IAtomicElement>>();
		contents.putAll(version419Mock.getSourceFileContents());

		final CloneClassBuildTask task = new CloneClassBuildTask(contents,
				rawCloneClassMock1, version419Mock);
		final CloneClass result = task.call();

		for (final CodeFragment frag1 : result.getCodeFragments()) {
			for (final CodeFragment frag2 : result.getCodeFragments()) {
				assertTrue(compareFragments(frag1, frag2, contents));
			}
		}
	}

	@Test
	public void testCall2() throws Exception {
		final ConcurrentMap<Long, SourceFileContent<? extends IAtomicElement>> contents = new ConcurrentHashMap<Long, SourceFileContent<? extends IAtomicElement>>();
		contents.putAll(version419Mock.getSourceFileContents());

		final CloneClassBuildTask task = new CloneClassBuildTask(contents,
				rawCloneClassMock2, version419Mock);
		final CloneClass result = task.call();

		for (final CodeFragment frag1 : result.getCodeFragments()) {
			for (final CodeFragment frag2 : result.getCodeFragments()) {
				assertTrue(compareFragments(frag1, frag2, contents));
			}
		}
	}

	@Test
	public void testCall3() throws Exception {
		final ConcurrentMap<Long, SourceFileContent<? extends IAtomicElement>> contents = new ConcurrentHashMap<Long, SourceFileContent<? extends IAtomicElement>>();
		contents.putAll(version419Mock.getSourceFileContents());

		final CloneClassBuildTask task = new CloneClassBuildTask(contents,
				rawCloneClassMock3, version419Mock);
		final CloneClass result = task.call();

		for (final CodeFragment frag1 : result.getCodeFragments()) {
			for (final CodeFragment frag2 : result.getCodeFragments()) {
				assertTrue(compareFragments(frag1, frag2, contents));
			}
		}
	}

	@Test
	public void testCall4() throws Exception {
		ExecutorService pool = Executors.newCachedThreadPool();

		try {
			final ConcurrentMap<Long, SourceFileContent<? extends IAtomicElement>> contents = new ConcurrentHashMap<Long, SourceFileContent<? extends IAtomicElement>>();
			contents.putAll(version419Mock.getSourceFileContents());

			final CloneClassBuildTask task1 = new CloneClassBuildTask(contents,
					rawCloneClassMock1, version419Mock);
			final CloneClassBuildTask task2 = new CloneClassBuildTask(contents,
					rawCloneClassMock2, version419Mock);
			final CloneClassBuildTask task3 = new CloneClassBuildTask(contents,
					rawCloneClassMock1, version419Mock);

			final List<Future<CloneClass>> futures = new ArrayList<>();
			futures.add(pool.submit(task1));
			futures.add(pool.submit(task2));
			futures.add(pool.submit(task3));

			final List<CloneClass> results = new ArrayList<>();

			for (Future<CloneClass> future : futures) {
				try {
					results.add(future.get());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			for (final CloneClass result : results) {
				for (final CodeFragment frag1 : result.getCodeFragments()) {
					for (final CodeFragment frag2 : result.getCodeFragments()) {
						assertTrue(compareFragments(frag1, frag2, contents));
					}
				}
			}
		} finally {
			pool.shutdown();
		}
	}

}
