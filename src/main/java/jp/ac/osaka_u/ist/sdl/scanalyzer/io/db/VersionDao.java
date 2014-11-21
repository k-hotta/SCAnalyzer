package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersionSourceFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
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
public class VersionDao extends AbstractDataDao<DBVersion> {

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
	public DBVersion refresh(DBVersion element) throws Exception {
		revisionDao.refresh(element.getRevision());

		fileChangeDao.refreshAll(element.getFileChanges());

		rawCloneClassDao.refreshAll(element.getRawCloneClasses());

		cloneClassDao.refreshAll(element.getCloneClasses());

		cloneClassMappingDao.refreshAll(element.getCloneClassMappings());

		element.setSourceFiles(getCorrespondingSourceFiles(element));

		return element;
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

		return sourceFileDao.query(sourceFilesForVersionQuery);
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
}
