package jp.ac.osaka_u.ist.sdl.c20r.diff;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.settings.Language;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.settings.Settings;
import jp.ac.osaka_u.ist.sdl.c20r.util.StringUtilities;

public class HunkDetector {

	private static final Language language = Settings.getIntsance()
			.getLanguage();

	public static Map<Long, Set<Hunk>> detectHunks(final String diffText,
			final long beforeRev, final long afterRev, final String root)
			throws Exception {
		final BufferedReader br = new BufferedReader(new StringReader(diffText));

		final Set<Hunk> beforeHunks = new HashSet<Hunk>();
		final Set<Hunk> afterHunks = new HashSet<Hunk>();

		String line;
		boolean inCode = false;
		String beforePath = null;
		String afterPath = null;
		int beforeStart = 0;
		int afterStart = 0;
		int beforeCount = 0;
		int afterCount = 0;
		final Set<Integer> beforeLines = new TreeSet<Integer>();
		final Set<Integer> afterLines = new TreeSet<Integer>();

		while ((line = br.readLine()) != null) {
			if (inCode) {
				if (UnifiedDiffRegrex.isFormattedWithUnchangedCodePattern(line)) {
					beforeCount++;
					afterCount++;
				} else if (UnifiedDiffRegrex
						.isFormattedWithBeforeCodePattern(line)) {
					beforeLines.add(beforeStart + beforeCount++);
				} else if (UnifiedDiffRegrex
						.isFormattedWithAfterCodePattern(line)) {
					afterLines.add(afterStart + afterCount++);
				} else {
					if (!beforeLines.isEmpty()
							&& language.isTargetFile(beforePath)) {
						final Hunk beforeHunk = new Hunk(beforeRev, beforePath,
								beforeLines);
						beforeHunks.add(beforeHunk);
					}
					if (!afterLines.isEmpty()
							&& language.isTargetFile(afterPath)) {
						final Hunk afterHunk = new Hunk(afterRev, afterPath,
								afterLines);
						afterHunks.add(afterHunk);
					}
					beforeCount = 0;
					afterCount = 0;
					beforeLines.clear();
					afterLines.clear();
					if (UnifiedDiffRegrex.isFormattedWithHunkStartPattern(line)) {
						beforeStart = UnifiedDiffRegrex
								.getStartLineInBeforeRevision(line);
						afterStart = UnifiedDiffRegrex
								.getStartLineInAfterRevision(line);
						inCode = true;
					} else {
						beforePath = null;
						afterPath = null;
						beforeStart = 0;
						afterStart = 0;
						inCode = false;
					}
				}
			} else {
				if (UnifiedDiffRegrex.isFormattedWithBeforePathPattern(line)) {
					beforePath = StringUtilities.detectAbsolutePath(root,
							UnifiedDiffRegrex.getBeforePath(line));
				} else if (UnifiedDiffRegrex
						.isFormattedWithAfterPathPattern(line)) {
					afterPath = StringUtilities.detectAbsolutePath(root,
							UnifiedDiffRegrex.getAfterPath(line));
				} else if (UnifiedDiffRegrex
						.isFormattedWithHunkStartPattern(line)) {
					beforeStart = UnifiedDiffRegrex
							.getStartLineInBeforeRevision(line);
					afterStart = UnifiedDiffRegrex
							.getStartLineInAfterRevision(line);
					inCode = true;
				}
			}
		}

		if (inCode) {
			if (!beforeLines.isEmpty()) {
				final Hunk beforeHunk = new Hunk(beforeRev, beforePath,
						beforeLines);
				beforeHunks.add(beforeHunk);
			}
			if (!afterLines.isEmpty()) {
				final Hunk afterHunk = new Hunk(afterRev, afterPath, afterLines);
				afterHunks.add(afterHunk);
			}
		}

		br.close();

		final Map<Long, Set<Hunk>> result = new TreeMap<Long, Set<Hunk>>();
		result.put(beforeRev, Collections.unmodifiableSet(beforeHunks));
		result.put(afterRev, Collections.unmodifiableSet(afterHunks));

		return Collections.unmodifiableMap(result);
	}
}
