package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBRawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBRawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBSegment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFileWithContent;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBVersion;
import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;

public class CloneClassBuildTask implements Callable<DBCloneClass> {

	private final ConcurrentMap<Long, SourceFileWithContent<? extends IProgramElement>> fileContents;

	private final DBRawCloneClass rawCloneClass;

	private final DBVersion version;

	public CloneClassBuildTask(
			final ConcurrentMap<Long, SourceFileWithContent<? extends IProgramElement>> fileContents,
			final DBRawCloneClass rawCloineClass, final DBVersion version) {
		this.fileContents = fileContents;
		this.rawCloneClass = rawCloineClass;
		this.version = version;
	}

	@Override
	public DBCloneClass call() throws Exception {
		final Map<Integer, List<IProgramElement>> targetFragments = new TreeMap<Integer, List<IProgramElement>>();
		final Map<Integer, DBSourceFile> targetFragmentsSourceFiles = new TreeMap<Integer, DBSourceFile>();
		final SortedMap<Integer, SortedSet<Integer>> lcsElementsInEachFragment = new TreeMap<Integer, SortedSet<Integer>>();

		// detect target fragments from raw cloned fragments
		extractTargetFragments(targetFragments, targetFragmentsSourceFiles);

		// detect LCS among the target fragments
		detectLCS(targetFragments, lcsElementsInEachFragment);

		// construct clone class and return it
		return constructCloneClass(targetFragments, targetFragmentsSourceFiles,
				lcsElementsInEachFragment);
	}

	private DBCloneClass constructCloneClass(
			final Map<Integer, List<IProgramElement>> targetFragments,
			final Map<Integer, DBSourceFile> targetFragmentsSourceFiles,
			SortedMap<Integer, SortedSet<Integer>> lcsElementsInEachFragment) {
		final DBCloneClass result = new DBCloneClass(
				IDGenerator.generate(DBCloneClass.class), version,
				new ArrayList<DBCodeFragment>());
		for (int i = 0; i < targetFragments.size(); i++) {
			final DBCodeFragment fragment = constructFragment(
					targetFragments.get(i), lcsElementsInEachFragment.get(i),
					targetFragmentsSourceFiles.get(i));
			result.getCodeFragments().add(fragment);
			fragment.setCloneClass(result);
		}
		return result;
	}

	private void detectLCS(
			final Map<Integer, List<IProgramElement>> targetFragments,
			SortedMap<Integer, SortedSet<Integer>> lcsElementsInEachFragment)
			throws PatchFailedException {
		// this is the LCS between all the fragments
		// first this is initialized with the first fragment
		List<IProgramElement> lcs = new ArrayList<IProgramElement>();
		lcs.addAll(targetFragments.get(0));

		// this is for storing which element is in the LCS for each fragment
		// the outer key corresponds to the index in LCS
		// the inner key corresponds to the id of each fragment
		// the inner value corresponds to the index in the fragment
		SortedMap<Integer, Map<Integer, Integer>> lcsElementsMapping = new TreeMap<Integer, Map<Integer, Integer>>();

		SortedSet<Integer> lcsElementsInFirstFragment = new TreeSet<Integer>();

		for (int i = 0; i < lcs.size(); i++) {
			final Map<Integer, Integer> newMapping = new TreeMap<Integer, Integer>();
			newMapping.put(0, i);
			lcsElementsMapping.put(i, newMapping);
			lcsElementsInFirstFragment.add(i);
		}
		lcsElementsInEachFragment.put(0, lcsElementsInFirstFragment);

		for (int i = 1; i < targetFragments.size(); i++) {
			final List<IProgramElement> target = targetFragments.get(i);

			final Patch<IProgramElement> patch = DiffUtils.diff(lcs, target);
			final SortedMap<Integer, Integer> mapping = detectMapping(patch,
					lcs, target);

			final SortedSet<Integer> lcsElementsInThisFragment = new TreeSet<Integer>();

			final SortedMap<Integer, Map<Integer, Integer>> previousLcsElementsMapping = new TreeMap<Integer, Map<Integer, Integer>>();
			previousLcsElementsMapping.putAll(lcsElementsMapping);
			for (final Map.Entry<Integer, Map<Integer, Integer>> entry : lcsElementsMapping
					.entrySet()) {
				if (!mapping.containsKey(entry.getKey())) {
					// the element in previous lcs is no longer in current one
					Map<Integer, Integer> noLongerLcsElements = previousLcsElementsMapping
							.remove(entry.getKey());

					for (final Map.Entry<Integer, Integer> noLongerEntry : noLongerLcsElements
							.entrySet()) {
						lcsElementsInEachFragment.get(noLongerEntry.getKey())
								.remove(noLongerEntry.getValue());
					}

					continue;
				}

				final Map<Integer, Integer> currentMapping = entry.getValue();
				currentMapping.put(i, mapping.get(entry.getKey()));
				lcsElementsInThisFragment.add(mapping.get(entry.getKey()));
			}
			lcsElementsInEachFragment.put(i, lcsElementsInThisFragment);

			lcsElementsMapping.clear();

			int newIndex = 0;
			for (final Map.Entry<Integer, Map<Integer, Integer>> entry : previousLcsElementsMapping
					.entrySet()) {
				lcsElementsMapping.put(newIndex++, entry.getValue());
			}

			lcs = update(lcs, patch);
		}
	}

