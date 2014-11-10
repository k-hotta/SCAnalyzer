package jp.ac.osaka_u.ist.sdl.scanalyzer.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import difflib.myers.Equalizer;

/**
 * This class provides utilities related to LCS.
 * 
 * @author k-hotta
 *
 */
public class LCSFinder {

	/**
	 * Detect LCS among all the given lists of program elements.
	 * 
	 * @param targetElements
	 *            a map has all the target lists of program elements, the key
	 *            must have continuous integer values starting from 0, the value
	 *            represents each of target list of program elements
	 * @param equalizer
	 *            how to compare each program elements, if <code>null</code>
	 *            default comparator will be used, which compare elements with
	 *            equals method
	 * @return a map contains the result, each of whose key represents an index
	 *         of a list, which equals to that in the given targetElements, each
	 *         of whose value represents a sorted set of indexes to the elements
	 *         that are included in the detected LCS
	 */
	public static <E extends IProgramElement> Map<Long, SortedSet<Integer>> detectLcs(
			final Map<Long, List<E>> targetElements,
			final Equalizer<E> equalizer) {
		final Map<Integer, List<E>> intermadiateTargetElements = new TreeMap<>();
		final Map<Long, Integer> indexesMapping = new TreeMap<>();

		int index = 0;
		for (final Map.Entry<Long, List<E>> entry : targetElements.entrySet()) {
			intermadiateTargetElements.put(index, entry.getValue());
			indexesMapping.put(entry.getKey(), index);
			index++;
		}

		final Map<Integer, SortedSet<Integer>> intermadiateResult = detect(
				intermadiateTargetElements, equalizer);

		final Map<Long, SortedSet<Integer>> result = new TreeMap<Long, SortedSet<Integer>>();

		for (final long id : targetElements.keySet()) {
			final int currentIndex = indexesMapping.get(id);
			result.put(id, intermadiateResult.get(currentIndex));
		}

		return result;
	}

	/**
	 * Detect LCS among all the given lists of program elements.
	 * 
	 * @param targetElements
	 *            a map has all the target lists of program elements, the key
	 *            must have continuous integer values starting from 0, the value
	 *            represents each of target list of program elements
	 * @param equalizer
	 *            how to compare each program elements, if <code>null</code>
	 *            default comparator will be used, which compare elements with
	 *            equals method
	 * @return a map contains the result, each of whose key represents an index
	 *         of a list, which equals to that in the given targetElements, each
	 *         of whose value represents a sorted set of indexes to the elements
	 *         that are included in the detected LCS
	 */
	private static <E extends IProgramElement> Map<Integer, SortedSet<Integer>> detect(
			final Map<Integer, List<E>> targetElements,
			final Equalizer<E> equalizer) {
		if (targetElements == null) {
			throw new IllegalArgumentException(
					"the list of target elements is null");
		}

		if (targetElements.isEmpty()) {
			return new TreeMap<Integer, SortedSet<Integer>>();
		}

		final Map<Integer, SortedSet<Integer>> result = new TreeMap<>();

		// this is the LCS between all the fragments
		// first this is initialized with the first fragment
		List<E> lcs = new ArrayList<E>();
		lcs.addAll(targetElements.get(0));

		// this is for storing which element is in the LCS for each fragment
		// the outer key corresponds to the index in LCS
		// the inner key corresponds to the id of each fragment
		// the inner value corresponds to the index in the fragment
		SortedMap<Integer, Map<Integer, Integer>> lcsElementsMapping = new TreeMap<Integer, Map<Integer, Integer>>();

		SortedSet<Integer> lcsElementsInFirstFragment = new TreeSet<Integer>();

		for (int i = 0; i < lcs.size(); i++) {
			final Map<Integer, Integer> newMapping = new TreeMap<Integer, Integer>();
			newMapping.put(0, i);
			lcsElementsMapping.put(i, newMapping);
			lcsElementsInFirstFragment.add(i);
		}
		result.put(0, lcsElementsInFirstFragment);

		for (int i = 1; i < targetElements.size(); i++) {
			final List<E> target = targetElements.get(i);

			final Patch<E> patch = DiffUtils.diff(lcs, target, equalizer);
			final SortedMap<Integer, Integer> mapping = detectMapping(patch,
					lcs, target);

			final SortedSet<Integer> lcsElementsInThisFragment = new TreeSet<Integer>();

			final SortedMap<Integer, Map<Integer, Integer>> previousLcsElementsMapping = new TreeMap<Integer, Map<Integer, Integer>>();
			previousLcsElementsMapping.putAll(lcsElementsMapping);
			for (final Map.Entry<Integer, Map<Integer, Integer>> entry : lcsElementsMapping
					.entrySet()) {
				if (!mapping.containsKey(entry.getKey())) {
					// the element in previous lcs is no longer in current one
					Map<Integer, Integer> noLongerLcsElements = previousLcsElementsMapping
							.remove(entry.getKey());

					for (final Map.Entry<Integer, Integer> noLongerEntry : noLongerLcsElements
							.entrySet()) {
						result.get(noLongerEntry.getKey()).remove(
								noLongerEntry.getValue());
					}

					continue;
				}

				final Map<Integer, Integer> currentMapping = entry.getValue();
				currentMapping.put(i, mapping.get(entry.getKey()));
				lcsElementsInThisFragment.add(mapping.get(entry.getKey()));
			}
			result.put(i, lcsElementsInThisFragment);

			lcsElementsMapping.clear();

			int newIndex = 0;
			for (final Map.Entry<Integer, Map<Integer, Integer>> entry : previousLcsElementsMapping
					.entrySet()) {
				lcsElementsMapping.put(newIndex++, entry.getValue());
			}

			lcs = update(lcs, patch);
		}

		return result;
	}

