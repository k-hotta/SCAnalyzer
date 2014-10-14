package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;

/**
 * The DAO for {@link RawClonedFragment}.
 * 
 * @author k-hotta
 * 
 * @see RawClonedFragment
 */
public class RawClonedFragmentDao extends AbstractDataDao<RawClonedFragment> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(RawClonedFragmentDao.class);

	/**
	 * The DAO for versions. <br>
	 * This is for refreshing.
	 */
	private final Dao<Version, Long> versionDao;

	/**
	 * The DAO for source files. <br>
	 * This is for refreshing.
	 */
	private final Dao<SourceFile, Long> sourceFileDao;

	/**
	 * The DAO for raw clone classes. <br>
	 * This is for refreshing.
	 */
	private final Dao<RawCloneClass, Long> rawCloneClassDao;

	@SuppressWarnings("unchecked")
	public RawClonedFragmentDao() throws SQLException {
		super((Dao<RawClonedFragment, Long>) DBManager.getInstance()
				.getNativeDao(RawClonedFragment.class));
		versionDao = this.manager.getNativeDao(Version.class);
		sourceFileDao = this.manager.getNativeDao(SourceFile.class);
		rawCloneClassDao = this.manager.getNativeDao(RawCloneClass.class);
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	public RawClonedFragment refresh(RawClonedFragment element)
			throws SQLException {
		versionDao.refresh(element.getVersion());
		sourceFileDao.refresh(element.getSourceFile());
		rawCloneClassDao.refresh(element.getCloneClass());

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
	public List<RawClonedFragment> getWithVersion(final Version version)
			throws SQLException {
		return refreshAll(originalDao.queryForEq(
				RawClonedFragment.VERSION_COLUMN_NAME, version));
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
	public List<RawClonedFragment> getWithSourceFile(final SourceFile sourceFile)
			throws SQLException {
		return refreshAll(originalDao.queryForEq(
				RawClonedFragment.SOURCE_FILE_COLUMN_NAME, sourceFile));
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
	public List<RawClonedFragment> getWithRawCloneClass(
			final RawCloneClass rawCloneClass) throws SQLException {
		return refreshAll(originalDao.queryForEq(
				RawClonedFragment.CLONE_CLASS_COLUMN_NAME, rawCloneClass));
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
	public List<RawClonedFragment> getWithStartLine(final int startLine)
			throws SQLException {
		return refreshAll(originalDao.queryForEq(
				RawClonedFragment.START_LINE_COLUMN_NAME, startLine));
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
	public List<RawClonedFragment> getWithLength(final int length)
			throws SQLException {
		return refreshAll(originalDao.queryForEq(
				RawClonedFragment.LENGTH_COLUMN_NAME, length));
	}

}
