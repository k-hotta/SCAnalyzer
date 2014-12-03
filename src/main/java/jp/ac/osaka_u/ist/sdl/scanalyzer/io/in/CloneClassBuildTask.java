package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;

public class CloneClassBuildTask<E extends IProgramElement> implements
		Callable<CloneClass<E>> {

	private final RawCloneClass<E> rawCloneClass;

	private final Version<E> version;

	public CloneClassBuildTask(final RawCloneClass<E> rawCloineClass,
			final Version<E> version) {
		this.rawCloneClass = rawCloineClass;
		this.version = version;
	}

	@Override
	public CloneClass<E> call() throws Exception {
		final Map<Integer, List<E>> targetFragments = new TreeMap<Integer, List<E>>();
		final Map<Integer, SourceFile<E>> targetFragmentsSourceFiles = new TreeMap<Integer, SourceFile<E>>();

		// detect target fragments from raw cloned fragments
		// this method call will change the states of both of the arguments
		extractTargetFragments(targetFragments, targetFragmentsSourceFiles);

		// construct clone class and return it
		return constructCloneClass(targetFragments, targetFragmentsSourceFiles);
	}

	private void extractTargetFragments(
			final Map<Integer, List<E>> targetFragments,
			final Map<Integer, SourceFile<E>> targetFragmentsSourceFiles) {
		int count = 0;

		for (final RawClonedFragment<E> rawClonedFragment : rawCloneClass
				.getRawClonedFragments().values()) {
			final SourceFile<E> sourceFile = rawClonedFragment.getSourceFile();
			final int startPosition = searchStartPositionWithLine(
					sourceFile.getContents(), rawClonedFragment.getStartLine());

			// first detects the first index of the NEXT line and decrement it
			final int endPosition = searchEndPositionWithLine(
					sourceFile.getContents(),
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

			final List<E> targetFragment = new ArrayList<E>();
			for (int i = startPosition; i <= endPosition; i++) {
				targetFragment.add(sourceFile.getContents().get(i));
			}
			targetFragments.put(count, targetFragment);

			targetFragmentsSourceFiles.put(count++,
					rawClonedFragment.getSourceFile());
		}
	}

	private int searchStartPositionWithLine(
			final SortedMap<Integer, E> elements, final int line) {
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

	private int searchEndPositionWithLine(final SortedMap<Integer, E> elements,
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

	private int traceBack(final SortedMap<Integer, E> elements, final int origin) {
		final int originLine = elements.get(origin).getLine();

		for (int i = origin; i > 1; i--) {
			final int currentLine = elements.get(i - 1).getLine();
			if (originLine != currentLine) {
				return i;
			}
		}

		return 1;
	}

	private CloneClass<E> constructCloneClass(
			final Map<Integer, List<E>> targetFragments,
			final Map<Integer, SourceFile<E>> targetFragmentsSourceFiles) {
		final DBCloneClass dbCloneClass = new DBCloneClass(
				IDGenerator.generate(DBCloneClass.class), version.getCore(),
				new ArrayList<DBCodeFragment>());
		final CloneClass<E> cloneClass = new CloneClass<E>(dbCloneClass);

		for (int i = 0; i < targetFragments.size(); i++) {
			final CodeFragment<E> fragment = constructFragment(
					targetFragments.get(i), targetFragmentsSourceFiles.get(i));
			dbCloneClass.addCodeFragment(fragment.getCore());
			fragment.getCore().setCloneClass(dbCloneClass);

			cloneClass.addCodeFragment(fragment);
			cloneClass.setVersion(version);

			fragment.setCloneClass(cloneClass);
		}
		return cloneClass;
	}

	private CodeFragment<E> constructFragment(final List<E> elements,
			final SourceFile<E> sourceFile) {
		final DBCodeFragment dbCodeFragment = new DBCodeFragment(
				IDGenerator.generate(DBCodeFragment.class),
				new ArrayList<DBSegment>(), null, false);
		final CodeFragment<E> codeFragment = new CodeFragment<E>(dbCodeFragment);

		final int startPosition = elements.get(0).getPosition();
		final int endPosition = elements.get(elements.size() - 1).getPosition();

		final DBSegment dbSegment = new DBSegment(
				IDGenerator.generate(DBSegment.class), sourceFile.getCore(),
				startPosition, endPosition, dbCodeFragment);
		dbCodeFragment.getSegments().add(dbSegment);

		final Segment<E> segment = new Segment<E>(dbSegment);
		segment.setSourceFile(sourceFile);
		segment.setContents(sourceFile.getContents()
				.subMap(startPosition, endPosition + 1).values());
		segment.setCodeFragment(codeFragment);

		codeFragment.addSegment(segment);

		return codeFragment;
	}

}
