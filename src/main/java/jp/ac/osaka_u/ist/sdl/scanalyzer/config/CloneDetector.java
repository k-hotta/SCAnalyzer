package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

/**
 * This enum represents the available clone detectors.
 * 
 * @author k-hotta
 *
 */
public enum CloneDetector {

	SCORPIO;

	/**
	 * Get the corresponding element of this enum for the given string.
	 * 
	 * @param str
	 *            the query to get CloneDetector ignoring the case
	 * @return the corresponding CloneDetector if found, <code>null</code> if
	 *         not found
	 */
	public static CloneDetector getCorrespondingCloneDetector(final String str) {
		if (str == null) {
			return null;
		}

		final String upperStr = str.toUpperCase();

		CloneDetector result = null;
		try {
			result = CloneDetector.valueOf(upperStr);
		} catch (Exception e) {
			// ignore
		}

		return result;
	}

	/**
	 * Provide a string that represents which values can be used as DBMS.
	 * 
	 * @return a string representing available values
	 */
	public static String canBe() {
		final StringBuilder builder = new StringBuilder();
		builder.append("{ ");

		for (final DBMS value : DBMS.values()) {
			builder.append(value.toString() + ", ");
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.deleteCharAt(builder.length() - 1);

		builder.append(" }");

		return builder.toString();
	}

}
