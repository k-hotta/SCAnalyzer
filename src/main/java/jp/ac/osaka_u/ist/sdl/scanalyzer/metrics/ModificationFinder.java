package jp.ac.osaka_u.ist.sdl.scanalyzer.metrics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneModification.Type;
import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import difflib.myers.Equalizer;

public class ModificationFinder<E extends IProgramElement> {

	/**
	 * This map contains lists of elements. Each of values is a list all of
	 * whose elements must be ordered and sequential.
	 */
	private final Map<Integer, List<E>> subLists;

	/**
	 * This is the number of sub lists contained in {@link #subLists}. This
	 * value equals to {@link Map#size()} of {@link #subLists}.
	 */
	private final int numSubLists;

	/**
	 * This value points the index of sub lists under consideration now. In
	 * other words, this index is used to get a sub list from {@link #subLists}.
	 */
	private int sublistIndex;

	/**
	 * This value points the index of elements in a sub list. In other words,
	 * this value is used to get an element from a sub list.
	 */
	private int elementIndex;

	/**
	 * This value points the index of elements in a chunk. In other words, this
	 * value is used to get an element from a chunk.
	 */
	private int chunkIndex;

	/**
	 * The comparator of elements.
	 */
	private final Comparator<E> comparator;

	/**
	 * The equalizer for elements
	 */
	private final Equalizer<E> equalizer;

	/**
	 * The old segment
	 */
	private final Segment<E> oldSegment;

	/**
	 * The new segment
	 */
	private final Segment<E> newSegment;

	/**
	 * The code fragment mapping
	 */
	private final CodeFragmentMapping<E> fragmentMapping;

	/**
	 * The contents in the old segment
	 */
	private final List<E> oldContents;

	/**
	 * The contents in the new segment
	 */
	private final List<E> newContents;

	/**
	 * The clone class mapping
	 */
	private final CloneClassMapping<E> cloneClassMapping;

	public ModificationFinder(final Map<Integer, List<E>> subLists,
			final Equalizer<E> equalizer, final Segment<E> oldSegment,
			final Segment<E> newSegment,
			final CodeFragmentMapping<E> fragmentMapping,
			final CloneClassMapping<E> cloneClassMapping) {
		this.subLists = subLists;
		this.numSubLists = subLists.size();
		this.sublistIndex = 0;
		this.elementIndex = 0;
		this.chunkIndex = 0;
		this.comparator = new ElementComparator();
		this.equalizer = equalizer;
		this.oldSegment = oldSegment;
		this.newSegment = newSegment;
		this.fragmentMapping = fragmentMapping;
		this.oldContents = new ArrayList<E>();
		this.newContents = new ArrayList<E>();
		this.cloneClassMapping = cloneClassMapping;
	}

	public ModificationFinder(final List<List<E>> subLists,
			final Equalizer<E> equalizer, final Segment<E> oldSegment,
			final Segment<E> newSegment,
			final CodeFragmentMapping<E> fragmentMapping,
			final CloneClassMapping<E> cloneClassMapping) {
		this(convertToMap(subLists), equalizer, oldSegment, newSegment,
				fragmentMapping, cloneClassMapping);
	}

	/**
	 * Convert the given list of lists to map.
	 * 
	 * @param subLists
	 * @return
	 */
	private static <E extends IProgramElement> Map<Integer, List<E>> convertToMap(
			final List<List<E>> subLists) {
		final Map<Integer, List<E>> subListsAsMap = new TreeMap<>();

		int count = 0;
		for (final List<E> subList : subLists) {
			subListsAsMap.put(count++, subList);
		}

		return subListsAsMap;
	}

	/**
	 * Find modifications from the given patch and register them.
	 */
	public void findAndRegisterModifications() {
		// get contents
		for (final E oldContent : oldSegment.getContents()) {
			oldContents.add(oldContent);
		}
		for (final E newContent : newSegment.getContents()) {
			newContents.add(newContent);
		}

		// compare segments with diff using the equalizer
		final Patch<E> patch = DiffUtils.diff(oldContents, newContents,
				equalizer);

		for (final Delta<E> delta : patch.getDeltas()) {
			switch (delta.getType()) {
			case DELETE:
				processDeletion(delta.getOriginal(), delta.getRevised());
				break;
			case INSERT:
				processAddition(delta.getOriginal(), delta.getRevised());
				break;
			case CHANGE:
				processChange(delta.getOriginal(), delta.getRevised());
				break;
			}
		}
	}

	private boolean processDeletion(final Chunk<E> original,
			final Chunk<E> revised) {
		final List<E> deletedElements = original.getLines();

		boolean inRange = false;
		while (chunkIndex < deletedElements.size()) {
			// get sublists under consideration
			final List<E> subList = getSubList();

			if (subList == null) {
				// all sublists have been processed
				break;
			}

			// the element of interest in the sub list
			final E currentElement = subList.get(elementIndex);

			// the element of interest in the deleted elements
			final E deletedElement = deletedElements.get(chunkIndex);

			// compare the above two elements
			int compare = comparator.compare(currentElement, deletedElement);

			if (compare > 0) {
				/*
				 * This means that the current element locates after the deleted
				 * element. In this case, the pointer for deleted elements
				 * should move forward and the next deleted element should be
				 * considered.
				 */
				chunkIndex++;
			}

			else if (compare == 0) {
				/*
				 * This means that the current element and the deleted element
				 * are the same as each other. This case requires the next step
				 * to make instances of modifications. Note that the pointers
				 * will move forward during the next step.
				 */
				makeDeletionsInChunk(subList, original, revised);
				inRange = true;
			}

			else {
				/*
				 * This means that the current element locates before the
				 * deleted element. In this case, the pointers for sub lists
				 * have to move forward.
				 */
				elementIndex++;
			}
		}

		return inRange;
	}

