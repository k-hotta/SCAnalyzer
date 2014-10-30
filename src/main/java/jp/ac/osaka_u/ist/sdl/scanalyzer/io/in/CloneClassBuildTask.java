package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IAtomicElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFileContent;
import difflib.DiffUtils;
import difflib.Patch;

public class CloneClassBuildTask implements Callable<CloneClass> {

	private final ConcurrentMap<Long, SourceFileContent<IAtomicElement>> fileContents;

	private final RawCloneClass rawCloneClass;

	public CloneClassBuildTask(
			final ConcurrentMap<Long, SourceFileContent<IAtomicElement>> fileContents,
			final RawCloneClass rawCloineClass) {
		this.fileContents = fileContents;
		this.rawCloneClass = rawCloineClass;
	}

	@Override
	public CloneClass call() throws Exception {
		final Map<Integer, List<IAtomicElement>> targetFragments = new TreeMap<Integer, List<IAtomicElement>>();
		int count = 0;

		for (final RawClonedFragment rawClonedFragment : rawCloneClass
				.getElements()) {
			final SourceFileContent<IAtomicElement> content = fileContents
					.get(rawClonedFragment.getSourceFile().getId());
			final int startPosition = searchPositionWithLine(
					content.getContents(), rawClonedFragment.getStartLine());
			final int endPosition = searchPositionWithLine(
					content.getContents(), rawClonedFragment.getStartLine()
							+ rawClonedFragment.getLength() - 1);

			if (startPosition == -1 || endPosition == -1) {
				throw new IllegalStateException(
						"cannot find corresponding elements");
			} else if (startPosition > endPosition) {
				throw new IllegalStateException(
						"cannot find corresponding elements");
			}

			final List<IAtomicElement> targetFragment = content.getContentsIn(
					startPosition, endPosition);
			targetFragments.put(count++, targetFragment);
		}

		for (int i = 0; i < targetFragments.size() - 1; i++) {
			final List<IAtomicElement> target1 = targetFragments.get(i);
			final List<IAtomicElement> target2 = targetFragments.get(i + 1);
			
			final Patch<IAtomicElement> patch = DiffUtils.diff(target1, target2);
		}

		return null;
	}

	private int searchPositionWithLine(
			final SortedMap<Integer, IAtomicElement> elements, final int line) {
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

	private int traceBack(final SortedMap<Integer, IAtomicElement> elements,
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
