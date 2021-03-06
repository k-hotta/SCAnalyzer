package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneModification;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RawRowMapper;

/**
 * The DAO for {@link DBCloneModification}.
 * 
 * @author k-hotta
 *
 */
public class CloneModificationDao
		extends
		AbstractDataDao<DBCloneModification, CloneModificationDao.InternalDBCloneModification> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(CloneModificationDao.class);

	/**
	 * The DAO for code fragment mappings
	 */
	private CodeFragmentMappingDao codeFragmentMappingDao;

	/**
	 * The DAO for clone class mappings
	 */
	private CloneClassMappingDao cloneClassMappingDao;

	/**
	 * The DAO for segments
	 */
	private SegmentDao segmentDao;

	@SuppressWarnings("unchecked")
	public CloneModificationDao() throws SQLException {
		super((Dao<DBCloneModification, Long>) DBManager.getInstance()
				.getNativeDao(DBCloneModification.class));
		codeFragmentMappingDao = null;
		cloneClassMappingDao = null;
		segmentDao = null;
	}

	/**
	 * Set the DAO for code fragment mappings
	 * 
	 * @param codeFragmentMappingDao
	 *            the DAO to be set
	 */
	void setCodeFragmentMappingDao(
			final CodeFragmentMappingDao codeFragmentMappingDao) {
		this.codeFragmentMappingDao = codeFragmentMappingDao;
	}

	/**
	 * Set the DAO for clone class mappings
	 * 
	 * @param cloneClassMappingDao
	 *            the DAO to be set
	 */
	void setCloneClassMappingDao(final CloneClassMappingDao cloneClassMappingDao) {
		this.cloneClassMappingDao = cloneClassMappingDao;
	}

	/**
	 * Set the DAO for segments
	 * 
	 * @param segmentDao
	 *            the DAO to be set
	 */
	void setSegmentDao(final SegmentDao segmentDao) {
		this.segmentDao = segmentDao;
	}

	/**
	 * Get the elements whose owner code fragment mappings are the specified
	 * ones.
	 * 
	 * @param ids
	 *            a collection contains IDs of interest
	 * 
	 * @return a map between ID of an element and the instance itself
	 * 
	 * @throws Exception
	 */
	public Map<Long, DBCloneModification> getWithCodeFragmentMappingIds(
			final Collection<Long> ids) throws Exception {
		final String query = QueryHelper.querySelectIdIn(getTableName(),
				DBCloneModification.CODE_FRAGMENT_MAPPING_COLUMN_NAME, ids);

		return queryRaw(query);
	}

	/**
	 * Get the elements whose owner clone class mappings are the specified ones.
	 * 
	 * @param ids
	 *            a collection contains IDS of interest
	 * 
	 * @return a map between ID of an element and the instance itself
	 * 
	 * @throws Exception
	 */
	public Map<Long, DBCloneModification> getWithCloneClassMappingIds(
			final Collection<Long> ids) throws Exception {
		final String query = QueryHelper.querySelectIdIn(getTableName(),
				DBCloneModification.CLONE_CLASS_MAPPING_COLUMN_NAME, ids);

		return queryRaw(query);
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	protected String getTableName() {
		return TableName.CLONE_MODIFICATION;
	}

	@Override
	protected String getIdColumnName() {
		return DBCloneModification.ID_COLUMN_NAME;
	}

	@Override
	protected RawRowMapper<InternalDBCloneModification> getRowMapper()
			throws Exception {
		return new RowMapper();
	}

	@Override
	protected DBCloneModification refreshChildren(DBCloneModification element)
			throws Exception {
		if (element.getRelatedOldSegment() != null) {
			segmentDao.refresh(element.getRelatedOldSegment());
		}

		if (element.getRelatedNewSegment() != null) {
			segmentDao.refresh(element.getRelatedNewSegment());
		}

		if (deepRefresh) {
			codeFragmentMappingDao.refresh(element.getCodeFragmentMapping());
			cloneClassMappingDao.refresh(element.getCloneClassMapping());
		}

		return element;
	}

	@Override
	protected Collection<DBCloneModification> refreshChildrenForAll(
			Collection<DBCloneModification> elements) throws Exception {
		final Set<DBCodeFragmentMapping> fragmentMappingsToBeRefreshed = new HashSet<>();
		final Set<DBCloneClassMapping> cloneClassMappingsToBeRefreshed = new HashSet<>();
		final Set<DBSegment> segmentsToBeRefreshed = new HashSet<>();

		for (final DBCloneModification element : elements) {
			cloneClassMappingsToBeRefreshed.add(element.getCloneClassMapping());

			if (element.getCodeFragmentMapping() != null) {
				fragmentMappingsToBeRefreshed.add(element
						.getCodeFragmentMapping());
			}

			if (element.getRelatedOldSegment() != null) {
				segmentsToBeRefreshed.add(element.getRelatedOldSegment());
			}
			if (element.getRelatedNewSegment() != null) {
				segmentsToBeRefreshed.add(element.getRelatedNewSegment());
			}
		}

		if (deepRefresh) {
			cloneClassMappingDao.refreshAll(cloneClassMappingsToBeRefreshed);
			codeFragmentMappingDao.refreshAll(fragmentMappingsToBeRefreshed);
		}
		segmentDao.refreshAll(segmentsToBeRefreshed);

		for (final DBCloneModification element : elements) {
			if (deepRefresh) {
				element.setCloneClassMapping(cloneClassMappingDao.get(element
						.getCloneClassMapping().getId()));

				if (element.getCodeFragmentMapping() != null) {
					element.setCodeFragmentMapping(codeFragmentMappingDao
							.get(element.getCodeFragmentMapping().getId()));
				}
			}

			if (element.getRelatedOldSegment() != null) {
				element.setRelatedOldSegment(segmentDao.get(element
						.getRelatedOldSegment().getId()));
			}

			if (element.getRelatedNewSegment() != null) {
				element.setRelatedNewSegment(segmentDao.get(element
						.getRelatedNewSegment().getId()));
			}
		}

		return elements;
	}

	@Override
	protected void initializeRelativeElementIds(
			Map<String, Set<Long>> relativeElementIds) {
		relativeElementIds.put(TableName.CLONE_CLASS_MAPPING,
				new TreeSet<Long>());
		relativeElementIds.put(TableName.CODE_FRAGMENT_MAPPING,
				new TreeSet<Long>());
		relativeElementIds.put(TableName.SEGMENT, new TreeSet<Long>());
	}

	@Override
	protected void initializeForeignChildElementIds(
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds) {
		// do nothing
	}

	@Override
	protected void updateRelativeElementIds(
			Map<String, Set<Long>> relativeElementIds,
			InternalDBCloneModification rawResult) throws Exception {
		relativeElementIds.get(TableName.CLONE_CLASS_MAPPING).add(
				rawResult.getCloneClassMappingId());

		if (rawResult.getCodeFragmentMappingId() != null) {
			relativeElementIds.get(TableName.CODE_FRAGMENT_MAPPING).add(
					rawResult.getCodeFragmentMappingId());
		}

		if (rawResult.getRelatedOldSegmentId() != null) {
			relativeElementIds.get(TableName.SEGMENT).add(
					rawResult.getRelatedOldSegmentId());
		}

		if (rawResult.getRelatedNewSegmentId() != null) {
			relativeElementIds.get(TableName.SEGMENT).add(
					rawResult.getRelatedNewSegmentId());
		}
	}

	@Override
	protected void retrieveRelativeElements(
			Map<String, Set<Long>> relativeElementIds,
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds)
			throws Exception {
		if (deepRefresh) {
			final Set<Long> codeFragmentMappingsToBeRetrieved = relativeElementIds
					.get(TableName.CODE_FRAGMENT_MAPPING);
			codeFragmentMappingDao.get(codeFragmentMappingsToBeRetrieved);

			final Set<Long> cloneClassMappingsToBeRetrieved = relativeElementIds
					.get(TableName.CLONE_CLASS_MAPPING);
			cloneClassMappingDao.get(cloneClassMappingsToBeRetrieved);
		}

		final Set<Long> segmentsToBeRetrieved = relativeElementIds
				.get(TableName.SEGMENT);
		segmentDao.get(segmentsToBeRetrieved);
	}

	@Override
	protected DBCloneModification makeInstance(
			InternalDBCloneModification rawResult,
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds)
			throws Exception {
		final DBCloneModification.Type type = DBCloneModification.Type
				.valueOf(rawResult.getType());

		if (type == null) {
			throw new IllegalStateException("cannot find type "
					+ rawResult.getType());
		}

		final DBCloneModification.Place place = DBCloneModification.Place
				.valueOf(rawResult.getPlace());

		if (place == null) {
			throw new IllegalStateException("cannot find place "
					+ rawResult.getPlace());
		}

		final DBCloneModification newInstance = new DBCloneModification(
				rawResult.getId(), rawResult.getOldStartPosition(),
				rawResult.getNewStartPosition(), rawResult.getLength(), type,
				place, rawResult.getContentHash(), null, null, null, null);

		if (autoRefresh) {
			if (rawResult.getCodeFragmentMappingId() != null) {
				if (deepRefresh) {
					newInstance.setCodeFragmentMapping(codeFragmentMappingDao
							.get(rawResult.getCodeFragmentMappingId()));
				} else {
					newInstance
							.setCodeFragmentMapping(new DBCodeFragmentMapping(
									rawResult.getCodeFragmentMappingId(), null,
									null, null, null));
				}
			}

			if (deepRefresh) {
				newInstance.setCloneClassMapping(cloneClassMappingDao
						.get(rawResult.getCloneClassMappingId()));
			} else {
				newInstance.setCloneClassMapping(new DBCloneClassMapping(
						rawResult.getCloneClassMappingId(), null, null, null,
						null, null));
			}

			if (rawResult.getRelatedOldSegmentId() != null) {
				newInstance.setRelatedOldSegment(segmentDao.get(rawResult
						.getRelatedOldSegmentId()));
			}

			if (rawResult.getRelatedNewSegmentId() != null) {
				newInstance.setRelatedNewSegment(segmentDao.get(rawResult
						.getRelatedNewSegmentId()));
			}
		}

		return newInstance;
	}

	class InternalDBCloneModification implements
			InternalDataRepresentation<DBCloneModification> {

		private final Long id;

		private final Integer oldStartPosition;

		private final Integer newStartPosition;

		private final Integer length;

		private final String type;

		private final String place;

		private final Integer contentHash;

		private final Long codeFragmentMappingId;

		private final Long relatedOldSegmentId;

		private final Long relatedNewSegmentId;

		private final Long cloneClassMappingId;

		public InternalDBCloneModification(final Long id,
				final Integer oldStartPosition, final Integer newStartPosition,
				final Integer length, final String type, final String place,
				final Integer contentHash, final Long codeFragmentMappingId,
				final Long relatedOldSegmentId, final Long relatedNewSegmentId,
				final Long cloneClassMappingId) {
			this.id = id;
			this.oldStartPosition = oldStartPosition;
			this.newStartPosition = newStartPosition;
			this.length = length;
			this.type = type;
			this.place = place;
			this.contentHash = contentHash;
			this.codeFragmentMappingId = codeFragmentMappingId;
			this.relatedOldSegmentId = relatedOldSegmentId;
			this.relatedNewSegmentId = relatedNewSegmentId;
			this.cloneClassMappingId = cloneClassMappingId;
		}

		@Override
		public final Long getId() {
			return id;
		}

		public final Integer getOldStartPosition() {
			return oldStartPosition;
		}

		public final Integer getNewStartPosition() {
			return newStartPosition;
		}

		public final Integer getLength() {
			return length;
		}

		public final String getType() {
			return type;
		}

		public final String getPlace() {
			return place;
		}

		public final Integer getContentHash() {
			return contentHash;
		}

		public final Long getCodeFragmentMappingId() {
			return codeFragmentMappingId;
		}

		public final Long getRelatedOldSegmentId() {
			return relatedOldSegmentId;
		}

		public final Long getRelatedNewSegmentId() {
			return relatedNewSegmentId;
		}

		public final Long getCloneClassMappingId() {
			return cloneClassMappingId;
		}

	}

	class RowMapper implements RawRowMapper<InternalDBCloneModification> {

		@Override
		public InternalDBCloneModification mapRow(String[] columnNames,
				String[] resultColumns) throws SQLException {
			Long id = null;
			Integer oldStartPosition = null;
			Integer newStartPosition = null;
			Integer length = null;
			String type = null;
			String place = null;
			Integer contentHash = null;
			Long codeFragmentMappingId = null;
			Long relatedOldSegmentId = null;
			Long relatedNewSegmentId = null;
			Long cloneClassMappingId = null;

			for (int i = 0; i < columnNames.length; i++) {
				final String columnName = columnNames[i];
				final String resultColumn = resultColumns[i];

				if (resultColumn == null) {
					continue;
				}

				switch (columnName) {
				case DBCloneModification.ID_COLUMN_NAME:
					id = Long.parseLong(resultColumn);
					break;
				case DBCloneModification.OLD_START_POSITION_COLUMN_NAME:
					oldStartPosition = Integer.parseInt(resultColumn);
					break;
				case DBCloneModification.NEW_START_POSITION_COLUMN_NAME:
					newStartPosition = Integer.parseInt(resultColumn);
					break;
				case DBCloneModification.LENGTH_COLUMN_NAME:
					length = Integer.parseInt(resultColumn);
					break;
				case DBCloneModification.TYPE_COLUMN_NAME:
					type = resultColumn;
					break;
				case DBCloneModification.PLACE_COLUMN_NAME:
					place = resultColumn;
					break;
				case DBCloneModification.CONTENT_HASH_COLUMN_NAME:
					contentHash = Integer.parseInt(resultColumn);
					break;
				case DBCloneModification.CODE_FRAGMENT_MAPPING_COLUMN_NAME:
					codeFragmentMappingId = Long.parseLong(resultColumn);
					break;
				case DBCloneModification.RELATED_OLD_SEGMENT_COLUMN_NAME:
					relatedOldSegmentId = Long.parseLong(resultColumn);
					break;
				case DBCloneModification.RELATED_NEW_SEGMENT_COLUMN_NAME:
					relatedNewSegmentId = Long.parseLong(resultColumn);
					break;
				case DBCloneModification.CLONE_CLASS_MAPPING_COLUMN_NAME:
					cloneClassMappingId = Long.parseLong(resultColumn);
					break;
				}
			}

			return new InternalDBCloneModification(id, oldStartPosition,
					newStartPosition, length, type, place, contentHash,
					codeFragmentMappingId, relatedOldSegmentId,
					relatedNewSegmentId, cloneClassMappingId);
		}

	}

}
