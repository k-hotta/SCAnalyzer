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

}
