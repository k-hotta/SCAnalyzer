package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

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
	 * The programming language in which the target is written
	 */
	private Language language;

	/**
	 * The path or URL of the target repository
	 */
	private String repository;

	/**
	 * The version control system under consideration
	 */
	private VersionControlSystem vcs;

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

	/**
	 * Get the programming language in which the target is written
	 * 
	 * @return the programming language
	 */
	public final Language getLanguage() {
		return language;
	}

	/**
	 * Set the programming language in which the target is written
	 * 
	 * @param language
	 *            the programming language to be set
	 */
	public final void setLanguage(Language language) {
		this.language = language;
	}

	/**
	 * Get the path or URL of the target repository
	 * 
	 * @return the path or URL of the target repository
	 */
	public final String getRepository() {
		return repository;
	}

	/**
	 * Set the path or URL of the target repository
	 * 
	 * @param repository
	 *            the path or URL of the target repository to be set
	 */
	public final void setRepository(String repository) {
		this.repository = repository;
	}

	/**
	 * Get the version control system under consideration
	 * 
	 * @return the version control system
	 */
	public final VersionControlSystem getVcs() {
		return vcs;
	}

	/**
	 * Set the version control system under consideration
	 * 
	 * @param vcs
	 *            the version control system under consideration to be set
	 */
	public final void setVcs(VersionControlSystem vcs) {
		this.vcs = vcs;
	}

}
