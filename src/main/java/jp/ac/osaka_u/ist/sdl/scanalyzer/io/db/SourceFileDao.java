package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.VersionSourceFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

/**
 * The DAO for {@link SourceFile}.
 * 
 * @author k-hotta
 * 
 * @see SourceFile
 * @see VersionSourceFile
 */
public class SourceFileDao extends AbstractDataDao<SourceFile> {

	/**
	 * The logger
	 */
	private static Logger logger = LogManager.getLogger(SourceFileDao.class);

	/**
	 * The DAO for VersionSourceFile. <br>
	 * This is for retrieving corresponding versions to each source file.
	 */
	private final Dao<VersionSourceFile, Long> versionSourceFileDao;

	/**
	 * The native DAO for Version. <br>
	 * This is for retrieving corresponding versions to each source file.
	 */
	private final Dao<Version, Long> nativeVersionDao;

	/**
	 * The data DAO for Version.
	 */
	private VersionDao versionDataDao;

	/**
	 * The query to get corresponding versions
	 */
	private PreparedQuery<Version> versionsForSourceFileQuery = null;

	@SuppressWarnings("unchecked")
	public SourceFileDao() throws SQLException {
		super((Dao<SourceFile, Long>) DBManager.getInstance().getNativeDao(
				SourceFile.class));
		this.versionSourceFileDao = this.manager
				.getNativeDao(VersionSourceFile.class);
		this.nativeVersionDao = this.manager.getNativeDao(Version.class);
		versionDataDao = null;
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	public SourceFile refresh(SourceFile element) throws SQLException {
		element.setVersions(getCorrespondingVersions(element));
		return element;
	}

	/**
	 * Set the data DAO for Version with the specified one
	 * 
	 * @param versionDataDao
	 *            the data DAO to be set
	 */
	void setVersionDao(final VersionDao versionDataDao) {
		this.versionDataDao = versionDataDao;
	}

	/**
	 * Get the elements whose paths are the specified one.
	 * 
	 * @param path
	 *            path as a query
	 * @return a list of elements whose paths are the specified one
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<SourceFile> getWithPath(final String path) throws SQLException {
		return refreshAll(originalDao.queryForEq(SourceFile.PATH_COLUMN_NAME,
				path));
	}

	/**
	 * Get versions corresponding to the given source file as a list.
	 * 
	 * @param sourceFile
	 *            source file as a query
	 * @return a list of corresponding versions
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<Version> getCorrespondingVersions(final SourceFile sourceFile)
			throws SQLException {
		if (versionsForSourceFileQuery == null) {
			versionsForSourceFileQuery = makeVersionsForSourceFileQuery();
		}
		versionsForSourceFileQuery.setArgumentHolderValue(0, sourceFile);

		return versionDataDao.query(versionsForSourceFileQuery);
	}

	private PreparedQuery<Version> makeVersionsForSourceFileQuery()
			throws SQLException {
		QueryBuilder<VersionSourceFile, Long> versionSourceFileQb = versionSourceFileDao
				.queryBuilder();

		versionSourceFileQb
				.selectColumns(VersionSourceFile.VERSION_COLUMN_NAME);
		SelectArg sourceFileSelectArg = new SelectArg();
		versionSourceFileQb.where().eq(
				VersionSourceFile.SOURCE_FILE_COLUMN_NAME, sourceFileSelectArg);

		QueryBuilder<Version, Long> versionQb = nativeVersionDao.queryBuilder();
		versionQb.where().in(Version.ID_COLUMN_NAME, versionSourceFileQb);

		return versionQb.prepare();
	}
}
