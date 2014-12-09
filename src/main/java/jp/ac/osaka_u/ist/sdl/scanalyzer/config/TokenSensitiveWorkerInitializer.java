package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

import difflib.myers.Equalizer;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.ExactTokenEqualizer;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.NearMissTokenEqualizer;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Token;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.ISourceFileParser;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.TokenSourceFileParser;
import jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.IProgramElementMapper;
import jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.TraditionalDiffTokenMapper;

/**
 * This class provides some workers which are designed to process tokens.
 * 
 * @author k-hotta
 *
 */
public class TokenSensitiveWorkerInitializer extends
		ElementTypeSensitiveWorkerInitializer<Token> {

	@Override
	ISourceFileParser<Token> setupSourceFileParser(Language language) {
		return new TokenSourceFileParser(language);
	}

	@Override
	Equalizer<Token> setupEqualizerForLcs(ElementEqualizer elementEqualizer) {
		switch (elementEqualizer) {
		case EXACT:
			return new ExactTokenEqualizer();
		case NEAR_MISS:
			return new NearMissTokenEqualizer();
		}

		throw new IllegalStateException("cannot initialize token equalizer");
	}

	@Override
	Equalizer<Token> setupEqualizerForDiff() {
		return new ExactTokenEqualizer();
	}

	@Override
	IProgramElementMapper<Token> setupElementMapper(
			final ElementMappingAlgorithm algorithm) {
		// the equalizer is used to detect changes, not clones
		// hence the exact equalizer will be used in any cases
		final Equalizer<Token> equalizer = new ExactTokenEqualizer();

		switch (algorithm) {
		case TRADITIONAL_DIFF:
			return new TraditionalDiffTokenMapper(equalizer);
		}

		throw new IllegalStateException("cannot initialize token mapper");
	}

}
