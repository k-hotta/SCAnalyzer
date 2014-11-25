package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersionSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

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
	protected String getTableName() {
		return TableName.SOURCE_FILE;
	}

	@Override
	protected String getIdColumnName() {
		return DBSourceFile.ID_COLUMN_NAME;
	}
	
	@Override
	protected DBSourceFile refreshChildren(DBSourceFile element)
			throws Exception {
		// do nothing because Revision doesn't have any foreign field
		return element;
	}

	@Override
	protected Collection<DBSourceFile> refreshChildrenForAll(
			Collection<DBSourceFile> elements) throws Exception {
		// do nothing because Revision doesn't have any foreign field
		return elements;
	}

	/**
	 * Get the elements whose paths are the specified one.
	 * 
	 * @param path
	 *            path as a query
	 * @return a list of elements whose paths are the specified one
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Collection<DBSourceFile> getWithPath(final String path)
			throws Exception {
		return refreshAll(originalDao.queryForEq(DBSourceFile.PATH_COLUMN_NAME,
				path));
	}

}
