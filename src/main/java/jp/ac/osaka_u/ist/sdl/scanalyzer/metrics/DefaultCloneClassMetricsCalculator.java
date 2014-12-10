package jp.ac.osaka_u.ist.sdl.scanalyzer.metrics;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(DefaultCloneClassMetricsCalculator.class);

	/**
	 * The equalizer of elements to detect LCS
	 */
	private final Equalizer<E> equalizerForLcs;

	/**
	 * The equalizer of elements to detect diff
	 */
	private final Equalizer<E> equalizerForDiff;

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

	public DefaultCloneClassMetricsCalculator(
			final Equalizer<E> equalizerForLcs,
			final Equalizer<E> equalizerForDiff) {
		this.equalizerForLcs = equalizerForLcs;
		this.equalizerForDiff = equalizerForDiff;
		this.allElements = new ConcurrentSkipListMap<>();
		this.lcsElementsInCloned = new ConcurrentSkipListMap<>();
		this.lcsElementsInAll = new ConcurrentSkipListMap<>();
	}

	@Override
	public void calculate(Version<E> previous, Version<E> next) {
		setup(previous);

		logger.debug("analyzing similarity of code fragments in each clone class");
		analyzeSimilarity(next);
		logger.debug("complete analyzing similarity");

		logger.debug("analyzing modifications on each clone class");
		analyzeModifications(next);
		logger.debug("complete analyzing modifications");
	}

	private void setup(Version<E> previous) {
		if (previous == null) {
			return;
		}

		final Map<Long, Map<Long, List<E>>> allElementsCopy = new TreeMap<>();
		allElementsCopy.putAll(allElements);

		final Map<Long, Map<Long, List<E>>> lcsElementsInClonedCopy = new TreeMap<>();
		lcsElementsInClonedCopy.putAll(lcsElementsInCloned);

		final Map<Long, Map<Long, List<E>>> lcsElementsInAllCopy = new TreeMap<>();
		lcsElementsInAllCopy.putAll(lcsElementsInAll);

		this.allElements.clear();
		this.lcsElementsInCloned.clear();
		this.lcsElementsInAll.clear();

		for (final CloneClass<E> oldCloneClass : previous.getCloneClasses()
				.values()) {
			final long id = oldCloneClass.getId();
			final Map<Long, List<E>> allElementsInCloneClass = allElementsCopy
					.get(id);
			final Map<Long, List<E>> lcsElementsInClonedInCloneClass = lcsElementsInClonedCopy
					.get(id);
			final Map<Long, List<E>> lcsElementsInAllInCloneClass = lcsElementsInAllCopy
					.get(id);

			if (allElementsInCloneClass == null
					|| lcsElementsInClonedInCloneClass == null
					|| lcsElementsInAllInCloneClass == null) {
				throw new IllegalStateException(
						"the information in previous revision has been lost");
			}

			this.allElements.put(id, allElementsInCloneClass);
			this.lcsElementsInCloned.put(id, lcsElementsInClonedInCloneClass);
			this.lcsElementsInAll.put(id, lcsElementsInAllInCloneClass);
		}
	}

	private void analyzeSimilarity(Version<E> next) {
		final CloneClassSimilarityAnalyzer<E> similarityAnalizer = new CloneClassSimilarityAnalyzer<E>(
				next.getCloneClasses().values(), allElements,
				lcsElementsInCloned, lcsElementsInAll, equalizerForLcs);
		similarityAnalizer.analyze();
	}

	private void analyzeModifications(Version<E> next) {
		final CloneClassModificationAnalyzer<E> modificationAnalyzer = new CloneClassModificationAnalyzer<>(
				allElements, lcsElementsInCloned, lcsElementsInAll, next
						.getCloneClassMappings().values(), equalizerForDiff);
		modificationAnalyzer.run();
	}

}
