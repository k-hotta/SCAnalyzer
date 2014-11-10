package jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.iclones;

import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.PositionElementComparator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SegmentComparator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegmentComparator;
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
	public static <E extends IProgramElement> CodeFragment<E> expect(
			final CodeFragment<E> codeFragment,
			final IProgramElementMapper<E> elementMapper) {
		// the core of updated segment
		// note that the clone class is set null, because we don't know to which
		// clone class the updated fragments belongs
		final DBCodeFragment updatedDBFragment = new DBCodeFragment(
				codeFragment.getId(), new TreeSet<DBSegment>(
						new DBSegmentComparator()), null);

		// the instance of updated fragment, which is the result of this method
		final CodeFragment<E> updatedFragment = new CodeFragment<>(
				updatedDBFragment);

		// this map contains which segments each source file has after updated
		final Map<String, SortedSet<Segment<E>>> updatedSegments = new TreeMap<String, SortedSet<Segment<E>>>();

		// update each segment in the code fragment
		for (final Map.Entry<String, SortedSet<Segment<E>>> entry : codeFragment
				.getSegmentsAsMap().entrySet()) {
			for (final Segment<E> segment : entry.getValue()) {
				// perform updating
				final Map<String, Segment<E>> currentUpdatedSegments = update(
						segment, elementMapper);

				// check if any location overlapping exists
				for (final Map.Entry<String, Segment<E>> updatedSegmentEntry : currentUpdatedSegments
						.entrySet()) {
					final String path = updatedSegmentEntry.getKey();

					if (updatedSegments.containsKey(path)) {
						updatedSegments.put(
								path,
								merge(updatedSegmentEntry.getValue(),
										updatedSegments.get(path)));
					} else {
						final SortedSet<Segment<E>> newSet = new TreeSet<>(
								new SegmentComparator<>());
						newSet.add(updatedSegmentEntry.getValue());
						updatedSegments.put(path, newSet);
					}
				}
			}
		}

		if (updatedSegments.isEmpty()) {
			// the fragment was completely removed
			return null;
		}

		// fix relationship between updated segments and updated fragment
		for (final Map.Entry<String, SortedSet<Segment<E>>> updatedEntry : updatedSegments
				.entrySet()) {
			for (final Segment<E> updatedSegment : updatedEntry.getValue()) {
				updatedDBFragment.getSegments().add(updatedSegment.getCore());
				updatedSegment.getCore().setCodeFragment(updatedDBFragment);
				updatedFragment.addSegment(updatedSegment);
				updatedSegment.setCodeFragment(updatedFragment);
			}
		}

		return updatedFragment;
	}

	private static <E extends IProgramElement> Map<String, Segment<E>> update(
			final Segment<E> segment,
			final IProgramElementMapper<E> elementMapper) {
		final String ownerFilePath = segment.getSourceFile().getPath();

		// this map contains the estimated elements in the next version
		// note, an element in a segment can move into another file, so this map
		// is made capable to handle multiple files even though our interest is
		// in only a single segment
		final SortedMap<String, SortedSet<E>> updatedElements = new TreeMap<String, SortedSet<E>>();

		// for each of elements in the segment,
		// estimate where they are in the next version
		for (final E element : segment.getContents()) {
			final E updatedElement = elementMapper.getNext(element);
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

		final Map<String, Segment<E>> result = new TreeMap<String, Segment<E>>();

		if (updatedElements.isEmpty()) {
			// the segment was completely removed
			return result;
		}

		// update the segment
		// note that two or more updated segments can be generated
		// if the elements of the segment is divided into multiple files
		for (final Map.Entry<String, SortedSet<E>> entry : updatedElements
				.entrySet()) {
			final String currentPath = entry.getKey();
			final int updatedStartPosition = entry.getValue().first()
					.getPosition();
			final int updatedEndPosition = entry.getValue().last()
					.getPosition();

			@SuppressWarnings("unchecked")
			final SourceFile<E> sourceFileInAfterVersion = (SourceFile<E>) entry
					.getValue().first().getOwnerSourceFile();

			final DBSegment updatedDBSegment = new DBSegment();
			updatedDBSegment.setStartPosition(updatedStartPosition);
			updatedDBSegment.setEndPosition(updatedEndPosition);
			updatedDBSegment.setSourceFile(sourceFileInAfterVersion.getCore());

			// inherit the id of the original segment if the owner file of the
			// updated segment is the same as the original one,
			// otherwise, a new id will be set
			if (currentPath.equals(ownerFilePath)) {
				updatedDBSegment.setId(segment.getId());
			} else {
				updatedDBSegment.setId(IDGenerator.generate(DBSegment.class));
			}

			final Segment<E> updatedSegment = new Segment<>(updatedDBSegment);
			updatedSegment.setSourceFile(sourceFileInAfterVersion);

			final Map<Integer, E> updatedContents = sourceFileInAfterVersion
					.getContents().subMap(updatedStartPosition,
							updatedEndPosition + 1);
			updatedSegment.setContents(updatedContents.values());
			result.put(currentPath, updatedSegment);
		}

		return result;
	}

	/**
	 * Merge segments in the same file if their locations are overlapping.
	 * 
	 * @param segment
	 *            the segment that is about to be newly added
	 * @param alreadyRegisteredSegments
	 *            a set of already registered segments
	 * @return a set of segments after overlapping is revolved
	 */
	private static <E extends IProgramElement> SortedSet<Segment<E>> merge(
			final Segment<E> segment,
			final SortedSet<Segment<E>> alreadyRegisteredSegments) {
		final int startPosition = segment.getFirstElement().getPosition();
		final int endPosition = segment.getLastElement().getPosition();

		final SortedSet<Segment<E>> toBeMerged = new TreeSet<>(
				new SegmentComparator<>());
		toBeMerged.add(segment);

		boolean mergeRequired = false;
		for (final Segment<E> alreadyRegisteredSegment : alreadyRegisteredSegments) {
			if (alreadyRegisteredSegment.getFirstElement().getPosition() <= startPosition
					&& startPosition <= alreadyRegisteredSegment
							.getLastElement().getPosition()) {
				toBeMerged.add(alreadyRegisteredSegment);
				mergeRequired = true;
			} else if (alreadyRegisteredSegment.getFirstElement().getPosition() <= endPosition
					&& endPosition <= alreadyRegisteredSegment.getLastElement()
							.getPosition()) {
				toBeMerged.add(alreadyRegisteredSegment);
				mergeRequired = true;
			}
		}

		alreadyRegisteredSegments.add(segment);

		if (mergeRequired) {
			int mergedStartPosition = Integer.MAX_VALUE;
			int mergedEndPosition = -1;
			for (final Segment<E> toBeMergedSegment : toBeMerged) {
				alreadyRegisteredSegments.remove(toBeMergedSegment);

				if (mergedStartPosition > toBeMergedSegment.getFirstElement()
						.getPosition()) {
					mergedStartPosition = toBeMergedSegment.getFirstElement()
							.getPosition();
				}
				if (mergedEndPosition < toBeMergedSegment.getLastElement()
						.getPosition()) {
					mergedEndPosition = toBeMergedSegment.getLastElement()
							.getPosition();
				}
			}

			final DBSegment mergedDBSegment = new DBSegment(segment.getId(),
					segment.getCore().getSourceFile(), mergedStartPosition,
					mergedEndPosition, null);

			final Segment<E> mergedSegment = new Segment<>(mergedDBSegment);
			mergedSegment.setSourceFile(segment.getSourceFile());
			final Map<Integer, E> contents = segment.getSourceFile()
					.getContents()
					.subMap(mergedStartPosition, mergedEndPosition + 1);
			mergedSegment.setContents(contents.values());

			alreadyRegisteredSegments.add(mergedSegment);
		}

		return alreadyRegisteredSegments;
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
			hash += calculateBucketHash(filePath, codeFragment
					.getStartPositions().get(filePath), codeFragment
					.getEndPositions().get(filePath));
		}
		return hash;
	}

	/**
	 * Calculate hash value for buckets with given file path, start position,
	 * and end position.
	 * 
	 * @param filePath
	 *            the file path
	 * @param startPosition
	 *            the start position
	 * @param endPosition
	 *            the end position
	 * @return a hash value calculated with given three values
	 */
	public static int calculateBucketHash(final String filePath,
			final int startPosition, final int endPosition) {
		return ((filePath.hashCode() + startPosition) * 23 + endPosition) * 23;
	}

	/**
	 * Make instance of the given expected fragment as an actual instance.
	 * 
	 * @param expectedFragment
	 *            the expected fragment
	 * @return a new instance of {@link CodeFragment} created from the given
	 *         expected fragment
	 */
	public static <E extends IProgramElement> CodeFragment<E> instanciateExpectedFragment(
			final CodeFragment<E> expectedFragment) {
		final Map<Long, Segment<E>> instanciatedSegments = new TreeMap<>();
		for (final Segment<E> expectedSegment : expectedFragment.getSegments()) {
			final DBSegment instanciatedDBSegment = new DBSegment(
					IDGenerator.generate(DBSegment.class), expectedSegment
							.getSourceFile().getCore(), expectedSegment
							.getFirstElement().getPosition(), expectedSegment
							.getLastElement().getPosition(), null);

			final Segment<E> instanciatedSegment = new Segment<>(
					instanciatedDBSegment);
			instanciatedSegment.setSourceFile(expectedSegment.getSourceFile());
			instanciatedSegment.setContents(expectedSegment.getContents());
			instanciatedSegments.put(instanciatedSegment.getId(),
					instanciatedSegment);
		}

		final DBCodeFragment instanciatedDBFragment = new DBCodeFragment(
				IDGenerator.generate(DBCodeFragment.class),
				new TreeSet<DBSegment>(new DBSegmentComparator()), null);

		final CodeFragment<E> instanciatedFragment = new CodeFragment<>(
				instanciatedDBFragment);

		for (final Segment<E> instanciatedSegment : instanciatedSegments
				.values()) {
			instanciatedDBFragment.getSegments().add(
					instanciatedSegment.getCore());
			instanciatedSegment.getCore().setCodeFragment(
					instanciatedDBFragment);

			instanciatedFragment.addSegment(instanciatedSegment);
			instanciatedSegment.setCodeFragment(instanciatedFragment);
		}

		return instanciatedFragment;
	}

	/**
	 * Make code fragment mapping instance from the given two fragments
	 * 
	 * @param oldFragment
	 *            the old fragment
	 * @param newFragment
	 *            the new fragment
	 * @param cloneClassMapping
	 *            the owner clone class mapping
	 * @return a new instance of {@link CodeFragmentMapping}
	 */
	public static <E extends IProgramElement> CodeFragmentMapping<E> makeInstance(
			final CodeFragment<E> oldFragment,
			final CodeFragment<E> newFragment,
			final CloneClassMapping<E> cloneClassMapping) {
		final DBCodeFragmentMapping dbMapping = new DBCodeFragmentMapping(
				IDGenerator.generate(DBCodeFragmentMapping.class),
				oldFragment.getCore(), newFragment.getCore(),
				cloneClassMapping.getCore());
		final CodeFragmentMapping<E> mapping = new CodeFragmentMapping<>(
				dbMapping);

		cloneClassMapping.getCore().getCodeFragmentMappings().add(dbMapping);

		mapping.setCloneClassMapping(cloneClassMapping);

		cloneClassMapping.addCodeFragmentMappings(mapping);

		mapping.setOldCodeFragment(oldFragment);
		mapping.setNewCodeFragment(newFragment);

		return mapping;
	}

}
