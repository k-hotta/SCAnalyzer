package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersionSourceFile;

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

	private Dao<DBVersion, Long> nativeVersionDao;

	private Dao<DBRevision, Long> nativeRevisionDao;

	private Dao<DBSourceFile, Long> nativeSourceFileDao;

	private Dao<DBFileChange, Long> nativeFileChangeDao;

	private Dao<DBSegment, Long> nativeSegmentDao;

	private Dao<DBCodeFragment, Long> nativeCodeFragmentDao;

	private Dao<DBCloneClass, Long> nativeCloneClassDao;

	private Dao<DBRawCloneClass, Long> nativeRawCloneClassDao;

	private Dao<DBRawClonedFragment, Long> nativeRawClonedFragmentDao;

	private Dao<DBVersionSourceFile, Long> nativeVersionSourceFileDao;

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
		manager.initializeTable(DBVersion.class);
		manager.initializeTable(DBRevision.class);
		manager.initializeTable(DBSourceFile.class);
		manager.initializeTable(DBFileChange.class);
		manager.initializeTable(DBSegment.class);
		manager.initializeTable(DBCodeFragment.class);
		manager.initializeTable(DBCloneClass.class);
		manager.initializeTable(DBRawCloneClass.class);
		manager.initializeTable(DBRawClonedFragment.class);
		manager.initializeTable(DBVersionSourceFile.class);
	}

	public final void initializeTable(final Class<?> clazz) throws Exception {
		manager.initializeTable(clazz);
	}

	private final void setupNativeDaos() throws Exception {
		nativeVersionDao = manager.getNativeDao(DBVersion.class);
		nativeRevisionDao = manager.getNativeDao(DBRevision.class);
		nativeSourceFileDao = manager.getNativeDao(DBSourceFile.class);
		nativeFileChangeDao = manager.getNativeDao(DBFileChange.class);
		nativeSegmentDao = manager.getNativeDao(DBSegment.class);
		nativeCodeFragmentDao = manager.getNativeDao(DBCodeFragment.class);
		nativeCloneClassDao = manager.getNativeDao(DBCloneClass.class);
		nativeRawCloneClassDao = manager.getNativeDao(DBRawCloneClass.class);
		nativeRawClonedFragmentDao = manager
				.getNativeDao(DBRawClonedFragment.class);
		nativeVersionSourceFileDao = manager
				.getNativeDao(DBVersionSourceFile.class);
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
		for (final DBVersion version : parser.getVersions().values()) {
			storeVersionWithNativeWay(version);
		}

		for (final DBRevision revision : parser.getRevisions().values()) {
			storeRevisionWithNativeWay(revision);
		}

		for (final DBSourceFile sourceFile : parser.getSourceFiles().values()) {
			storeSourceFileWithNativeWay(sourceFile);
		}

		for (final DBFileChange fileChange : parser.getFileChanges().values()) {
			storeFileChangeWithNativeWay(fileChange);
		}
		
		for (final DBSegment segment : parser.getSegments().values()) {
			storeSegmentWithNativeWay(segment);
		}
		
		for (final DBCodeFragment codeFragment : parser.getCodeFragments().values()) {
			storeCodeFragmentWithNativeWay(codeFragment);
		}
		
		for (final DBCloneClass cloneClass : parser.getCloneClasses().values()) {
			storeCloneClassWithNativeWay(cloneClass);
		}

		for (final DBRawCloneClass rawCloneClass : parser.getRawCloneClasses()
				.values()) {
			storeRawCloneClassWithNativeWay(rawCloneClass);
		}

		for (final DBRawClonedFragment rawClonedFragment : parser
				.getRawClonedFragments().values()) {
			storeRawClonedFragmentWithNativeWay(rawClonedFragment);
		}

		for (final DBVersionSourceFile versionSourceFile : parser
				.getVersionSourceFiles().values()) {
			storeVersionSourceFileWithNativeWay(versionSourceFile);
		}
	}

	public void storeVersionWithNativeWay(final DBVersion version)
			throws Exception {
		nativeVersionDao.create(version);
	}

	public void storeRevisionWithNativeWay(final DBRevision revision)
			throws Exception {
		nativeRevisionDao.create(revision);
	}

	public void storeSourceFileWithNativeWay(final DBSourceFile sourceFile)
			throws Exception {
		nativeSourceFileDao.create(sourceFile);
	}

	public void storeFileChangeWithNativeWay(final DBFileChange fileChange)
			throws Exception {
		nativeFileChangeDao.create(fileChange);
	}

	public void storeSegmentWithNativeWay(final DBSegment segment)
			throws Exception {
		nativeSegmentDao.create(segment);
	}

	public void storeCodeFragmentWithNativeWay(final DBCodeFragment codeFragment)
			throws Exception {
		nativeCodeFragmentDao.create(codeFragment);
	}

	public void storeCloneClassWithNativeWay(final DBCloneClass cloneClass)
			throws Exception {
		nativeCloneClassDao.create(cloneClass);
	}

	public void storeRawCloneClassWithNativeWay(
			final DBRawCloneClass rawCloneClass) throws Exception {
		nativeRawCloneClassDao.create(rawCloneClass);
	}

	public void storeRawClonedFragmentWithNativeWay(
			final DBRawClonedFragment rawClonedFragment) throws Exception {
		nativeRawClonedFragmentDao.create(rawClonedFragment);
	}

	public void storeVersionSourceFileWithNativeWay(
			final DBVersionSourceFile versionSourceFile) throws Exception {
		nativeVersionSourceFileDao.create(versionSourceFile);
	}

}
