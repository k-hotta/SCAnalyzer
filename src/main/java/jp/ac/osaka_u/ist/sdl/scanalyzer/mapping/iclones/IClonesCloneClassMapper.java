package jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.iclones;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;

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
		// ready?
		check(previousVersion, nextVersion);

		// prepare clone classes for both of before/after versions
		final Map<Long, CloneClass<E>> previousClones = new TreeMap<>();
		previousClones.putAll(previousVersion.getCloneClasses());
		final Map<Long, CloneClass<E>> nextClones = new TreeMap<>();
		nextClones.putAll(nextVersion.getCloneClasses());

		// for each code fragments in before clone classes
		// estimate their next locations in the next version
		// if a code fragment was removed between the two version, it will not
		// appear in this map
		final ConcurrentMap<Long, CodeFragment<E>> estimatedFragments = IClonesCloneClassMappingHelper
				.estimateNextFragments(previousVersion, mapper);

		// collect code fragments in next version
		// the key is id, and the value is the instance
		final ConcurrentMap<Long, CodeFragment<E>> codeFragmentsAfter = IClonesCloneClassMappingHelper
				.collectFragments(nextClones.values());

		/*
		 * make buckets for both of the two versions for the ease of access, the
		 * buckets for before version and after one have different structure
		 */

		// the bucket for before version
		// the key is the id of the fragment, and the value is its hash value
		final ConcurrentMap<Long, Integer> beforeFragmentsToHash = IClonesCloneClassMappingHelper
				.makeBucketHashingMap(estimatedFragments);

		// the bucket for after version
		// the key is the hash value, and the value is a list of ids of
		// fragments whose hash value is specified in the key
		final ConcurrentMap<Integer, List<Long>> bucketsActual = IClonesCloneClassMappingHelper
				.makeBuckets(codeFragmentsAfter.values());

		// detect mapping of clone classes between two versions
		// the information about fragment mapping will not be stored
		// ghost fragments in new clone classes will be detected if any mapping
		// for the clone classes have been found
		final List<CloneClassMapping<E>> mapping = IClonesCloneClassMappingHelper
				.createMapping(previousVersion, nextVersion,
						beforeFragmentsToHash, bucketsActual,
						codeFragmentsAfter);

		// detect mapping of code fragments for each clone class mapping
		IClonesCloneClassMappingHelper.setCodeFragmentMapping(mapping,
				estimatedFragments);

		// TODO processing for branched/branching clone classes

		// remove already mapped clone classes
		retainUnmappedClones(previousClones, nextClones, mapping);

		// find ghost clone classes for remaining old clone classes if exists
		final List<CloneClassMapping<E>> ghostMapping = IClonesCloneClassMappingHelper
				.processUnmappedCloneClasses(previousClones.values(),
						estimatedFragments, nextVersion);
		mapping.addAll(ghostMapping);

		return mapping;
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
