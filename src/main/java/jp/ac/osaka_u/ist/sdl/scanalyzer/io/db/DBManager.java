package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * A class to manage db connections. <br>
 * SCAnalyzer uses ORMLite as an ORM library, and so this class connects
 * database with ORMLite APIs. <br>
 * <p>
 * <b>NOTE:</b> This class is not thread-safe.
 * </p>
 * 
 * @author k-hotta
 * 
 */
public class DBManager {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager.getLogger(DBManager.class);

	/**
	 * The logger for error messages
	 */
	private static final Logger eLogger = LogManager.getLogger("error");

	/**
	 * The singleton object
	 */
	private static DBManager SINGLETON = null;

	/**
	 * The connection between database
	 */
	private final ConnectionSource connectionSource;

	/**
	 * The url of the database
	 */
	private final String url;

	/**
	 * The private default constructor to use singleton pattern
	 * 
	 * @param url
	 *            the url of the database
	 * @throws SQLException
	 *             If the connection cannot be created
	 */
	private DBManager(final String url) throws SQLException {
		if (!url.startsWith("jdbc")) {
			eLogger.fatal("the specified URL doesn't start with \"jdbc\"");
			throw new IllegalStateException(
					"the URL of database must start with \"jdbc\"");
		}
		this.url = url;
		this.connectionSource = new JdbcConnectionSource(url);
	}

	/**
	 * Set up the instance of DBManager. <br>
	 * This method is valid only at its first call. <br>
	 * After the second or later calls, this method will do nothing and will
	 * just return the instance that has been already initialized. <br>
	 * 
	 * @param url
	 *            the url of the database
	 * @return the instance that initialized by the method call (at the first
	 *         call)<br>
	 *         the instance that has been already initialized (otherwise)
	 * @throws SQLException
	 *             If the connection cannot be created
	 */
	public static DBManager setup(final String url) throws SQLException {
		try {
			if (SINGLETON == null) {
				SINGLETON = new DBManager(url);
				logger.trace("set up the database connection");
			} else {
				assert false; // here shouldn't be reached
				eLogger.warn("the instance of DBManager has been already initialized, so nothing will be done here");
			}
		} catch (SQLException e) {
			eLogger.fatal("cannot initialize the connection between database");
			throw e;
		}

		return SINGLETON;
	}

	/**
	 * Get the instance of DBManager. <br>
	 * The instance will be initialized at the first call of this method,
	 * otherwise the instance that has been already created will be returned.
	 * 
	 * @return the instance of DBManager
	 */
	public static DBManager getInstance() throws IllegalStateException {
		if (SINGLETON == null) {
			eLogger.fatal("the instance of DBManager must be initialized before calling the getInstance method");
			eLogger.fatal("you should call the setup method before calling the getIntance method");
			throw new IllegalStateException(
					"the instance of DBManager has not been initialized");
		}

		return SINGLETON;
	}

	/**
	 * Close the connection
	 * 
	 * @throws SQLException
	 *             If failed to close the connection
	 */
	public void closeConnection() throws SQLException {
		this.connectionSource.close();
		logger.trace("the database connection is closed");
	}

	/**
	 * Get the corresponding DAO for the given data class.
	 * 
	 * @param clazz
	 *            The data class that are of interest
	 * @return The DAO corresponding to the given data class
	 * @throws SQLException
	 *             If connecting to database failed
	 */
	public <D extends Dao<T, ?>, T> D getDao(final Class<T> clazz)
			throws SQLException {
		try {
			final D dao = DaoManager.createDao(connectionSource, clazz);
			logger.trace("get the DAO for " + clazz.getName());
			return dao;
		} catch (SQLException e) {
			eLogger.fatal("cannot get DAO for " + clazz.getName());
			throw e;
		}
	}

	/**
	 * Initialize the table of database for the given data class. <br>
	 * A new table will be created if the corresponding table for the given data
	 * class has not existed. <br>
	 * Then, the table will be cleared so all the data existed in the table will
	 * be disposed. <br>
	 * 
	 * @param clazz
	 *            The data class that are of interest
	 * @throws SQLException
	 *             If failed to create a new table or clear a table
	 */
	public void initializeTable(final Class<?> clazz) throws SQLException {
		try {
			TableUtils.createTableIfNotExists(connectionSource, clazz);
			TableUtils.clearTable(connectionSource, clazz);
		} catch (SQLException e) {
			eLogger.fatal("cannot initialize the table for " + clazz.getName());
			throw e;
		}
	}

}
