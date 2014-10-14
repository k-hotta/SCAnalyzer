package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawClonedFragment;
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
	private final Dao<Revision, Long> revisionDao;

	/**
	 * The DAO for FileChange. <br>
	 * This is for refreshing
	 */
	private final Dao<FileChange, Long> fileChangeDao;

	/**
	 * The DAO for RawCloneClass. <br>
	 * This is for refreshing.
	 */
	private final Dao<RawCloneClass, Long> rawCloneClassDao;

	/**
	 * The DAO for SourceFile. <br>
	 * This is for refreshing and to get corresponding source files.
	 */
	private final Dao<SourceFile, Long> nativeSourceFileDao;

	/**
	 * The DAO for RawClonedFragment. <br>
	 * This is for refreshing.
	 */
	private final Dao<RawClonedFragment, Long> rawClonedFragmentDao;

	/**
	 * The DAO for VersionSourceFile. <br>
	 * This is for retrieving corresponding source files to each version.
	 */
	private final Dao<VersionSourceFile, Long> versionSourceFileDao;

	/**
	 * The data DAO for SourceFile
	 */
	private SourceFileDao sourceFileDataDao;

	/**
	 * The query to get corresponding source files
	 */
	private PreparedQuery<SourceFile> sourceFilesForVersionQuery;

	@SuppressWarnings("unchecked")
	public VersionDao() throws SQLException {
		super((Dao<Version, Long>) DBManager.getInstance().getNativeDao(
				Version.class));
		this.revisionDao = this.manager.getNativeDao(Revision.class);
		this.fileChangeDao = this.manager.getNativeDao(FileChange.class);
		this.rawCloneClassDao = this.manager.getNativeDao(RawCloneClass.class);
		this.nativeSourceFileDao = this.manager.getNativeDao(SourceFile.class);
		this.rawClonedFragmentDao = this.manager
				.getNativeDao(RawClonedFragment.class);
		this.versionSourceFileDao = this.manager
				.getNativeDao(VersionSourceFile.class);
		sourceFileDataDao = null;
		sourceFilesForVersionQuery = null;
	}

	/**
	 * Set the data DAO for SourceFile with the specified one.
	 * 
	 * @param sourceFileDataDao
	 *            the data DAO to be set
	 */
	void setSourceFileDataDao(final SourceFileDao sourceFileDataDao) {
		this.sourceFileDataDao = sourceFileDataDao;
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	public Version refresh(Version element) throws SQLException {
		revisionDao.refresh(element.getRevision());
		for (final FileChange fileChange : element.getFileChanges()) {
			fileChangeDao.refresh(fileChange);
			nativeSourceFileDao.refresh(fileChange.getOldSourceFile());
			nativeSourceFileDao.refresh(fileChange.getNewSourceFile());
		}
		for (final RawCloneClass rawCloneClass : element.getRawCloneClasses()) {
			rawCloneClassDao.refresh(rawCloneClass);
			for (final RawClonedFragment rawClonedFragment : rawCloneClass
					.getElements()) {
				rawClonedFragmentDao.refresh(rawClonedFragment);
			}
		}
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

		return sourceFileDataDao.query(sourceFilesForVersionQuery);
	}

	private PreparedQuery<SourceFile> makeSourceFilesForVersionQuery()
			throws SQLException {
		QueryBuilder<VersionSourceFile, Long> versionSourceFileQb = versionSourceFileDao
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
}
