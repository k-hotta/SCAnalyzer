package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RawRowMapper;

/**
 * The DAO for {@link DBRawClonedFragment}.
 * 
 * @author k-hotta
 * 
 * @see DBRawClonedFragment
 */
public class RawClonedFragmentDao
		extends
		AbstractDataDao<DBRawClonedFragment, RawClonedFragmentDao.InternalDBRawClonedFragment> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(RawClonedFragmentDao.class);

	/**
	 * The DAO for versions. <br>
	 * This is for refreshing.
	 */
	private VersionDao versionDao;

	/**
	 * The DAO for source files. <br>
	 * This is for refreshing.
	 */
	private SourceFileDao sourceFileDao;

	/**
	 * The DAO for raw clone classes. <br>
	 * This is for refreshing.
	 */
	private RawCloneClassDao rawCloneClassDao;

	@SuppressWarnings("unchecked")
	public RawClonedFragmentDao() throws SQLException {
		super((Dao<DBRawClonedFragment, Long>) DBManager.getInstance()
				.getNativeDao(DBRawClonedFragment.class));
		versionDao = null;
		sourceFileDao = null;
		rawCloneClassDao = null;
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

	/**
	 * Set the DAO for RawCloneClass with the specified one
	 * 
	 * @param rawCloneClassDao
	 *            the DAO to be set
	 */
	void setRawCloneClassDao(final RawCloneClassDao rawCloneClassDao) {
		this.rawCloneClassDao = rawCloneClassDao;
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	protected String getTableName() {
		return TableName.RAW_CLONED_FRAGMENT;
	}

	@Override
	protected String getIdColumnName() {
		return DBRawClonedFragment.ID_COLUMN_NAME;
	}

	@Override
	protected DBRawClonedFragment refreshChildren(DBRawClonedFragment element)
			throws Exception {
		if (deepRefresh) {
			versionDao.refresh(element.getVersion());
			sourceFileDao.refresh(element.getSourceFile());
			rawCloneClassDao.refresh(element.getCloneClass());
		}

		return element;
	}

	@Override
	protected Collection<DBRawClonedFragment> refreshChildrenForAll(
			Collection<DBRawClonedFragment> elements) throws Exception {
		if (deepRefresh) {
			final Set<DBVersion> versionsToBeRefreshed = new HashSet<>();
			final Set<DBSourceFile> sourceFilesToBeRefreshed = new HashSet<>();
			final Set<DBRawCloneClass> rawCloneClassesToBeRefreshed = new HashSet<>();

			for (final DBRawClonedFragment element : elements) {
				versionsToBeRefreshed.add(element.getVersion());
				sourceFilesToBeRefreshed.add(element.getSourceFile());
				rawCloneClassesToBeRefreshed.add(element.getCloneClass());
			}

			versionDao.refreshAll(versionsToBeRefreshed);
			sourceFileDao.refreshAll(sourceFilesToBeRefreshed);
			rawCloneClassDao.refreshAll(rawCloneClassesToBeRefreshed);

			for (final DBRawClonedFragment element : elements) {
				element.setVersion(versionDao.get(element.getVersion().getId()));
				element.setSourceFile(sourceFileDao.get(element.getSourceFile()
						.getId()));
				element.setCloneClass(rawCloneClassDao.get(element
						.getCloneClass().getId()));
			}
		}

		return elements;
	}

	/**
	 * Get the elements whose versions are the specified one.
	 * 
	 * @param version
	 *            version as a query
	 * @return a list of elements whose versions are the specified one
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Collection<DBRawClonedFragment> getWithVersion(
			final DBVersion version) throws Exception {
		return refreshAll(originalDao.queryForEq(
				DBRawClonedFragment.VERSION_COLUMN_NAME, version));
	}

	/**
	 * Get the elements whose source files are the specified one.
	 * 
	 * @param sourceFile
	 *            source file as a query
	 * @return a list of elements whose source files are the specified one
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Collection<DBRawClonedFragment> getWithSourceFile(
			final DBSourceFile sourceFile) throws Exception {
		return refreshAll(originalDao.queryForEq(
				DBRawClonedFragment.SOURCE_FILE_COLUMN_NAME, sourceFile));
	}

	/**
	 * Get the elements whose raw clone classes are the specified one.
	 * 
	 * @param rawCloneClass
	 *            raw clone class as a query
	 * @return a list of elements whose raw clone classes are the specified one
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Collection<DBRawClonedFragment> getWithRawCloneClass(
			final DBRawCloneClass rawCloneClass) throws Exception {
		return refreshAll(originalDao.queryForEq(
				DBRawClonedFragment.CLONE_CLASS_COLUMN_NAME, rawCloneClass));
	}

	/**
	 * Get the elements whose start lines are the specified value.
	 * 
	 * @param startLine
	 *            start line as a query
	 * @return a list of elements whose start lines are the specified value
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Collection<DBRawClonedFragment> getWithStartLine(final int startLine)
			throws Exception {
		return refreshAll(originalDao.queryForEq(
				DBRawClonedFragment.START_LINE_COLUMN_NAME, startLine));
	}

	/**
	 * Get the elements whose lengths are the specified value.
	 * 
	 * @param length
	 *            length as a query
	 * @return a list of elements whose lengths are the specified value
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public Collection<DBRawClonedFragment> getWithLength(final int length)
			throws Exception {
		return refreshAll(originalDao.queryForEq(
				DBRawClonedFragment.LENGTH_COLUMN_NAME, length));
	}

	class InternalDBRawClonedFragment implements
			InternalDataRepresentation<DBRawClonedFragment> {

		private final Long id;

		private final Long versionId;

		private final Long sourceFileId;

		private final Long rawCloneClassId;

		private final Integer startLine;

		private final Integer length;

		public InternalDBRawClonedFragment(final Long id, final Long versionId,
				final Long sourceFileId, final Long rawCloneClassId,
				final Integer startLine, final Integer length) {
			this.id = id;
			this.versionId = versionId;
			this.sourceFileId = sourceFileId;
			this.rawCloneClassId = rawCloneClassId;
			this.startLine = startLine;
			this.length = length;
		}

		@Override
		public final Long getId() {
			return id;
		}

		public final Long getVersionId() {
			return versionId;
		}

		public final Long getSourceFileId() {
			return sourceFileId;
		}

		public final Long getRawCloneClassId() {
			return rawCloneClassId;
		}

		public final Integer getStartLine() {
			return startLine;
		}

		public final Integer getLength() {
			return length;
		}

	}

	class RowMapper implements RawRowMapper<InternalDBRawClonedFragment> {

		@Override
		public InternalDBRawClonedFragment mapRow(String[] columnNames,
				String[] resultColumns) throws SQLException {
			Long id = null;
			Long versionId = null;
			Long sourceFileId = null;
			Long rawCloneClassId = null;
			Integer startLine = null;
			Integer length = null;

			for (int i = 0; i < columnNames.length; i++) {
				final String columnName = columnNames[i];
				final String resultColumn = resultColumns[i];

				switch (columnName) {
				case DBRawClonedFragment.ID_COLUMN_NAME:
					id = Long.parseLong(resultColumn);
					break;
				case DBRawClonedFragment.VERSION_COLUMN_NAME:
					versionId = Long.parseLong(resultColumn);
					break;
				case DBRawClonedFragment.SOURCE_FILE_COLUMN_NAME:
					sourceFileId = Long.parseLong(resultColumn);
					break;
				case DBRawClonedFragment.CLONE_CLASS_COLUMN_NAME:
					rawCloneClassId = Long.parseLong(resultColumn);
					break;
				case DBRawClonedFragment.START_LINE_COLUMN_NAME:
					startLine = Integer.parseInt(resultColumn);
					break;
				case DBRawClonedFragment.LENGTH_COLUMN_NAME:
					length = Integer.parseInt(resultColumn);
					break;
				}
			}

			return new InternalDBRawClonedFragment(id, versionId, sourceFileId,
					rawCloneClassId, startLine, length);
		}
	}

	@Override
	protected RawRowMapper<InternalDBRawClonedFragment> getRowMapper()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void updateRelativeElementIds(
			InternalDBRawClonedFragment rawResult) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void retrieveRelativeElements(
			Map<String, Set<Long>> relativeElementIds) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected DBRawClonedFragment makeInstance(
			InternalDBRawClonedFragment rawResult) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<Long, DBRawClonedFragment> queryRaw(String query)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
