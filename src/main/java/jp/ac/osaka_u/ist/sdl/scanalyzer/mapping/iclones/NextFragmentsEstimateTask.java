package jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.iclones;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.IProgramElementMapper;

/**
 * This class estimates the next states of every code fragment in a clone class.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program elements
 */
public class NextFragmentsEstimateTask<E extends IProgramElement> implements
		Callable<SortedMap<Long, CodeFragment<E>>> {

	/**
	 * The target clone class
	 */
	private final CloneClass<E> cloneClass;

	/**
	 * The mapping of program elements between versions
	 */
	private final IProgramElementMapper<E> mapper;

	/**
	 * Construct an instance
	 * 
	 * @param cloneClass
	 *            the target clone class
	 * @param mapper
	 *            the mapping of program elements between versions
	 */
	NextFragmentsEstimateTask(final CloneClass<E> cloneClass,
			final IProgramElementMapper<E> mapper) {
		this.cloneClass = cloneClass;
		this.mapper = mapper;
	}

	/**
	 * Estimates the next states of every code fragment in the clone class. The
	 * result maps each of ids of before fragments to its its corresponding
	 * estimated fragment. If a fragment was removed, it will not appear in the
	 * result.
	 */
	@Override
	public SortedMap<Long, CodeFragment<E>> call() throws Exception {
		final SortedMap<Long, CodeFragment<E>> result = new TreeMap<>();

		for (final CodeFragment<E> codeFragment : cloneClass.getCodeFragments()
				.values()) {
			final CodeFragment<E> estimated = IClonesCodeFragmentMappingHelper
					.expect(codeFragment, mapper);
			if (estimated != null) {
				result.put(codeFragment.getId(), estimated);
			}
		}

		for (final CodeFragment<E> ghostFragment : cloneClass
				.getGhostFragments().values()) {
			final CodeFragment<E> estimated = IClonesCodeFragmentMappingHelper
					.expect(ghostFragment, mapper);
			if (estimated != null) {
				result.put(ghostFragment.getId(), estimated);
			}
		}

		return result;
	}

}
