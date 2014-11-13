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
abstract class ElementTypeSensitiveWorkerInitializer<E extends IProgramElement> {

	/**
	 * Set up source file parser
	 * 
	 * @param language
	 * @return
	 */
	abstract ISourceFileParser<E> setupSourceFileParser(final Language language);

	/**
	 * Set up element equalizer
	 * 
	 * @param elementEqualizer
	 * @return
	 */
	abstract Equalizer<E> setupEqualizer(final ElementEqualizer elementEqualizer);

	/**
	 * Set up element mapper
	 * 
	 * @return
	 */
	abstract IProgramElementMapper<E> setupElementMapper(
			final ElementMappingAlgorithm algorithm);

}
