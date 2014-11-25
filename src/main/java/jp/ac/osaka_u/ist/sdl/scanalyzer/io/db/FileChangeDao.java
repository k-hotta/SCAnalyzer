package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange.Type;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

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
	protected String getTableName() {
		return TableName.FILE_CHANGE;
	}

	@Override
	protected String getIdColumnName() {
		return DBFileChange.ID_COLUMN_NAME;
	}

	@Override
	protected DBFileChange refreshChildren(DBFileChange element)
			throws Exception {
		if (element.getOldSourceFile() != null) {
			sourceFileDao.refresh(element.getOldSourceFile());
		}
		if (element.getNewSourceFile() != null) {
			sourceFileDao.refresh(element.getNewSourceFile());
		}

		if (deepRefresh) {
			versionDao.refresh(element.getVersion());
		}

		return element;
	}

	@Override
	protected Collection<DBFileChange> refreshChildrenForAll(
			Collection<DBFileChange> elements) throws Exception {
		final Set<DBSourceFile> sourceFilesToBeRefreshed = new HashSet<>();
		for (final DBFileChange element : elements) {
			if (element.getOldSourceFile() != null) {
				sourceFilesToBeRefreshed.add(element.getOldSourceFile());
			}
			if (element.getNewSourceFile() != null) {
				sourceFilesToBeRefreshed.add(element.getNewSourceFile());
			}
		}
		sourceFileDao.refreshAll(sourceFilesToBeRefreshed);
		for (final DBFileChange element : elements) {
			if (element.getOldSourceFile() != null) {
				element.setOldSourceFile(sourceFileDao.get(element
						.getOldSourceFile().getId()));
			}
			if (element.getNewSourceFile() != null) {
				element.setNewSourceFile(sourceFileDao.get(element
						.getNewSourceFile().getId()));
			}
		}

		if (deepRefresh) {
			final Set<DBVersion> versionsToBeRefreshed = new HashSet<>();
			for (final DBFileChange element : elements) {
				versionsToBeRefreshed.add(element.getVersion());
			}
			versionDao.refreshAll(versionsToBeRefreshed);
			for (final DBFileChange element : elements) {
				element.setVersion(versionDao.get(element.getVersion().getId()));
			}
		}

		return elements;
	}

	/**
	 * Get the elements whose old source files are the specified one.
	 * 
	 * @param oldSourceFile
	 *            old source file as a query
	 * @return a list of the elements whose old source files are the specified
	 *         one
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Collection<DBFileChange> getWithOldSourceFile(
			final DBSourceFile oldSourceFile) throws Exception {
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
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Collection<DBFileChange> getWithNewSourceFile(
			final DBSourceFile newSourceFile) throws Exception {
		return refreshAll(originalDao.queryForEq(
				DBFileChange.NEW_SOURCE_FILE_COLUMN_NAME, newSourceFile));
	}

	/**
	 * Get the elements whose versions are the specified one
	 * 
	 * @param version
	 *            version as a query
	 * @return a list of the elements whose versions are the specified one
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Collection<DBFileChange> getWithVersion(final DBVersion version)
			throws Exception {
		return refreshAll(originalDao.queryForEq(
				DBFileChange.VERSION_COLUMN_NAME, version));
	}

	/**
	 * Get the elements whose types are the specified one.
	 * 
	 * @param type
	 *            type as a query
	 * @return a list of the elements whose types are the specified one
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Collection<DBFileChange> getWithType(final Type type)
			throws Exception {
		return refreshAll(originalDao.queryForEq(DBFileChange.TYPE_COLUMN_NAME,
				type));
	}

}
