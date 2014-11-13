package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersionSourceFile;

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
	 * The DAO for FileChange
	 */
	private FileChangeDao fileChangeDao;

	/**
	 * The DAO for RawCloneClass
	 */
	private RawCloneClassDao rawCloneClassDao;

	/**
	 * The DAO for RawClonedFragment
	 */
	private RawClonedFragmentDao rawClonedFragmentDao;

	/**
	 * The DAO for Segment
	 */
	private SegmentDao segmentDao;

	/**
	 * The DAO for CodeFragment
	 */
	private CodeFragmentDao codeFragmentDao;

	/**
	 * The DAO for CloneClass
	 */
	private CloneClassDao cloneClassDao;

	/**
	 * The DAO for Revision
	 */
	private RevisionDao revisionDao;

	/**
	 * The DAO for SourceFile
	 */
	private SourceFileDao sourceFileDao;

	/**
	 * The DAO for Version
	 */
	private VersionDao versionDao;

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
				setupDaos(SINGLETON);
				logger.trace("set up the database connection");
			} else {
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
	 * Set up all the DAOs.
	 * 
	 * @param instance
	 *            the instance for which DAOs required to be set up
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	private static void setupDaos(final DBManager instance) throws SQLException {
		final FileChangeDao fileChangeDao = new FileChangeDao();
		final RawCloneClassDao rawCloneClassDao = new RawCloneClassDao();
		final RawClonedFragmentDao rawClonedFragmentDao = new RawClonedFragmentDao();
		final SegmentDao segmentDao = new SegmentDao();
		final CodeFragmentDao codeFragmentDao = new CodeFragmentDao();
		final CloneClassDao cloneClassDao = new CloneClassDao();
		final RevisionDao revisionDao = new RevisionDao();
		final SourceFileDao sourceFileDao = new SourceFileDao();
		final VersionDao versionDao = new VersionDao();

		fileChangeDao.setSourceFileDao(sourceFileDao);
		fileChangeDao.setVersionDao(versionDao);

		rawCloneClassDao.setRawClonedFragmentDao(rawClonedFragmentDao);
		rawCloneClassDao.setVersionDao(versionDao);

		rawClonedFragmentDao.setRawCloneClassDao(rawCloneClassDao);
		rawClonedFragmentDao.setSourceFileDao(sourceFileDao);
		rawClonedFragmentDao.setVersionDao(versionDao);

		segmentDao.setSourceFileDao(sourceFileDao);

		codeFragmentDao.setSegmentDao(segmentDao);
		codeFragmentDao.setCloneClassDao(cloneClassDao);

		cloneClassDao.setCodeFragmentDao(codeFragmentDao);
		cloneClassDao.setVersionDao(versionDao);

		versionDao.setRevidionDao(revisionDao);
		versionDao.setFileChangeDao(fileChangeDao);
		versionDao.setRawCloneClassDao(rawCloneClassDao);
		versionDao.setCloneClassDao(cloneClassDao);
		versionDao.setSourceFileDao(sourceFileDao);

		instance.setFileChangeDao(fileChangeDao);
		instance.setRawCloneClassDao(rawCloneClassDao);
		instance.setRawClonedFragmentDao(rawClonedFragmentDao);
		instance.setSegmentDao(segmentDao);
		instance.setCodeFragmentDao(codeFragmentDao);
		instance.setCloneClassDao(cloneClassDao);
		instance.setRevisionDao(revisionDao);
		instance.setSourceFileDao(sourceFileDao);
		instance.setVersionDao(versionDao);
	}

	private void setFileChangeDao(final FileChangeDao fileChangeDao) {
		this.fileChangeDao = fileChangeDao;
	}

	private void setRawCloneClassDao(final RawCloneClassDao rawCloneClassDao) {
		this.rawCloneClassDao = rawCloneClassDao;
	}

	private void setRawClonedFragmentDao(
			final RawClonedFragmentDao rawClonedFragmentDao) {
		this.rawClonedFragmentDao = rawClonedFragmentDao;
	}

	private void setSegmentDao(final SegmentDao segmentDao) {
		this.segmentDao = segmentDao;
	}

	private void setCodeFragmentDao(final CodeFragmentDao codeFragmentDao) {
		this.codeFragmentDao = codeFragmentDao;
	}

	private void setCloneClassDao(final CloneClassDao cloneClassDao) {
		this.cloneClassDao = cloneClassDao;
	}

	private void setRevisionDao(final RevisionDao revisionDao) {
		this.revisionDao = revisionDao;
	}

	private void setSourceFileDao(final SourceFileDao sourceFileDao) {
		this.sourceFileDao = sourceFileDao;
	}

	private void setVersionDao(final VersionDao versionDao) {
		this.versionDao = versionDao;
	}

	/**
	 * Clear all the DAOs.
	 */
	public final void clearDaos() {
		fileChangeDao.clear();
		rawCloneClassDao.clear();
		rawClonedFragmentDao.clear();
		segmentDao.clear();
		codeFragmentDao.clear();
		cloneClassDao.clear();
		revisionDao.clear();
		sourceFileDao.clear();
		versionDao.clear();
	}

	/**
	 * Get the DAO for
	 * {@link jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange}.
	 * 
	 * @return the DAO for
	 *         {@link jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange}
	 */
	public final FileChangeDao getFileChangeDao() {
		return fileChangeDao;
	}

	/**
	 * Get the DAO for
	 * {@link jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawCloneClass}.
	 * 
	 * @return the DAO for
	 *         {@link jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawCloneClass}
	 */
	public final RawCloneClassDao getRawCloneClassDao() {
		return rawCloneClassDao;
	}

	/**
	 * Get the DAO for
	 * {@link jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawClonedFragment}.
	 * 
	 * @return the DAO for
	 *         {@link jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawClonedFragment}
	 */
	public final RawClonedFragmentDao getRawClonedFragmentDao() {
		return rawClonedFragmentDao;
	}

	/**
	 * Get the DAO for
	 * {@link jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment}.
	 * 
	 * @return the DAO for
	 *         {@link jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment}
	 */
	public final SegmentDao getSegmentDao() {
		return segmentDao;
	}

	/**
	 * Get the DAO for
	 * {@link jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment}.
	 * 
	 * @return the DAO for
	 *         {@link jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment}
	 */
	public final CodeFragmentDao getCodeFragmentDao() {
		return codeFragmentDao;
	}

	/**
	 * Get the DAO for
	 * {@link jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass}.
	 * 
	 * @return the DAO for
	 *         {@link jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass}
	 */
	public final CloneClassDao getCloneClassDao() {
		return cloneClassDao;
	}

	/**
	 * Get the DAO for
	 * {@link jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision}.
	 * 
	 * @return the DAO for
	 *         {@link jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision}
	 */
	public final RevisionDao getRevisionDao() {
		return revisionDao;
	}

	/**
	 * Get the DAO for
	 * {@link jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile}.
	 * 
	 * @return the DAO for
	 *         {@link jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile}
	 */
	public final SourceFileDao getSourceFileDao() {
		return sourceFileDao;
	}

	/**
	 * Get the DAO for
	 * {@link jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion}.
	 * 
	 * @return the DAO for
	 *         {@link jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion}
	 */
	public final VersionDao getVersionDao() {
		return versionDao;
	}

	/**
	 * Close the connection
	 * 
	 * @throws SQLException
	 *             If failed to close the connection
	 */
	public static void closeConnection() throws SQLException {
		if (SINGLETON != null) {
			SINGLETON.connectionSource.close();
			logger.trace("the database connection is closed");
		}

	}

	/**
	 * Get the corresponding native DAO for the given data class. <br>
	 * Here, a native DAO refers to the one provided by ORMLite.
	 * 
	 * @param clazz
	 *            The data class that are of interest
	 * @return The native DAO corresponding to the given data class
	 * @throws SQLException
	 *             If connecting to database failed
	 */
	public <D extends Dao<T, ?>, T> D getNativeDao(final Class<T> clazz)
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

	/**
	 * Initialize all the database tables. For each data class, this method
	 * delegates table creation procedure to
	 * {@link DBManager#initializeTable(Class)}.
	 * 
	 * @throws SQLException
	 *             If failed to create any of new tables or clear it.
	 */
	public void initializeAllTables() throws SQLException {
		initializeTable(DBVersion.class);
		initializeTable(DBRevision.class);
		initializeTable(DBSourceFile.class);
		initializeTable(DBFileChange.class);
		initializeTable(DBVersionSourceFile.class);
		initializeTable(DBRawCloneClass.class);
		initializeTable(DBRawClonedFragment.class);
		initializeTable(DBCloneClass.class);
		initializeTable(DBCodeFragment.class);
		initializeTable(DBSegment.class);
		initializeTable(DBCloneClassMapping.class);
		initializeTable(DBCodeFragmentMapping.class);
	}

	/**
	 * Get the URL of the database under managed.
	 * 
	 * @return the URL of the database
	 */
	public final String getUrl() {
		return url;
	}

}
