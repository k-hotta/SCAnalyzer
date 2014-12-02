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
import java.util.concurrent.Callable;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersionSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

/**
 * The DAO for {@link DBVersion}.
 * 
 * @author k-hotta
 * 
 * @see DBVersion
 * @see DBVersionSourceFile
 */
public class VersionDao extends
		AbstractDataDao<DBVersion, VersionDao.InternalDBVersion> {

	/**
	 * The logger
	 */
	private static Logger logger = LogManager.getLogger(VersionDao.class);

	/**
	 * The DAO for Revision. <br>
	 * This is for refreshing.
	 */
	private RevisionDao revisionDao;

	/**
	 * The DAO for FileChange. <br>
	 * This is for refreshing
	 */
	private FileChangeDao fileChangeDao;

	/**
	 * The DAO for RawCloneClass. <br>
	 * This is for refreshing.
	 */
	private RawCloneClassDao rawCloneClassDao;

	/**
	 * The DAO for SourceFile. <br>
	 * This is for refreshing and to get corresponding source files.
	 */
	private final Dao<DBSourceFile, Long> nativeSourceFileDao;

	/**
	 * The DAO for CloneClass. <br>
	 * This is for refreshing.
	 */
	private CloneClassDao cloneClassDao;

	/**
	 * The DAO for CloneClassMapping. <br>
	 * This is for refreshing.
	 */
	private CloneClassMappingDao cloneClassMappingDao;

	/**
	 * The DAO for VersionSourceFile. <br>
	 * This is for retrieving corresponding source files to each version.
	 */
	private final Dao<DBVersionSourceFile, Long> nativeVersionSourceFileDao;

	/**
	 * The data DAO for SourceFile
	 */
	private SourceFileDao sourceFileDao;

	/**
	 * The query to get corresponding source files
	 */
	private PreparedQuery<DBSourceFile> sourceFilesForVersionQuery;

	@SuppressWarnings("unchecked")
	public VersionDao() throws SQLException {
		super((Dao<DBVersion, Long>) DBManager.getInstance().getNativeDao(
				DBVersion.class));
		this.revisionDao = null;
		this.fileChangeDao = null;
		this.rawCloneClassDao = null;
		this.nativeSourceFileDao = this.manager
				.getNativeDao(DBSourceFile.class);
		this.cloneClassDao = null;
		this.cloneClassMappingDao = null;
		this.nativeVersionSourceFileDao = this.manager
				.getNativeDao(DBVersionSourceFile.class);
		this.sourceFileDao = null;
		this.sourceFilesForVersionQuery = null;
	}

	/**
	 * Set the DAO for Revision with the specified one.
	 * 
	 * @param revisionDao
	 *            the DAO to be set
	 */
	void setRevisionDao(final RevisionDao revisionDao) {
		this.revisionDao = revisionDao;
	}

	/**
	 * Set the DAO for FileChange with the specified one.
	 * 
	 * @param fileChangeDao
	 *            the DAO to be set
	 */
	void setFileChangeDao(final FileChangeDao fileChangeDao) {
		this.fileChangeDao = fileChangeDao;
	}

	/**
	 * Set the DAO for RawCloneClass with the specified one.
	 * 
	 * @param rawCloneClassDao
	 *            the DAO to be set
	 */
	void setRawCloneClassDao(final RawCloneClassDao rawCloneClassDao) {
		this.rawCloneClassDao = rawCloneClassDao;
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
	 * Set the DAO for CloneClassMapping with the specified one.
	 * 
	 * @param cloneClassMappingDao
	 *            the DAO to be set
	 */
	void setCloneClassMappingDao(final CloneClassMappingDao cloneClassMappingDao) {
		this.cloneClassMappingDao = cloneClassMappingDao;
	}

	/**
	 * Set the DAO for SourceFile with the specified one.
	 * 
	 * @param sourceFileDao
	 *            the DAO to be set
	 */
	void setSourceFileDao(final SourceFileDao sourceFileDao) {
		this.sourceFileDao = sourceFileDao;
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	protected String getTableName() {
		return TableName.VERSION;
	}

	@Override
	protected String getIdColumnName() {
		return DBVersion.ID_COLUMN_NAME;
	}

	@Override
	protected DBVersion refreshChildren(DBVersion element) throws Exception {
		revisionDao.refresh(element.getRevision());

		fileChangeDao.refreshAll(element.getFileChanges());

		rawCloneClassDao.refreshAll(element.getRawCloneClasses());

		cloneClassDao.refreshAll(element.getCloneClasses());

		cloneClassMappingDao.refreshAll(element.getCloneClassMappings());

		element.setSourceFiles(sourceFileDao
				.refreshAll(getCorrespondingSourceFiles(element)));

		return element;
	}

	@Override
	protected Collection<DBVersion> refreshChildrenForAll(
			Collection<DBVersion> elements) throws Exception {
		final Set<DBRevision> revisionsToBeRefreshed = new HashSet<>();
		final Set<DBFileChange> fileChangesToBeRefreshed = new HashSet<>();
		final Set<DBRawCloneClass> rawCloneClassesToBeRefreshed = new HashSet<>();
		final Set<DBCloneClass> cloneClassesToBeRefreshed = new HashSet<>();
		final Set<DBCloneClassMapping> cloneClassMappingsToBeRefreshed = new HashSet<>();
		final Set<DBSourceFile> sourceFilesToBeRefreshed = new HashSet<>();
		final Map<Long, Collection<DBSourceFile>> sourceFilesInVersions = new TreeMap<>();

		for (final DBVersion element : elements) {
			revisionsToBeRefreshed.add(element.getRevision());
			fileChangesToBeRefreshed.addAll(element.getFileChanges());
			rawCloneClassesToBeRefreshed.addAll(element.getRawCloneClasses());
			cloneClassesToBeRefreshed.addAll(element.getCloneClasses());
			cloneClassMappingsToBeRefreshed.addAll(element
					.getCloneClassMappings());

			final Collection<DBSourceFile> sourceFilesInVersion = getCorrespondingSourceFiles(element);
			sourceFilesToBeRefreshed.addAll(sourceFilesInVersion);
			sourceFilesInVersions.put(element.getId(), sourceFilesInVersion);
		}

		revisionDao.refreshAll(revisionsToBeRefreshed);
		fileChangeDao.refreshAll(fileChangesToBeRefreshed);
		rawCloneClassDao.refreshAll(rawCloneClassesToBeRefreshed);
		cloneClassDao.refreshAll(cloneClassesToBeRefreshed);
		cloneClassMappingDao.refreshAll(cloneClassMappingsToBeRefreshed);
		sourceFileDao.refreshAll(sourceFilesToBeRefreshed);

		for (final DBVersion element : elements) {
			element.setRevision(revisionDao.get(element.getRevision().getId()));

			final List<DBFileChange> fileChangesToBeStored = new ArrayList<>();
			for (final DBFileChange fileChange : element.getFileChanges()) {
				fileChangesToBeStored
						.add(fileChangeDao.get(fileChange.getId()));
			}
			element.setFileChanges(fileChangesToBeStored);

			final List<DBRawCloneClass> rawCloneClassesToBeStored = new ArrayList<>();
			for (final DBRawCloneClass rawCloneClass : element
					.getRawCloneClasses()) {
				rawCloneClassesToBeStored.add(rawCloneClassDao
						.get(rawCloneClass.getId()));
			}
			element.setRawCloneClasses(rawCloneClassesToBeStored);

			final List<DBCloneClass> cloneClassesToBeStored = new ArrayList<>();
			for (final DBCloneClass cloneClass : element.getCloneClasses()) {
				cloneClassesToBeStored
						.add(cloneClassDao.get(cloneClass.getId()));
			}
			element.setCloneClasses(cloneClassesToBeStored);

			final List<DBCloneClassMapping> cloneClassMappingsToBeStored = new ArrayList<>();
			for (final DBCloneClassMapping cloneClassMapping : element
					.getCloneClassMappings()) {
				cloneClassMappingsToBeStored.add(cloneClassMappingDao
						.get(cloneClassMapping.getId()));
			}
			element.setCloneClassMappings(cloneClassMappingsToBeStored);

			final List<DBSourceFile> sourceFilesToBeStored = new ArrayList<>();
			for (final DBSourceFile sourceFile : sourceFilesInVersions
					.get(element.getId())) {
				sourceFilesToBeStored
						.add(sourceFileDao.get(sourceFile.getId()));
			}
			element.setSourceFiles(sourceFilesToBeStored);
		}

		return elements;
	}

	@Override
	protected RawRowMapper<InternalDBVersion> getRowMapper() throws Exception {
		return new RowMapper();
	}

	@Override
	protected void initializeRelativeElementIds(
			Map<String, Set<Long>> relativeElementIds) {
		relativeElementIds.put(TableName.VERSION, new TreeSet<Long>());
		relativeElementIds.put(TableName.REVISION, new TreeSet<Long>());
	}

	@Override
	protected void initializeForeignChildElementIds(
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds) {
		foreignChildElementIds.put(TableName.FILE_CHANGE,
				new TreeMap<Long, Set<Long>>());
		foreignChildElementIds.put(TableName.RAW_CLONE_CLASS,
				new TreeMap<Long, Set<Long>>());
		foreignChildElementIds.put(TableName.CLONE_CLASS,
				new TreeMap<Long, Set<Long>>());
		foreignChildElementIds.put(TableName.SOURCE_FILE,
				new TreeMap<Long, Set<Long>>());
	}

	@Override
	protected void updateRelativeElementIds(
			Map<String, Set<Long>> relativeElementIds,
			InternalDBVersion rawResult) throws Exception {
		relativeElementIds.get(TableName.VERSION).add(rawResult.getId());
		relativeElementIds.get(TableName.REVISION).add(
				rawResult.getRevisionId());
	}

	@Override
	protected void retrieveRelativeElements(
			Map<String, Set<Long>> relativeElementIds,
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds)
			throws Exception {
		// retrieve revisions
		final Set<Long> revisionIds = relativeElementIds
				.get(TableName.REVISION);
		revisionDao.get(revisionIds);

		/*
		 * this is used for retrieving foreign collection elements
		 */
		final Set<Long> versionIds = relativeElementIds.get(TableName.VERSION);

		// retrieve file changes
		{
			final Map<Long, DBFileChange> fileChanges = fileChangeDao
					.getWithVersionIds(versionIds);
			final Map<Long, Set<Long>> fileChangeIdsByVersion = foreignChildElementIds
					.get(TableName.FILE_CHANGE);
			fileChangeIdsByVersion.putAll(ForeignCollectionRetrieveHelper
					.sortIdsByParentElement(fileChanges.values(), (e) -> {
						return e.getVersion().getId();
					}));
		}

		// retrieve raw clone classes
		{
			final Map<Long, DBRawCloneClass> rawCloneClasses = rawCloneClassDao
					.getWithVersionIds(versionIds);
			final Map<Long, Set<Long>> rawCloneClassIdsByVersion = foreignChildElementIds
					.get(TableName.RAW_CLONE_CLASS);
			rawCloneClassIdsByVersion.putAll(ForeignCollectionRetrieveHelper
					.sortIdsByParentElement(rawCloneClasses.values(), (e) -> {
						return e.getVersion().getId();
					}));
		}

		// retrieve clone classes
		{
			final Map<Long, DBCloneClass> cloneClasses = cloneClassDao
					.getWithVersionIds(versionIds);
			final Map<Long, Set<Long>> cloneClassIdsByVersion = foreignChildElementIds
					.get(TableName.CLONE_CLASS);
			cloneClassIdsByVersion.putAll(ForeignCollectionRetrieveHelper
					.sortIdsByParentElement(cloneClasses.values(), (e) -> {
						return e.getVersion().getId();
					}));
		}

		// retrieve clone class mappings
		{
			final Map<Long, DBCloneClassMapping> cloneClassMappings = cloneClassMappingDao
					.getWithVersionIds(versionIds);
			final Map<Long, Set<Long>> cloneClassMappingIdsByVersion = foreignChildElementIds
					.get(TableName.CLONE_CLASS_MAPPING);
			cloneClassMappingIdsByVersion
					.putAll(ForeignCollectionRetrieveHelper
							.sortIdsByParentElement(
									cloneClassMappings.values(), (e) -> {
										return e.getVersion().getId();
									}));
		}

		// retrieve source files

		// query for version source files
		{
			final String queryForSourceFiles = QueryHelper.querySelectIdIn(
					TableName.VERSION_SOURCE_FILE,
					DBVersionSourceFile.VERSION_COLUMN_NAME, versionIds);

			final IntermediateRowMapper<DBVersionSourceFile, InternalDBVersionSourceFile> rowMapper = new IntermediateRowMapper<>();
			rowMapper.setInstantiateFunction((map) -> {
				return new InternalDBVersionSourceFile( //
						map.get(DBVersionSourceFile.ID_COLUMN_NAME), //
						map.get(DBVersionSourceFile.VERSION_COLUMN_NAME), //
						map.get(DBVersionSourceFile.SOURCE_FILE_COLUMN_NAME));
			});

			final GenericRawResults<InternalDBVersionSourceFile> rawIntermediateResults = nativeVersionSourceFileDao
					.queryRaw(queryForSourceFiles, rowMapper);

			final Map<Long, Set<Long>> sourceFileIdsByVersionIds = foreignChildElementIds
					.get(TableName.SOURCE_FILE);

			final Set<Long> sourceFileIds = ForeignCollectionRetrieveHelper
					.getRightIdsAndUpdate(sourceFileIdsByVersionIds,
							rawIntermediateResults);

			sourceFileDao.get(sourceFileIds);
		}
	}

	@Override
	protected DBVersion makeInstance(InternalDBVersion rawResult,
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds)
			throws Exception {
		final DBVersion newInstance = new DBVersion(rawResult.getId(), null,
				null, null, null, null, null);

		if (autoRefresh) {
			newInstance.setRevision(revisionDao.get(rawResult.getRevisionId()));

			{
				final List<DBFileChange> fileChanges = ForeignCollectionRetrieveHelper
						.getChildObjects(rawResult.getId(),
								foreignChildElementIds, TableName.FILE_CHANGE,
								(set) -> {
									try {
										return fileChangeDao.get(set).values();
									} catch (Exception e) {
										return null;
									}
								});

				newInstance.setFileChanges(fileChanges);
			}

			{
				final List<DBRawCloneClass> rawCloneClasses = ForeignCollectionRetrieveHelper
						.getChildObjects(rawResult.getId(),
								foreignChildElementIds,
								TableName.RAW_CLONE_CLASS, (set) -> {
									try {
										return rawCloneClassDao.get(set)
												.values();
									} catch (Exception e) {
										return null;
									}
								});

				newInstance.setRawCloneClasses(rawCloneClasses);
			}

			{
				final List<DBCloneClass> cloneClasses = ForeignCollectionRetrieveHelper
						.getChildObjects(rawResult.getId(),
								foreignChildElementIds, TableName.CLONE_CLASS,
								(set) -> {
									try {
										return cloneClassDao.get(set).values();
									} catch (Exception e) {
										return null;
									}
								});

				newInstance.setCloneClasses(cloneClasses);
			}

			{
				final List<DBCloneClassMapping> cloneClassMapping = ForeignCollectionRetrieveHelper
						.getChildObjects(rawResult.getId(),
								foreignChildElementIds, TableName.CLONE_CLASS,
								(set) -> {
									try {
										return cloneClassMappingDao.get(set)
												.values();
									} catch (Exception e) {
										return null;
									}
								});

				newInstance.setCloneClassMappings(cloneClassMapping);
			}

			{
				final List<DBSourceFile> sourceFiles = ForeignCollectionRetrieveHelper
						.getChildObjects(rawResult.getId(),
								foreignChildElementIds, TableName.CLONE_CLASS,
								(set) -> {
									try {
										return sourceFileDao.get(set).values();
									} catch (Exception e) {
										return null;
									}
								});

				newInstance.setSourceFiles(sourceFiles);
			}
		}

		return newInstance;
	}

	/**
	 * Get the elements whose revisions are the specified one.
	 * 
	 * @param revision
	 *            revision as a query
	 * @return a list of the elements whose revisions are the specified one
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Collection<DBVersion> getWithRevision(final DBRevision revision)
			throws Exception {
		return refreshAll(originalDao.queryForEq(
				DBVersion.REVISION_COLUMN_NAME, revision));
	}

	/**
	 * Get source files corresponding to the given version as a list.
	 * 
	 * @param version
	 *            version as a query
	 * @return a list of corresponding source files
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Collection<DBSourceFile> getCorrespondingSourceFiles(
			final DBVersion version) throws Exception {
		if (sourceFilesForVersionQuery == null) {
			sourceFilesForVersionQuery = makeSourceFilesForVersionQuery();
		}
		sourceFilesForVersionQuery.setArgumentHolderValue(0, version);

		return sourceFileDao.query(sourceFilesForVersionQuery, false);
	}

	private PreparedQuery<DBSourceFile> makeSourceFilesForVersionQuery()
			throws SQLException {
		QueryBuilder<DBVersionSourceFile, Long> versionSourceFileQb = nativeVersionSourceFileDao
				.queryBuilder();

		versionSourceFileQb
				.selectColumns(DBVersionSourceFile.SOURCE_FILE_COLUMN_NAME);
		SelectArg versionSelectArg = new SelectArg();
		versionSourceFileQb.where().eq(DBVersionSourceFile.VERSION_COLUMN_NAME,
				versionSelectArg);

		QueryBuilder<DBSourceFile, Long> sourceFileQb = nativeSourceFileDao
				.queryBuilder();
		sourceFileQb.where().in(DBSourceFile.ID_COLUMN_NAME,
				versionSourceFileQb);

		return sourceFileQb.prepare();
	}

	@Override
	public void register(final DBVersion element) throws Exception {
		super.register(element); // register Version itself

		final List<DBVersionSourceFile> vsfs = new ArrayList<>();
		for (final DBSourceFile sourceFile : element.getSourceFiles()) {
			final DBVersionSourceFile vsf = new DBVersionSourceFile(
					IDGenerator.generate(DBVersionSourceFile.class), element,
					sourceFile);
			vsfs.add(vsf);
		}

		nativeVersionSourceFileDao.callBatchTasks(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				for (final DBVersionSourceFile vsf : vsfs) {
					nativeVersionSourceFileDao.create(vsf);
				}
				return null;
			}
		});
	}

	class InternalDBVersion implements InternalDataRepresentation<DBVersion> {

		private final Long id;

		private final Long revisionId;

		public InternalDBVersion(final Long id, final Long revisionId) {
			this.id = id;
			this.revisionId = revisionId;
		}

		@Override
		public final Long getId() {
			return id;
		}

		public final Long getRevisionId() {
			return revisionId;
		}

	}

	class RowMapper implements RawRowMapper<InternalDBVersion> {

		@Override
		public InternalDBVersion mapRow(String[] columnNames,
				String[] resultColumns) throws SQLException {
			Long id = null;
			Long revisionId = null;

			for (int i = 0; i < columnNames.length; i++) {
				final String columnName = columnNames[i];
				final String resultColumn = resultColumns[i];

				switch (columnName) {
				case DBVersion.ID_COLUMN_NAME:
					id = Long.parseLong(resultColumn);
					break;
				case DBVersion.REVISION_COLUMN_NAME:
					revisionId = Long.parseLong(resultColumn);
					break;
				}
			}

			return new InternalDBVersion(id, revisionId);
		}

	}

	class InternalDBVersionSourceFile implements
			InternalIntermediateDataRepresentation<DBVersionSourceFile> {

		private final Long id;

		private final Long versionId;

		private final Long sourceFileId;

		public InternalDBVersionSourceFile(final Long id, final Long versionId,
				final Long sourceFileId) {
			this.id = id;
			this.versionId = id;
			this.sourceFileId = sourceFileId;
		}

		@Override
		public final Long getId() {
			return id;
		}

		@Override
		public final Long getLeftId() {
			return versionId;
		}

		@Override
		public final Long getRightId() {
			return sourceFileId;
		}

		public final Long getVersionId() {
			return versionId;
		}

		public final Long getSourceFileId() {
			return sourceFileId;
		}

	}

}
