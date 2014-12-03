package jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.iclones;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBElementComparator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.IProgramElementMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is a helper class to map clone classes in <i>iClones</i> mapping mode,
 * which is equipped with some static methods to help the mapping procedure.
 * 
 * @author k-hotta
 *
 */
public class IClonesCloneClassMappingHelper {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(IClonesCloneClassMappingHelper.class);

	/**
	 * Estimates the next states of every fragment in every clone class in the
	 * previous version. This operation will be done in a parallel way.
	 * 
	 * @param previousVersion
	 *            the previous version
	 * @return a map, which maps the IDs of code fragments in the previous
	 *         version to instances of code fragments after updated
	 */
	public static <E extends IProgramElement> ConcurrentMap<Long, CodeFragment<E>> estimateNextFragments(
			final Version<E> previousVersion,
			final IProgramElementMapper<E> mapper) {
		final ConcurrentMap<Long, CodeFragment<E>> result = new ConcurrentSkipListMap<>();

		if (previousVersion == null) {
			// special treat for the initial version
			return result;
		}

		final List<NextFragmentsEstimateTask<E>> tasks = new ArrayList<NextFragmentsEstimateTask<E>>();

		for (final CloneClass<E> cloneClass : previousVersion.getCloneClasses()
				.values()) {
			tasks.add(new NextFragmentsEstimateTask<>(cloneClass, mapper));
		}

		final ExecutorService pool = Executors.newCachedThreadPool();

		try {
			final List<Future<SortedMap<Long, CodeFragment<E>>>> futures = new ArrayList<>();

			for (final NextFragmentsEstimateTask<E> task : tasks) {
				futures.add(pool.submit(task));
			}

			for (Future<SortedMap<Long, CodeFragment<E>>> future : futures) {
				try {
					final SortedMap<Long, CodeFragment<E>> taskResult = future
							.get();
					for (final Map.Entry<Long, CodeFragment<E>> entry : taskResult
							.entrySet()) {
						if (result.containsKey(entry.getKey())) {
							throw new IllegalStateException(
									"there are duplicated code fragments in multiple clone classes");
						} else {
							result.put(entry.getKey(), entry.getValue());
						}
					}
				} catch (IllegalStateException e1) {
					throw e1;
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}

		} finally {
			pool.shutdown();
		}

		return result;
	}

	/**
	 * Provide code fragments included in the given clone classes as a map,
	 * whose key is the id of a code fragment and whose value is the code
	 * fragment itself.
	 * 
	 * @param cloneClasses
	 *            the clone classes
	 * @return a concurrent map between id of code fragments and the instance
	 */
	public static <E extends IProgramElement> ConcurrentMap<Long, CodeFragment<E>> collectFragments(
			final Collection<CloneClass<E>> cloneClasses) {
		final ConcurrentMap<Long, CodeFragment<E>> result = new ConcurrentHashMap<Long, CodeFragment<E>>();

		for (final CloneClass<E> cloneClass : cloneClasses) {
			for (final CodeFragment<E> codeFragment : cloneClass
					.getCodeFragments().values()) {
				result.put(codeFragment.getId(), codeFragment);
			}
		}

		return result;
	}

	/**
	 * Make mapping between id of code fragments and its hash value. This method
	 * is supposed to be used for making buckets of fragments in BEFORE version.
	 * 
	 * @param expectedFragments
	 *            the expected fragments
	 * @return a concurrent map that maps each id of code fragments to its
	 *         bucket hash value.
	 */
	public static <E extends IProgramElement> ConcurrentMap<Long, Integer> makeBucketHashingMap(
			final Map<Long, CodeFragment<E>> expectedFragments) {
		final Stream<Long> stream = Collections
				.synchronizedMap(expectedFragments).keySet().parallelStream();

		// the function to generate keys
		// the key will be id of the fragment
		final Function<Long, Long> keyMapper = l -> l;

		// the function to generate values
		// the value will be bucket hash value
		final Function<Long, Integer> valueMapper = l -> {
			final CodeFragment<E> expectedFragment = expectedFragments.get(l);
			return IClonesCodeFragmentMappingHelper
					.calculateBucketHash(expectedFragment);
		};

		// return the result as a concurrent map
		return stream.collect(Collectors
				.toConcurrentMap(keyMapper, valueMapper));
	}

	/**
	 * Make buckets of code fragments in given clone classes. This method is
	 * supposed to be used for making buckets of fragments in AFTER version.
	 * 
	 * @param codeFragments
	 *            a collection of code fragments
	 * @return a concurrent map that maps each hash value to a list of long,
	 *         which list contains IDs of code fragments in after version
	 */
	public static <E extends IProgramElement> ConcurrentMap<Integer, List<Long>> makeBuckets(
			final Collection<CodeFragment<E>> codeFragments) {
		final Stream<CodeFragment<E>> stream = Collections
				.synchronizedCollection(codeFragments).parallelStream();

		// the function to generate keys
		// the key will be the bucket hash values calculated from the fragment
		final Function<CodeFragment<E>, Integer> keyMapper = cf -> IClonesCodeFragmentMappingHelper
				.calculateBucketHash(cf);

		// the function to generate values
		// the value will be a list contains the id of the fragment under
		// consideration
		final Function<CodeFragment<E>, List<Long>> valueMapper = cf -> {
			final List<Long> fragmentIds = new ArrayList<Long>();
			fragmentIds.add(cf.getId());
			return fragmentIds;
		};

		// the merge function
		// provide a list that contains all the elements in the two lists
		final BinaryOperator<List<Long>> mergeFunction = (v1, v2) -> {
			v1.addAll(v2);
			return v1;
		};

		// return the result as a concurrent map
		return stream.collect(Collectors.toConcurrentMap(keyMapper,
				valueMapper, mergeFunction));
	}

	/**
	 * Create mapping of clone classes between the given two versions.
	 * 
	 * @param previousVersion
	 *            the previous version
	 * @param nextVersion
	 *            the next version
	 * @param beforeFragmentsToHash
	 *            a map contains ids of fragments in previous version and their
	 *            bucket hash values
	 * @param afterBucket
	 *            a map contains hash values and a list of fragments whose hash
	 *            values are the value specified in the key
	 * @param codeFragmentsAfter
	 *            code fragments in after version
	 * @return a list of detected clone class mapping
	 */
	public static <E extends IProgramElement> List<CloneClassMapping<E>> createMapping(
			final Version<E> previousVersion, final Version<E> nextVersion,
			final ConcurrentMap<Long, Integer> beforeFragmentsToHash,
			final ConcurrentMap<Integer, List<Long>> afterBucket,
			final ConcurrentMap<Long, CodeFragment<E>> codeFragmentsAfter) {
		// special treat for the initial version
		if (previousVersion == null) {
			return createMappingForFirstVersion(nextVersion);
		}

		final Map<CloneClass<E>, List<CloneClass<E>>> perfectMatches = new TreeMap<>(
				(k1, k2) -> Long.compare(k1.getId(), k2.getId()));
		final Map<CloneClass<E>, List<CloneClass<E>>> inclusiveMatches = new TreeMap<>(
				(k1, k2) -> Long.compare(k1.getId(), k2.getId()));
		final Map<CloneClass<E>, Map<CloneClass<E>, Integer>> bestMatches = new TreeMap<>(
				(k1, k2) -> Long.compare(k1.getId(), k2.getId()));

		final ExecutorService pool = Executors.newCachedThreadPool();

		try {
			final Map<CloneClassMappingTask<E>, Future<?>> futures = new HashMap<>();
			for (final CloneClass<E> cloneClass : previousVersion
					.getCloneClasses().values()) {
				final CloneClassMappingTask<E> task = new CloneClassMappingTask<E>(
						cloneClass, beforeFragmentsToHash, afterBucket,
						codeFragmentsAfter);
				futures.put(task, pool.submit(task));
			}

			for (final Map.Entry<CloneClassMappingTask<E>, Future<?>> entry : futures
					.entrySet()) {
				try {
					if (entry.getValue().get() == null) {
						logger.trace("finished the task for clone class "
								+ entry.getKey().getTargetCloneClass().getId());

						final CloneClassMappingTask<E> finishedTask = entry
								.getKey();
						final CloneClass<E> targetCloneClass = finishedTask
								.getTargetCloneClass();
						final List<CloneClass<E>> currentPerfectMatches = finishedTask
								.getPerfectMatches();
						final List<CloneClass<E>> currentInclusiveMatches = finishedTask
								.getInclusiveMatches();
						final Map<CloneClass<E>, Integer> currentBestMatches = finishedTask
								.getBestMatches();

						if (!currentPerfectMatches.isEmpty()) {
							perfectMatches.put(targetCloneClass,
									currentPerfectMatches);
						}

						if (!currentInclusiveMatches.isEmpty()) {
							inclusiveMatches.put(targetCloneClass,
									currentInclusiveMatches);
						}

						if (!currentBestMatches.isEmpty()) {
							bestMatches.put(targetCloneClass,
									currentBestMatches);
						}
					}
				} catch (IllegalStateException e) {
					throw e;
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		} finally {
			pool.shutdown();
		}

		return fixMatches(previousVersion, nextVersion, perfectMatches,
				inclusiveMatches, bestMatches);
	}

	/**
	 * Special treat for the initial version.
	 * 
	 * @param nextVersion
	 *            the initial version
	 * @return a list of clone class mapping, all of whose values will be
	 *         addition of a clone class
	 */
	public static <E extends IProgramElement> List<CloneClassMapping<E>> createMappingForFirstVersion(
			final Version<E> nextVersion) {
		final List<CloneClassMapping<E>> result = new ArrayList<>();

		for (final CloneClass<E> newCloneClass : nextVersion.getCloneClasses()
				.values()) {
			result.add(makeMapping(null, newCloneClass));
		}

		return result;
	}

	/**
	 * Resolve perfect/best matches and provide instances of clone class
	 * mapping.
	 * 
	 * @param previousVersion
	 *            the previous version
	 * @param nextVersion
	 *            the next version
	 * @param perfectMatches
	 *            the detected perfect matches
	 * @param inclusiveMatches
	 *            the detected inclusive matches
	 * @param bestMatches
	 *            the detected best matches
	 * 
	 * @return a list of clone class mapping
	 */
	public static <E extends IProgramElement> List<CloneClassMapping<E>> fixMatches(
			final Version<E> previousVersion, final Version<E> nextVersion,
			final Map<CloneClass<E>, List<CloneClass<E>>> perfectMatches,
			final Map<CloneClass<E>, List<CloneClass<E>>> inclusiveMatches,
			final Map<CloneClass<E>, Map<CloneClass<E>, Integer>> bestMatches) {
		final List<CloneClassMapping<E>> result = new ArrayList<CloneClassMapping<E>>();

		final Map<Long, CloneClass<E>> previousClones = new TreeMap<>();
		final Map<Long, CloneClass<E>> nextClones = new TreeMap<>();

		previousClones.putAll(previousVersion.getCloneClasses());
		nextClones.putAll(nextVersion.getCloneClasses());

		// process perfect matches first
		// clone classes that have any perfect matching partner will be ignored
		// in processing best matches
		for (final Map.Entry<CloneClass<E>, List<CloneClass<E>>> perfectEntry : perfectMatches
				.entrySet()) {
			final CloneClass<E> oldCloneClass = perfectEntry.getKey();
			for (final CloneClass<E> newCloneClass : perfectEntry.getValue()) {
				final CloneClassMapping<E> mapping = makeMapping(oldCloneClass,
						newCloneClass);
				result.add(mapping);
				previousClones.remove(oldCloneClass.getId());
				nextClones.remove(newCloneClass.getId());
			}
		}

		// process inclusive matches second
		// clone classes that processed in this phase are still of interest in
		// the further process
		for (final Map.Entry<CloneClass<E>, List<CloneClass<E>>> inclusiveEntry : inclusiveMatches
				.entrySet()) {
			final CloneClass<E> oldCloneClass = inclusiveEntry.getKey();
			for (final CloneClass<E> newCloneClass : inclusiveEntry.getValue()) {
				result.add(makeMapping(oldCloneClass, newCloneClass));
			}
		}

		// internal data structure to resolve best matches
		final Map<CloneClass<E>, CloneClass<E>> reversedBestMatches = new TreeMap<>(
				(k1, k2) -> Long.compare(k1.getId(), k2.getId()));
		final Map<CloneClass<E>, Integer> currentHighest = new TreeMap<>((k1,
				k2) -> Long.compare(k1.getId(), k2.getId()));

		// resolve best matches
		for (final Map.Entry<CloneClass<E>, Map<CloneClass<E>, Integer>> bestEntry : bestMatches
				.entrySet()) {
			final CloneClass<E> oldCloneClass = bestEntry.getKey();
			if (!previousClones.containsKey(oldCloneClass.getId())) {
				// the old clone class has perfect match
				continue;
			}

			final Map<CloneClass<E>, Integer> currentBestMatch = bestEntry
					.getValue();
			for (final CloneClass<E> newCloneClass : currentBestMatch.keySet()) {
				if (!nextClones.containsKey(newCloneClass.getId())) {
					// the new clone class has perfect match
					continue;
				}

				// if this predicate is true, the new clone class has not been
				// mapped or has been mapped to an ancestor whose matching
				// elements are fewer than this old clone class
				if (!currentHighest.containsKey(newCloneClass)
						|| currentHighest.get(newCloneClass) < currentBestMatch
								.get(newCloneClass)) {
					currentHighest.put(newCloneClass,
							currentBestMatch.get(newCloneClass));
					reversedBestMatches.put(newCloneClass, oldCloneClass);
				}
			}
		}

		// make instances of resolved best matches
		for (final Map.Entry<CloneClass<E>, CloneClass<E>> reversedBestEntry : reversedBestMatches
				.entrySet()) {
			result.add(makeMapping(reversedBestEntry.getValue(),
					reversedBestEntry.getKey()));
		}

		return result;
	}

	/**
	 * Make mapping instance for the given two clone classes.
	 * 
	 * @param oldCloneClass
	 *            the old clone class
	 * @param newCloneClass
	 *            the new clone class
	 * @return a clone class mapping between the two clone class
	 */
	public static <E extends IProgramElement> CloneClassMapping<E> makeMapping(
			final CloneClass<E> oldCloneClass, final CloneClass<E> newCloneClass) {
		final DBCloneClass oldCore = (oldCloneClass == null) ? null
				: oldCloneClass.getCore();
		final DBCloneClass newCore = (newCloneClass == null) ? null
				: newCloneClass.getCore();

		final DBCloneClassMapping mappingCore = new DBCloneClassMapping(
				IDGenerator.generate(DBCloneClassMapping.class), oldCore,
				newCore, new TreeSet<DBCodeFragmentMapping>(
						new DBElementComparator()), null);
		final CloneClassMapping<E> mapping = new CloneClassMapping<>(
				mappingCore);

		mapping.setOldCloneClass(oldCloneClass);
		mapping.setNewCloneClass(newCloneClass);

		return mapping;
	}

	/**
	 * Detect mapping of code fragments and set them.
	 * 
	 * @param cloneClassMapping
	 *            a collection of clone class mapping
	 */
	public static <E extends IProgramElement> void setCodeFragmentMapping(
			final Collection<CloneClassMapping<E>> cloneClassMapping,
			final ConcurrentMap<Long, CodeFragment<E>> estimatedFragments) {
		final List<CloneClassMappingPostProcessTask<E>> tasks = new ArrayList<>();
		for (final CloneClassMapping<E> tmp : cloneClassMapping) {
			tasks.add(new CloneClassMappingPostProcessTask<>(tmp,
					estimatedFragments));
		}

		final ExecutorService pool = Executors.newCachedThreadPool();

		try {

			final List<Future<?>> futures = new ArrayList<>();
			for (final CloneClassMappingPostProcessTask<E> task : tasks) {
				futures.add(pool.submit(task));
			}

			for (final Future<?> future : futures) {
				try {
					// the task will automatically set the results to the
					// instance
					// hence the result of get() is ignored
					future.get();
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}

		} finally {
			pool.shutdown();
		}
	}

	/**
	 * Make ghost clone classes for unmapped old clone classes if there exist at
	 * least two fragments.
	 * 
	 * @param unmappedOldCloneClasses
	 *            a collection of unmapped clone classes
	 * @param estimatedFragments
	 *            estimated fragments
	 * @param nextVersion
	 *            the next version
	 */
	public static <E extends IProgramElement> List<CloneClassMapping<E>> processUnmappedCloneClasses(
			final Collection<CloneClass<E>> unmappedOldCloneClasses,
			final ConcurrentMap<Long, CodeFragment<E>> estimatedFragments,
			final Version<E> nextVersion) {
		final List<CloneClassMapping<E>> result = new ArrayList<>();

		for (final CloneClass<E> unmappedOldCloneClass : unmappedOldCloneClasses) {
			final Map<Long, CodeFragment<E>> oldFragments = new TreeMap<>();
			oldFragments.putAll(unmappedOldCloneClass.getCodeFragments());
			oldFragments.putAll(unmappedOldCloneClass.getGhostFragments());

			final Map<Long, CodeFragment<E>> relatedEstimatedFragments = new TreeMap<>();

			for (final Map.Entry<Long, CodeFragment<E>> oldFragmentEntry : oldFragments
					.entrySet()) {
				if (estimatedFragments.containsKey(oldFragmentEntry.getKey())) {
					relatedEstimatedFragments.put(oldFragmentEntry.getKey(),
							estimatedFragments.get(oldFragmentEntry.getKey()));
				}
			}

			if (relatedEstimatedFragments.size() >= 2) {
				final Map<Long, CodeFragment<E>> instanciatedFragments = new TreeMap<Long, CodeFragment<E>>();

				for (final CodeFragment<E> estimatedFragment : relatedEstimatedFragments
						.values()) {
					final CodeFragment<E> instanciatedFragment = IClonesCodeFragmentMappingHelper
							.instanciateExpectedFragment(estimatedFragment);

					// with the instanciated fragment mapped to OLD fragment id
					instanciatedFragments.put(estimatedFragment.getId(),
							instanciatedFragment);
				}

				final CloneClass<E> ghostCloneClass = makeGhostCloneClass(
						instanciatedFragments.values(), nextVersion);

				final CloneClassMapping<E> cloneClassMapping = makeMapping(
						unmappedOldCloneClass, ghostCloneClass);
				result.add(cloneClassMapping);

				for (final long fragmentId : instanciatedFragments.keySet()) {
					final CodeFragment<E> oldFragment = oldFragments
							.get(fragmentId);
					final CodeFragment<E> newFragment = instanciatedFragments
							.get(fragmentId);

					IClonesCodeFragmentMappingHelper.makeInstance(oldFragment,
							newFragment, cloneClassMapping);
				}
			} else {
				// clone removed
				final CloneClassMapping<E> deleteMapping = IClonesCloneClassMappingHelper
						.makeMapping(unmappedOldCloneClass, null);
				result.add(deleteMapping);
			}
		}

		return result;
	}

	private static <E extends IProgramElement> CloneClass<E> makeGhostCloneClass(
			final Collection<CodeFragment<E>> instanciatedFragments,
			final Version<E> nextVersion) {
		final DBCloneClass ghostDBCloneClass = new DBCloneClass(
				IDGenerator.generate(DBCloneClass.class),
				nextVersion.getCore(), new TreeSet<DBCodeFragment>(
						new DBElementComparator()));

		nextVersion.getCore().getCloneClasses().add(ghostDBCloneClass);

		final CloneClass<E> ghostCloneClass = new CloneClass<>(
				ghostDBCloneClass);

		nextVersion.addCloneClass(ghostCloneClass);
		ghostCloneClass.setVersion(nextVersion);

		for (final CodeFragment<E> ghostFragment : instanciatedFragments) {
			ghostFragment.getCore().setGhost(true);
			ghostFragment.getCore().setCloneClass(ghostDBCloneClass);
			ghostDBCloneClass.addCodeFragment(ghostFragment.getCore());

			ghostFragment.setCloneClass(ghostCloneClass);
			ghostCloneClass.addGhostFragment(ghostFragment);
		}

		return ghostCloneClass;
	}

}
