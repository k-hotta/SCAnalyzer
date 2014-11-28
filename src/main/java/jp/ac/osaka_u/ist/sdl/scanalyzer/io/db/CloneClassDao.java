package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;

/**
 * The DAO for {@link DBCloneClass}.
 * 
 * @author k-hotta
 * 
 * @see DBCloneClass
 */
public class CloneClassDao extends AbstractDataDao<DBCloneClass> {

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
	protected Map<Long, DBCloneClass> queryRaw(String query) throws Exception {
		final GenericRawResults<InternalDBCloneClass> rawResults = originalDao
				.queryRaw(query, new RowMapper());

		final SortedMap<Long, DBCloneClass> result = new TreeMap<>();
		final Set<Long> cloneClassIdsToBeRetrieved = new TreeSet<>();
		final Set<Long> versionIdsToBeRetrieved = new TreeSet<>();

		for (final InternalDBCloneClass rawResult : rawResults) {
			final long id = rawResult.getId();
			if (retrievedElements.containsKey(id)) {
				cloneClassIdsToBeRetrieved.add(id);
				versionIdsToBeRetrieved.add(rawResult.getVersionId());
			}
		}

		final Map<Long, DBVersion> versions = (deepRefresh) ? versionDao
				.get(versionIdsToBeRetrieved) : new TreeMap<>();
		final Map<Long, DBCodeFragment> relatedCodeFragments = codeFragmentDao
				.getWithCloneClassIds(cloneClassIdsToBeRetrieved);

		final Map<Long, List<DBCodeFragment>> codeFragmentsByCloneClassId = new TreeMap<>();
		for (final DBCodeFragment codeFragment : relatedCodeFragments.values()) {
			final long cloneClassId = codeFragment.getCloneClass().getId();
			if (codeFragmentsByCloneClassId.containsKey(cloneClassId)) {
				codeFragmentsByCloneClassId.get(cloneClassId).add(codeFragment);
			} else {
				final List<DBCodeFragment> newList = new ArrayList<>();
				newList.add(codeFragment);
				codeFragmentsByCloneClassId.put(cloneClassId, newList);
			}
		}

		for (final InternalDBCloneClass rawResult : rawResults) {
			final long id = rawResult.getId();

			if (!retrievedElements.containsKey(id)) {
				makeNewInstance(versions, codeFragmentsByCloneClassId,
						rawResult, id);
			}

			result.put(id, retrievedElements.get(id));
		}

		return result;
	}

	private void makeNewInstance(final Map<Long, DBVersion> versions,
			final Map<Long, List<DBCodeFragment>> codeFragmentsByCloneClassId,
			final InternalDBCloneClass rawResult, final long id) {
		final DBCloneClass newInstance = new DBCloneClass(id, null, null);

		if (autoRefresh) {
			if (deepRefresh) {
				newInstance.setVersion(versions.get(rawResult.getVersionId()));
			} else {
				newInstance.setVersion(new DBVersion(rawResult.getId(), null,
						null, null, null, null, null));
			}
			newInstance.setCodeFragments(new ArrayList<>());
			newInstance.getCodeFragments().addAll(
					codeFragmentsByCloneClassId.get(id));
		}

		retrievedElements.put(id, newInstance);
	}

	private class InternalDBCloneClass implements
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

	private class RowMapper implements RawRowMapper<InternalDBCloneClass> {

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
