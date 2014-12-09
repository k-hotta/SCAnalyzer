package jp.ac.osaka_u.ist.sdl.scanalyzer.metrics;

import java.util.ArrayList;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneModification;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneModification.Type;
import difflib.Chunk;

/**
 * This is a helper class to analyze modifications.
 * 
 * @author k-hotta
 *
 */
public class ModificationAnalyzeHelper {

	/**
	 * Divide the given list of elements (an LCS) to sub lists all elements in
	 * which are sequential. Note this method does not change the state of the
	 * given list. This method creates a copy of the given list and returns the
	 * result calculated by the copied list.
	 * 
	 * @param lcs
	 *            the lcs to be divided
	 * 
	 * @return a list of sub lists all elements in which are sequential
	 */
	public static <E extends IProgramElement> List<List<E>> divide(
			final List<E> lcs) {
		final List<List<E>> result = new ArrayList<>();
		List<E> currentList = new ArrayList<>();
		E lastElement = null;

		for (final E element : lcs) {
			if (lastElement == null) {
				currentList.add(element);
			} else {
				final int lastElementPos = lastElement.getPosition();
				final int currentElementPos = element.getPosition();

				final boolean posCondition = (currentElementPos
						- lastElementPos == 1);
				final boolean fileCondition = element.getOwnerSourceFile()
						.getHashOfPath() == lastElement.getOwnerSourceFile()
						.getHashOfPath();

				if (posCondition && fileCondition) {
					// sequential
					currentList.add(element);
				} else {
					// not sequential
					result.add(currentList);
					currentList = new ArrayList<>();
					currentList.add(element);
				}
			}

			lastElement = element;
		}

		if (!currentList.isEmpty()) {
			result.add(currentList);
		}

		return result;
	}

	/**
	 * Make an instance of modification and register it.
	 * 
	 * @param type
	 * @param elements
	 * @param oldStartPosition
	 * @param newStartPosition
	 * @param oldSegment
	 * @param newSegment
	 * @param fragmentMapping
	 */
	public static <E extends IProgramElement> void registerModification(
			final Type type, List<E> elements, final int oldStartPosition,
			final int newStartPosition, final Segment<E> oldSegment,
			final Segment<E> newSegment,
			final CodeFragmentMapping<E> fragmentMapping) {
		final DBCloneModification modification = new DBCloneModification(
				IDGenerator.generate(DBCloneModification.class),
				oldStartPosition, newStartPosition, elements.size(), type,
				calculateContentHash(elements),
				(fragmentMapping == null) ? null : fragmentMapping.getCore(),
				(oldSegment == null) ? null : oldSegment.getCore(),
				(newSegment == null) ? null : newSegment.getCore());

		fragmentMapping.getCore().addModification(modification);
	}

	/**
	 * Calculate content hash for the given contents.
	 * 
	 * @param elements
	 *            the contents under consideration
	 * 
	 * @return a hash code calculated from the given elements
	 */
	public static <E extends IProgramElement> int calculateContentHash(
			final List<E> elements) {
		int result = 0;

		for (final E element : elements) {
			result *= 31;
			result += 23 * element.getHashForChangeAnalysis();
		}

		return result;
	}

	/**
	 * Get the position where the given chunk starts in the file.
	 * 
	 * @param chunk
	 *            a chunk under consideration
	 * @param elements
	 *            a list of elements
	 * 
	 * @return the position the start position of the chunk in the file
	 */
	public static <E extends IProgramElement> int getPositionInFile(
			final Chunk<E> chunk, final List<E> elements) {
		// this value is the position in the SEGMENT, not in the FILE
		// hence this value cannot be used as it is
		final int revisedIndex = chunk.getPosition();

		final E elementAt = elements.get(revisedIndex);

		return elementAt.getPosition();
	}

}
