package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBElementComparator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.VersionSourceFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

/**
 * The DAO for {@link Version}.
 * 
 * @author k-hotta
 * 
 * @see Version
 * @see VersionSourceFile
 */
public class VersionDao extends AbstractDataDao<Version> {

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
	private final Dao<SourceFile, Long> nativeSourceFileDao;

	/**
	 * The DAO for CloneClass. <br>
	 * This is for refreshing.
	 */
	private CloneClassDao cloneClassDao;

	/**
	 * The DAO for VersionSourceFile. <br>
	 * This is for retrieving corresponding source files to each version.
	 */
	private final Dao<VersionSourceFile, Long> nativeVersionSourceFileDao;

	/**
	 * The data DAO for SourceFile
	 */
	private SourceFileDao sourceFileDao;

	/**
	 * The query to get corresponding source files
	 */
	private PreparedQuery<SourceFile> sourceFilesForVersionQuery;

	@SuppressWarnings("unchecked")
	public VersionDao() throws SQLException {
		super((Dao<Version, Long>) DBManager.getInstance().getNativeDao(
				Version.class));
		this.revisionDao = null;
		this.fileChangeDao = null;
		this.rawCloneClassDao = null;
		this.nativeSourceFileDao = this.manager.getNativeDao(SourceFile.class);
		this.cloneClassDao = null;
		this.nativeVersionSourceFileDao = this.manager
				.getNativeDao(VersionSourceFile.class);
		this.sourceFileDao = null;
		this.sourceFilesForVersionQuery = null;
	}

	/**
	 * Set the DAO for Revision with the specified one.
	 * 
	 * @param revisionDao
	 *            the DAO to be set
	 */
	void setRevidionDao(final RevisionDao revisionDao) {
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
	public Version refresh(Version element) throws SQLException {
		element.setRevision(revisionDao.get(element.getId()));

		final Collection<FileChange> fileChanges = new TreeSet<FileChange>(
				new DBElementComparator());
		for (final FileChange fileChange : element.getFileChanges()) {
			fileChanges.add(fileChangeDao.get(fileChange.getId()));
		}
		element.setFileChanges(fileChanges);

		final Collection<RawCloneClass> rawCloneClasses = new TreeSet<RawCloneClass>();
		for (final RawCloneClass rawCloneClass : element.getRawCloneClasses()) {
			rawCloneClasses.add(rawCloneClassDao.get(rawCloneClass.getId()));
		}
		element.setRawCloneClasses(rawCloneClasses);

		final Collection<CloneClass> cloneClasses = new TreeSet<CloneClass>();
		for (final CloneClass cloneClass : element.getCloneClasses()) {
			cloneClasses.add(cloneClassDao.get(cloneClass.getId()));
		}
		element.setCloneClasses(cloneClasses);

		element.setSourceFiles(getCorrespondingSourceFiles(element));

		return element;
	}

	/**
	 * Get the elements whose revisions are the specified one.
	 * 
	 * @param revision
	 *            revision as a query
	 * @return a list of the elements whose revisions are the specified one
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<Version> getWithRevision(final Revision revision)
			throws SQLException {
		return refreshAll(originalDao.queryForEq(Version.REVISION_COLUMN_NAME,
				revision));
	}

	/**
	 * Get source files corresponding to the given version as a list.
	 * 
	 * @param version
	 *            version as a query
	 * @return a list of corresponding source files
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<SourceFile> getCorrespondingSourceFiles(final Version version)
			throws SQLException {
		if (sourceFilesForVersionQuery == null) {
			sourceFilesForVersionQuery = makeSourceFilesForVersionQuery();
		}
		sourceFilesForVersionQuery.setArgumentHolderValue(0, version);

		return sourceFileDao.query(sourceFilesForVersionQuery);
	}

	private PreparedQuery<SourceFile> makeSourceFilesForVersionQuery()
			throws SQLException {
		QueryBuilder<VersionSourceFile, Long> versionSourceFileQb = nativeVersionSourceFileDao
				.queryBuilder();

		versionSourceFileQb
				.selectColumns(VersionSourceFile.SOURCE_FILE_COLUMN_NAME);
		SelectArg versionSelectArg = new SelectArg();
		versionSourceFileQb.where().eq(VersionSourceFile.VERSION_COLUMN_NAME,
				versionSelectArg);

		QueryBuilder<SourceFile, Long> sourceFileQb = nativeSourceFileDao
				.queryBuilder();
		sourceFileQb.where().in(SourceFile.ID_COLUMN_NAME, versionSourceFileQb);

		return sourceFileQb.prepare();
	}

	@Override
	public void register(final Version element) throws SQLException {
		super.register(element); // register Version itself

		for (final SourceFile sourceFile : element.getSourceFiles()) {
			final VersionSourceFile vsf = new VersionSourceFile(
					IDGenerator.generate(VersionSourceFile.class), element,
					sourceFile);
			nativeVersionSourceFileDao.create(vsf);
		}
	}
}
