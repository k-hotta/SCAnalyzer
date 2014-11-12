package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

/**
 * This enumeration represents the DBMSs that can be used in SCAnalyzer. <br>
 * Currently, it supports only SQLite. <br>
 * 
 * @author k-hotta
 * 
 */
public enum DBMS {

	/**
	 * This element represents SQLite
	 */
	SQLITE("jdbc:sqlite:");

	/**
	 * The string of JDBC driver of the DBMS, which is used as a header of URL
	 * of database.
	 */
	private final String driverStr;

	/**
	 * The private constructor
	 * 
	 * @param driverStr
	 *            the string of JDBC driver of the DBMS
	 */
	private DBMS(String driverStr) {
		this.driverStr = driverStr;
	}

	/**
	 * Get the string of JDBC driver of the DBMS.
	 * 
	 * @return the string of JDBC driver of the DBMS
	 */
	public String getDriverStr() {
		return this.driverStr;
	}

	/**
	 * Get the corresponding element of this enum for the given string.
	 * 
	 * @param str
	 *            the query to get DBMS ignoring the case
	 * @return the corresponding DBMS if found, <code>null</code> if not found
	 */
	public static DBMS getCorrespondingDBMS(final String str) {
		if (str == null) {
			return null;
		}

		final String upperStr = str.toUpperCase();

		DBMS result = null;
		try {
			result = DBMS.valueOf(upperStr);
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

		for (final DBMS value : values()) {
			builder.append(value.toString() + ", ");
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.deleteCharAt(builder.length() - 1);

		builder.append(" }");

		return builder.toString();
	}

}
