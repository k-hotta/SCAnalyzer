package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;

/**
 * The DAO for {@link Segment}.
 * 
 * @author k-hotta
 * 
 * @see Segment
 */
public class SegmentDao extends AbstractDataDao<Segment> {

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
		super((Dao<Segment, Long>) DBManager.getInstance().getNativeDao(
				Segment.class));
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
	public Segment refresh(Segment element) throws SQLException {
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
	public List<Segment> getWithSourceFile(final SourceFile sourceFile)
			throws SQLException {
		return refreshAll(originalDao.queryForEq(
				Segment.SOURCE_FILE_COLUMN_NAME, sourceFile));
	}

}
