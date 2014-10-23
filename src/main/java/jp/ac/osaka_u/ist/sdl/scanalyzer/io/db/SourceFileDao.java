package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.VersionSourceFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;

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

	@SuppressWarnings("unchecked")
	public SourceFileDao(final int maximumElementsStored) throws SQLException {
		super((Dao<SourceFile, Long>) DBManager.getInstance().getNativeDao(
				SourceFile.class), maximumElementsStored);
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	public SourceFile refresh(SourceFile element) throws SQLException {
		// do nothing because Revision doesn't have any foreign field
		return element;
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

}
