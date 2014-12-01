package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RawRowMapper;

/**
 * The DAO for {@link DBSegment}.
 * 
 * @author k-hotta
 * 
 * @see DBSegment
 */
public class SegmentDao extends
		AbstractDataDao<DBSegment, SegmentDao.InternalDBSegment> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager.getLogger(SegmentDao.class);

	/**
	 * The DAO for source files. <br>
	 * This is for refreshing.
	 */
	private SourceFileDao sourceFileDao;

	/**
	 * The DAO for code fragments. <br>
	 * This is for refreshing.
	 */
	private CodeFragmentDao codeFragmentDao;

	@SuppressWarnings("unchecked")
	public SegmentDao() throws SQLException {
		super((Dao<DBSegment, Long>) DBManager.getInstance().getNativeDao(
				DBSegment.class));
		this.sourceFileDao = null;
		this.codeFragmentDao = null;
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
	 * Set the DAO for CodeFragment with the specified one.
	 * 
	 * @param codeFragmentDao
	 *            the DAO to be set
	 */
	void setCodeFragmentDao(final CodeFragmentDao codeFragmentDao) {
		this.codeFragmentDao = codeFragmentDao;
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	protected String getTableName() {
		return TableName.SEGMENT;
	}

	@Override
	protected String getIdColumnName() {
		return DBSegment.ID_COLUMN_NAME;
	}

	@Override
	protected DBSegment refreshChildren(DBSegment element) throws Exception {
		if (deepRefresh) {
			sourceFileDao.refresh(element.getSourceFile());
			codeFragmentDao.refresh(element.getCodeFragment());
		}
		return element;
	}

	@Override
	protected Collection<DBSegment> refreshChildrenForAll(
			Collection<DBSegment> elements) throws Exception {
		if (deepRefresh) {
			final Set<DBSourceFile> sourceFilesToBeRefreshed = new HashSet<>();
			final Set<DBCodeFragment> codeFragmentsToBeRefreshed = new HashSet<>();

			for (final DBSegment element : elements) {
				sourceFilesToBeRefreshed.add(element.getSourceFile());
				codeFragmentsToBeRefreshed.add(element.getCodeFragment());
			}

			sourceFileDao.refreshAll(sourceFilesToBeRefreshed);
			codeFragmentDao.refreshAll(codeFragmentsToBeRefreshed);

			for (final DBSegment element : elements) {
				element.setSourceFile(sourceFileDao.get(element.getSourceFile()
						.getId()));
				element.setCodeFragment(codeFragmentDao.get(element
						.getCodeFragment().getId()));
			}
		}

		return elements;
	}

	/**
	 * Get the elements whose source files are the specified one.
	 * 
	 * @param sourceFile
	 *            source file as a key
	 * @return a list of the elements whose source files are the specified one
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Collection<DBSegment> getWithSourceFile(final DBSourceFile sourceFile)
			throws Exception {
		return refreshAll(originalDao.queryForEq(
				DBSegment.SOURCE_FILE_COLUMN_NAME, sourceFile));
	}

	/**
	 * Get the elements whose owner code fragment IDs are the specified one.
	 * 
	 * @param ids
	 *            a collection contains IDs of interest
	 * @return a map between ID of an element and the instance itself.
	 */
	public Map<Long, DBSegment> getWithCodeFragmentIds(
			final Collection<Long> ids) {
		// TODO implement
		return null;
	}

	class InternalDBSegment implements InternalDataRepresentation<DBSegment> {

		private final Long id;

		private final Long sourceFileId;

		private final Integer startPosition;

		private final Integer endPosition;

		private final Long codeFragmentId;

		public InternalDBSegment(final Long id, final Long sourceFileId,
				final Integer startPosition, final Integer endPosition,
				final Long codeFragmentId) {
			this.id = id;
			this.sourceFileId = sourceFileId;
			this.startPosition = startPosition;
			this.endPosition = endPosition;
			this.codeFragmentId = codeFragmentId;
		}

		public final Long getId() {
			return id;
		}

		public final Long getSourceFileId() {
			return sourceFileId;
		}

		public final Integer getStartPosition() {
			return startPosition;
		}

		public final Integer getEndPosition() {
			return endPosition;
		}

		public final Long getCodeFragmentId() {
			return codeFragmentId;
		}

	}

	class RowMapper implements RawRowMapper<InternalDBSegment> {

		@Override
		public InternalDBSegment mapRow(String[] columnNames,
				String[] resultColumns) throws SQLException {
			Long id = null;
			Long sourceFileId = null;
			Integer startPosition = null;
			Integer endPosition = null;
			Long codeFragmentId = null;

			for (int i = 0; i < columnNames.length; i++) {
				final String columnName = columnNames[i];
				final String resultColumn = resultColumns[i];

				switch (columnName) {
				case DBSegment.ID_COLUMN_NAME:
					id = Long.parseLong(resultColumn);
					break;
				case DBSegment.SOURCE_FILE_COLUMN_NAME:
					sourceFileId = Long.parseLong(resultColumn);
					break;
				case DBSegment.START_POSITION_COLUMN_NAME:
					startPosition = Integer.parseInt(resultColumn);
					break;
				case DBSegment.END_POSITION_COLUMN_NAME:
					endPosition = Integer.parseInt(resultColumn);
					break;
				case DBSegment.CODE_FRAGMENT_COLUMN_NAME:
					codeFragmentId = Long.parseLong(resultColumn);
					break;
				}
			}

			return new InternalDBSegment(id, sourceFileId, startPosition,
					endPosition, codeFragmentId);
		}

	}

	@Override
	protected RawRowMapper<InternalDBSegment> getRowMapper() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void updateRelativeElementIds(InternalDBSegment rawResult)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void retrieveRelativeElements(
			Map<String, Set<Long>> relativeElementIds) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected DBSegment makeInstance(InternalDBSegment rawResult) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<Long, DBSegment> queryRaw(String query) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
