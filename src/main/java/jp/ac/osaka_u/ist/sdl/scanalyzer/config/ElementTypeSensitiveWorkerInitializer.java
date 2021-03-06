package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.ISourceFileParser;
import jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.IProgramElementMapper;
import difflib.myers.Equalizer;

/**
 * This abstract class provides some methods to initialize workers which are
 * element type sensitive.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public abstract class ElementTypeSensitiveWorkerInitializer<E extends IProgramElement> {

	/**
	 * Set up source file parser
	 * 
	 * @param language
	 * @return
	 */
	abstract ISourceFileParser<E> setupSourceFileParser(final Language language);

	/**
	 * Set up element equalizer for LCS
	 * 
	 * @param elementEqualizer
	 * @return
	 */
	abstract Equalizer<E> setupEqualizerForLcs(
			final ElementEqualizer elementEqualizer);

	/**
	 * Set up element equalizer for Diff. This method is expected to return the
	 * most strict equalizer so that any differences are identified.
	 * 
	 * @return
	 */
	abstract Equalizer<E> setupEqualizerForDiff();

	/**
	 * Set up element mapper
	 * 
	 * @return
	 */
	abstract IProgramElementMapper<E> setupElementMapper(
			final ElementMappingAlgorithm algorithm);

}
