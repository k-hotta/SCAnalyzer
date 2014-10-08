package jp.ac.osaka_u.ist.sdl.scanalyzer.io;

import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.DBMS;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class that contains configuration values of SCAnalyzer. <br>
 * 
 * @author k-hotta
 * 
 */
public class Config {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager.getLogger(Config.class);

	/**
	 * The singleton object
	 */
	private static Config SINGLETON = null;

	/**
	 * The DBMS
	 */
	private DBMS dbms;

	/**
	 * The path of the database
	 */
	private String dbPath;

	/**
	 * The private constructor for adopting the singleton pattern
	 */
	private Config() {

	}

	/**
	 * Get the instance of the configuration. <br>
	 * It initializes the instance at the first call, and it returns the
	 * already-initialized instance after the second call or later. <br>
	 * 
	 * @return the singleton instance of this class
	 */
	public static Config getInstancce() {
		if (SINGLETON == null) {
			SINGLETON = new Config();
			logger.trace("the instance of " + Config.class.getName()
					+ " has been initialized");
		}

		return SINGLETON;
	}

	/*
	 * getters and setters follow
	 */

	/**
	 * Get DBMS
	 * 
	 * @return DBMS
	 */
	public final DBMS getDbms() {
		return dbms;
	}

	/**
	 * Set DBMS with the specified one
	 * 
	 * @param dbms
	 *            DBMS to be set
	 */
	public final void setDbms(DBMS dbms) {
		this.dbms = dbms;
	}

	/**
	 * Get the path of database
	 * 
	 * @return the path of database
	 */
	public final String getDbPath() {
		return dbPath;
	}

	/**
	 * Set the path of database
	 * 
	 * @param dbPath
	 *            the path of database to be set
	 */
	public final void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}

}
