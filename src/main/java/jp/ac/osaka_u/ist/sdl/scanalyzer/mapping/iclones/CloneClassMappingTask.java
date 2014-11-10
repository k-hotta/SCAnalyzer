package jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.iclones;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;

/**
 * This class represents a task to map clone classes between two versions.
 * 
 * @author k-hotta
 *
 */
class CloneClassMappingTask<E extends IProgramElement> implements Runnable {

	/**
	 * This list contains perfect matches.
	 */
	private final List<CloneClass<E>> perfectMatches;

	/**
	 * This list contains specials cases of best matches. That is, all the
	 * elements of the target clone class OR all the elements in a clone class
	 * of this list completely matched those in the other clone class, but there
	 * exist some other unmatched fragments in the other clone class. This will
	 * happen when a new fragment is introduced or a fragment is deleted.
	 * According to the literature, such a match will be included even though
	 * there exist other best matches.
	 */
	private final List<CloneClass<E>> inclusiveMatches;

	/**
	 * This map contains best matches. The value stores how many code fragments
	 * are mapped between the target and the key clone class.
	 */
	private final Map<CloneClass<E>, Integer> bestMatches;

	/**
	 * The clone class of which the task attempts to find matches.
	 */
	private final CloneClass<E> targetCloneClass;

	/**
	 * The bucket created from the before version, which must have been updated
	 * with the diff information between two versions.
	 */
	private final ConcurrentMap<Long, Integer> beforeBucket;

	/**
	 * The bucket created from the after version.
	 */
	private final ConcurrentMap<Integer, List<Long>> afterBucket;

	/**
	 * A map contains all the code fragments in after version
	 */
	private final ConcurrentMap<Long, CodeFragment<E>> afterCodeFragments;

	CloneClassMappingTask(final CloneClass<E> targetCloneClass,
			final ConcurrentMap<Long, Integer> beforeBucket,
			final ConcurrentMap<Integer, List<Long>> afterBucket,
			final ConcurrentMap<Long, CodeFragment<E>> afterCodeFragments) {
		this.perfectMatches = new ArrayList<CloneClass<E>>();
		this.inclusiveMatches = new ArrayList<CloneClass<E>>();
		this.bestMatches = new TreeMap<>((k1, k2) -> Long.compare(k1.getId(),
				k2.getId()));
		this.targetCloneClass = targetCloneClass;
		this.beforeBucket = beforeBucket;
		this.afterBucket = afterBucket;
		this.afterCodeFragments = afterCodeFragments;
	}

	final CloneClass<E> getTargetCloneClass() {
		return targetCloneClass;
	}

	final List<CloneClass<E>> getPerfectMatches() {
		return perfectMatches;
	}

	final Map<CloneClass<E>, Integer> getBestMatches() {
		return bestMatches;
	}

	final List<CloneClass<E>> getInclusiveMatches() {
		return inclusiveMatches;
	}

	@Override
	public void run() {
		// find similar fragments for each of code fragments
		final Map<CodeFragment<E>, List<CodeFragment<E>>> similarFragmentMapping = findSimliarFragmentMapping();

		if (similarFragmentMapping.isEmpty()) {
			// no candidate of mapping found
			return;
		}

		// find candidate clone classes
		final Map<Integer, List<CloneClass<E>>> occurrenceToCloneClass = findCandidateCloneClasses(similarFragmentMapping);

		// detect the maximum number of matched code fragments
		// we are only interested in clone classes that have the highest number
		// of matched code fragments
		final Optional<Integer> opt = occurrenceToCloneClass.keySet().stream()
				.max((i1, i2) -> Integer.compare(i1, i2));
		final int max = opt.get();

		if (max < 2) {
			// if the maximum number of matched fragments is smaller than 2,
			// we must not map the target clone class to any of clone classes
			// this is according to the referenced paper
			return;
		}

		final List<CloneClass<E>> mostMatchedCloneClasses = occurrenceToCloneClass
				.get(max);

		boolean perfectMatchFound = false;
		boolean bestMatchFound = false;
		for (final CloneClass<E> mostMatchedCloneClass : mostMatchedCloneClasses) {

			if (targetCloneClass.getCodeFragments().size() == mostMatchedCloneClass
					.getCodeFragments().size()
					&& targetCloneClass.getCodeFragments().size() == max) {
				// The numbers of code fragments in both of the two clone
				// classes equal to that of matched fragments.
				// This means every of the code fragments in before version is
				// matched to those in after version and vice versa.
				// In other words, this means these two clone classes are a
				// perfect match

				this.perfectMatches.add(mostMatchedCloneClass);
				perfectMatchFound = true;
			}

			else if (targetCloneClass.getCodeFragments().size() == max
					|| mostMatchedCloneClass.getCodeFragments().size() == max) {
				// All the fragments in a clone class are completely mapped, but
				// not vice versa. This may occur in case where a new fragment
				// was added/deleted. This case should be treated in a special
				// way, which is stated in the paper of Saman Bazrafshan.

				this.inclusiveMatches.add(mostMatchedCloneClass);
				bestMatchFound = true;
			}

			else {
				// In this case, the most matched clone class is keeped as a
				// likely match

				this.bestMatches.put(mostMatchedCloneClass, max);
			}
		}

		// If any perfect match found, the best matches and likely matches
		// should be ignored.
		// Similarly, if no perfect match but any best match found, the likely
		// matches should be ignored.
		if (perfectMatchFound) {
			this.inclusiveMatches.clear();
			this.bestMatches.clear();
		} else if (bestMatchFound) {
			this.bestMatches.clear();
		}

	}

