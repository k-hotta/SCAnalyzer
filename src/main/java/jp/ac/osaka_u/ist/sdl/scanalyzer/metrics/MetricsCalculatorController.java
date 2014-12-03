package jp.ac.osaka_u.ist.sdl.scanalyzer.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;

/**
 * This class controls to run multiple metrics calculators.
 * 
 * @author k-hotta
 *
 * @param <E>
 */
public class MetricsCalculatorController<E extends IProgramElement> {

	private final List<IMetricsCalculator<E>> calculators;

	public MetricsCalculatorController() {
		this.calculators = new ArrayList<>();
	}

	public void addCalculator(final IMetricsCalculator<E> calculator) {
		this.calculators.add(calculator);
	}

	public void calculateAll(final Version<E> previous, final Version<E> next) {
		for (final IMetricsCalculator<E> calculator : calculators) {
			calculator.calculate(previous, next);
		}
	}

}
