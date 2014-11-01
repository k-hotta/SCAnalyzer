package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBSegment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBSourceFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;

/**
 * The DAO for {@link DBSegment}.
 * 
 * @author k-hotta
 * 
 * @see DBSegment
 */
public class SegmentDao extends AbstractDataDao<DBSegment> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager.getLogger(SegmentDao.class);

	/**
	 * The DAO for source files. <br>
	 * This is for refreshing.
	 */
	private SourceFileDao sourceFileDao;

	@SuppressWarnings("unchecked")
	public SegmentDao() throws SQLException {
		super((Dao<DBSegment, Long>) DBManager.getInstance().getNativeDao(
				DBSegment.class));
		this.sourceFileDao = null;
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

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	public DBSegment refresh(DBSegment element) throws SQLException {
		element.setSourceFile(sourceFileDao
				.get(element.getSourceFile().getId()));
		return element;
	}

	/**
	 * Get the elements whose source files are the specified one.
	 * 
	 * @param sourceFile
	 *            source file as a key
	 * @return a list of the elements whose source files are the specified one
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<DBSegment> getWithSourceFile(final DBSourceFile sourceFile)
			throws SQLException {
		return refreshAll(originalDao.queryForEq(
				DBSegment.SOURCE_FILE_COLUMN_NAME, sourceFile));
	}

}
