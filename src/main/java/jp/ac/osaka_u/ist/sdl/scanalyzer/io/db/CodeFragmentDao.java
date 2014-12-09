package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RawRowMapper;

/**
 * The DAO for {@link DBCodeFragment}.
 * 
 * @author k-hotta
 * 
 * @see DBCodeFragment
 */
public class CodeFragmentDao extends
		AbstractDataDao<DBCodeFragment, CodeFragmentDao.InternalDBCodeFragment> {

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
	protected String getTableName() {
		return TableName.CODE_FRAGMENT;
	}

	@Override
	protected String getIdColumnName() {
		return DBCodeFragment.ID_COLUMN_NAME;
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

	public Map<Long, DBCodeFragment> getWithCloneClassIds(
			final Collection<Long> ids) throws Exception {
		final String query = QueryHelper.querySelectIdIn(getTableName(),
				DBCodeFragment.CLONE_CLASS_COLUMN_NAME, ids);

		return queryRaw(query);
	}

	@Override
	protected RawRowMapper<InternalDBCodeFragment> getRowMapper()
			throws Exception {
		return new RowMapper();
	}

	@Override
	protected void initializeRelativeElementIds(
			Map<String, Set<Long>> relativeElementIds) {
		relativeElementIds.put(TableName.CODE_FRAGMENT, new TreeSet<Long>());
		relativeElementIds.put(TableName.CLONE_CLASS, new TreeSet<Long>());
	}

	@Override
	protected void initializeForeignChildElementIds(
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds) {
		foreignChildElementIds.put(TableName.SEGMENT,
				new TreeMap<Long, Set<Long>>());
	}

	@Override
	protected void updateRelativeElementIds(
			Map<String, Set<Long>> relativeElementIds,
			InternalDBCodeFragment rawResult) throws Exception {
		relativeElementIds.get(TableName.CODE_FRAGMENT).add(rawResult.getId());
		relativeElementIds.get(TableName.CLONE_CLASS).add(
				rawResult.getCloneClassId());
	}

	@Override
	protected void retrieveRelativeElements(
			Map<String, Set<Long>> relativeElementIds,
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds)
			throws Exception {
		// retrieve segments
		final Set<Long> codeFragmentIdsToBeRetrieved = relativeElementIds
				.get(TableName.CODE_FRAGMENT);
		final Map<Long, DBSegment> segments = segmentDao
				.getWithCodeFragmentIds(codeFragmentIdsToBeRetrieved);
		final Map<Long, Set<Long>> segmentsByCodeFragmentId = foreignChildElementIds
				.get(TableName.SEGMENT);

		for (final DBSegment segment : segments.values()) {
			final long codeFragmentId = segment.getCodeFragment().getId();
			Set<Long> segmentIdsInCodeFragment = segmentsByCodeFragmentId
					.get(codeFragmentId);

			if (segmentIdsInCodeFragment == null) {
				segmentIdsInCodeFragment = new TreeSet<Long>();
				segmentsByCodeFragmentId.put(codeFragmentId,
						segmentIdsInCodeFragment);
			}

			segmentIdsInCodeFragment.add(segment.getId());
		}

		// retrieve clone classes if deep refreshing is ON
		if (deepRefresh) {
			final Set<Long> cloneClassIdsToBeRetrieved = relativeElementIds
					.get(TableName.CLONE_CLASS);
			cloneClassDao.get(cloneClassIdsToBeRetrieved);
		}
	}

	@Override
	protected DBCodeFragment makeInstance(InternalDBCodeFragment rawResult,
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds)
			throws Exception {
		final DBCodeFragment newInstance = new DBCodeFragment(
				rawResult.getId(), null, null, rawResult.getGhost().equals(1));

		if (autoRefresh) {
			if (deepRefresh) {
				newInstance.setCloneClass(cloneClassDao.get(rawResult
						.getCloneClassId()));
			} else {
				newInstance.setCloneClass(new DBCloneClass(rawResult
						.getCloneClassId(), null, null));
			}

			final Map<Long, Set<Long>> segmentIdsByCodeFragmentIds = foreignChildElementIds
					.get(TableName.SEGMENT);
			final Set<Long> segmentIdsInCodeFragment = segmentIdsByCodeFragmentIds
					.get(rawResult.getId());

			newInstance.setSegments(new ArrayList<>());

			if (segmentIdsInCodeFragment != null
					&& !segmentIdsInCodeFragment.isEmpty()) {
				final Collection<DBSegment> segments = segmentDao.get(
						segmentIdsInCodeFragment).values();
				newInstance.getSegments().addAll(segments);
			}
		}

		return newInstance;
	}

	class InternalDBCodeFragment implements
			InternalDataRepresentation<DBCodeFragment> {

		private final Long id;

		private final Long cloneClassId;

		private final Integer ghost;

		private InternalDBCodeFragment(final Long id, final Long cloneClassId,
				final Integer ghost) {
			this.id = id;
			this.cloneClassId = cloneClassId;
			this.ghost = ghost;
		}

		public final Long getId() {
			return id;
		}

		public final Long getCloneClassId() {
			return cloneClassId;
		}

		public final Integer getGhost() {
			return ghost;
		}

	}

	class RowMapper implements RawRowMapper<InternalDBCodeFragment> {

		@Override
		public InternalDBCodeFragment mapRow(String[] columnNames,
				String[] resultColumns) throws SQLException {
			Long id = null;
			Long cloneClassId = null;
			Integer ghost = null;

			for (int i = 0; i < columnNames.length; i++) {
				final String columnName = columnNames[i];

				switch (columnName) {
				case DBCodeFragment.ID_COLUMN_NAME:
					id = Long.parseLong(resultColumns[i]);
					break;
				case DBCodeFragment.CLONE_CLASS_COLUMN_NAME:
					cloneClassId = Long.parseLong(resultColumns[i]);
					break;
				case DBCodeFragment.GHOST_NAME:
					ghost = Integer.parseInt(resultColumns[i]);
					break;
				}
			}

			return new InternalDBCodeFragment(id, cloneClassId, ghost);
		}

	}

}
