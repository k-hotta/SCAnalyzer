package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange.Type;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;

/**
 * The DAO for {@link FileChange}.
 * 
 * @author k-hotta
 * 
 * @see FileChange
 */
public class FileChangeDao extends AbstractDataDao<FileChange> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(FileChangeDao.class);

	/**
	 * The DAO for source files. <br>
	 * This is for refreshing.
	 */
	private final Dao<SourceFile, Long> sourceFileDao;

	/**
	 * The DAO for versions. <br>
	 * This is for refreshing.
	 */
	private final Dao<Version, Long> versionDao;

	@SuppressWarnings("unchecked")
	public FileChangeDao() throws SQLException {
		super((Dao<FileChange, Long>) DBManager.getInstance().getNativeDao(
				FileChange.class));
		this.sourceFileDao = this.manager.getNativeDao(SourceFile.class);
		this.versionDao = this.manager.getNativeDao(Version.class);
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	public FileChange refresh(FileChange element) throws SQLException {
		if (element != null) {
			sourceFileDao.refresh(element.getOldSourceFile());
			sourceFileDao.refresh(element.getNewSourceFile());
			versionDao.refresh(element.getVersion());
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
	public List<FileChange> getWithOldSourceFile(final SourceFile oldSourceFile)
			throws SQLException {
		return refreshAll(originalDao.queryForEq(
				FileChange.OLD_SOURCE_FILE_COLUMN_NAME, oldSourceFile));
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
	public List<FileChange> getWithNewSourceFile(final SourceFile newSourceFile)
			throws SQLException {
		return refreshAll(originalDao.queryForEq(
				FileChange.NEW_SOURCE_FILE_COLUMN_NAME, newSourceFile));
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
	public List<FileChange> getWithVersion(final Version version)
			throws SQLException {
		return refreshAll(originalDao.queryForEq(
				FileChange.VERSION_COLUMN_NAME, version));
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
	public List<FileChange> getWithType(final Type type) throws SQLException {
		return refreshAll(originalDao.queryForEq(FileChange.TYPE_COLUMN_NAME,
				type));
	}

}
