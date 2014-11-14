package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange.Type;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;

/**
 * The DAO for {@link DBFileChange}.
 * 
 * @author k-hotta
 * 
 * @see DBFileChange
 */
public class FileChangeDao extends AbstractDataDao<DBFileChange> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(FileChangeDao.class);

	/**
	 * The DAO for source files. <br>
	 * This is for refreshing.
	 */
	private SourceFileDao sourceFileDao;

	/**
	 * The DAO for versions. <br>
	 * This is for refreshing.
	 */
	private VersionDao versionDao;

	@SuppressWarnings("unchecked")
	public FileChangeDao() throws SQLException {
		super((Dao<DBFileChange, Long>) DBManager.getInstance().getNativeDao(
				DBFileChange.class));
		this.sourceFileDao = null;
		this.versionDao = null;
	}

	/**
	 * Set the DAO for SourceFile with the specified one
	 * 
	 * @param sourceFileDao
	 *            the DAO to be set
	 */
	void setSourceFileDao(final SourceFileDao sourceFileDao) {
		this.sourceFileDao = sourceFileDao;
	}

	/**
	 * Set the DAO for Version with the specified one
	 * 
	 * @param versionDao
	 *            the DAO to be set
	 */
	void setVersionDao(final VersionDao versionDao) {
		this.versionDao = versionDao;
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	public DBFileChange refresh(DBFileChange element) throws SQLException {
		if (element.getOldSourceFile() != null) {
			element.setOldSourceFile(sourceFileDao.get(element
					.getOldSourceFile().getId()));
		}
		if (element.getNewSourceFile() != null) {
			element.setNewSourceFile(sourceFileDao.get(element
					.getNewSourceFile().getId()));
		}

		if (deepRefresh) {
			element.setVersion(versionDao.get(element.getVersion().getId()));
		}

		return element;
	}

	/**
	 * Get the elements whose old source files are the specified one.
	 * 
	 * @param oldSourceFile
	 *            old source file as a query
	 * @return a list of the elements whose old source files are the specified
	 *         one
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<DBFileChange> getWithOldSourceFile(
			final DBSourceFile oldSourceFile) throws SQLException {
		return refreshAll(originalDao.queryForEq(
				DBFileChange.OLD_SOURCE_FILE_COLUMN_NAME, oldSourceFile));
	}

	/**
	 * Get the elements whose new source files are the specified one.
	 * 
	 * @param newSourceFile
	 *            new source file as a query
	 * @return a list of the elements whose new source files are the specified
	 *         one
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<DBFileChange> getWithNewSourceFile(
			final DBSourceFile newSourceFile) throws SQLException {
		return refreshAll(originalDao.queryForEq(
				DBFileChange.NEW_SOURCE_FILE_COLUMN_NAME, newSourceFile));
	}

	/**
	 * Get the elements whose versions are the specified one
	 * 
	 * @param version
	 *            version as a query
	 * @return a list of the elements whose versions are the specified one
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<DBFileChange> getWithVersion(final DBVersion version)
			throws SQLException {
		return refreshAll(originalDao.queryForEq(
				DBFileChange.VERSION_COLUMN_NAME, version));
	}

	/**
	 * Get the elements whose types are the specified one.
	 * 
	 * @param type
	 *            type as a query
	 * @return a list of the elements whose types are the specified one
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<DBFileChange> getWithType(final Type type) throws SQLException {
		return refreshAll(originalDao.queryForEq(DBFileChange.TYPE_COLUMN_NAME,
				type));
	}

}
