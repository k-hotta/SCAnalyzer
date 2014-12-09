package jp.ac.osaka_u.ist.sdl.scanalyzer.metrics;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
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
		this.lcsElementsInCloned.clear();
		this.lcsElementsInAll.clear();

		analyzeSimilarity(next);

		analyzeModifications(next);
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
