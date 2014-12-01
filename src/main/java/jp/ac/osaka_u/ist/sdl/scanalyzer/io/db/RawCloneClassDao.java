package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RawRowMapper;

/**
 * The DAO for {@link DBRawCloneClass}.
 * 
 * @author k-hotta
 * 
 * @see DBRawCloneClass
 */
public class RawCloneClassDao
		extends
		AbstractDataDao<DBRawCloneClass, RawCloneClassDao.InternalDBRawCloneClass> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(RawCloneClassDao.class);

	/**
	 * The DAO for versions. <br>
	 * This is for refreshing
	 */
	private VersionDao versionDao;

	/**
	 * The DAO for raw cloned fragments. <br>
	 * This is for refreshing.
	 */
	private RawClonedFragmentDao rawClonedFragmentDao;

	@SuppressWarnings("unchecked")
	public RawCloneClassDao() throws SQLException {
		super((Dao<DBRawCloneClass, Long>) DBManager.getInstance()
				.getNativeDao(DBRawCloneClass.class));
		this.versionDao = null;
		this.rawClonedFragmentDao = null;
	}

	/**
	 * Set the DAO for SourceFile with the specified one
	 * 
	 * @param rawClonedFragmentDao
	 *            the DAO to be set
	 */
	void setRawClonedFragmentDao(final RawClonedFragmentDao rawClonedFragmentDao) {
		this.rawClonedFragmentDao = rawClonedFragmentDao;
	}

	/**
	 * Set the DAO for Version with the specified one
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
		return TableName.RAW_CLONE_CLASS;
	}

	@Override
	protected String getIdColumnName() {
		return DBRawCloneClass.ID_COLUMN_NAME;
	}

	@Override
	protected DBRawCloneClass refreshChildren(DBRawCloneClass element)
			throws Exception {
		rawClonedFragmentDao.refreshAll(element.getElements());

		if (deepRefresh) {
			versionDao.refresh(element.getVersion());
		}

		return element;
	}

	@Override
	protected Collection<DBRawCloneClass> refreshChildrenForAll(
			Collection<DBRawCloneClass> elements) throws Exception {
		final Set<DBRawClonedFragment> rawClonedFragmentsToBeRefreshed = new HashSet<>();
		for (final DBRawCloneClass element : elements) {
			rawClonedFragmentsToBeRefreshed.addAll(element.getElements());
		}
		rawClonedFragmentDao.refreshAll(rawClonedFragmentsToBeRefreshed);
		for (final DBRawCloneClass element : elements) {
			final List<DBRawClonedFragment> toBeStored = new ArrayList<>();
			for (final DBRawClonedFragment rawClonedFragment : element
					.getElements()) {
				toBeStored.add(rawClonedFragmentDao.get(rawClonedFragment
						.getId()));
			}
			element.setElements(toBeStored);
		}

		if (deepRefresh) {
			final Set<DBVersion> versionsToBeRefreshed = new HashSet<>();
			for (final DBRawCloneClass element : elements) {
				versionsToBeRefreshed.add(element.getVersion());
			}
			versionDao.refreshAll(versionsToBeRefreshed);
			for (final DBRawCloneClass element : elements) {
				element.setVersion(versionDao.get(element.getVersion().getId()));
			}
		}

		return elements;
	}

	/**
	 * Get the elements whose versions are the specified one.
	 * 
	 * @param version
	 *            version as a query
	 * @return a list of the elements whose versions are the specified one
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Collection<DBRawCloneClass> getWithVersion(final DBVersion version)
			throws Exception {
		return refreshAll(originalDao.queryForEq(
				DBRawCloneClass.VERSION_COLUMN_NAME, version));
	}

	class InternalDBRawCloneClass implements
			InternalDataRepresentation<DBRawCloneClass> {

		private final Long id;

		private final Long versionId;

		public InternalDBRawCloneClass(final Long id, final Long versionId) {
			this.id = id;
			this.versionId = versionId;
		}

		@Override
		public final Long getId() {
			return id;
		}

		public final Long getVersionId() {
			return versionId;
		}

	}

	class RowMapper implements RawRowMapper<InternalDBRawCloneClass> {

		@Override
		public InternalDBRawCloneClass mapRow(String[] columnNames,
				String[] resultColumns) throws SQLException {
			Long id = null;
			Long versionId = null;

			for (int i = 0; i < columnNames.length; i++) {
				final String columnName = columnNames[i];
				final String resultColumn = resultColumns[i];

				switch (columnName) {
				case DBRawCloneClass.ID_COLUMN_NAME:
					id = Long.parseLong(resultColumn);
					break;
				case DBRawCloneClass.VERSION_COLUMN_NAME:
					versionId = Long.parseLong(resultColumn);
					break;
				}
			}

			return new InternalDBRawCloneClass(id, versionId);
		}

	}

	@Override
	protected RawRowMapper<InternalDBRawCloneClass> getRowMapper()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void updateRelativeElementIds(InternalDBRawCloneClass rawResult)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void retrieveRelativeElements(
			Map<String, Set<Long>> relativeElementIds) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected DBRawCloneClass makeInstance(InternalDBRawCloneClass rawResult) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<Long, DBRawCloneClass> queryRaw(String query)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
