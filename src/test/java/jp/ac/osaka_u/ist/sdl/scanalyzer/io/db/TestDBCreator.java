package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.VersionSourceFile;

import com.j256.ormlite.dao.Dao;
import com.sun.jna.platform.win32.Version;

/**
 * This class creates new database just for testing.
 * 
 * @author k-hotta
 * 
 */
public class TestDBCreator {

	/**
	 * The DBMS for testing
	 */
	private static final DBMS DBMS_FOR_TEST = DBMS.SQLITE;

	/**
	 * The path of database for testing
	 */
	private static final String PATH_FOR_TEST = "src\\test\\resources\\for-test.db";

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

	private Dao<RawCloneClass, Long> nativeRawCloneClassDao;

	private Dao<RawClonedFragment, Long> nativeRawClonedFragmentDao;

	private Dao<VersionSourceFile, Long> nativeVersionSourceFileDao;

	private TestDBCreator() throws Exception {
		// the instance must be created only from this class
		final String url = DBUrlProvider.getUrl(DBMS_FOR_TEST, PATH_FOR_TEST);
		this.manager = DBManager.setup(url);
	}

	/**
	 * Create new database and stores data for testing.
	 * 
	 * @throws Exception
	 *             If any error occurred
	 */
	public static final void create() throws Exception {
		final TestDBCreator creator = new TestDBCreator();
		creator.initializeTables();
		creator.setupNativeDaos();
	}

	private final void initializeTables() throws Exception {
		manager.initializeTable(Version.class);
		manager.initializeTable(Revision.class);
		manager.initializeTable(SourceFile.class);
		manager.initializeTable(FileChange.class);
		manager.initializeTable(RawCloneClass.class);
		manager.initializeTable(RawClonedFragment.class);
		manager.initializeTable(VersionSourceFile.class);
	}

	private final void setupNativeDaos() throws Exception {
		nativeVersionDao = manager.getNativeDao(Version.class);
		nativeRevisionDao = manager.getNativeDao(Revision.class);
		nativeSourceFileDao = manager.getNativeDao(SourceFile.class);
		nativeRawCloneClassDao = manager.getNativeDao(RawCloneClass.class);
		nativeRawClonedFragmentDao = manager
				.getNativeDao(RawClonedFragment.class);
		nativeVersionSourceFileDao = manager
				.getNativeDao(VersionSourceFile.class);
	}

}
