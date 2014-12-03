package jp.ac.osaka_u.ist.sdl.scanalyzer.metrics;

import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;

/**
 * This interface represents a protocol of how to calculate metrics of elements.
 * 
 * @author k-hotta
 *
 */
public interface IMetricsCalculator<E extends IProgramElement> {

	/**
	 * Calculate metrics. All the calculated values must be stored in
	 * DBElements, otherwise the values will not be persist.
	 * 
	 * @param previous
	 *            the previous version
	 * @param next
	 *            the next version
	 * @param mappings
	 *            a collection containing all the clone class mappings between
	 *            the given two version
	 */
	public void calculate(final Version<E> previous, final Version<E> next,
			final Collection<CloneClassMapping<E>> mappings);

}
