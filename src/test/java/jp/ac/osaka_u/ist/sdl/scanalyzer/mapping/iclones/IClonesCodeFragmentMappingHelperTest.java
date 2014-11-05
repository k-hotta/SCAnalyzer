package jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.iclones;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.ExactTokenEqualizer;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Token;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.IProgramElementMapper;
import jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.TraditionalDiffTokenMapper;
import jp.ac.osaka_u.ist.sdl.scanalyzer.util.TestVersionBuilder;

import org.junit.BeforeClass;
import org.junit.Test;

public class IClonesCodeFragmentMappingHelperTest {

	private static Version<Token> beforeVersion;

	private static Version<Token> afterVersion;

	private static IProgramElementMapper<Token> mapper;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		final TestVersionBuilder versionBuilder = new TestVersionBuilder();
		final Map<Long, Version<Token>> versions = versionBuilder.build();

		beforeVersion = versions.get((long) 410);
		afterVersion = versions.get((long) 419);

		mapper = new TraditionalDiffTokenMapper(new ExactTokenEqualizer());
		mapper.prepare(beforeVersion, afterVersion);
	}

	@Test
	public void testExpectFragment1() throws Exception {
		final Set<CloneClass<Token>> firstJudge = retrieveCloneClass(
				beforeVersion,
				"/c20r_main/src/jp/ac/osaka_u/ist/sdl/c20r/clone_tracer/TraceThread.java",
				28);
		final Set<CloneClass<Token>> secondJudge = retrieveCloneClass(
				firstJudge,
				"/c20r_main/src/jp/ac/osaka_u/ist/sdl/c20r/genealogy/clonepairdetector/ClonePairDetectThread.java",
				36);

		CloneClass<Token> target = null;
		for (CloneClass<Token> tmp : secondJudge) {
			target = tmp;
			break;
		}

		final CodeFragment<Token> targetFragment = target.getCodeFragments()
				.get(target.getCodeFragments().firstKey());

		final Map<String, ExpectedSegment> expected = IClonesCodeFragmentMappingHelper
				.expect(targetFragment, mapper);

		assertTrue(expected.size() == 1);
		ExpectedSegment expectedSeg = expected
				.get("/c20r_main/src/jp/ac/osaka_u/ist/sdl/c20r/clone_tracer/TraceThread.java");

		final Set<CloneClass<Token>> referenceFirstJudge = retrieveCloneClass(
				afterVersion,
				"/c20r_main/src/jp/ac/osaka_u/ist/sdl/c20r/clone_tracer/TraceThread.java",
				32);
		final Set<CloneClass<Token>> referenceSecondJudge = retrieveCloneClass(
				referenceFirstJudge,
				"/c20r_main/src/jp/ac/osaka_u/ist/sdl/c20r/genealogy/clonepairdetector/ClonePairDetectThread.java",
				41);

		CloneClass<Token> reference = null;
		for (CloneClass<Token> tmp : referenceSecondJudge) {
			reference = tmp;
			break;
		}

		final CodeFragment<Token> referenceFragment = reference
				.getCodeFragments()
				.get(reference.getCodeFragments().firstKey());
		final Segment<Token> first = referenceFragment.getSegments().get(0);
		final Segment<Token> last = referenceFragment.getSegments().get(
				referenceFragment.getSegments().size() - 1);

		assertTrue(first.getFirstElement().getPosition() == expectedSeg
				.getStartPosition());
		assertTrue(last.getLastElement().getPosition() == expectedSeg
				.getEndPosition());
	}

	private Set<CloneClass<Token>> retrieveCloneClass(
			final Collection<CloneClass<Token>> cloneClasses,
			final String path, final int startLine) {
		List<Segment<Token>> segments = new ArrayList<Segment<Token>>();
		for (CloneClass<Token> cc : cloneClasses) {
			for (CodeFragment<Token> cf : cc.getCodeFragments().values()) {
				segments.addAll(cf.getSegments());
			}
		}

		List<Segment<Token>> resultSegments = segments
				.parallelStream()
				.filter(sg -> sg.getContents().first().getOwnerSourceFile()
						.getPath().equals(path))
				.filter(sg -> sg.getContents().first().getLine() == startLine)
				.collect(Collectors.toList());

		final Set<CloneClass<Token>> result = new HashSet<CloneClass<Token>>();
		for (final Segment<Token> segment : resultSegments) {
			result.add(segment.getCodeFragment().getCloneClass());
		}
		return result;
	}

	private Set<CloneClass<Token>> retrieveCloneClass(
			final Version<Token> version, final String path, final int startLine) {
		return retrieveCloneClass(version.getCloneClasses().values(), path,
				startLine);
	}
}
