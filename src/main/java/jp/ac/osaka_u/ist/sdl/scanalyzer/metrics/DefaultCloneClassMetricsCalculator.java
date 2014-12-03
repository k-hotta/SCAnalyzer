package jp.ac.osaka_u.ist.sdl.scanalyzer.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.util.LCSFinder;
import difflib.myers.Equalizer;

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

	/**
	 * The equalizer of elements
	 */
	private final Equalizer<E> equalizer;

	public DefaultCloneClassMetricsCalculator(final Equalizer<E> equalizer) {
		this.equalizer = equalizer;
	}

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
			final Map<Long, CodeFragment<E>> clonedFragments = cloneClass
					.getCodeFragments();
			final Map<Long, CodeFragment<E>> ghostFragments = cloneClass
					.getGhostFragments();

			// find LCS among all the CLONED fragments
			final Map<Long, List<E>> clonedFragmentElements = LCSFinder
					.getFragmentElements(clonedFragments);
			final Map<Long, SortedSet<Integer>> lcsInCloned = LCSFinder
					.detectLcs(clonedFragmentElements, equalizer);

			int numCommonClonedElements = 0;
			final Map<Long, List<E>> clonedFragmentElementsInCloneLcs = new TreeMap<>();

			Long firstKey = null;
			for (final Map.Entry<Long, SortedSet<Integer>> entry : lcsInCloned
					.entrySet()) {
				final List<E> all = clonedFragmentElements.get(entry.getKey());
				final List<E> newList = new ArrayList<>();

				for (final Integer index : entry.getValue()) {
					newList.add(all.get(index));
				}

				clonedFragmentElementsInCloneLcs.put(entry.getKey(), newList);

				if (firstKey == null) {
					firstKey = entry.getKey();
					numCommonClonedElements = newList.size();
				}
			}

			// find LCS among the LCS of cloned fragments and
			// all the ghost fragments
			final Map<Long, List<E>> ghostFragmentElements = LCSFinder
					.getFragmentElements(ghostFragments);

			// add the LCS of cloned fragments into target
			if (firstKey != null) {
				ghostFragmentElements.put(firstKey,
						clonedFragmentElementsInCloneLcs.get(firstKey));
			}

			final Map<Long, SortedSet<Integer>> lcsInAll = LCSFinder.detectLcs(
					ghostFragmentElements, equalizer);
			final Map<Long, List<E>> fragmentElementsInAllLcs = new TreeMap<>();

			int numCommonAllElements = 0;

			for (final Map.Entry<Long, SortedSet<Integer>> entry : lcsInAll
					.entrySet()) {
				final List<E> all = ghostFragmentElements.get(entry.getKey());
				final List<E> newList = new ArrayList<>();

				for (final Integer index : entry.getValue()) {
					newList.add(all.get(index));
				}

				if (entry.getKey() == firstKey) {
					// special treat for the LCS among cloned fragments
					for (final Map.Entry<Long, List<E>> clonedEntry : clonedFragmentElementsInCloneLcs
							.entrySet()) {
						if (clonedEntry.getKey() != firstKey) {
							final List<E> allInCloned = clonedEntry.getValue();
							final List<E> newListForCloned = new ArrayList<>();

							for (final Integer index : entry.getValue()) {
								newListForCloned.add(allInCloned.get(index));
							}

							fragmentElementsInAllLcs.put(clonedEntry.getKey(),
									newListForCloned);
						}
					}
				}

				fragmentElementsInAllLcs.put(entry.getKey(), newList);
				if (numCommonAllElements == 0) {
					numCommonAllElements = newList.size();
				}
			}

			cloneClass.getCore().setNumCommonClonedElements(
					numCommonClonedElements);
			cloneClass.getCore().setNumCommonAllElements(numCommonAllElements);
		}

	}

}
