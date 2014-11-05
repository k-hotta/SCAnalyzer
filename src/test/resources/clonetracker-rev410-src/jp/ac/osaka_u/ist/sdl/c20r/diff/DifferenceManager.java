package jp.ac.osaka_u.ist.sdl.c20r.diff;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * リビジョン間の差分を管理するマネージャー
 * 
 * @author k-hotta
 * 
 */
public class DifferenceManager {

	private static DifferenceManager SINGLETON;

	private final Map<String, Set<Integer>> beforeDiff;

	private final Map<String, Set<Integer>> afterDiff;

	private DifferenceManager(final Set<Hunk> beforeHunks,
			final Set<Hunk> afterHunks) {
		this.beforeDiff = detectDifferences(beforeHunks);
		this.afterDiff = detectDifferences(afterHunks);
	}

	private Map<String, Set<Integer>> detectDifferences(final Set<Hunk> hunks) {
		final Map<String, Set<Integer>> result = new TreeMap<String, Set<Integer>>();
		for (final Hunk hunk : hunks) {
			final String path = hunk.getFilePath();
			final Set<Integer> lines = hunk.getLines();
			if (result.containsKey(path)) {
				result.get(path).addAll(lines);
			} else {
				final Set<Integer> newSet = new TreeSet<Integer>();
				newSet.addAll(lines);
				result.put(path, newSet);
			}
		}
		return result;
	}

	public static DifferenceManager createInstance(final Set<Hunk> beforeHunks,
			final Set<Hunk> afterHunks) {
		SINGLETON = new DifferenceManager(beforeHunks, afterHunks);
		return SINGLETON;
	}

	public static DifferenceManager getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new DifferenceManager(new HashSet<Hunk>(),
					new HashSet<Hunk>());
		}
		return SINGLETON;
	}

	public final Map<String, Set<Integer>> getBeforeDiff() {
		return Collections.unmodifiableMap(beforeDiff);
	}

	public final Map<String, Set<Integer>> getAfterDiff() {
		return Collections.unmodifiableMap(afterDiff);
	}

	public final boolean isContainedInBeforeDiff(final String path,
			final int start, final int end) {
		if (!beforeDiff.containsKey(path)) {
			return false;
		}

		final Set<Integer> target = new TreeSet<Integer>();
		for (int i = start; i <= end; i++) {
			target.add(i);
		}
		return beforeDiff.get(path).containsAll(target);
	}

	public final boolean isContainedInAfterDiff(final String path,
			final int start, final int end) {
		if (!afterDiff.containsKey(path)) {
			return false;
		}

		final Set<Integer> target = new TreeSet<Integer>();
		for (int i = start; i <= end; i++) {
			target.add(i);
		}
		return afterDiff.get(path).containsAll(target);
	}

}
