package jp.ac.osaka_u.ist.sdl.scanalyzer.metrics;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;

/**
 * This interface represents a protocol of how to calculate metrics of elements.
 * 
 * @author k-hotta
 *
 */
@FunctionalInterface
public interface IMetricsCalculator<E extends IProgramElement> {

	/**
	 * Calculate metrics. If the calculated values are stored as fields of
	 * DBElements, the values will be stored into database. If it is not the
	 * case, the implementations of this method should output the results in
	 * their own ways, otherwise the results will not be reported at all.
	 * 
	 * @param previous
	 *            the previous version
	 * @param next
	 *            the next version
	 */
	public void calculate(final Version<E> previous, final Version<E> next);

}