	private static <E extends IProgramElement> SortedMap<Integer, Integer> detectMapping(
			final Patch<E> patch, final List<E> left, final List<E> right) {
		final SortedMap<Integer, Integer> result = new TreeMap<Integer, Integer>();

		int counterForLeft = 0;
		// list of indexes for elements in left which are in the lcs
		final List<Integer> lcsIndexLeft = new ArrayList<Integer>();

		int counterForRight = 0;
		// list of indexes for elements in right which are in the lcs
		final List<Integer> lcsIndexRight = new ArrayList<Integer>();

		final List<Delta<E>> deltas = new ArrayList<Delta<E>>();
		deltas.addAll(patch.getDeltas());

		// make sure deltas are sorted based on left
		Collections.sort(deltas, (v1, v2) -> Integer.compare(v1.getOriginal()
				.getPosition(), v2.getOriginal().getPosition()));

		for (final Delta<E> delta : deltas) {
			final Chunk<E> chunk = delta.getOriginal();
			while (counterForLeft < chunk.getPosition()) {
				lcsIndexLeft.add(counterForLeft++);
			}
			counterForLeft += chunk.getLines().size();
		}
		while (counterForLeft < left.size()) {
			lcsIndexLeft.add(counterForLeft++);
		}

		// make sure deltas are sorted based on right
		Collections.sort(deltas, (v1, v2) -> Integer.compare(v1.getRevised()
				.getPosition(), v2.getRevised().getPosition()));

		for (final Delta<E> delta : deltas) {
			final Chunk<E> chunk = delta.getRevised();
			while (counterForRight < chunk.getPosition()) {
				lcsIndexRight.add(counterForRight++);
			}
			counterForRight += chunk.getLines().size();
		}
		while (counterForRight < right.size()) {
			lcsIndexRight.add(counterForRight++);
		}

		assert (lcsIndexLeft.size() == lcsIndexRight.size());

		for (int i = 0; i < lcsIndexLeft.size(); i++) {
			result.put(lcsIndexLeft.get(i), lcsIndexRight.get(i));
		}

		return result;
	}

	private static <E extends IProgramElement> List<E> update(
			final List<E> target, final Patch<E> patch) {
		final List<Integer> toBeRemoved = new ArrayList<Integer>();

		for (final Delta<E> delta : patch.getDeltas()) {
			int position = delta.getOriginal().getPosition();
			int size = delta.getOriginal().size();
			for (int i = 0; i < size; i++) {
				toBeRemoved.add(position + i);
			}
		}

		// make sure toBeRemoved is descending order
		// note the order of o1 and o2 is reversed
		Collections.sort(toBeRemoved, (o1, o2) -> Integer.compare(o2, o1));

		for (int i : toBeRemoved) {
			target.remove(i);
		}

		return target;
	}

}