	/**
	 * Get the sub list under consideration.
	 * 
	 * @return a list of elements under consideration, <code>null</code> if no
	 *         list was found, which means all the sub lists have been
	 *         considered.
	 */
	private List<E> getSubList() {
		if (sublistIndex >= numSubLists) {
			// all sub lists have been considered
			return null;
		}

		List<E> subList = subLists.get(sublistIndex);

		if (elementIndex >= subList.size()) {
			// the element index exceeds the bounds of the current sublist
			elementIndex = 0;
			sublistIndex++;
			subList = getSubList();
		}

		return subList;
	}

	/**
	 * Make instances of modifications for deletions. This method should be
	 * called in case where the chunk of interest locates in any of sub lists.
	 * 
	 * @param subList
	 *            the sublist under consideration
	 * @param elementsInChunk
	 *            the elements in the chunk under consideration
	 */
	private void makeDeletionsInChunk(final List<E> subList,
			final Chunk<E> original, final Chunk<E> revised) {
		final List<E> elementsInChunk = original.getLines();
		int commonIndex = 0;

		while (true) {
			final int currentElementIndex = elementIndex + commonIndex;
			final int currentChunkIndex = chunkIndex + commonIndex;

			final boolean reachEndOfSublist = (currentElementIndex >= subList
					.size());
			final boolean reachEndOfChunk = (currentChunkIndex >= elementsInChunk
					.size());

			if (reachEndOfSublist && reachEndOfChunk) {
				/*
				 * The pointer reached at the end of both of the sublist and the
				 * chunk.
				 */
				makeDeletion(subList, commonIndex,
						ModificationAnalyzeHelper.getPositionInFile(revised,
								newContents));
				break;
			}

			else if (reachEndOfSublist) {
				/*
				 * The pointer reached at the end of the sublist, but there
				 * exist some elements left in the chunk.
				 */
				makeDeletion(subList, commonIndex,
						ModificationAnalyzeHelper.getPositionInFile(revised,
								newContents));
				break;
			}

			else if (reachEndOfChunk) {
				/*
				 * The pointer reached at the end of the chunk, but there exist
				 * some elements left in the sub list.
				 */

				// + 1 is mandatory only for this case
				makeDeletion(subList, commonIndex + 1,
						ModificationAnalyzeHelper.getPositionInFile(revised,
								newContents));
				break;
			}

			commonIndex++;
		}

		elementIndex += commonIndex;
		chunkIndex += commonIndex;
	}

	/**
	 * Make an instance of deletion.
	 * 
	 * @param subList
	 * @param commonIndex
	 */
	private void makeDeletion(final List<E> subList, final int commonIndex,
			final int newPos) {
		final List<E> modifiedElements = new ArrayList<>();
		for (int i = elementIndex; i < elementIndex + commonIndex; i++) {
			modifiedElements.add(subList.get(i));
		}

		ModificationAnalyzeHelper.registerModification(Type.REMOVE,
				modifiedElements, modifiedElements.get(0).getPosition(),
				newPos, oldSegment, newSegment, fragmentMapping,
				cloneClassMapping);
	}

	private boolean processAddition(final Chunk<E> original,
			final Chunk<E> revised) {
		final int originalPosition = ModificationAnalyzeHelper
				.getPositionInFile(original, oldContents);
		boolean inRange = false;

		while (true) {
			final List<E> subList = getSubList();
			if (subList == null) {
				break;
			}

			final E firstElement = subList.get(0);
			final E lastElement = subList.get(subList.size() - 1);

			if (firstElement.getPosition() <= originalPosition
					&& originalPosition < lastElement.getPosition()) {
				/*
				 * XXX why the latter is "<", not "<="? This is because, in the
				 * case where originalPosition == lastElement.getPosition(), the
				 * insertion is performed just after the sublist. Hence, there
				 * is no way to judge whether the insertion is performed inside
				 * or outside of the sublist. Did the developer intend to insert
				 * some elements into the tail of the sublist? Did she/he intend
				 * to insert them into the head of the part following the
				 * sublist? This is a known challenge in iClones as well.
				 */
				makeAddition(revised.getLines(), originalPosition);
				inRange = true;

				break;
			}

			sublistIndex++;
		}

		return inRange;
	}

	/**
	 * Make an instance of addition.
	 * 
	 * @param addedElements
	 * @param startPos
	 */
	private void makeAddition(final List<E> addedElements, final int startPos) {
		ModificationAnalyzeHelper.registerModification(Type.ADD, addedElements,
				startPos, addedElements.get(0).getPosition(), oldSegment,
				newSegment, fragmentMapping, cloneClassMapping);
	}

	private boolean processChange(final Chunk<E> original,
			final Chunk<E> revised) {
		final boolean inRange = processDeletion(original, revised);
		if (inRange) {
			makeAddition(revised.getLines(), original.getLines().get(0)
					.getPosition());
		}

		return inRange;
	}

	/**
	 * This is a comparator for program elements.
	 * 
	 * @author k-hotta
	 *
	 */
	private class ElementComparator implements Comparator<E> {

		@Override
		public int compare(E e1, E e2) {
			final int comparePath = e1.getOwnerSourceFile().getPath()
					.compareTo(e2.getOwnerSourceFile().getPath());
			if (comparePath != 0) {
				return comparePath;
			}

			return Integer.compare(e1.getPosition(), e2.getPosition());
		}

	}

}
