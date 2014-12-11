package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

public enum MiningUnit {

	GENEALOGY;

	/**
	 * Provide a string that represents which values can be used as MiningUnit.
	 * 
	 * @return a string representing available values
	 */
	public static String canBe() {
		final StringBuilder builder = new StringBuilder();
		builder.append("{ ");

		for (final MiningUnit value : values()) {
			builder.append(value.toString() + ", ");
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.deleteCharAt(builder.length() - 1);

		builder.append(" }");

		return builder.toString();
	}

	/**
	 * Get the corresponding element of this enum for the given string.
	 * 
	 * @param str
	 *            the query to get MiningUnit ignoring the case
	 * @return the corresponding MiningUnit if found, <code>null</code> if not
	 *         found
	 */
	public static MiningUnit getCorrespondingStrategy(final String str) {
		if (str == null) {
			return null;
		}

		final String upperStr = str.toUpperCase();

		MiningUnit result = null;
		try {
			result = MiningUnit.valueOf(upperStr);
		} catch (Exception e) {
			// ignore
		}

		return result;
	}

}
