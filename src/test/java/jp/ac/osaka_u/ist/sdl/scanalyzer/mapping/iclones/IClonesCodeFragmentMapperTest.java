package jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.iclones;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.ExactTokenEqualizer;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Token;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.IProgramElementMapper;
import jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.TraditionalDiffTokenMapper;
import jp.ac.osaka_u.ist.sdl.scanalyzer.util.TestVersionBuilder;

import org.junit.BeforeClass;
import org.junit.Test;

public class IClonesCodeFragmentMapperTest {

	private static Version<Token> beforeVersion;

	private static Version<Token> afterVersion;

	private static IProgramElementMapper<Token> mapper;

	private static Method mCollectFragments;

	private static Method mEstimateNextFragments;

	private static Method mMakeBucketsBefore;

	private static Method mMakeBucketsAfter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		final TestVersionBuilder versionBuilder = new TestVersionBuilder();
		final Map<Long, Version<Token>> versions = versionBuilder.build();

		beforeVersion = versions.get((long) 410);
		afterVersion = versions.get((long) 419);

		mapper = new TraditionalDiffTokenMapper(new ExactTokenEqualizer());
		mapper.prepare(beforeVersion, afterVersion);

		mCollectFragments = IClonesCodeFragmentMapper.class.getDeclaredMethod(
				"collectFragments", Collection.class);
		mCollectFragments.setAccessible(true);

		mMakeBucketsBefore = IClonesCodeFragmentMapper.class.getDeclaredMethod(
				"makeBuckets", Map.class);
		mMakeBucketsBefore.setAccessible(true);

		mEstimateNextFragments = IClonesCodeFragmentMapper.class
				.getDeclaredMethod("estimateNextFragments", Version.class);
		mEstimateNextFragments.setAccessible(true);

		mMakeBucketsAfter = IClonesCodeFragmentMapper.class.getDeclaredMethod(
				"makeBuckets", Collection.class);
		mMakeBucketsAfter.setAccessible(true);
	}

	@Test
	public void testCollectFragment1() throws Exception {
		final Map<Long, CodeFragment<Token>> reference = new TreeMap<Long, CodeFragment<Token>>();

		for (final CloneClass<Token> cloneClass : beforeVersion
				.getCloneClasses().values()) {
			for (final CodeFragment<Token> codeFragment : cloneClass
					.getCodeFragments().values()) {
				reference.put(codeFragment.getId(), codeFragment);
			}
		}

		final IClonesCodeFragmentMapper<Token> instance = new IClonesCodeFragmentMapper<>(
				mapper);

		@SuppressWarnings("unchecked")
		final Map<Long, CodeFragment<Token>> result = (Map<Long, CodeFragment<Token>>) mCollectFragments
				.invoke(instance, beforeVersion.getCloneClasses().values());

		assertTrue(result.size() == reference.size());
		assertTrue(result.keySet().containsAll(reference.keySet()));
	}

	@Test
	public void testEstimateNextFragments1() throws Exception {
		final Map<Long, CodeFragment<Token>> fragments = new TreeMap<Long, CodeFragment<Token>>();

		for (final CloneClass<Token> cloneClass : beforeVersion
				.getCloneClasses().values()) {
			for (final CodeFragment<Token> codeFragment : cloneClass
					.getCodeFragments().values()) {
				fragments.put(codeFragment.getId(), codeFragment);
			}
		}

		final IClonesCodeFragmentMapper<Token> instance = new IClonesCodeFragmentMapper<>(
				mapper);
		@SuppressWarnings("unchecked")
		final Map<Long, SortedMap<String, ExpectedSegment>> result = (Map<Long, SortedMap<String, ExpectedSegment>>) mEstimateNextFragments
				.invoke(instance, beforeVersion);

		assertTrue(fragments.size() == result.size());
	}

	@Test
	public void testMakeBucketsBefore1() throws Exception {
		final IClonesCodeFragmentMapper<Token> instance = new IClonesCodeFragmentMapper<>(
				mapper);

		@SuppressWarnings("unchecked")
		final Map<Long, SortedMap<String, ExpectedSegment>> expected = (Map<Long, SortedMap<String, ExpectedSegment>>) mEstimateNextFragments
				.invoke(instance, beforeVersion);

		@SuppressWarnings("unchecked")
		final Map<Integer, List<Long>> result = (Map<Integer, List<Long>>) mMakeBucketsBefore
				.invoke(instance, expected);

		int count = 0;
		for (final List<Long> tmp : result.values()) {
			assertTrue(tmp.size() > 0);
			count += tmp.size();
		}

		assertEquals(count, expected.size());
	}

	@Test
	public void testMakeBucketsAfter1() throws Exception {
		final IClonesCodeFragmentMapper<Token> instance = new IClonesCodeFragmentMapper<>(
				mapper);

		@SuppressWarnings("unchecked")
		final Map<Long, CodeFragment<Token>> fragments = (Map<Long, CodeFragment<Token>>) mCollectFragments
				.invoke(instance, afterVersion.getCloneClasses().values());

		@SuppressWarnings("unchecked")
		final Map<Integer, List<Long>> result = (Map<Integer, List<Long>>) mMakeBucketsAfter
				.invoke(instance, fragments.values());

		int count = 0;
		for (final List<Long> tmp : result.values()) {
			assertTrue(tmp.size() > 0);
			count += tmp.size();

			if (tmp.size() >= 2) {
				CodeFragment<Token> reference = null;
				for (final long id : tmp) {
					if (reference == null) {
						reference = fragments.get(id);
						assertNotNull(reference);
					} else {
						final CodeFragment<Token> another = fragments.get(id);
						assertNotNull(another);

						assertTrue(reference.getStartPositions().size() == another
								.getStartPositions().size());

						final String refFirstPath = reference
								.getStartPositions().firstKey();
						final String anoFirstPath = another.getStartPositions()
								.firstKey();

						assertEquals(refFirstPath, anoFirstPath);

						final int refFirstStart = reference.getStartPositions()
								.get(refFirstPath);
						final int anoFirstStart = another.getStartPositions()
								.get(anoFirstPath);

						assertEquals(refFirstStart, anoFirstStart);

						final int refFirstEnd = reference.getEndPositions()
								.get(refFirstPath);
						final int anoFirstEnd = another.getEndPositions().get(
								anoFirstPath);

						assertEquals(refFirstEnd, anoFirstEnd);
					}
				}
			}
		}

		assertEquals(count, fragments.size());
	}
}
