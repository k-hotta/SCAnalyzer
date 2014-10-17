package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.VersionSourceFile;

import com.j256.ormlite.dao.Dao;

/**
 * This class creates new database just for testing.
 * 
 * @author k-hotta
 * 
 */
public class TestDBConnection {

	/**
	 * The manager of database connection
	 */
	private DBManager manager;

	/*
	 * native DAOs, which are provided by ORMLite
	 */

	private Dao<Version, Long> nativeVersionDao;

	private Dao<Revision, Long> nativeRevisionDao;

	private Dao<SourceFile, Long> nativeSourceFileDao;

	private Dao<FileChange, Long> nativeFileChangeDao;

	private Dao<RawCloneClass, Long> nativeRawCloneClassDao;

	private Dao<RawClonedFragment, Long> nativeRawClonedFragmentDao;

	private Dao<VersionSourceFile, Long> nativeVersionSourceFileDao;

	public TestDBConnection(DBXmlParser parser) throws Exception {
		// the instance must be created only from this class
		final String url = DBUrlProvider.getUrl(parser.getDbms(),
				parser.getPath());
		this.manager = DBManager.setup(url);
	}

	public static final TestDBConnection create(final DBXmlParser parser)
			throws Exception {
		final TestDBConnection connection = new TestDBConnection(parser);
		connection.initializeTables();
		connection.setupNativeDaos();
		return connection;
	}

	public final void initializeTables() throws Exception {
		manager.initializeTable(Version.class);
		manager.initializeTable(Revision.class);
		manager.initializeTable(SourceFile.class);
		manager.initializeTable(FileChange.class);
		manager.initializeTable(RawCloneClass.class);
		manager.initializeTable(RawClonedFragment.class);
		manager.initializeTable(VersionSourceFile.class);
	}
	
	public final void initializeTable(final Class<?> clazz) throws Exception {
		manager.initializeTable(clazz);
	}

	private final void setupNativeDaos() throws Exception {
		nativeVersionDao = manager.getNativeDao(Version.class);
		nativeRevisionDao = manager.getNativeDao(Revision.class);
		nativeSourceFileDao = manager.getNativeDao(SourceFile.class);
		nativeFileChangeDao = manager.getNativeDao(FileChange.class);
		nativeRawCloneClassDao = manager.getNativeDao(RawCloneClass.class);
		nativeRawClonedFragmentDao = manager
				.getNativeDao(RawClonedFragment.class);
		nativeVersionSourceFileDao = manager
				.getNativeDao(VersionSourceFile.class);
	}

	public void close() throws SQLException {
		manager.closeConnection();
	}

	/**
	 * Store all the data contained in the given parser
	 * 
	 * @param parser
	 *            xml parser after finished parsing
	 */
	public void storeAll(final DBXmlParser parser) throws Exception {
		for (final Version version : parser.getVersions().values()) {
			storeVersionWithNativeWay(version);
		}

		for (final Revision revision : parser.getRevisions().values()) {
			storeRevisionWithNativeWay(revision);
		}

		for (final SourceFile sourceFile : parser.getSourceFiles().values()) {
			storeSourceFileWithNativeWay(sourceFile);
		}

		for (final FileChange fileChange : parser.getFileChanges().values()) {
			storeFileChangeWithNativeWay(fileChange);
		}

		for (final RawCloneClass rawCloneClass : parser.getRawCloneClasses()
				.values()) {
			storeRawCloneClassWithNativeWay(rawCloneClass);
		}

		for (final RawClonedFragment rawClonedFragment : parser
				.getRawClonedFragments().values()) {
			storeRawClonedFragmentWithNativeWay(rawClonedFragment);
		}

		for (final VersionSourceFile versionSourceFile : parser
				.getVersionSourceFiles().values()) {
			storeVersionSourceFileWithNativeWay(versionSourceFile);
		}
	}

	public void storeVersionWithNativeWay(final Version version)
			throws Exception {
		nativeVersionDao.create(version);
	}

	public void storeRevisionWithNativeWay(final Revision revision)
			throws Exception {
		nativeRevisionDao.create(revision);
	}

	public void storeSourceFileWithNativeWay(final SourceFile sourceFile)
			throws Exception {
		nativeSourceFileDao.create(sourceFile);
	}

	public void storeFileChangeWithNativeWay(final FileChange fileChange)
			throws Exception {
		nativeFileChangeDao.create(fileChange);
	}

	public void storeRawCloneClassWithNativeWay(
			final RawCloneClass rawCloneClass) throws Exception {
		nativeRawCloneClassDao.create(rawCloneClass);
	}

	public void storeRawClonedFragmentWithNativeWay(
			final RawClonedFragment rawClonedFragment) throws Exception {
		nativeRawClonedFragmentDao.create(rawClonedFragment);
	}

	public void storeVersionSourceFileWithNativeWay(
			final VersionSourceFile versionSourceFile) throws Exception {
		nativeVersionSourceFileDao.create(versionSourceFile);
	}

}
