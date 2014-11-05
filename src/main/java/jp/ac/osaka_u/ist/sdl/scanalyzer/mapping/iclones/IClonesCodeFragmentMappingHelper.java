package jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.iclones;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.PositionElementComparator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.IProgramElementMapper;

/**
 * This is a helper class to map code fragments in <i>iClones</i> mapping mode.
 * 
 * @author k-hotta
 *
 */
public class IClonesCodeFragmentMappingHelper {

	/**
	 * Expect the state of the given code fragment in the next version with the
	 * information of the element mapping.
	 * 
	 * @param codeFragment
	 *            the code fragment the next state of which should be expected
	 * @param elementMapper
	 *            the information of mapping of elements
	 * @return a list has all the expected segments
	 */
	public static <E extends IProgramElement> List<ExpectedSegment> expect(
			final CodeFragment<E> codeFragment,
			final IProgramElementMapper<E> elementMapper) {
		final SortedSet<ExpectedSegment> result = new TreeSet<ExpectedSegment>();

		for (final Segment<E> segment : codeFragment.getSegments().values()) {
			result.addAll(expect(segment, elementMapper));
		}

		final List<ExpectedSegment> list = new ArrayList<ExpectedSegment>();
		list.addAll(result);

		return list;
	}

	/**
	 * Expect the state of the given segment in the next version with the
	 * information of the element mapping.
	 * 
	 * @param segment
	 *            the segment the next state of which should be expected
	 * @param elementMapper
	 *            the information of the mapping of elements
	 * @return a list has all the expected segments
	 */
	public static <E extends IProgramElement> List<ExpectedSegment> expect(
			final Segment<E> segment,
			final IProgramElementMapper<E> elementMapper) {
		final SortedSet<ExpectedSegment> result = new TreeSet<ExpectedSegment>();

		final Map<String, SortedSet<E>> updatedElements = new TreeMap<String, SortedSet<E>>();
		for (final E beforeElement : segment.getContents().values()) {
			final E updatedElement = elementMapper.getNext(beforeElement);

			// if null the element was removed
			if (updatedElement != null) {
				final String path = updatedElement.getOwnerSourceFile()
						.getPath();
				if (updatedElements.containsKey(path)) {
					updatedElements.get(path).add(updatedElement);
				} else {
					final SortedSet<E> newSet = new TreeSet<E>(
							new PositionElementComparator<E>());
					newSet.add(updatedElement);
					updatedElements.put(path, newSet);
				}
			}
		}

		for (Map.Entry<String, SortedSet<E>> entry : updatedElements.entrySet()) {
			final SortedSet<E> elementsInCurrentSegment = new TreeSet<E>(
					new PositionElementComparator<>());

			E previous = null;
			for (final E element : entry.getValue()) {
				if (previous == null) {
					elementsInCurrentSegment.add(element);
				} else {
					if (element.getPosition() == previous.getPosition() + 1) {
						// the elements are continuous
						elementsInCurrentSegment.add(element);
					} else {
						// the elements are not continuous
						result.add(makeExpectedSegment(elementsInCurrentSegment));
						elementsInCurrentSegment.clear();
						elementsInCurrentSegment.add(element);
					}
				}

				previous = element;
			}

			if (!elementsInCurrentSegment.isEmpty()) {
				result.add(makeExpectedSegment(elementsInCurrentSegment));
			}
		}

		final List<ExpectedSegment> list = new ArrayList<ExpectedSegment>();
		list.addAll(result);

		return list;
	}

	/**
	 * Make an instance of ExpectedSegment with the specified elements.
	 * 
	 * @param elements
	 *            the elements that should be included in the expected segment
	 * @return an instance of expected segment
	 */
	private static ExpectedSegment makeExpectedSegment(
			final SortedSet<? extends IProgramElement> elements) {
		final String path = elements.first().getOwnerSourceFile().getPath();
		final int startPosition = elements.first().getPosition();
		final int endPosition = elements.last().getPosition();

		return new ExpectedSegment(path, startPosition, endPosition);
	}

}
