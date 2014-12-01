package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange.Type;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;

/**
 * The DAO for {@link DBFileChange}.
 * 
 * @author k-hotta
 * 
 * @see DBFileChange
 */
public class FileChangeDao extends AbstractDataDao<DBFileChange> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(FileChangeDao.class);

	/**
	 * The DAO for source files. <br>
	 * This is for refreshing.
	 */
	private SourceFileDao sourceFileDao;

	/**
	 * The DAO for versions. <br>
	 * This is for refreshing.
	 */
	private VersionDao versionDao;

	@SuppressWarnings("unchecked")
	public FileChangeDao() throws SQLException {
		super((Dao<DBFileChange, Long>) DBManager.getInstance().getNativeDao(
				DBFileChange.class));
		this.sourceFileDao = null;
		this.versionDao = null;
	}

	/**
	 * Set the DAO for SourceFile with the specified one
	 * 
	 * @param sourceFileDao
	 *            the DAO to be set
	 */
	void setSourceFileDao(final SourceFileDao sourceFileDao) {
		this.sourceFileDao = sourceFileDao;
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
		return TableName.FILE_CHANGE;
	}

	@Override
	protected String getIdColumnName() {
		return DBFileChange.ID_COLUMN_NAME;
	}

	@Override
	protected DBFileChange refreshChildren(DBFileChange element)
			throws Exception {
		if (element.getOldSourceFile() != null) {
			sourceFileDao.refresh(element.getOldSourceFile());
		}
		if (element.getNewSourceFile() != null) {
			sourceFileDao.refresh(element.getNewSourceFile());
		}

		if (deepRefresh) {
			versionDao.refresh(element.getVersion());
		}

		return element;
	}

	@Override
	protected Collection<DBFileChange> refreshChildrenForAll(
			Collection<DBFileChange> elements) throws Exception {
		final Set<DBSourceFile> sourceFilesToBeRefreshed = new HashSet<>();
		for (final DBFileChange element : elements) {
			if (element.getOldSourceFile() != null) {
				sourceFilesToBeRefreshed.add(element.getOldSourceFile());
			}
			if (element.getNewSourceFile() != null) {
				sourceFilesToBeRefreshed.add(element.getNewSourceFile());
			}
		}
		sourceFileDao.refreshAll(sourceFilesToBeRefreshed);
		for (final DBFileChange element : elements) {
			if (element.getOldSourceFile() != null) {
				element.setOldSourceFile(sourceFileDao.get(element
						.getOldSourceFile().getId()));
			}
			if (element.getNewSourceFile() != null) {
				element.setNewSourceFile(sourceFileDao.get(element
						.getNewSourceFile().getId()));
			}
		}

		if (deepRefresh) {
			final Set<DBVersion> versionsToBeRefreshed = new HashSet<>();
			for (final DBFileChange element : elements) {
				versionsToBeRefreshed.add(element.getVersion());
			}
			versionDao.refreshAll(versionsToBeRefreshed);
			for (final DBFileChange element : elements) {
				element.setVersion(versionDao.get(element.getVersion().getId()));
			}
		}

		return elements;
	}

	/**
	 * Get the elements whose old source files are the specified one.
	 * 
	 * @param oldSourceFile
	 *            old source file as a query
	 * @return a list of the elements whose old source files are the specified
	 *         one
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Collection<DBFileChange> getWithOldSourceFile(
			final DBSourceFile oldSourceFile) throws Exception {
		return refreshAll(originalDao.queryForEq(
				DBFileChange.OLD_SOURCE_FILE_COLUMN_NAME, oldSourceFile));
	}

	/**
	 * Get the elements whose new source files are the specified one.
	 * 
	 * @param newSourceFile
	 *            new source file as a query
	 * @return a list of the elements whose new source files are the specified
	 *         one
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Collection<DBFileChange> getWithNewSourceFile(
			final DBSourceFile newSourceFile) throws Exception {
		return refreshAll(originalDao.queryForEq(
				DBFileChange.NEW_SOURCE_FILE_COLUMN_NAME, newSourceFile));
	}

	/**
	 * Get the elements whose versions are the specified one
	 * 
	 * @param version
	 *            version as a query
	 * @return a list of the elements whose versions are the specified one
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Collection<DBFileChange> getWithVersion(final DBVersion version)
			throws Exception {
		return refreshAll(originalDao.queryForEq(
				DBFileChange.VERSION_COLUMN_NAME, version));
	}

	/**
	 * Get the elements whose types are the specified one.
	 * 
	 * @param type
	 *            type as a query
	 * @return a list of the elements whose types are the specified one
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Collection<DBFileChange> getWithType(final Type type)
			throws Exception {
		return refreshAll(originalDao.queryForEq(DBFileChange.TYPE_COLUMN_NAME,
				type));
	}

	@Override
	protected Map<Long, DBFileChange> queryRaw(String query) throws Exception {
		final GenericRawResults<InternalDBFileChange> rawResults = originalDao
				.queryRaw(query, new RowMapper());

		final SortedMap<Long, DBFileChange> result = new TreeMap<>();
		final Set<Long> sourceFileIdsToBeRetrieved = new TreeSet<>();
		final Set<Long> versionIdsToBeRetrieved = new TreeSet<>();

		for (final InternalDBFileChange rawResult : rawResults) {
			final long id = rawResult.getId();
			if (!retrievedElements.containsKey(id)) {
				if (rawResult.getOldSourceFileId() != null) {
					sourceFileIdsToBeRetrieved.add(rawResult
							.getOldSourceFileId());
				}
				if (rawResult.getNewSourceFileId() != null) {
					sourceFileIdsToBeRetrieved.add(rawResult
							.getNewSourceFileId());
				}
				versionIdsToBeRetrieved.add(rawResult.getVersionId());
			}
		}

		final Map<Long, DBSourceFile> sourceFiles = sourceFileDao
				.get(sourceFileIdsToBeRetrieved);
		final Map<Long, DBVersion> versions = (deepRefresh) ? versionDao
				.get(versionIdsToBeRetrieved) : new TreeMap<>();

		for (final InternalDBFileChange rawResult : rawResults) {
			final long id = rawResult.getId();

			if (!retrievedElements.containsKey(id)) {
				final DBFileChange newInstance = new DBFileChange(id, null,
						null, null, null);

				if (autoRefresh) {
					if (rawResult.getOldSourceFileId() != null) {
						newInstance.setOldSourceFile(sourceFiles.get(rawResult
								.getOldSourceFileId()));
					}
					if (rawResult.getNewSourceFileId() != null) {
						newInstance.setNewSourceFile(sourceFiles.get(rawResult
								.getNewSourceFileId()));
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

			result.put(id, retrievedElements.get(id));
		}

		return Collections.unmodifiableSortedMap(result);
	}

	private class InternalDBFileChange implements
			InternalDataRepresentation<DBFileChange> {

		private final Long id;

		private final Long oldSourceFileId;

		private final Long newSourceFileId;

		private final String type;

		private final Long versionId;

		public InternalDBFileChange(final Long id, final Long oldSourceFileId,
				final Long newSourceFileId, final String type,
				final Long versionId) {
			this.id = id;
			this.oldSourceFileId = oldSourceFileId;
			this.newSourceFileId = newSourceFileId;
			this.type = type;
			this.versionId = versionId;
		}

		@Override
		public final Long getId() {
			return id;
		}

		public final Long getOldSourceFileId() {
			return oldSourceFileId;
		}

		public final Long getNewSourceFileId() {
			return newSourceFileId;
		}

		public final String getType() {
			return type;
		}

		public final Long getVersionId() {
			return versionId;
		}

	}

	private class RowMapper implements RawRowMapper<InternalDBFileChange> {

		@Override
		public InternalDBFileChange mapRow(String[] columnNames,
				String[] resultColumns) throws SQLException {
			Long id = null;
			Long oldSourceFileId = null;
			Long newSourceFileId = null;
			String type = null;
			Long versionId = null;

			for (int i = 0; i < columnNames.length; i++) {
				final String columnName = columnNames[i];
				final String resultColumn = resultColumns[i];

				switch (columnName) {
				case DBFileChange.ID_COLUMN_NAME:
					id = Long.parseLong(resultColumn);
					break;
				case DBFileChange.OLD_SOURCE_FILE_COLUMN_NAME:
					oldSourceFileId = Long.parseLong(resultColumn);
					break;
				case DBFileChange.NEW_SOURCE_FILE_COLUMN_NAME:
					newSourceFileId = Long.parseLong(resultColumn);
					break;
				case DBFileChange.TYPE_COLUMN_NAME:
					type = resultColumn;
					break;
				case DBFileChange.VERSION_COLUMN_NAME:
					versionId = Long.parseLong(resultColumn);
					break;
				}
			}

			return new InternalDBFileChange(id, oldSourceFileId,
					newSourceFileId, type, versionId);
		}

	}

}
