package jp.ac.osaka_u.ist.sdl.scanalyzer.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.util.LCSFinder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import difflib.myers.Equalizer;

/**
 * This class represents a task to calculate metrics for a given clone class.
 * 
 * @author k-hotta
 *
 */
public class CloneClassSimilarityAnalyzer<E extends IProgramElement> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(CloneClassSimilarityAnalyzer.class);

	/**
	 * The clone classes
	 */
	private final Collection<CloneClass<E>> cloneClasses;

	/**
	 * This map contains all the elements in code fragments
	 */
	private final ConcurrentMap<Long, Map<Long, List<E>>> allElements;

	/**
	 * This map contains the information about which elements are included in
	 * LCS between CLONED fragments for each clone class. The keys are the ids
	 * of clone classes, and the values are maps between fragment ids and
	 * elements in each of the fragment.
	 */
	private final ConcurrentMap<Long, Map<Long, List<E>>> lcsElementsInCloned;

	/**
	 * This map contains the information about which elements are included in
	 * LCS between ALL fragments for each clone class. The keys are the ids of
	 * clone classes, and the values are maps between fragment ids and elements
	 * in each of the fragment.
	 */
	private final ConcurrentMap<Long, Map<Long, List<E>>> lcsElementsInAll;

	/**
	 * The equalizer to detect LCS
	 */
	private final Equalizer<E> equalizer;

	/**
	 * The counter, this is just for logging
	 */
	private final AtomicLong count;

	public CloneClassSimilarityAnalyzer(
			final Collection<CloneClass<E>> cloneClasses,
			final ConcurrentMap<Long, Map<Long, List<E>>> allElements,
			final ConcurrentMap<Long, Map<Long, List<E>>> lcsElementsInCloned,
			final ConcurrentMap<Long, Map<Long, List<E>>> lcsElementsInAll,
			final Equalizer<E> equalizer) {
		this.cloneClasses = cloneClasses;
		this.allElements = allElements;
		this.lcsElementsInCloned = lcsElementsInCloned;
		this.lcsElementsInAll = lcsElementsInAll;
		this.equalizer = equalizer;
		this.count = new AtomicLong(0);
	}

	public void analyze() {
		final ExecutorService pool = Executors.newCachedThreadPool();

		try {
			final List<Future<?>> futures = new ArrayList<>();
			for (final CloneClass<E> cloneClass : cloneClasses) {
				final SimilarityAnalyzeTask task = new SimilarityAnalyzeTask(
						cloneClass);
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

	private class SimilarityAnalyzeTask implements Runnable {

		/**
		 * the clone class under consideration
		 */
		private final CloneClass<E> cloneClass;

		private SimilarityAnalyzeTask(final CloneClass<E> cloneClass) {
			this.cloneClass = cloneClass;
		}

		public void run() {
			final Map<Long, CodeFragment<E>> clonedFragments = cloneClass
					.getCodeFragments();
			final Map<Long, CodeFragment<E>> ghostFragments = cloneClass
					.getGhostFragments();

			// elements included in LCS
			final Map<Long, List<E>> clonedFragmentElements = LCSFinder
					.getFragmentElements(clonedFragments);
			final Map<Long, List<E>> ghostFragmentElements = LCSFinder
					.getFragmentElements(ghostFragments);

			final Map<Long, List<E>> allFragmentElements = new TreeMap<>();
			allFragmentElements.putAll(clonedFragmentElements);
			allFragmentElements.putAll(ghostFragmentElements);

			/*
			 * find LCS among all the CLONED fragments
			 */
			final SortedMap<Long, List<E>> clonedFragmentElementsInCloneLcs = findLcsInCloned(clonedFragmentElements);

			final Long firstKey = clonedFragmentElementsInCloneLcs.firstKey();
			final int numCommonClonedElements = (firstKey == null) ? 0
					: clonedFragmentElementsInCloneLcs.get(firstKey).size();

			/*
			 * find LCS among the LCS of cloned fragments and all the ghost
			 * fragments
			 */
			final SortedMap<Long, List<E>> fragmentElementsInAllLcs = findLcsInAll(
					ghostFragmentElements, clonedFragmentElementsInCloneLcs,
					firstKey);

			final int numCommonAllElements = fragmentElementsInAllLcs.get(
					fragmentElementsInAllLcs.firstKey()).size();

			// record the metric values for clone classes
			cloneClass.getCore().setNumCommonClonedElements(
					numCommonClonedElements);
			cloneClass.getCore().setNumCommonAllElements(numCommonAllElements);

			// store the information of LCSs
			allElements.put(cloneClass.getId(), allFragmentElements);
			lcsElementsInCloned.put(cloneClass.getId(),
					clonedFragmentElementsInCloneLcs);
			lcsElementsInAll.put(cloneClass.getId(), fragmentElementsInAllLcs);

			logger.debug("[" + count.incrementAndGet() + "/"
					+ cloneClasses.size() + "] complete analyzing clone class "
					+ cloneClass.getId());
		}

		private SortedMap<Long, List<E>> findLcsInCloned(
				final Map<Long, List<E>> clonedFragmentElements) {
			final Map<Long, SortedSet<Integer>> lcsInCloned = LCSFinder
					.detectLcs(clonedFragmentElements, equalizer);

			final SortedMap<Long, List<E>> clonedFragmentElementsInCloneLcs = new TreeMap<>();

			for (final Map.Entry<Long, SortedSet<Integer>> entry : lcsInCloned
					.entrySet()) {
				final List<E> all = clonedFragmentElements.get(entry.getKey());
				final List<E> newList = new ArrayList<>();

				for (final Integer index : entry.getValue()) {
					newList.add(all.get(index));
				}

				clonedFragmentElementsInCloneLcs.put(entry.getKey(), newList);
			}

			return clonedFragmentElementsInCloneLcs;
		}

		private SortedMap<Long, List<E>> findLcsInAll(
				final Map<Long, List<E>> ghostFragmentElements,
				final SortedMap<Long, List<E>> clonedFragmentElementsInCloneLcs,
				final Long firstKey) {
			// add the LCS of cloned fragments into target
			if (firstKey != null) {
				ghostFragmentElements.put(firstKey,
						clonedFragmentElementsInCloneLcs.get(firstKey));
			}

			final Map<Long, SortedSet<Integer>> lcsInAll = LCSFinder.detectLcs(
					ghostFragmentElements, equalizer);
			final SortedMap<Long, List<E>> fragmentElementsInAllLcs = new TreeMap<>();

			for (final Map.Entry<Long, SortedSet<Integer>> entry : lcsInAll
					.entrySet()) {
				final List<E> all = ghostFragmentElements.get(entry.getKey());
				final List<E> newList = new ArrayList<>();

				for (final Integer index : entry.getValue()) {
					newList.add(all.get(index));
				}

				if (firstKey != null && firstKey.equals(entry.getKey())) {
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
			}

			return fragmentElementsInAllLcs;
		}

	}

}