	/**
	 * Detect mapping between code fragments in the target clone class and its
	 * similar fragments. Here, similar fragments mean those have the same
	 * bucket hash value.
	 * 
	 * @return a map of similar code fragments, whose key is a code fragment,
	 *         whose value is a list of similar fragments to the key.
	 */
	private Map<CodeFragment<E>, List<CodeFragment<E>>> findSimliarFragmentMapping() {
		final Map<CodeFragment<E>, List<CodeFragment<E>>> similarFragmentMapping = new TreeMap<>(
				(k1, k2) -> Long.compare(k1.getId(), k2.getId()));
		
		final List<CodeFragment<E>> codeFragmentsOfInterest = new ArrayList<CodeFragment<E>>();
		codeFragmentsOfInterest.addAll(targetCloneClass.getCodeFragments().values());
		codeFragmentsOfInterest.addAll(targetCloneClass.getGhostFragments().values());

		for (final CodeFragment<E> codeFragment : codeFragmentsOfInterest) {
			if (!beforeBucket.containsKey(codeFragment.getId())) {
				// the fragment was removed and so no further process is
				// required
				continue;
			}

			final int bucketHash = beforeBucket.get(codeFragment.getId());
			if (afterBucket.containsKey(bucketHash)) {
				final List<Long> similarFragmentIds = afterBucket
						.get(bucketHash);

				if (!afterCodeFragments.keySet()
						.containsAll(similarFragmentIds)) {
					throw new IllegalStateException(
							"cannot find some fragments in after fragments");
				}

				final List<CodeFragment<E>> similarFragments = similarFragmentIds
						.stream().map(l -> afterCodeFragments.get(l))
						.collect(Collectors.toList());
				similarFragmentMapping.put(codeFragment, similarFragments);
			}
		}

		return similarFragmentMapping;
	}

	/**
	 * Find clone classes in after version that are the candidates of mapping
	 * between the target clone class.
	 * 
	 * @param similarFragmentMapping
	 *            a map between code fragment and a list of its similar
	 *            fragments
	 * @return a map that represents candidate clone classes, whose key is the
	 *         number of shared fragments, whose value is a list of clone
	 *         classes which share the same number of fragments as the key.
	 */
	private Map<Integer, List<CloneClass<E>>> findCandidateCloneClasses(
			final Map<CodeFragment<E>, List<CodeFragment<E>>> similarFragmentMapping) {
		// this map contains how many fragments in a clone class shared with the
		// target clone class
		final Map<CloneClass<E>, Integer> cloneClassToOccurrence = new TreeMap<>(
				(k1, k2) -> Long.compare(k1.getId(), k2.getId()));

		for (final List<CodeFragment<E>> listSimilarFragments : similarFragmentMapping
				.values()) {
			for (final CodeFragment<E> codeFragment : listSimilarFragments) {
				final CloneClass<E> candidateCloneClass = codeFragment
						.getCloneClass();
				if (cloneClassToOccurrence.containsKey(candidateCloneClass)) {
					cloneClassToOccurrence
							.put(candidateCloneClass, cloneClassToOccurrence
									.get(candidateCloneClass) + 1);
				} else {
					cloneClassToOccurrence.put(candidateCloneClass, 1);
				}
			}
		}

		final Map<Integer, List<CloneClass<E>>> occurrenceToCloneClass = cloneClassToOccurrence
				.keySet()
				.stream()
				.collect(
						Collectors.toMap(
								cc -> cloneClassToOccurrence.get(cc),
								cc -> {
									final List<CloneClass<E>> list = new ArrayList<>();
									list.add(cc);
									return list;
								}, (v1, v2) -> {
									v1.addAll(v2);
									return v1;
								}));
		return occurrenceToCloneClass;
	}

}
