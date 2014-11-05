package jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.iclones;

import static org.junit.Assert.fail;

import java.util.Map;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Token;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.ExactTokenEqualizer;
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
	public void testExpectSegment1() throws Exception {
		fail("Not yet implemented");
	}

}
