package jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.iclones;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.ICloneClassMapper;
import jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.IProgramElementMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class represents the protocol of detecting clone class mapping based on
 * the algorithm used in <i>iClones</i>. This class adopts the algorithm for
 * Type-3 clones.
 * <p>
 * literature: S. Bazrafshan "Evolution of Near-miss Clones", in Proceedings of
 * the 12th International Working Conference on Source Code Analysis and
 * Manipulation (SCAM'12)
 * </p>
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class IClonesCloneClassMapper<E extends IProgramElement> implements
		ICloneClassMapper<E> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(IClonesCloneClassMapper.class);

	/**
	 * The logger for errors
	 */
	private static final Logger eLogger = LogManager.getLogger("error");

	/**
	 * The mapper of elements between two versions
	 */
	private IProgramElementMapper<E> mapper;

	/**
	 * Construct an instance with the given mapper.
	 * 
	 * @param mapper
	 *            the mapper of elements.
	 */
	public IClonesCloneClassMapper(final IProgramElementMapper<E> mapper) {
		this.mapper = mapper;
	}

	@Override
	public Collection<CloneClassMapping<E>> detectMapping(
			Version<E> previousVersion, Version<E> nextVersion) {
		check(previousVersion, nextVersion);

		final Map<Long, SortedMap<String, ExpectedSegment>> estimatedFragments = estimateNextFragments(previousVersion);

		final ConcurrentMap<Long, CodeFragment<E>> codeFragmentsBefore = collectFragments(previousVersion
				.getCloneClasses().values());
		final ConcurrentMap<Long, CodeFragment<E>> codeFragmentsAfter = collectFragments(nextVersion
				.getCloneClasses().values());

		final ConcurrentMap<Integer, List<Long>> bucketsExpected = makeBuckets(estimatedFragments);
		final ConcurrentMap<Integer, List<Long>> bucketsActual = makeBuckets(codeFragmentsAfter
				.values());

		// TODO implement
		return null;
	}

	/**
	 * Check the state of this instance and given two versions are valid.
	 * 
	 * @param previousVersion
	 *            the previous version
	 * @param nextVersion
	 *            the next version
	 * @return <code>true</code> if everything is OK, otherwise a runtime
	 *         exception will be thrown.
	 */
	private boolean check(Version<E> previousVersion, Version<E> nextVersion) {
		if (mapper == null) {
			throw new IllegalStateException(
					"the mapper of elements has not been set");
		}

		if (previousVersion == null) {
			throw new IllegalArgumentException(
					"the given previous version is null");
		}

		if (nextVersion == null) {
			throw new IllegalArgumentException("the given next version is null");
		}

		return true;
	}

	/**
	 * Estimates the next states of every fragment in every clone class in the
	 * previous version. This operation will be done in a parallel way.
	 * 
	 * @param previousVersion
	 *            the previous version
	 * @return a map, which maps the IDs of code fragments in the previous
	 *         version to another map between file path and expected segments in
	 *         the file.
	 */
	private Map<Long, SortedMap<String, ExpectedSegment>> estimateNextFragments(
			final Version<E> previousVersion) {
		final Map<Long, SortedMap<String, ExpectedSegment>> result = new TreeMap<Long, SortedMap<String, ExpectedSegment>>();

		final List<NextFragmentsEstimateTask<E>> tasks = new ArrayList<NextFragmentsEstimateTask<E>>();

		for (final CloneClass<E> cloneClass : previousVersion.getCloneClasses()
				.values()) {
			tasks.add(new NextFragmentsEstimateTask<>(cloneClass, mapper));
		}

		final ExecutorService pool = Executors.newCachedThreadPool();

		try {
			final List<Future<SortedMap<Long, SortedMap<String, ExpectedSegment>>>> futures = new ArrayList<>();

			for (final NextFragmentsEstimateTask<E> task : tasks) {
				futures.add(pool.submit(task));
			}

			for (Future<SortedMap<Long, SortedMap<String, ExpectedSegment>>> future : futures) {
				try {
					final SortedMap<Long, SortedMap<String, ExpectedSegment>> taskResult = future
							.get();
					for (final Map.Entry<Long, SortedMap<String, ExpectedSegment>> entry : taskResult
							.entrySet()) {
						if (result.containsKey(entry.getKey())) {
							throw new IllegalStateException(
									"there are duplicated code fragments in multiple clone classes");
						} else {
							// if empty, the fragment was completely removed
							if (!entry.getValue().isEmpty()) {
								result.put(entry.getKey(), entry.getValue());
							}
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
	 * Make buckets of code fragments with expected segments. This method is
	 * supposed to be used for making buckets of fragments in BEFORE version.
	 * 
	 * @param expectedSegments
	 *            the expected segments
	 * @return a concurrent map that maps each hash value to a list of long,
	 *         which list contains IDs of code fragments (NOT expected ones but
	 *         actual ones in before version)
	 */
	private ConcurrentMap<Integer, List<Long>> makeBuckets(
			final Map<Long, SortedMap<String, ExpectedSegment>> expectedSegments) {
		final Stream<Long> stream = Collections
				.synchronizedMap(expectedSegments).keySet().parallelStream();

		// the function to generate keys
		// the key will be the bucket hash values calculated from each of
		// expected segments of each of fragments in before version.
		final Function<Long, Integer> keyMapper = l -> {
			final SortedMap<String, ExpectedSegment> expectedFragment = expectedSegments
					.get(l);
			return IClonesCodeFragmentMappingHelper
					.calculateBucketHash(expectedFragment);
		};

		// the function to generate values
		// the value will be a list contains the id of the fragment under
		// consideration
		final Function<Long, List<Long>> valueMapper = l -> {
			final List<Long> fragmentIds = new ArrayList<Long>();
			fragmentIds.add(l);
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
	 * Make buckets of code fragments in given clone classes. This method is
	 * supposed to be used for making buckets of fragments in AFTER version.
	 * 
	 * @param cloneClasses
	 *            a collection of clone classes
	 * @return a concurrent map that maps each hash value to a list of long,
	 *         which list contains IDs of code fragments in after version
	 */
	private ConcurrentMap<Integer, List<Long>> makeBuckets(
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
	 * Provide code fragments included in the given clone classes as a map,
	 * whose key is the id of a code fragment and whose value is the code
	 * fragment itself.
	 * 
	 * @param cloneClasses
	 *            the clone classes
	 * @return a concurrent map between id of code fragments and the instance
	 */
	private ConcurrentMap<Long, CodeFragment<E>> collectFragments(
			final Collection<CloneClass<E>> cloneClasses) {
		final ConcurrentMap<Long, CodeFragment<E>> result = new ConcurrentHashMap<Long, CodeFragment<E>>();

		for (final CloneClass<E> cloneClass : cloneClasses) {
			for (final CodeFragment<E> codeFragment : cloneClass
					.getCodeFragments().values()) {
				result.put(codeFragment.getId(), codeFragment);
			}
		}

		return result;

		// return cloneClasses
		// .parallelStream()
		// .flatMap(
		// cc -> {
		// final List<CodeFragment<E>> list = new ArrayList<CodeFragment<E>>();
		// list.addAll(cc.getCodeFragments().values());
		// return list.parallelStream();
		// })
		// .collect(Collectors.toConcurrentMap(cf -> cf.getId(), cf -> cf));
	}

}
