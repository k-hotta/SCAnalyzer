package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;

/**
 * The DAO for {@link DBRawClonedFragment}.
 * 
 * @author k-hotta
 * 
 * @see DBRawClonedFragment
 */
public class RawClonedFragmentDao extends AbstractDataDao<DBRawClonedFragment> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(RawClonedFragmentDao.class);

	/**
	 * The DAO for versions. <br>
	 * This is for refreshing.
	 */
	private VersionDao versionDao;

	/**
	 * The DAO for source files. <br>
	 * This is for refreshing.
	 */
	private SourceFileDao sourceFileDao;

	/**
	 * The DAO for raw clone classes. <br>
	 * This is for refreshing.
	 */
	private RawCloneClassDao rawCloneClassDao;

	@SuppressWarnings("unchecked")
	public RawClonedFragmentDao() throws SQLException {
		super((Dao<DBRawClonedFragment, Long>) DBManager.getInstance()
				.getNativeDao(DBRawClonedFragment.class));
		versionDao = null;
		sourceFileDao = null;
		rawCloneClassDao = null;
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

	/**
	 * Set the DAO for RawCloneClass with the specified one
	 * 
	 * @param rawCloneClassDao
	 *            the DAO to be set
	 */
	void setRawCloneClassDao(final RawCloneClassDao rawCloneClassDao) {
		this.rawCloneClassDao = rawCloneClassDao;
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	public DBRawClonedFragment refresh(DBRawClonedFragment element)
			throws SQLException {
		if (deepRefresh) {
			element.setVersion(versionDao.get(element.getVersion().getId()));
			element.setSourceFile(sourceFileDao.get(element.getSourceFile()
					.getId()));
			element.setCloneClass(rawCloneClassDao.get(element.getCloneClass()
					.getId()));
		}

		return element;
	}

	/**
	 * Get the elements whose versions are the specified one.
	 * 
	 * @param version
	 *            version as a query
	 * @return a list of elements whose versions are the specified one
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<DBRawClonedFragment> getWithVersion(final DBVersion version)
			throws SQLException {
		return refreshAll(originalDao.queryForEq(
				DBRawClonedFragment.VERSION_COLUMN_NAME, version));
	}

	/**
	 * Get the elements whose source files are the specified one.
	 * 
	 * @param sourceFile
	 *            source file as a query
	 * @return a list of elements whose source files are the specified one
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<DBRawClonedFragment> getWithSourceFile(
			final DBSourceFile sourceFile) throws SQLException {
		return refreshAll(originalDao.queryForEq(
				DBRawClonedFragment.SOURCE_FILE_COLUMN_NAME, sourceFile));
	}

	/**
	 * Get the elements whose raw clone classes are the specified one.
	 * 
	 * @param rawCloneClass
	 *            raw clone class as a query
	 * @return a list of elements whose raw clone classes are the specified one
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<DBRawClonedFragment> getWithRawCloneClass(
			final DBRawCloneClass rawCloneClass) throws SQLException {
		return refreshAll(originalDao.queryForEq(
				DBRawClonedFragment.CLONE_CLASS_COLUMN_NAME, rawCloneClass));
	}

	/**
	 * Get the elements whose start lines are the specified value.
	 * 
	 * @param startLine
	 *            start line as a query
	 * @return a list of elements whose start lines are the specified value
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<DBRawClonedFragment> getWithStartLine(final int startLine)
			throws SQLException {
		return refreshAll(originalDao.queryForEq(
				DBRawClonedFragment.START_LINE_COLUMN_NAME, startLine));
	}

	/**
	 * Get the elements whose lengths are the specified value.
	 * 
	 * @param length
	 *            length as a query
	 * @return a list of elements whose lengths are the specified value
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<DBRawClonedFragment> getWithLength(final int length)
			throws SQLException {
		return refreshAll(originalDao.queryForEq(
				DBRawClonedFragment.LENGTH_COLUMN_NAME, length));
	}

}
