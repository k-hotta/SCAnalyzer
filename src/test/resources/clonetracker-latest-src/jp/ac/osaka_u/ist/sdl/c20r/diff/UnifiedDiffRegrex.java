package jp.ac.osaka_u.ist.sdl.c20r.diff;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnifiedDiffRegrex {

	private static final String INDEX_STR = "(^Index:)(\\s+)(.+)";

	private static final String DIVIDER_STR = "^=+$";

	private static final String BEFORE_PATH_STR = "(^---)(\\s+)(.+)(\\s+)(\\(revision\\s+)(\\d+)(\\))";

	private static final String AFTER_PATH_STR = "(^\\+\\+\\+)(\\s+)(.+)(\\s+)(\\(revision\\s+)(\\d+)(\\))";

	private static final String HUNK_START_STR = "(^@@)(\\s+)(-)(\\d+)(,)(\\d+)(\\s+)(\\+)(\\d+)(,)(\\d+)(\\s+)(@@)";

	private static final String BEFORE_CODE_STR = "(^-)(.*)";

	private static final String AFTER_CODE_STR = "(^\\+)(.*)";

	private static final String UNCHANGED_CODE_STR = "(\\s)(.*)";

	private static final Pattern INDEX_PATTERN = Pattern.compile(INDEX_STR);

	private static final Pattern DIVIDER_PATTERN = Pattern.compile(DIVIDER_STR);

	private static final Pattern BEFORE_PATH_PATTERN = Pattern
			.compile(BEFORE_PATH_STR);

	private static final Pattern AFTER_PATH_PATTERN = Pattern
			.compile(AFTER_PATH_STR);

	private static final Pattern HUNK_START_PATTERN = Pattern
			.compile(HUNK_START_STR);

	private static final Pattern BEFORE_CODE_PATTERN = Pattern
			.compile(BEFORE_CODE_STR);

	private static final Pattern AFTER_CODE_PATTERN = Pattern
			.compile(AFTER_CODE_STR);

	private static final Pattern UNCHANGED_CODE_PATTERN = Pattern
			.compile(UNCHANGED_CODE_STR);

	public static boolean isFormattedWithIndexPattern(final String line) {
		return INDEX_PATTERN.matcher(line).matches();
	}

	public static boolean isFormattedWithDividerPattern(final String line) {
		return DIVIDER_PATTERN.matcher(line).matches();
	}

	public static boolean isFormattedWithBeforePathPattern(final String line) {
		return BEFORE_PATH_PATTERN.matcher(line).matches();
	}

	public static boolean isFormattedWithAfterPathPattern(final String line) {
		return AFTER_PATH_PATTERN.matcher(line).matches();
	}

	public static boolean isFormattedWithHunkStartPattern(final String line) {
		return HUNK_START_PATTERN.matcher(line).matches();
	}

	public static boolean isFormattedWithBeforeCodePattern(final String line) {
		return BEFORE_CODE_PATTERN.matcher(line).matches();
	}

	public static boolean isFormattedWithAfterCodePattern(final String line) {
		return AFTER_CODE_PATTERN.matcher(line).matches();
	}
	
	public static boolean isFormattedWithUnchangedCodePattern(final String line) {
		return UNCHANGED_CODE_PATTERN.matcher(line).matches();
	}
	
	public static String getFilePathInIndex(final String line) {
		final Matcher m = INDEX_PATTERN.matcher(line);
		if (m.matches()) {
			return m.group(3);
		} else {
			return null;
		}
	}
	
	public static String getBeforePath(final String line) {
		final Matcher m = BEFORE_PATH_PATTERN.matcher(line);
		if (m.matches()) {
			return m.group(3);
		} else {
			return null;
		}
	}
	
	public static int getBeforeRevision(final String line) {
		final Matcher m = BEFORE_PATH_PATTERN.matcher(line);
		if (m.matches()) {
			return Integer.parseInt(m.group(6));
		} else {
			return -1;
		}
	}
	
	public static String getAfterPath(final String line) {
		final Matcher m = AFTER_PATH_PATTERN.matcher(line);
		if (m.matches()) {
			return m.group(3);
		} else {
			return null;
		}
	}
	
	public static int getAfterRevision(final String line) {
		final Matcher m = AFTER_PATH_PATTERN.matcher(line);
		if (m.matches()) {
			return Integer.parseInt(m.group(6));
		} else {
			return -1;
		}
	}
	
	public static int getStartLineInBeforeRevision(final String line) {
		final Matcher m = HUNK_START_PATTERN.matcher(line);
		if (m.matches()) {
			return Integer.parseInt(m.group(4));
		} else {
			return -1;
		}
	}
	
	public static int getLineCountInBeforeRevision(final String line) {
		final Matcher m = HUNK_START_PATTERN.matcher(line);
		if (m.matches()) {
			return Integer.parseInt(m.group(6));
		} else {
			return -1;
		}
	}
	
	public static int getStartLineInAfterRevision(final String line) {
		final Matcher m = HUNK_START_PATTERN.matcher(line);
		if (m.matches()) {
			return Integer.parseInt(m.group(9));
		} else {
			return -1;
		}
	}
	
	public static int getLineCountInAfterRevision(final String line) {
		final Matcher m = HUNK_START_PATTERN.matcher(line);
		if (m.matches()) {
			return Integer.parseInt(m.group(11));
		} else {
			return -1;
		}
	}

}