	private List<IProgramElement> update(final List<IProgramElement> target,
			final Patch<IProgramElement> patch) {
		final List<Integer> toBeRemoved = new ArrayList<Integer>();

		for (final Delta<IProgramElement> delta : patch.getDeltas()) {
			int position = delta.getOriginal().getPosition();
			int size = delta.getOriginal().size();
			for (int i = 0; i < size; i++) {
				toBeRemoved.add(position + i);
			}
		}

		// make sure toBeRemoved is descending order
		// note the order of o1 and o2 is reversed
		Collections.sort(toBeRemoved, (o1, o2) -> Integer.compare(o2, o1));

		for (int i : toBeRemoved) {
			target.remove(i);
		}

		return target;
	}

	private void extractTargetFragments(
			final Map<Integer, List<IProgramElement>> targetFragments,
			final Map<Integer, DBSourceFile> targetFragmentsSourceFiles) {
		int count = 0;

		for (final DBRawClonedFragment rawClonedFragment : rawCloneClass
				.getElements()) {
			final SourceFileWithContent<? extends IProgramElement> content = fileContents
					.get(rawClonedFragment.getSourceFile().getId());
			final int startPosition = searchStartPositionWithLine(
					content.getContents(), rawClonedFragment.getStartLine());

			// first detects the first index of the NEXT line and decrement it
			final int endPosition = searchEndPositionWithLine(
					content.getContents(),
					startPosition,
					rawClonedFragment.getStartLine()
							+ rawClonedFragment.getLength() - 1);

			if (startPosition == -1 || endPosition == -1) {
				throw new IllegalStateException(
						"cannot find corresponding elements");
			} else if (startPosition > endPosition) {
				throw new IllegalStateException(
						"cannot find corresponding elements");
			}

			@SuppressWarnings("unchecked")
			final List<IProgramElement> targetFragment = (List<IProgramElement>) content
					.getContentsIn(startPosition, endPosition);
			targetFragments.put(count, targetFragment);

			targetFragmentsSourceFiles.put(count++,
					rawClonedFragment.getSourceFile());
		}
	}

	private DBCodeFragment constructFragment(final List<IProgramElement> elements,
			final SortedSet<Integer> indexInLcs, final DBSourceFile sourceFile) {
		final DBCodeFragment result = new DBCodeFragment(
				IDGenerator.generate(DBCodeFragment.class),
				new ArrayList<DBSegment>(), null);

		final List<Integer> indexesInSegment = new ArrayList<Integer>();
		for (int i = 0; i < elements.size(); i++) {
			if (indexInLcs.contains(i)) {
				indexesInSegment.add(i);
				continue;
			} else {
				if (indexesInSegment.size() > 0) {
					int startPosition = elements.get(indexesInSegment.get(0))
							.getPosition();
					int endPosition = startPosition + indexesInSegment.size()
							- 1;
					final DBSegment segment = new DBSegment(
							IDGenerator.generate(DBSegment.class), sourceFile,
							startPosition, endPosition, result);
					result.getSegments().add(segment);
					indexesInSegment.clear();
				}
			}
		}

		if (indexesInSegment.size() > 0) {
			int startPosition = elements.get(indexesInSegment.get(0))
					.getPosition();
			int endPosition = startPosition + indexesInSegment.size() - 1;
			final DBSegment segment = new DBSegment(
					IDGenerator.generate(DBSegment.class), sourceFile,
					startPosition, endPosition, result);
			result.getSegments().add(segment);
			indexesInSegment.clear();
		}

		return result;
	}

