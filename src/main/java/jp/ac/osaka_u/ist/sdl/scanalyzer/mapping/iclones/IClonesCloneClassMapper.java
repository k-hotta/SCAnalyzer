package jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.iclones;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
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

		final Map<Long, CloneClass<E>> previousClones = new TreeMap<>();
		previousClones.putAll(previousVersion.getCloneClasses());
		final Map<Long, CloneClass<E>> nextClones = new TreeMap<>();
		nextClones.putAll(nextVersion.getCloneClasses());

		final ConcurrentMap<Long, CodeFragment<E>> estimatedFragments = IClonesCloneClassMappingHelper
				.estimateNextFragments(previousVersion, mapper);

		final ConcurrentMap<Long, CodeFragment<E>> codeFragmentsBefore = IClonesCloneClassMappingHelper
				.collectFragments(previousClones.values());
		final ConcurrentMap<Long, CodeFragment<E>> codeFragmentsAfter = IClonesCloneClassMappingHelper
				.collectFragments(nextClones.values());

		final ConcurrentMap<Long, Integer> beforeFragmentsToHash = IClonesCloneClassMappingHelper
				.makeBucketHashingMap(estimatedFragments);
		final ConcurrentMap<Integer, List<Long>> bucketsActual = IClonesCloneClassMappingHelper
				.makeBuckets(codeFragmentsAfter.values());

		final List<CloneClassMapping<E>> mapping = IClonesCloneClassMappingHelper
				.createMapping(previousVersion, nextVersion,
						beforeFragmentsToHash, bucketsActual,
						codeFragmentsAfter);
		
		IClonesCloneClassMappingHelper.setCodeFragmentMapping(mapping, estimatedFragments);

		retainUnmappedClones(previousClones, nextClones, mapping);

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
	 * Remove already mapped clone classes from the given maps.
	 * 
	 * @param previousClones
	 *            clones in previous version
	 * @param nextClones
	 *            clones in next version
	 * @param mapping
	 *            clone class mapping
	 */
	private void retainUnmappedClones(
			final Map<Long, CloneClass<E>> previousClones,
			final Map<Long, CloneClass<E>> nextClones,
			final List<CloneClassMapping<E>> mapping) {
		for (final CloneClassMapping<E> tmpMapping : mapping) {
			final CloneClass<E> previousClone = tmpMapping.getOldCloneClass();
			final CloneClass<E> nextClone = tmpMapping.getNewCloneClass();

			if (previousClone != null
					&& previousClones.containsKey(previousClone.getId())) {
				previousClones.remove(previousClone.getId());
			}

			if (nextClone != null && nextClones.containsKey(nextClone.getId())) {
				nextClones.remove(nextClone.getId());
			}
		}
	}

}
