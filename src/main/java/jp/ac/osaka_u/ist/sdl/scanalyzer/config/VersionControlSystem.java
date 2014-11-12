package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

/**
 * This enum describes version control systems under support.
 * 
 * @author k-hotta
 * 
 */
public enum VersionControlSystem {

	/**
	 * Subversion
	 */
	SVN;

	/**
	 * Provide a string that represents which values can be used as
	 * VersionControlSystem.
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
	
	/**
	 * Get the corresponding element of this enum for the given string.
	 * 
	 * @param str
	 *            the query to get VersionControlSystem ignoring the case
	 * @return the corresponding VersionControlSystem if found, <code>null</code> if not found
	 */
	public static VersionControlSystem getCorrespondingVersionControlSystem(final String str) {
		if (str == null) {
			return null;
		}

		final String upperStr = str.toUpperCase();

		VersionControlSystem result = null;
		try {
			result = VersionControlSystem.valueOf(upperStr);
		} catch (Exception e) {
			// ignore
		}

		return result;
	}

}
