package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

/**
 * This enum represents available equalizers of elements.
 * 
 * @author k-hotta
 *
 */
public enum ElementEqualizer {

	EXACT, NEAR_MISS;

	/**
	 * Get the corresponding element of this enum for the given string.
	 * 
	 * @param str
	 *            the query to get ElementEqualizer ignoring the case
	 * @return the corresponding ElementEqualizer if found, <code>null</code> if
	 *         not found
	 */
	public static ElementEqualizer getCorrespondingElementEqualizer(
			final String str) {
		if (str == null) {
			return null;
		}

		final String upperStr = str.toUpperCase();

		ElementEqualizer result = null;
		try {
			result = ElementEqualizer.valueOf(upperStr);
		} catch (Exception e) {
			// ignore
		}

		return result;
	}

	/**
	 * Provide a string that represents which values can be used as ElementType.
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
