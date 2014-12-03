package jp.ac.osaka_u.ist.sdl.scanalyzer.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;

/**
 * This is an implementation of {@link IMetricsCalculator} that calculates
 * metrics for clone classes.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class DefaultCloneClassMetricsCalculator<E extends IProgramElement>
		implements IMetricsCalculator<E> {

	@Override
	public void calculate(Version<E> previous, Version<E> next) {
		final Map<Long, CloneClass<E>> cloneClasses = next.getCloneClasses();

		final ExecutorService pool = Executors.newCachedThreadPool();

		try {
			final List<Future<?>> futures = new ArrayList<>();

			for (final CloneClass<E> cloneClass : cloneClasses.values()) {
				final CalculateTask task = new CalculateTask(cloneClass);
				futures.add(pool.submit(task));
			}

			for (final Future<?> future : futures) {
				try {
					future.get();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} finally {
			pool.shutdown();
		}
	}

	/**
	 * This class represents a task to calculate metrics for a given clone
	 * class.
	 * 
	 * @author k-hotta
	 *
	 */
	private class CalculateTask implements Runnable {

		private final CloneClass<E> cloneClass;

		private CalculateTask(final CloneClass<E> cloneClass) {
			this.cloneClass = cloneClass;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

		}

	}

}
