package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
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
	protected Map<Long, DBCodeFragment> queryRaw(String query) throws Exception {
		final GenericRawResults<InternalDBCodeFragment> rawResults = originalDao
				.queryRaw(query, new RowMapper());

		final SortedMap<Long, DBCodeFragment> result = new TreeMap<>();
		final Set<Long> codeFragmentsToBeRetrieved = new TreeSet<>();
		final Set<Long> cloneClassIdsToBeRetrieved = new TreeSet<>();

		for (final InternalDBCodeFragment rawResult : rawResults) {
			final long id = rawResult.getId();
			if (!retrievedElements.containsKey(id)) {
				codeFragmentsToBeRetrieved.add(id);
				cloneClassIdsToBeRetrieved.add(rawResult.getCloneClassId());
			}
		}

		final Map<Long, DBCloneClass> cloneClasses = (deepRefresh) ? cloneClassDao
				.get(cloneClassIdsToBeRetrieved) : new TreeMap<>();
		final Map<Long, DBSegment> relatedSegments = segmentDao
				.getWithCodeFragmentIds(codeFragmentsToBeRetrieved);

		final Map<Long, List<DBSegment>> segmentsByCodeFragmentIds = new TreeMap<>();
		for (final DBSegment relatedSegment : relatedSegments.values()) {
			final long codeFragmentId = relatedSegment.getCodeFragment()
					.getId();
			if (segmentsByCodeFragmentIds.containsKey(codeFragmentId)) {
				segmentsByCodeFragmentIds.get(codeFragmentId).add(
						relatedSegment);
			} else {
				final List<DBSegment> newList = new ArrayList<>();
				newList.add(relatedSegment);
				segmentsByCodeFragmentIds.put(codeFragmentId, newList);
			}
		}

		for (final InternalDBCodeFragment rawResult : rawResults) {
			final long id = rawResult.getId();

			if (!retrievedElements.containsKey(id)) {
				makeNewInstance(cloneClasses, segmentsByCodeFragmentIds,
						rawResult, id);
			}

			result.put(id, retrievedElements.get(id));
		}

		return Collections.unmodifiableSortedMap(result);
	}

	private void makeNewInstance(final Map<Long, DBCloneClass> cloneClasses,
			final Map<Long, List<DBSegment>> segmentsByCodeFragmentIds,
			final InternalDBCodeFragment rawResult, final long id) {
		final DBCodeFragment newInstance = new DBCodeFragment(id, null, null,
				false);

		if (autoRefresh) {
			if (deepRefresh) {
				newInstance.setCloneClass(cloneClasses.get(rawResult
						.getCloneClassId()));
			} else {
				newInstance.setCloneClass(new DBCloneClass(rawResult
						.getCloneClassId(), null, null));
			}

			newInstance.setSegments(new ArrayList<>());
			newInstance.getSegments().addAll(segmentsByCodeFragmentIds.get(id));

			newInstance.setGhost(rawResult.getGhost() == 1);
		}

		retrievedElements.put(id, newInstance);
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

	@Override
	protected RawRowMapper<InternalDBCodeFragment> getRowMapper()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void updateRelativeElementIds(InternalDBCodeFragment rawResult)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void retrieveRelativeElements(
			Map<String, Set<Long>> relativeElementIds) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected DBCodeFragment makeInstance(InternalDBCodeFragment rawResult) {
		// TODO Auto-generated method stub
		return null;
	}

}
