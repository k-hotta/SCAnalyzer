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
		Callable<SortedMap<Long, SortedMap<String, ExpectedSegment>>> {

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
	 * result provided by this method maps each ID of code fragments to a
	 * mapping between file paths and expected segments.
	 */
	@Override
	public SortedMap<Long, SortedMap<String, ExpectedSegment>> call()
			throws Exception {
		final SortedMap<Long, SortedMap<String, ExpectedSegment>> result = new TreeMap<Long, SortedMap<String, ExpectedSegment>>();
		for (final CodeFragment<E> codeFragment : cloneClass.getCodeFragments()
				.values()) {
			result.put(codeFragment.getId(), IClonesCodeFragmentMappingHelper
					.expect(codeFragment, mapper));
		}
		return result;
	}

}
