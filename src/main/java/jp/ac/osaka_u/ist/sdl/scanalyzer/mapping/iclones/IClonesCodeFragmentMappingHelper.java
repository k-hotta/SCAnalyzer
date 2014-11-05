package jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.iclones;

import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.PositionElementComparator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
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
	public static <E extends IProgramElement> SortedMap<String, ExpectedSegment> expect(
			final CodeFragment<E> codeFragment,
			final IProgramElementMapper<E> elementMapper) {
		final SortedMap<String, ExpectedSegment> result = new TreeMap<String, ExpectedSegment>();

		for (final Map.Entry<String, SortedSet<Segment<E>>> entry : codeFragment
				.getSegmentsAsMap().entrySet()) {
			final Segment<E> firstElement = entry.getValue().first();
			final Segment<E> lastElement = entry.getValue().last();
			final SourceFile<E> sourceFile = firstElement.getSourceFile();
			final SortedMap<String, ExpectedSegment> expectedSegments = expect(
					sourceFile, firstElement.getFirstElement().getPosition(),
					lastElement.getLastElement().getPosition(), elementMapper);

			for (final Map.Entry<String, ExpectedSegment> expectedEntry : expectedSegments
					.entrySet()) {
				if (result.containsKey(expectedEntry.getKey())) {
					final ExpectedSegment alreadyRegistered = result
							.get(expectedEntry.getKey());
					result.put(expectedEntry.getKey(),
							merge(alreadyRegistered, expectedEntry.getValue()));
				} else {
					result.put(expectedEntry.getKey(), expectedEntry.getValue());
				}
			}
		}

		return result;
	}

	private static <E extends IProgramElement> SortedMap<String, ExpectedSegment> expect(
			final SourceFile<E> sourceFile, final int startPosition,
			final int endPosition, final IProgramElementMapper<E> elementMapper) {
		final SortedMap<String, ExpectedSegment> result = new TreeMap<String, ExpectedSegment>();

		final Map<Integer, E> contents = sourceFile.getContents();

		final SortedMap<String, SortedSet<E>> updatedElements = new TreeMap<String, SortedSet<E>>();
		for (int index = startPosition; index <= endPosition; index++) {
			final E updatedElement = elementMapper.getNext(contents.get(index));
			if (updatedElement != null) {
				final String path = updatedElement.getOwnerSourceFile()
						.getPath();
				if (updatedElements.containsKey(path)) {
					updatedElements.get(path).add(updatedElement);
				} else {
					final SortedSet<E> newSet = new TreeSet<E>(
							new PositionElementComparator<>());
					newSet.add(updatedElement);
					updatedElements.put(path, newSet);
				}
			}
		}

		for (Map.Entry<String, SortedSet<E>> entry : updatedElements.entrySet()) {
			final String path = entry.getKey();
			final E firstElement = entry.getValue().first();
			final E lastElement = entry.getValue().last();

			result.put(path,
					new ExpectedSegment(path, firstElement.getPosition(),
							lastElement.getPosition()));
		}

		return result;
	}

	private static ExpectedSegment merge(final ExpectedSegment es1,
			final ExpectedSegment es2) {
		if (!es1.getPath().equals(es2.getPath())) {
			throw new IllegalStateException(
					"the given expected segments are not in the same file");
		}

		final int minStart = Math.min(es1.getStartPosition(),
				es2.getStartPosition());
		final int maxEnd = Math.max(es1.getEndPosition(), es2.getEndPosition());

		return new ExpectedSegment(es1.getPath(), minStart, maxEnd);
	}

	/**
	 * Calculate hash value of the given fragment based on file path, start
	 * position, and end position. This hash is used to put fragments into
	 * buckets.
	 * 
	 * @return a hash value
	 */
	public static int calculateBucketHash(final CodeFragment<?> codeFragment) {
		int hash = 0;
		for (final String filePath : codeFragment.getSegmentsAsMap().keySet()) {
			hash *= 31;
			hash += ((filePath.hashCode() + codeFragment.getStartPositions()
					.get(filePath)) * 23 + codeFragment.getEndPositions().get(
					filePath)) * 23;
		}
		return hash;
	}

}
