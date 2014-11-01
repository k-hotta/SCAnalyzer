package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBVersionSourceFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;

/**
 * The DAO for {@link DBSourceFile}.
 * 
 * @author k-hotta
 * 
 * @see DBSourceFile
 * @see DBVersionSourceFile
 */
public class SourceFileDao extends AbstractDataDao<DBSourceFile> {

	/**
	 * The logger
	 */
	private static Logger logger = LogManager.getLogger(SourceFileDao.class);

	@SuppressWarnings("unchecked")
	public SourceFileDao() throws SQLException {
		super((Dao<DBSourceFile, Long>) DBManager.getInstance().getNativeDao(
				DBSourceFile.class));
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	public DBSourceFile refresh(DBSourceFile element) throws SQLException {
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
	public List<DBSourceFile> getWithPath(final String path) throws SQLException {
		return refreshAll(originalDao.queryForEq(DBSourceFile.PATH_COLUMN_NAME,
				path));
	}

}
