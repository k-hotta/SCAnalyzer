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
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;

/**
 * The DAO for {@link DBCloneClassMapping}.
 * 
 * @author k-hotta
 *
 * @see DBCloneClassMapping
 */
public class CloneClassMappingDao extends AbstractDataDao<DBCloneClassMapping> {

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
	 * The DAO for version
	 */
	private VersionDao versionDao;

	@SuppressWarnings("unchecked")
	public CloneClassMappingDao() throws SQLException {
		super((Dao<DBCloneClassMapping, Long>) DBManager.getInstance()
				.getNativeDao(DBCloneClassMapping.class));
		cloneClassDao = null;
		codeFragmentMappingDao = null;
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

	protected Map<Long, DBCloneClassMapping> queryRaw(final String query)
			throws Exception {
		final GenericRawResults<InternalDBCloneClassMapping> rawResults = originalDao
				.queryRaw(query, new RowMapper());

		final SortedMap<Long, DBCloneClassMapping> result = new TreeMap<>();
		final Set<Long> cloneClassMappingIdsToBeRetrieved = new TreeSet<>();
		final Set<Long> cloneClassIdsToBeRetrieved = new TreeSet<>();
		final Set<Long> versionIdsToBeRetrieved = new TreeSet<>();

		for (final InternalDBCloneClassMapping rawResult : rawResults) {
			final long id = rawResult.getId();
			if (!retrievedElements.containsKey(id)) {
				cloneClassMappingIdsToBeRetrieved.add(id);
				cloneClassIdsToBeRetrieved.add(rawResult.getOldCloneClassId());
				cloneClassIdsToBeRetrieved.add(rawResult.getNewCloneClassId());
				versionIdsToBeRetrieved.add(rawResult.getVersionId());
			}
		}

		final Map<Long, DBCloneClass> cloneClasses = cloneClassDao
				.get(cloneClassIdsToBeRetrieved);
		final Map<Long, DBVersion> versions = (deepRefresh) ? versionDao
				.get(versionIdsToBeRetrieved) : new TreeMap<>();
		final Map<Long, DBCodeFragmentMapping> relatedFragmentMappings = codeFragmentMappingDao
				.getWithCloneClassMappingIds(cloneClassMappingIdsToBeRetrieved);

		final Map<Long, List<DBCodeFragmentMapping>> fragmentMappingsByCloneMappingId = new TreeMap<>();
		for (final DBCodeFragmentMapping relatedFragmentMapping : relatedFragmentMappings
				.values()) {
			final long cloneClassMappingId = relatedFragmentMapping
					.getCloneClassMapping().getId();
			if (fragmentMappingsByCloneMappingId
					.containsKey(cloneClassMappingId)) {
				fragmentMappingsByCloneMappingId.get(cloneClassMappingId).add(
						relatedFragmentMapping);
			} else {
				final List<DBCodeFragmentMapping> newList = new ArrayList<>();
				newList.add(relatedFragmentMapping);
				fragmentMappingsByCloneMappingId.put(cloneClassMappingId,
						newList);
			}
		}

		for (final InternalDBCloneClassMapping rawResult : rawResults) {
			final long id = rawResult.getId();
			if (retrievedElements.containsKey(id)) {
				result.put(id, retrievedElements.get(id));
			} else {
				final DBCloneClassMapping newInstance = new DBCloneClassMapping(
						id, null, null, null, null);

				if (autoRefresh) {
					if (rawResult.getOldCloneClassId() != null) {
						newInstance.setOldCloneClass(cloneClasses.get(rawResult
								.getOldCloneClassId()));
					}
					if (rawResult.getNewCloneClassId() != null) {
						newInstance.setNewCloneClass(cloneClasses.get(rawResult
								.getNewCloneClassId()));
					}
					if (deepRefresh) {
						newInstance.setVersion(versions.get(rawResult
								.getVersionId()));
					} else {
						newInstance.setVersion(new DBVersion(rawResult
								.getVersionId(), null, null, null, null, null,
								null));
					}
				}

				retrievedElements.put(id, newInstance);
			}
		}

		return Collections.unmodifiableSortedMap(result);
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

		if (deepRefresh) {
			versionDao.refresh(element.getVersion());
		}

		return element;
	}

	@Override
	protected Collection<DBCloneClassMapping> refreshChildrenForAll(
			Collection<DBCloneClassMapping> elements) throws Exception {
		final Set<DBCloneClass> cloneClassesToBeRefreshed = new HashSet<>();
		for (final DBCloneClassMapping element : elements) {
			if (element.getOldCloneClass() != null) {
				cloneClassesToBeRefreshed.add(element.getOldCloneClass());
			}
			if (element.getNewCloneClass() != null) {
				cloneClassesToBeRefreshed.add(element.getNewCloneClass());
			}
		}
		cloneClassDao.refreshAll(cloneClassesToBeRefreshed);
		for (final DBCloneClassMapping element : elements) {
			if (element.getOldCloneClass() != null) {
				element.setOldCloneClass(cloneClassDao.get(element
						.getOldCloneClass().getId()));
			}
			if (element.getNewCloneClass() != null) {
				element.setOldCloneClass(cloneClassDao.get(element
						.getNewCloneClass().getId()));
			}
		}

		final Set<DBCodeFragmentMapping> fragmentMappingsToBeRefreshed = new HashSet<>();
		final Map<Long, Collection<DBCodeFragmentMapping>> fragmentMappingsInElements = new TreeMap<>();
		for (final DBCloneClassMapping element : elements) {
			final Collection<DBCodeFragmentMapping> fragmentMappingsInElement = element
					.getCodeFragmentMappings();
			fragmentMappingsToBeRefreshed.addAll(fragmentMappingsInElement);
			fragmentMappingsInElements.put(element.getId(),
					fragmentMappingsInElement);
		}
		codeFragmentMappingDao.refreshAll(fragmentMappingsToBeRefreshed);
		for (final DBCloneClassMapping element : elements) {
			final List<DBCodeFragmentMapping> toBeStored = new ArrayList<>();
			for (final DBCodeFragmentMapping fragmentMapping : fragmentMappingsInElements
					.get(element.getId())) {
				toBeStored.add(codeFragmentMappingDao.get(fragmentMapping
						.getId()));
			}
			element.setCodeFragmentMappings(toBeStored);
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

	private class InternalDBCloneClassMapping implements
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

	private class RowMapper implements
			RawRowMapper<InternalDBCloneClassMapping> {

		@Override
		public InternalDBCloneClassMapping mapRow(String[] columnNames,
				String[] resultColumns) throws SQLException {
			Long id = null;
			Long oldCloneClassId = null;
			Long newCloneClassId = null;
			Long versionId = null;

			for (int i = 0; i < columnNames.length; i++) {
				final String column = columnNames[i];
				switch (column) {
				case DBCloneClassMapping.ID_COLUMN_NAME:
					id = Long.parseLong(resultColumns[i]);
					break;
				case DBCloneClassMapping.OLD_CLONE_CLASS_COLUMN_NAME:
					oldCloneClassId = Long.parseLong(resultColumns[i]);
					break;
				case DBCloneClassMapping.NEW_CLONE_CLASS_COLUMN_NAME:
					newCloneClassId = Long.parseLong(resultColumns[i]);
					break;
				case DBCloneClassMapping.VERSION_COLUMN_NAME:
					versionId = Long.parseLong(resultColumns[i]);
					break;
				}
			}

			return new InternalDBCloneClassMapping(id, oldCloneClassId,
					newCloneClassId, versionId);
		}

	}

}
