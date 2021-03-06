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
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneModification;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RawRowMapper;

/**
 * The DAO for {@link DBCloneClassMapping}.
 * 
 * @author k-hotta
 *
 * @see DBCloneClassMapping
 */
public class CloneClassMappingDao
		extends
		AbstractDataDao<DBCloneClassMapping, CloneClassMappingDao.InternalDBCloneClassMapping> {

	/**
	 * The logger
	 */
	private static Logger logger = LogManager
			.getLogger(CloneClassMappingDao.class);

	/**
	 * The DAO for clone classes.
	 */
	private CloneClassDao cloneClassDao;

	/**
	 * The DAO for code fragment mappings.
	 */
	private CodeFragmentMappingDao codeFragmentMappingDao;

	/**
	 * The DAO for clone modifications.
	 */
	private CloneModificationDao cloneModificationDao;

	/**
	 * The DAO for version
	 */
	private VersionDao versionDao;

	@SuppressWarnings("unchecked")
	public CloneClassMappingDao() throws SQLException {
		super((Dao<DBCloneClassMapping, Long>) DBManager.getInstance()
				.getNativeDao(DBCloneClassMapping.class));
		cloneClassDao = null;
		codeFragmentMappingDao = null;
		cloneModificationDao = null;
		versionDao = null;
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

	/**
	 * Set the DAO for CodeFragmentMapping with the specified one.
	 * 
	 * @param codeFragmentMappingDao
	 *            the DAO to be set
	 */
	void setCodeFragmentMappingDao(
			final CodeFragmentMappingDao codeFragmentMappingDao) {
		this.codeFragmentMappingDao = codeFragmentMappingDao;
	}

	/**
	 * Set the DAO for cloneModification with the specified one.
	 * 
	 * @param cloneModificationDao
	 */
	void setCloneModificationDao(final CloneModificationDao cloneModificationDao) {
		this.cloneModificationDao = cloneModificationDao;
	}

	/**
	 * Set the DAO for Version with the specified one.
	 * 
	 * @param versionDao
	 *            the DAO to be set
	 */
	void setVersionDao(final VersionDao versionDao) {
		this.versionDao = versionDao;
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	protected String getTableName() {
		return TableName.CLONE_CLASS_MAPPING;
	}

	@Override
	protected String getIdColumnName() {
		return DBCloneClassMapping.ID_COLUMN_NAME;
	}

	public Map<Long, DBCloneClassMapping> getWithVersionIds(Set<Long> ids)
			throws Exception {
		final String query = QueryHelper.querySelectIdIn(getTableName(),
				DBCloneClassMapping.VERSION_COLUMN_NAME, ids);

		return queryRaw(query);
	}

	@Override
	protected RawRowMapper<InternalDBCloneClassMapping> getRowMapper()
			throws Exception {
		return new RowMapper();
	}

	@Override
	protected void initializeRelativeElementIds(
			Map<String, Set<Long>> relativeElementIds) {
		relativeElementIds.put(TableName.CLONE_CLASS_MAPPING,
				new TreeSet<Long>());
		relativeElementIds.put(TableName.CLONE_CLASS, new TreeSet<Long>());
		relativeElementIds.put(TableName.VERSION, new TreeSet<Long>());
	}

	@Override
	protected void initializeForeignChildElementIds(
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds) {
		foreignChildElementIds.put(TableName.CODE_FRAGMENT_MAPPING,
				new TreeMap<Long, Set<Long>>());
		foreignChildElementIds.put(TableName.CLONE_MODIFICATION,
				new TreeMap<Long, Set<Long>>());
	}

	@Override
	protected void updateRelativeElementIds(
			Map<String, Set<Long>> relativeElementIds,
			InternalDBCloneClassMapping rawResult) throws Exception {
		relativeElementIds.get(TableName.CLONE_CLASS_MAPPING).add(
				rawResult.getId());

		if (rawResult.getOldCloneClassId() != null) {
			relativeElementIds.get(TableName.CLONE_CLASS).add(
					rawResult.getOldCloneClassId());
		}

		if (rawResult.getNewCloneClassId() != null) {
			relativeElementIds.get(TableName.CLONE_CLASS).add(
					rawResult.getNewCloneClassId());
		}

		relativeElementIds.get(TableName.VERSION).add(rawResult.getVersionId());
	}

	@Override
	protected void retrieveRelativeElements(
			Map<String, Set<Long>> relativeElementIds,
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds)
			throws Exception {
		final Set<Long> cloneClassMappingIdsToBeRetrieved = relativeElementIds
				.get(TableName.CLONE_CLASS_MAPPING);

		// retrieve code fragment mappings
		final Map<Long, DBCodeFragmentMapping> codeFragmentMappings = codeFragmentMappingDao
				.getWithCloneClassMappingIds(cloneClassMappingIdsToBeRetrieved);
		final Map<Long, Set<Long>> codeFragmentMappingsByCloneClassMappingId = foreignChildElementIds
				.get(TableName.CODE_FRAGMENT_MAPPING);

		for (final DBCodeFragmentMapping codeFragmentMapping : codeFragmentMappings
				.values()) {
			final long cloneClassMappingId = codeFragmentMapping
					.getCloneClassMapping().getId();
			Set<Long> codeFragmentMappingIdsInCloneClassMapping = codeFragmentMappingsByCloneClassMappingId
					.get(cloneClassMappingId);

			if (codeFragmentMappingIdsInCloneClassMapping == null) {
				codeFragmentMappingIdsInCloneClassMapping = new TreeSet<>();
				codeFragmentMappingsByCloneClassMappingId.put(
						cloneClassMappingId,
						codeFragmentMappingIdsInCloneClassMapping);
			}

			codeFragmentMappingIdsInCloneClassMapping.add(codeFragmentMapping
					.getId());
		}

		// retrieve clone modifications
		final Map<Long, DBCloneModification> cloneModifications = cloneModificationDao
				.getWithCloneClassMappingIds(cloneClassMappingIdsToBeRetrieved);
		final Map<Long, Set<Long>> cloneModificationsByCloneClassMappingId = foreignChildElementIds
				.get(TableName.CLONE_MODIFICATION);

		for (final DBCloneModification cloneModification : cloneModifications
				.values()) {
			final long cloneClassMappingId = cloneModification.getId();
			Set<Long> cloneModificationIdsInCloneClassMapping = cloneModificationsByCloneClassMappingId
					.get(cloneClassMappingId);

			if (cloneModificationIdsInCloneClassMapping == null) {
				cloneModificationIdsInCloneClassMapping = new TreeSet<>();
				cloneModificationsByCloneClassMappingId.put(
						cloneClassMappingId,
						cloneModificationIdsInCloneClassMapping);
			}

			cloneModificationIdsInCloneClassMapping.add(cloneModification
					.getId());
		}

		// retrieve clone classes
		final Set<Long> CloneClassIdsToBeRetrieved = relativeElementIds
				.get(TableName.CLONE_CLASS);
		cloneClassDao.get(CloneClassIdsToBeRetrieved);

		// retrieve versions if deep refreshing is ON
		if (deepRefresh) {
			final Set<Long> versionIdsToBeRetrieved = relativeElementIds
					.get(TableName.VERSION);
			versionDao.get(versionIdsToBeRetrieved);
		}
	}

	@Override
	protected DBCloneClassMapping makeInstance(
			InternalDBCloneClassMapping rawResult,
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds)
			throws Exception {
		final DBCloneClassMapping newInstance = new DBCloneClassMapping(
				rawResult.getId(), null, null, null, null, null);

		if (autoRefresh) {
			if (deepRefresh) {
				newInstance
						.setVersion(versionDao.get(rawResult.getVersionId()));
			} else {
				newInstance.setVersion(new DBVersion(rawResult.getVersionId(),
						null, null, null, null, null, null));
			}

			if (rawResult.getOldCloneClassId() != null) {
				newInstance.setOldCloneClass(cloneClassDao.get(rawResult
						.getOldCloneClassId()));
			}

			if (rawResult.getNewCloneClassId() != null) {
				newInstance.setNewCloneClass(cloneClassDao.get(rawResult
						.getNewCloneClassId()));
			}

			final Map<Long, Set<Long>> codeFragmentMappingIdsByCloneClassMappingIds = foreignChildElementIds
					.get(TableName.CODE_FRAGMENT_MAPPING);
			final Set<Long> codeFragmentMappingIdsInCloneClassMapping = codeFragmentMappingIdsByCloneClassMappingIds
					.get(rawResult.getId());

			newInstance.setCodeFragmentMappings(new ArrayList<>());

			if (codeFragmentMappingIdsInCloneClassMapping != null
					&& !codeFragmentMappingIdsInCloneClassMapping.isEmpty()) {
				final Collection<DBCodeFragmentMapping> codeFragmentMappings = codeFragmentMappingDao
						.get(codeFragmentMappingIdsInCloneClassMapping)
						.values();
				newInstance.getCodeFragmentMappings().addAll(
						codeFragmentMappings);
			}

			final Map<Long, Set<Long>> cloneModificationIdsByCloneClassMappingIds = foreignChildElementIds
					.get(TableName.CLONE_MODIFICATION);
			final Set<Long> cloneModificationIdsInCloneClassMapping = cloneModificationIdsByCloneClassMappingIds
					.get(rawResult.getId());

			newInstance.setCloneModifications(new ArrayList<>());
			if (cloneModificationIdsInCloneClassMapping != null
					&& cloneModificationIdsInCloneClassMapping.isEmpty()) {
				final Collection<DBCloneModification> cloneModifications = cloneModificationDao
						.get(cloneModificationIdsInCloneClassMapping).values();
				newInstance.getCloneModifications().addAll(cloneModifications);
			}
		}

		return newInstance;
	}

	@Override
	protected DBCloneClassMapping refreshChildren(DBCloneClassMapping element)
			throws Exception {
		if (element.getOldCloneClass() != null) {
			cloneClassDao.refresh(element.getOldCloneClass());
		}

		if (element.getNewCloneClass() != null) {
			cloneClassDao.refresh(element.getNewCloneClass());
		}

		codeFragmentMappingDao.refreshAll(element.getCodeFragmentMappings());

		cloneModificationDao.refreshAll(element.getCloneModifications());

		if (deepRefresh) {
			versionDao.refresh(element.getVersion());
		}

		return element;
	}

	@Override
	protected Collection<DBCloneClassMapping> refreshChildrenForAll(
			Collection<DBCloneClassMapping> elements) throws Exception {
		final Set<DBCloneClass> cloneClassesToBeRefreshed = new HashSet<>();

		final Set<DBCodeFragmentMapping> fragmentMappingsToBeRefreshed = new HashSet<>();
		final Map<Long, Collection<DBCodeFragmentMapping>> fragmentMappingsInElements = new TreeMap<>();

		final Set<DBCloneModification> cloneModificationsToBeRefreshed = new HashSet<>();
		final Map<Long, Collection<DBCloneModification>> cloneModificationsInElements = new TreeMap<>();

		for (final DBCloneClassMapping element : elements) {
			if (element.getOldCloneClass() != null) {
				cloneClassesToBeRefreshed.add(element.getOldCloneClass());
			}
			if (element.getNewCloneClass() != null) {
				cloneClassesToBeRefreshed.add(element.getNewCloneClass());
			}

			final Collection<DBCodeFragmentMapping> fragmentMappingsInElement = element
					.getCodeFragmentMappings();
			fragmentMappingsToBeRefreshed.addAll(fragmentMappingsInElement);
			fragmentMappingsInElements.put(element.getId(),
					fragmentMappingsInElement);

			final Collection<DBCloneModification> cloneModificationsInElement = element
					.getCloneModifications();
			cloneModificationsToBeRefreshed.addAll(cloneModificationsInElement);
			cloneModificationsInElements.put(element.getId(),
					cloneModificationsInElement);
		}

		cloneClassDao.refreshAll(cloneClassesToBeRefreshed);
		codeFragmentMappingDao.refreshAll(fragmentMappingsToBeRefreshed);
		cloneModificationDao.refreshAll(cloneModificationsToBeRefreshed);

		for (final DBCloneClassMapping element : elements) {
			if (element.getOldCloneClass() != null) {
				element.setOldCloneClass(cloneClassDao.get(element
						.getOldCloneClass().getId()));
			}
			if (element.getNewCloneClass() != null) {
				element.setOldCloneClass(cloneClassDao.get(element
						.getNewCloneClass().getId()));
			}

			final List<DBCodeFragmentMapping> fragmentMappingsToBeStored = new ArrayList<>();
			for (final DBCodeFragmentMapping fragmentMapping : fragmentMappingsInElements
					.get(element.getId())) {
				fragmentMappingsToBeStored.add(codeFragmentMappingDao
						.get(fragmentMapping.getId()));
			}
			element.setCodeFragmentMappings(fragmentMappingsToBeStored);

			final List<DBCloneModification> cloneModificationsToBeStored = new ArrayList<>();
			for (final DBCloneModification cloneModification : cloneModificationsInElements
					.get(element.getId())) {
				cloneModificationsToBeStored.add(cloneModificationDao
						.get(cloneModification.getId()));
			}
			element.setCloneModifications(cloneModificationsToBeStored);
		}

		if (deepRefresh) {
			final Set<DBVersion> versionsToBeRefreshed = new HashSet<>();
			for (final DBCloneClassMapping element : elements) {
				versionsToBeRefreshed.add(element.getVersion());
			}
			versionDao.refreshAll(versionsToBeRefreshed);
			for (final DBCloneClassMapping element : elements) {
				element.setVersion(versionDao.get(element.getVersion().getId()));
			}
		}

		return elements;
	}

	class InternalDBCloneClassMapping implements
			InternalDataRepresentation<DBCloneClassMapping> {

		private final Long id;

		private final Long oldCloneClassId;

		private final Long newCloneClassId;

		private final Long versionId;

		private InternalDBCloneClassMapping(final Long id,
				final Long oldCloneClassId, final Long newCloneClassId,
				final Long versionId) {
			this.id = id;
			this.oldCloneClassId = oldCloneClassId;
			this.newCloneClassId = newCloneClassId;
			this.versionId = versionId;
		}

		public final Long getId() {
			return id;
		}

		private final Long getOldCloneClassId() {
			return oldCloneClassId;
		}

		private final Long getNewCloneClassId() {
			return newCloneClassId;
		}

		private final Long getVersionId() {
			return versionId;
		}

	}

	class RowMapper implements RawRowMapper<InternalDBCloneClassMapping> {

		@Override
		public InternalDBCloneClassMapping mapRow(String[] columnNames,
				String[] resultColumns) throws SQLException {
			Long id = null;
			Long oldCloneClassId = null;
			Long newCloneClassId = null;
			Long versionId = null;

			for (int i = 0; i < columnNames.length; i++) {
				final String column = columnNames[i];
				final String resultColumn = resultColumns[i];

				if (resultColumn == null) {
					continue;
				}

				switch (column) {
				case DBCloneClassMapping.ID_COLUMN_NAME:
					id = Long.parseLong(resultColumn);
					break;
				case DBCloneClassMapping.OLD_CLONE_CLASS_COLUMN_NAME:
					try {
						oldCloneClassId = Long.parseLong(resultColumn);
					} catch (Exception e) {
						// ignore
					}
					break;
				case DBCloneClassMapping.NEW_CLONE_CLASS_COLUMN_NAME:
					try {
						newCloneClassId = Long.parseLong(resultColumn);
					} catch (Exception e) {
						// ignore
					}
					break;
				case DBCloneClassMapping.VERSION_COLUMN_NAME:
					versionId = Long.parseLong(resultColumn);
					break;
				}
			}

			return new InternalDBCloneClassMapping(id, oldCloneClassId,
					newCloneClassId, versionId);
		}

	}

}
