package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	 * The logger for errors
	 */
	private static final Logger eLogger = LogManager.getLogger("error");

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
	 * Get the corresponding element of this enum for the given string. <br>
	 * It first calls {@link DBMS#valueOf(String)} and returns the result if any
	 * valid result can be obtained. <br>
	 * Otherwise, it returns {@link DBMS#SQLITE} as a default value. <br>
	 * Call this method instead of {@link DBMS#valueOf(String)} if you want to
	 * force SCAnalyzer to use {@link DBMS#SQLITE} as the default in the case
	 * where the given string is not valid. <br>
	 * 
	 * @param str
	 *            the query to get DBMS
	 * @return the corresponding DBMS if found, otherwise {@link DBMS#SQLITE}
	 */
	public static DBMS getCorrespondingDBMS(final String str) {
		DBMS value = null;
		try {
			value = DBMS.valueOf(str);
		} catch (Exception e) {
			eLogger.warn(
					"cannot find the DBMS correponds to {}, SQLite will be used instead",
					str);
			value = SQLITE;
		}

		return value;
	}

}