	private SortedMap<Integer, Integer> detectMapping(
			final Patch<IProgramElement> patch, final List<IProgramElement> left,
			final List<IProgramElement> right) {
		final SortedMap<Integer, Integer> result = new TreeMap<Integer, Integer>();

		int counterForLeft = 0;
		// list of indexes for elements in left which are in the lcs
		final List<Integer> lcsIndexLeft = new ArrayList<Integer>();

		int counterForRight = 0;
		// list of indexes for elements in right which are in the lcs
		final List<Integer> lcsIndexRight = new ArrayList<Integer>();

		final List<Delta<IProgramElement>> deltas = new ArrayList<Delta<IProgramElement>>();
		deltas.addAll(patch.getDeltas());

		// make sure deltas are sorted based on left
		Collections.sort(deltas, new OriginalDeltaComparator());

		for (final Delta<IProgramElement> delta : deltas) {
			final Chunk<IProgramElement> chunk = delta.getOriginal();
			while (counterForLeft < chunk.getPosition()) {
				lcsIndexLeft.add(counterForLeft++);
			}
			counterForLeft += chunk.getLines().size();
		}
		while (counterForLeft < left.size()) {
			lcsIndexLeft.add(counterForLeft++);
		}

		// make sure deltas are sorted based on right
		Collections.sort(deltas, new RevisedDeltaComparator());

		for (final Delta<IProgramElement> delta : deltas) {
			final Chunk<IProgramElement> chunk = delta.getRevised();
			while (counterForRight < chunk.getPosition()) {
				lcsIndexRight.add(counterForRight++);
			}
			counterForRight += chunk.getLines().size();
		}
		while (counterForRight < right.size()) {
			lcsIndexRight.add(counterForRight++);
		}

		assert (lcsIndexLeft == lcsIndexRight);

		for (int i = 0; i < lcsIndexLeft.size(); i++) {
			result.put(lcsIndexLeft.get(i), lcsIndexRight.get(i));
		}

		return result;
	}

	private class OriginalDeltaComparator implements Comparator<Delta<?>> {

		@Override
		public int compare(Delta<?> o1, Delta<?> o2) {
			return ((Integer) o1.getOriginal().getPosition()).compareTo(o2
					.getOriginal().getPosition());
		}

	}

	private class RevisedDeltaComparator implements Comparator<Delta<?>> {

		@Override
		public int compare(Delta<?> o1, Delta<?> o2) {
			return ((Integer) o1.getRevised().getPosition()).compareTo(o2
					.getRevised().getPosition());
		}

	}

	private int searchEndPositionWithLine(
			final SortedMap<Integer, ? extends IProgramElement> elements,
			final int startPosition, final int line) {
		int result = elements.lastKey();

		for (int i = startPosition; i < result; i++) {
			final IProgramElement currentElement = elements.get(i);
			final int currentLine = currentElement.getLine();
			if (currentLine > line) {
				result = i - 1;
			}
		}

		return result;
	}

	private int searchStartPositionWithLine(
			final SortedMap<Integer, ? extends IProgramElement> elements,
			final int line) {
		int lowKey = elements.firstKey();
		int lowLine = elements.get(lowKey).getLine();

		int highKey = elements.lastKey();
		int highLine = elements.get(highKey).getLine();

		while (lowKey <= highKey) {
			int midKey = (lowKey + highKey) / 2;
			int midLine = elements.get(midKey).getLine();

			if (midLine == line) {
				return traceBack(elements, midKey);
			} else if (midLine < line) {
				lowKey = midKey + 1;
				if (lowKey > elements.size()) {
					break;
				}
				lowLine = elements.get(lowKey).getLine();
				if (lowLine > line) {
					return midKey + 1;
				}
			} else {
				highKey = midKey - 1;
				if (highKey <= 0) {
					break;
				}
				highLine = elements.get(highKey).getLine();
				if (highLine < line) {
					return midKey;
				}
			}
		}

		return -1; // not found
	}

	private int traceBack(
			final SortedMap<Integer, ? extends IProgramElement> elements,
			final int origin) {
		final int originLine = elements.get(origin).getLine();

		for (int i = origin; i > 1; i--) {
			final int currentLine = elements.get(i - 1).getLine();
			if (originLine != currentLine) {
				return i;
			}
		}

		return 1;
	}

}
