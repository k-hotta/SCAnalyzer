package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
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

	@SuppressWarnings("unchecked")
	public CodeFragmentMappingDao() throws SQLException {
		super((Dao<DBCodeFragmentMapping, Long>) DBManager.getInstance()
				.getNativeDao(DBCodeFragmentMapping.class));
		codeFragmentDao = null;
		cloneClassMappingDao = null;
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

		if (deepRefresh) {
			cloneClassMappingDao.refresh(element.getCloneClassMapping());
		}

		return element;
	}

	@Override
	protected Collection<DBCodeFragmentMapping> refreshChildrenForAll(
			Collection<DBCodeFragmentMapping> elements) throws Exception {
		final Set<DBCodeFragment> codeFragmentsToBeRefreshed = new HashSet<>();
		for (final DBCodeFragmentMapping element : elements) {
			if (element.getOldCodeFragment() != null) {
				codeFragmentsToBeRefreshed.add(element.getOldCodeFragment());
			}
			if (element.getNewCodeFragment() != null) {
				codeFragmentsToBeRefreshed.add(element.getNewCodeFragment());
			}
		}
		codeFragmentDao.refreshAll(codeFragmentsToBeRefreshed);
		for (final DBCodeFragmentMapping element : elements) {
			if (element.getOldCodeFragment() != null) {
				element.setOldCodeFragment(codeFragmentDao.get(element
						.getOldCodeFragment().getId()));
			}
			if (element.getNewCodeFragment() != null) {
				element.setNewCodeFragment(codeFragmentDao.get(element
						.getNewCodeFragment().getId()));
			}
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
		relativeElementIds.put(TableName.CODE_FRAGMENT, new TreeSet<Long>());
		relativeElementIds.put(TableName.CLONE_CLASS_MAPPING,
				new TreeSet<Long>());
	}

	@Override
	protected void initializeForeignChildElementIds(
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds) {
		// do nothing
	}

	@Override
	protected void updateRelativeElementIds(
			Map<String, Set<Long>> relativeElementIds,
			InternalDBCodeFragmentMapping rawResult) throws Exception {
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
	}

	@Override
	protected DBCodeFragmentMapping makeInstance(
			InternalDBCodeFragmentMapping rawResult,
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds)
			throws Exception {
		final DBCodeFragmentMapping newInstance = new DBCodeFragmentMapping(
				rawResult.getId(), null, null, null);

		if (autoRefresh) {
			if (rawResult.getOldCodeFragmentId() != null) {
				newInstance.setOldCodeFragment(codeFragmentDao.get(rawResult
						.getOldCodeFragmentId()));
			}
			if (rawResult.getNewCodeFragmentId() != null) {
				newInstance.setNewCodeFragment(codeFragmentDao.get(rawResult
						.getNewCodeFragmentId()));
			}

			if (deepRefresh) {
				newInstance.setCloneClassMapping(cloneClassMappingDao
						.get(rawResult.getCloneClassMappingId()));
			} else {
				newInstance.setCloneClassMapping(new DBCloneClassMapping(
						rawResult.getCloneClassMappingId(), null, null, null,
						null));
			}
		}

		return newInstance;
	}

	public Map<Long, DBCodeFragmentMapping> getWithCloneClassMappingIds(
			final Collection<Long> cloneClassMappingIds) throws Exception {
		final String query = QueryHelper.querySelectIdIn(getTableName(),
				DBCodeFragmentMapping.CLONE_CLASS_MAPPING_COLUMN_NAME,
				cloneClassMappingIds);

		return runRawQuery(query);
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
					oldCodeFragmentId = Long.parseLong(resultColumns[i]);
					break;
				case DBCodeFragmentMapping.NEW_CODE_FRAGMENT_COLUMN_NAME:
					newCodeFragmentId = Long.parseLong(resultColumns[i]);
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
