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

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneModification;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RawRowMapper;

public class CodeFragmentMappingDao
		extends
		AbstractDataDao<DBCodeFragmentMapping, CodeFragmentMappingDao.InternalDBCodeFragmentMapping> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(CodeFragmentMappingDao.class);

	/**
	 * The DAO for code fragments.
	 */
	private CodeFragmentDao codeFragmentDao;

	/**
	 * The DAO for clone class mappings.
	 */
	private CloneClassMappingDao cloneClassMappingDao;

	/**
	 * The DAO for clone modifications
	 */
	private CloneModificationDao cloneModificationDao;

	@SuppressWarnings("unchecked")
	public CodeFragmentMappingDao() throws SQLException {
		super((Dao<DBCodeFragmentMapping, Long>) DBManager.getInstance()
				.getNativeDao(DBCodeFragmentMapping.class));
		codeFragmentDao = null;
		cloneClassMappingDao = null;
		cloneModificationDao = null;
	}

	/**
	 * Set the DAO for CodeFragment with the specified one.
	 * 
	 * @param codeFragmentDao
	 *            the DAO to be set
	 */
	void setCodeFragmentDao(final CodeFragmentDao codeFragmentDao) {
		this.codeFragmentDao = codeFragmentDao;
	}

	/**
	 * Set the DAO for CloneClassMapping with the specified one.
	 * 
	 * @param cloneClassMappingDao
	 *            the DAO to be set
	 */
	void setCloneClassMappingDao(final CloneClassMappingDao cloneClassMappingDao) {
		this.cloneClassMappingDao = cloneClassMappingDao;
	}

	/**
	 * Set the DAO for CloneModification with the specified one.
	 * 
	 * @param cloneModificationDao
	 *            the DAO to be set
	 */
	void setCloneModificationDao(final CloneModificationDao cloneModificationDao) {
		this.cloneModificationDao = cloneModificationDao;
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	protected String getTableName() {
		return TableName.CODE_FRAGMENT_MAPPING;
	}

	@Override
	protected String getIdColumnName() {
		return DBCodeFragmentMapping.ID_COLUMN_NAME;
	}

	@Override
	protected DBCodeFragmentMapping refreshChildren(
			DBCodeFragmentMapping element) throws Exception {
		if (element.getOldCodeFragment() != null) {
			codeFragmentDao.refresh(element.getOldCodeFragment());
		}

		if (element.getNewCodeFragment() != null) {
			codeFragmentDao.refresh(element.getNewCodeFragment());
		}

		cloneModificationDao.refreshAll(element.getModifications());

		if (deepRefresh) {
			cloneClassMappingDao.refresh(element.getCloneClassMapping());
		}

		return element;
	}

	@Override
	protected Collection<DBCodeFragmentMapping> refreshChildrenForAll(
			Collection<DBCodeFragmentMapping> elements) throws Exception {
		final Set<DBCodeFragment> codeFragmentsToBeRefreshed = new HashSet<>();
		final Set<DBCloneModification> cloneModificationsToBeRefreshed = new HashSet<>();
		for (final DBCodeFragmentMapping element : elements) {
			if (element.getOldCodeFragment() != null) {
				codeFragmentsToBeRefreshed.add(element.getOldCodeFragment());
			}
			if (element.getNewCodeFragment() != null) {
				codeFragmentsToBeRefreshed.add(element.getNewCodeFragment());
			}
			cloneModificationsToBeRefreshed.addAll(element.getModifications());
		}
		codeFragmentDao.refreshAll(codeFragmentsToBeRefreshed);
		cloneModificationDao.refreshAll(cloneModificationsToBeRefreshed);
		for (final DBCodeFragmentMapping element : elements) {
			if (element.getOldCodeFragment() != null) {
				element.setOldCodeFragment(codeFragmentDao.get(element
						.getOldCodeFragment().getId()));
			}
			if (element.getNewCodeFragment() != null) {
				element.setNewCodeFragment(codeFragmentDao.get(element
						.getNewCodeFragment().getId()));
			}

			final List<DBCloneModification> toBeStored = new ArrayList<>();
			for (final DBCloneModification cloneModification : element
					.getModifications()) {
				toBeStored.add(cloneModificationDao.get(cloneModification
						.getId()));
			}
			element.setModifications(toBeStored);
		}

		if (deepRefresh) {
			final Set<DBCloneClassMapping> cloneClassMappingsToBeRetrieved = new HashSet<>();
			for (final DBCodeFragmentMapping element : elements) {
				cloneClassMappingsToBeRetrieved.add(element
						.getCloneClassMapping());
			}
			cloneClassMappingDao.refreshAll(cloneClassMappingsToBeRetrieved);
			for (final DBCodeFragmentMapping element : elements) {
				element.setCloneClassMapping(cloneClassMappingDao.get(element
						.getCloneClassMapping().getId()));
			}
		}

		return elements;
	}

	@Override
	protected RawRowMapper<InternalDBCodeFragmentMapping> getRowMapper()
			throws Exception {
		return new RowMapper();
	}

	@Override
	protected void initializeRelativeElementIds(
			Map<String, Set<Long>> relativeElementIds) {
		relativeElementIds.put(TableName.CODE_FRAGMENT_MAPPING,
				new TreeSet<Long>());
		relativeElementIds.put(TableName.CODE_FRAGMENT, new TreeSet<Long>());
		relativeElementIds.put(TableName.CLONE_CLASS_MAPPING,
				new TreeSet<Long>());
	}

	@Override
	protected void initializeForeignChildElementIds(
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds) {
		foreignChildElementIds.put(TableName.CLONE_MODIFICATION,
				new TreeMap<>());
	}

	@Override
	protected void updateRelativeElementIds(
			Map<String, Set<Long>> relativeElementIds,
			InternalDBCodeFragmentMapping rawResult) throws Exception {
		relativeElementIds.get(TableName.CODE_FRAGMENT_MAPPING).add(
				rawResult.getId());

		if (rawResult.getOldCodeFragmentId() != null) {
			relativeElementIds.get(TableName.CODE_FRAGMENT).add(
					rawResult.getOldCodeFragmentId());
		}
		if (rawResult.getNewCodeFragmentId() != null) {
			relativeElementIds.get(TableName.CODE_FRAGMENT).add(
					rawResult.getNewCodeFragmentId());
		}

		relativeElementIds.get(TableName.CLONE_CLASS_MAPPING).add(
				rawResult.getCloneClassMappingId());
	}

	@Override
	protected void retrieveRelativeElements(
			Map<String, Set<Long>> relativeElementIds,
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds)
			throws Exception {
		// retrieve code fragments
		final Set<Long> codeFragmentsToBeRetrieved = relativeElementIds
				.get(TableName.CODE_FRAGMENT);
		codeFragmentDao.get(codeFragmentsToBeRetrieved);

		// retrieve clone class mappings if deep refreshing is ON
		if (deepRefresh) {
			final Set<Long> cloneClassMappingIdsToBeRetrieved = relativeElementIds
					.get(TableName.CLONE_CLASS_MAPPING);
			cloneClassMappingDao.get(cloneClassMappingIdsToBeRetrieved);
		}

		// retrieve modifications
		final Set<Long> codeFragmentMappingsToBeRetrieved = relativeElementIds
				.get(TableName.CODE_FRAGMENT_MAPPING);
		final Map<Long, DBCloneModification> modifications = cloneModificationDao
				.getWithCodeFragmentMappingIds(codeFragmentMappingsToBeRetrieved);
		final Map<Long, Set<Long>> modificationsByMappingIds = foreignChildElementIds
				.get(TableName.CLONE_MODIFICATION);

		for (final DBCloneModification modification : modifications.values()) {
			final long mappingId = modification.getCodeFragmentMapping()
					.getId();
			Set<Long> modificationsInMapping = modificationsByMappingIds
					.get(mappingId);

			if (modificationsInMapping == null) {
				modificationsInMapping = new TreeSet<>();
				modificationsByMappingIds
						.put(mappingId, modificationsInMapping);
			}

			modificationsInMapping.add(modification.getId());
		}
	}

	@Override
	protected DBCodeFragmentMapping makeInstance(
			InternalDBCodeFragmentMapping rawResult,
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds)
			throws Exception {
		final DBCodeFragmentMapping newInstance = new DBCodeFragmentMapping(
				rawResult.getId(), null, null, null, null);

		if (autoRefresh) {
			if (rawResult.getOldCodeFragmentId() != null) {
				newInstance.setOldCodeFragment(codeFragmentDao.get(rawResult
						.getOldCodeFragmentId()));
			}
			if (rawResult.getNewCodeFragmentId() != null) {
				newInstance.setNewCodeFragment(codeFragmentDao.get(rawResult
						.getNewCodeFragmentId()));
			}

			final Map<Long, Set<Long>> modificationsByMappings = foreignChildElementIds
					.get(TableName.CLONE_MODIFICATION);
			final Set<Long> modificationsInMapping = modificationsByMappings
					.get(rawResult.getId());

			newInstance.setModifications(new ArrayList<>());

			if (modificationsInMapping != null
					&& !modificationsInMapping.isEmpty()) {
				final Collection<DBCloneModification> modifications = cloneModificationDao
						.get(modificationsInMapping).values();
				newInstance.addModifications(modifications);
			}

			if (deepRefresh) {
				newInstance.setCloneClassMapping(cloneClassMappingDao
						.get(rawResult.getCloneClassMappingId()));
			} else {
				newInstance.setCloneClassMapping(new DBCloneClassMapping(
						rawResult.getCloneClassMappingId(), null, null, null,
						null, null));
			}
		}

		return newInstance;
	}

	public Map<Long, DBCodeFragmentMapping> getWithCloneClassMappingIds(
			final Collection<Long> cloneClassMappingIds) throws Exception {
		final String query = QueryHelper.querySelectIdIn(getTableName(),
				DBCodeFragmentMapping.CLONE_CLASS_MAPPING_COLUMN_NAME,
				cloneClassMappingIds);

		return queryRaw(query);
	}

	class InternalDBCodeFragmentMapping implements
			InternalDataRepresentation<DBCodeFragmentMapping> {

		private final Long id;

		private final Long oldCodeFragmentId;

		private final Long newCodeFragmentId;

		private final Long cloneClassMappingId;

		public InternalDBCodeFragmentMapping(final Long id,
				final Long oldCodeFragmentId, final Long newCodeFragmentId,
				final Long cloneClassMappingId) {
			this.id = id;
			this.oldCodeFragmentId = oldCodeFragmentId;
			this.newCodeFragmentId = newCodeFragmentId;
			this.cloneClassMappingId = cloneClassMappingId;
		}

		public final Long getId() {
			return id;
		}

		public final Long getOldCodeFragmentId() {
			return oldCodeFragmentId;
		}

		public final Long getNewCodeFragmentId() {
			return newCodeFragmentId;
		}

		public final Long getCloneClassMappingId() {
			return cloneClassMappingId;
		}

	}

	class RowMapper implements RawRowMapper<InternalDBCodeFragmentMapping> {

		@Override
		public InternalDBCodeFragmentMapping mapRow(String[] columnNames,
				String[] resultColumns) throws SQLException {
			Long id = null;
			Long oldCodeFragmentId = null;
			Long newCodeFragmentId = null;
			Long cloneClassMappingId = null;

			for (int i = 0; i < columnNames.length; i++) {
				final String columnName = columnNames[i];

				switch (columnName) {
				case DBCodeFragmentMapping.ID_COLUMN_NAME:
					id = Long.parseLong(resultColumns[i]);
					break;
				case DBCodeFragmentMapping.OLD_CODE_FRAGMENT_COLUMN_NAME:
					try {
						oldCodeFragmentId = Long.parseLong(resultColumns[i]);
					} catch (Exception e) {
						// ignore
					}
					break;
				case DBCodeFragmentMapping.NEW_CODE_FRAGMENT_COLUMN_NAME:
					try {
						newCodeFragmentId = Long.parseLong(resultColumns[i]);
					} catch (Exception e) {
						// ignore
					}
					break;
				case DBCodeFragmentMapping.CLONE_CLASS_MAPPING_COLUMN_NAME:
					cloneClassMappingId = Long.parseLong(resultColumns[i]);
					break;
				}
			}

			return new InternalDBCodeFragmentMapping(id, oldCodeFragmentId,
					newCodeFragmentId, cloneClassMappingId);
		}

	}

}
