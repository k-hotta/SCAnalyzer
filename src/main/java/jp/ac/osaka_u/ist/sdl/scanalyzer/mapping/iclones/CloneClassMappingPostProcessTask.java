package jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.iclones;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragmentMapping;

/**
 * This class performs post processing for each {@link CloneClassMapping}. The
 * post processing includes, (1) detecting {@link CodeFragmentMapping} in the
 * clone class mapping, and (2) detecting ghost fragments in the new clone
 * class.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class CloneClassMappingPostProcessTask<E extends IProgramElement>
		implements Runnable {

	private final CloneClassMapping<E> cloneClassMapping;

	private final ConcurrentMap<Long, CodeFragment<E>> estimatedFragments;

	public CloneClassMappingPostProcessTask(
			final CloneClassMapping<E> cloneClassMapping,
			final ConcurrentMap<Long, CodeFragment<E>> estimatedFragments) {
		this.cloneClassMapping = cloneClassMapping;
		this.estimatedFragments = estimatedFragments;
	}

	@Override
	public void run() {
		final CloneClass<E> oldCloneClass = cloneClassMapping
				.getOldCloneClass();
		final CloneClass<E> newCloneClass = cloneClassMapping
				.getNewCloneClass();

		if (oldCloneClass == null || newCloneClass == null) {
			// deleted or added clone class
			return;
		}

		final Map<Long, CodeFragment<E>> oldFragments = new TreeMap<>();
		oldFragments.putAll(oldCloneClass.getCodeFragments());

		final Map<Long, CodeFragment<E>> newFragments = new TreeMap<>();
		newFragments.putAll(newCloneClass.getCodeFragments());

		final Map<Integer, List<Long>> afterBucket = IClonesCloneClassMappingHelper
				.makeBuckets(newFragments.values());

		final List<CodeFragmentMapping<E>> fragmentMappings = new ArrayList<>();

		for (final CodeFragment<E> oldFragment : oldFragments.values()) {
			final int hash = IClonesCodeFragmentMappingHelper
					.calculateBucketHash(oldFragment);
			final List<Long> matchingFragmentIds = afterBucket.get(hash);

			if (matchingFragmentIds != null) {
				// the number of matching fragment is supposed 1
				// otherwise, it means that two or more same fragments are in
				// the clone class
				// but this implementation supports this situation just in case

				final long matchingFragmentId = matchingFragmentIds.remove(0);
				final CodeFragment<E> matchingFragment = newFragments
						.get(matchingFragmentId);

				if (matchingFragment == null) {
					throw new IllegalStateException(
							"the matching fragment is null");
				}

				fragmentMappings
						.add(makeInstance(oldFragment, matchingFragment));
			}
		}

		// remove matched fragments
		for (final CodeFragmentMapping<E> fragmentMapping : fragmentMappings) {
			oldFragments.remove(fragmentMapping.getOldCodeFragment().getId());
			newFragments.remove(fragmentMapping.getNewCodeFragment().getId());
		}

		// process remaining old fragments
		for (final CodeFragment<E> remainingOldFragment : oldFragments.values()) {
			final CodeFragment<E> updatedOldFragment = estimatedFragments
					.get(remainingOldFragment.getId());

			if (updatedOldFragment != null) {
				// in case the update old fragment exists but is not included in
				// clone class
				// this means the old fragment exists as a ghost fragment

				// make new instance for the ghost fragment
				final CodeFragment<E> instanciatedFragment = IClonesCodeFragmentMappingHelper
						.instanciateExpectedFragment(updatedOldFragment);

				// store the ghost fragment into the new clone class
				newCloneClass.getCore().getGhostFragments()
						.add(instanciatedFragment.getCore());
				instanciatedFragment.getCore().setCloneClass(
						newCloneClass.getCore());

				newCloneClass.addGhostFragment(instanciatedFragment);
				instanciatedFragment.setCloneClass(newCloneClass);

				// make code fragment mapping between the old fragment and the
				// ghost one
				IClonesCodeFragmentMappingHelper.makeInstance(
						remainingOldFragment, instanciatedFragment,
						cloneClassMapping);
			}
		}

	}

}
