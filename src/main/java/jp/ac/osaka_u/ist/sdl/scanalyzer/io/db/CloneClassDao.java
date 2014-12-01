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
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RawRowMapper;

/**
 * The DAO for {@link DBCloneClass}.
 * 
 * @author k-hotta
 * 
 * @see DBCloneClass
 */
public class CloneClassDao extends
		AbstractDataDao<DBCloneClass, CloneClassDao.InternalDBCloneClass> {

	/**
	 * The logger
	 */
	private static Logger logger = LogManager.getLogger(CloneClassDao.class);

	/**
	 * The DAO for version
	 */
	private VersionDao versionDao;

	/**
	 * The DAO for CodeFragment
	 */
	private CodeFragmentDao codeFragmentDao;

	@SuppressWarnings("unchecked")
	public CloneClassDao() throws SQLException {
		super((Dao<DBCloneClass, Long>) DBManager.getInstance().getNativeDao(
				DBCloneClass.class));
		codeFragmentDao = null;
		versionDao = null;
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
		return TableName.CLONE_CLASS;
	}

	@Override
	protected String getIdColumnName() {
		return DBCloneClass.ID_COLUMN_NAME;
	}

	@Override
	protected DBCloneClass refreshChildren(DBCloneClass element)
			throws Exception {
		codeFragmentDao.refreshAll(element.getCodeFragments());

		if (deepRefresh) {
			versionDao.refresh(element.getVersion());
		}

		return element;
	}

	@Override
	protected Collection<DBCloneClass> refreshChildrenForAll(
			Collection<DBCloneClass> elements) throws Exception {
		final Set<DBCodeFragment> codeFragmentsToBeRefreshed = new HashSet<>();
		for (final DBCloneClass element : elements) {
			codeFragmentsToBeRefreshed.addAll(element.getCodeFragments());
		}
		codeFragmentDao.refreshAll(codeFragmentsToBeRefreshed);
		for (final DBCloneClass element : elements) {
			final List<DBCodeFragment> toBeStored = new ArrayList<>();
			for (final DBCodeFragment codeFragment : element.getCodeFragments()) {
				toBeStored.add(codeFragmentDao.get(codeFragment.getId()));
			}
			element.setCodeFragments(toBeStored);
		}

		if (deepRefresh) {
			final Set<DBVersion> versionsToBeRefreshed = new HashSet<>();
			for (final DBCloneClass element : elements) {
				versionsToBeRefreshed.add(element.getVersion());
			}
			versionDao.refreshAll(versionsToBeRefreshed);
			for (final DBCloneClass element : elements) {
				element.setVersion(versionDao.get(element.getVersion().getId()));
			}
		}

		return elements;
	}

	@Override
	protected RawRowMapper<InternalDBCloneClass> getRowMapper()
			throws Exception {
		return new RowMapper();
	}

	@Override
	protected void initializeRelativeElementIds(
			final Map<String, Set<Long>> relativeElementIds) {
		relativeElementIds.put(TableName.CLONE_CLASS, new TreeSet<Long>());
		relativeElementIds.put(TableName.VERSION, new TreeSet<Long>());
	}

	@Override
	protected void initializeForeignChildElementIds(
			final Map<String, Map<Long, Set<Long>>> foreignChildElementIds) {
		foreignChildElementIds.put(TableName.CODE_FRAGMENT,
				new TreeMap<Long, Set<Long>>());
	}

	@Override
	protected void updateRelativeElementIds(
			final Map<String, Set<Long>> relativeElementIds,
			final InternalDBCloneClass rawResult) throws Exception {
		relativeElementIds.get(TableName.CLONE_CLASS).add(rawResult.getId());
		relativeElementIds.get(TableName.VERSION).add(rawResult.getVersionId());
	}

	@Override
	protected void retrieveRelativeElements(
			Map<String, Set<Long>> relativeElementIds,
			final Map<String, Map<Long, Set<Long>>> foreignChildElementIds)
			throws Exception {
		final Set<Long> cloneClassIdsToBeRetrieved = relativeElementIds
				.get(TableName.CLONE_CLASS);
		final Set<Long> versionIdsToBeRetrieved = relativeElementIds
				.get(TableName.VERSION);

		final Map<Long, DBCodeFragment> codeFragments = codeFragmentDao
				.getWithCloneClassIds(cloneClassIdsToBeRetrieved);
		final Map<Long, Set<Long>> codeFragmentsByCloneClassIds = foreignChildElementIds
				.get(TableName.CODE_FRAGMENT);

		for (final DBCodeFragment codeFragment : codeFragments.values()) {
			final long cloneClassId = codeFragment.getCloneClass().getId();
			Set<Long> codeFragmentIdsInCloneClass = codeFragmentsByCloneClassIds
					.get(cloneClassId);

			if (codeFragmentIdsInCloneClass == null) {
				codeFragmentIdsInCloneClass = new TreeSet<>();
				codeFragmentsByCloneClassIds.put(cloneClassId,
						codeFragmentIdsInCloneClass);
			}

			codeFragmentIdsInCloneClass.add(codeFragment.getId());
		}

		if (deepRefresh) {
			versionDao.get(versionIdsToBeRetrieved);
		}
	}

	@Override
	protected DBCloneClass makeInstance(InternalDBCloneClass rawResult,
			final Map<String, Map<Long, Set<Long>>> foreignChildElementIds)
			throws Exception {
		final DBCloneClass newInstance = new DBCloneClass(rawResult.getId(),
				null, null);

		if (autoRefresh) {
			if (deepRefresh) {
				newInstance
						.setVersion(versionDao.get(rawResult.getVersionId()));
			} else {
				newInstance.setVersion(new DBVersion(rawResult.getVersionId(),
						null, null, null, null, null, null));
			}

			final Map<Long, Set<Long>> codeFragmentIdsByCloneClassIds = foreignChildElementIds
					.get(TableName.CODE_FRAGMENT);
			final Set<Long> codeFragmentIdsInCloneClass = codeFragmentIdsByCloneClassIds
					.get(rawResult.getId());

			newInstance.setCodeFragments(new ArrayList<>());

			if (codeFragmentIdsInCloneClass != null
					&& !codeFragmentIdsInCloneClass.isEmpty()) {
				final Collection<DBCodeFragment> codeFragments = codeFragmentDao
						.get(codeFragmentIdsInCloneClass).values();
				newInstance.getCodeFragments().addAll(codeFragments);
			}
		}

		return newInstance;
	}

	class InternalDBCloneClass implements
			InternalDataRepresentation<DBCloneClass> {

		private final Long id;

		private final Long versionId;

		private InternalDBCloneClass(final Long id, final Long versionId) {
			this.id = id;
			this.versionId = versionId;
		}

		@Override
		public Long getId() {
			return id;
		}

		public Long getVersionId() {
			return versionId;
		}

	}

	class RowMapper implements RawRowMapper<InternalDBCloneClass> {

		@Override
		public InternalDBCloneClass mapRow(String[] columnNames,
				String[] resultColumns) throws SQLException {
			Long id = null;
			Long versionId = null;

			for (int i = 0; i < columnNames.length; i++) {
				final String columnName = columnNames[i];
				switch (columnName) {
				case DBCloneClass.ID_COLUMN_NAME:
					id = Long.parseLong(resultColumns[i]);
					break;
				case DBCloneClass.VERSION_COLUMN_NAME:
					versionId = Long.parseLong(resultColumns[i]);
					break;
				}
			}

			return new InternalDBCloneClass(id, versionId);
		}
	}

}
