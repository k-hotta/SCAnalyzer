package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

/**
 * This enum represents available algorithms for mapping elements.
 * 
 * @author k-hotta
 *
 */
public enum ElementMappingAlgorithm {

	TRADITIONAL_DIFF;

	/**
	 * Get the corresponding element of this enum for the given string.
	 * 
	 * @param str
	 *            the query to get ElementMappingAlgorithm ignoring the case
	 * @return the corresponding ElementMappingAlgorithm if found,
	 *         <code>null</code> if not found
	 */
	public static ElementMappingAlgorithm getCorrespondingDBMS(final String str) {
		if (str == null) {
			return null;
		}

		final String upperStr = str.toUpperCase();

		ElementMappingAlgorithm result = null;
		try {
			result = ElementMappingAlgorithm.valueOf(upperStr);
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

		for (final ElementMappingAlgorithm value : values()) {
			builder.append(value.toString() + ", ");
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.deleteCharAt(builder.length() - 1);

		builder.append(" }");

		return builder.toString();
	}

}
