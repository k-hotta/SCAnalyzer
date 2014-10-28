package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;

/**
 * The DAO for {@link CodeFragment}.
 * 
 * @author k-hotta
 * 
 * @see CodeFragment
 */
public class CodeFragmentDao extends AbstractDataDao<CodeFragment> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(CodeFragmentDao.class);

	/**
	 * The DAO for segments. <br>
	 * This is for refreshing.
	 */
	private SegmentDao segmentDao;

	@SuppressWarnings("unchecked")
	public CodeFragmentDao() throws SQLException {
		super((Dao<CodeFragment, Long>) DBManager.getInstance().getNativeDao(
				CodeFragment.class));
		segmentDao = null;
	}

	/**
	 * Set the DAO for Segment with the specified one.
	 * 
	 * @param segmentDao
	 *            the DAO to be set
	 */
	void setSegmentDao(final SegmentDao segmentDao) {
		this.segmentDao = segmentDao;
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	public CodeFragment refresh(CodeFragment element) throws SQLException {
		final Collection<Segment> segments = new ArrayList<Segment>();
		for (final Segment segment : element.getSegments()) {
			segments.add(segmentDao.get(segment.getId()));
		}
		element.setSegments(segments);
		
		return element;
	}

}
