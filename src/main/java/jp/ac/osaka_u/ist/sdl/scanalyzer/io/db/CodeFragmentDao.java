package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;

/**
 * The DAO for {@link DBCodeFragment}.
 * 
 * @author k-hotta
 * 
 * @see DBCodeFragment
 */
public class CodeFragmentDao extends AbstractDataDao<DBCodeFragment> {

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

	/**
	 * The DAO for clone classes. <br>
	 * This is for refreshing.
	 */
	private CloneClassDao cloneClassDao;

	@SuppressWarnings("unchecked")
	public CodeFragmentDao() throws SQLException {
		super((Dao<DBCodeFragment, Long>) DBManager.getInstance().getNativeDao(
				DBCodeFragment.class));
		segmentDao = null;
		cloneClassDao = null;
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

	/**
	 * Set the DAO for CloneClass with the specified one.
	 * 
	 * @param cloneClassDao
	 *            the DAO to be set
	 */
	void setCloneClassDao(final CloneClassDao cloneClassDao) {
		this.cloneClassDao = cloneClassDao;
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	protected DBCodeFragment refreshChildren(DBCodeFragment element)
			throws Exception {
		segmentDao.refreshAll(element.getSegments());

		if (deepRefresh) {
			cloneClassDao.refresh(element.getCloneClass());
		}

		return element;
	}

	@Override
	protected Collection<DBCodeFragment> refreshChildrenForAll(
			Collection<DBCodeFragment> elements) throws Exception {
		final Set<DBSegment> segmentsToBeRefreshed = new HashSet<>();
		for (final DBCodeFragment element : elements) {
			segmentsToBeRefreshed.addAll(element.getSegments());
		}
		segmentDao.refreshAll(segmentsToBeRefreshed);
		for (final DBCodeFragment element : elements) {
			final List<DBSegment> toBeStored = new ArrayList<>();
			for (final DBSegment segment : element.getSegments()) {
				toBeStored.add(segmentDao.get(segment.getId()));
			}
			element.setSegments(toBeStored);
		}

		if (deepRefresh) {
			final Set<DBCloneClass> cloneClassesToBeRefreshed = new HashSet<>();
			for (final DBCodeFragment element : elements) {
				cloneClassesToBeRefreshed.add(element.getCloneClass());
			}
			cloneClassDao.refreshAll(cloneClassesToBeRefreshed);
			for (final DBCodeFragment element : elements) {
				element.setCloneClass(cloneClassDao.get(element.getCloneClass()
						.getId()));
			}
		}

		return elements;
	}

}
