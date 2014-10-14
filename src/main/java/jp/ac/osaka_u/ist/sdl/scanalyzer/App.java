package jp.ac.osaka_u.ist.sdl.scanalyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange.Type;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.VersionSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.DBManager;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.FileChangeDao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) throws Exception {
		Logger logger = LogManager.getLogger(App.class);
		Logger eLogger = LogManager.getLogger("error");

		String dbUrl = "jdbc:sqlite:/C:/Users/k-hotta/git/SCAnalyzer/src/test/resources/test.db";
		// String dbUrl = "jdbc:sqlite:src/test/resources/test.db";
		// String dbUrl =
		// "jdbc:sqlite:C:/Users/k-hotta/git/SCAnalyzer/src/test/resources/test.db";
		// ConnectionSource connectionSource = new JdbcConnectionSource(dbUrl);
		//
		// Dao<Revision, Long> revisionDao = DaoManager.createDao(
		// connectionSource, Revision.class);
		// TableUtils.createTable(connectionSource, Revision.class);
		//
		// Revision newRevision = new Revision(1, "init");
		// revisionDao.create(newRevision);
		//
		// Revision retrieved = revisionDao.queryForId((long) 1);
		// logger.info(retrieved.getId() + "," + retrieved.getIdentifier());
		//
		// connectionSource.close();

		DBManager.setup(dbUrl);
		final DBManager dbManager = DBManager.getInstance();

		dbManager.initializeTable(Version.class);
		dbManager.initializeTable(Revision.class);
		dbManager.initializeTable(SourceFile.class);
		dbManager.initializeTable(FileChange.class);
		dbManager.initializeTable(RawCloneClass.class);
		dbManager.initializeTable(RawClonedFragment.class);
		dbManager.initializeTable(VersionSourceFile.class);

		final Dao<Revision, Long> revisionDao = dbManager
				.getDao(Revision.class);
		final Dao<Version, Long> versionDao = dbManager.getDao(Version.class);
		final Dao<SourceFile, Long> sourceFileDao = dbManager
				.getDao(SourceFile.class);
		final Dao<RawCloneClass, Long> rawCloneClassDao = dbManager
				.getDao(RawCloneClass.class);
		final Dao<RawClonedFragment, Long> rawClonedFragmentDao = dbManager
				.getDao(RawClonedFragment.class);
		final Dao<FileChange, Long> fileChangeDao = dbManager
				.getDao(FileChange.class);
		final Dao<VersionSourceFile, Long> versionSourceFileDao = dbManager
				.getDao(VersionSourceFile.class);

		Revision rev1 = new Revision(1, "init", new Date());
		revisionDao.create(rev1);

		Revision rev2 = new Revision(2, "second", new Date());
		revisionDao.create(rev2);

		Collection<FileChange> fileChanges = new ArrayList<FileChange>();
		Collection<RawCloneClass> rawCloneClasses = new ArrayList<RawCloneClass>();

		Version ver1 = new Version(1, rev1, fileChanges, rawCloneClasses);
		versionDao.create(ver1);

		SourceFile file1 = new SourceFile(1, "A.java");
		sourceFileDao.create(file1);
		SourceFile file2 = new SourceFile(2, "B.java");
		sourceFileDao.create(file2);
		SourceFile file0 = new SourceFile(0, "C.java");
		sourceFileDao.create(file0);

		VersionSourceFile vf1 = new VersionSourceFile(1, ver1, file1);
		VersionSourceFile vf2 = new VersionSourceFile(2, ver1, file2);
		VersionSourceFile vf0 = new VersionSourceFile(0, ver1, file0);
		versionSourceFileDao.create(vf1);
		versionSourceFileDao.create(vf2);
		versionSourceFileDao.create(vf0);

		Collection<FileChange> fileChanges2 = new ArrayList<FileChange>();
		Collection<RawCloneClass> rawCloneClasses2 = new ArrayList<RawCloneClass>();

		Version ver2 = new Version(2, rev2, fileChanges2, rawCloneClasses2);

		SourceFile file3 = new SourceFile(3, "A.java");
		sourceFileDao.create(file3);
		SourceFile file4 = new SourceFile(4, "B.java");
		sourceFileDao.create(file4);

		FileChange change1 = new FileChange(1, file1, file3, Type.MODIFY, ver2);
		fileChangeDao.create(change1);
		FileChange change2 = new FileChange(2, file2, file4, Type.MODIFY, ver2);
		fileChangeDao.create(change2);

		fileChanges2.add(change1);
		fileChanges2.add(change2);

		RawCloneClass cloneClass = new RawCloneClass();
		cloneClass.setId(1);
		cloneClass.setVersion(ver2);

		RawClonedFragment frag1 = new RawClonedFragment(1, ver2, file3, 2, 5,
				cloneClass);
		RawClonedFragment frag2 = new RawClonedFragment(2, ver2, file3, 10, 5,
				cloneClass);
		Collection<RawClonedFragment> elements = new ArrayList<RawClonedFragment>();
		elements.add(frag1);
		elements.add(frag2);

		cloneClass.setElements(elements);

		rawCloneClassDao.create(cloneClass);
		rawClonedFragmentDao.create(frag1);
		rawClonedFragmentDao.create(frag2);

		rawCloneClasses2.add(cloneClass);

		versionDao.create(ver2);

		VersionSourceFile vf3 = new VersionSourceFile(3, ver2, file3);
		VersionSourceFile vf4 = new VersionSourceFile(4, ver2, file4);
		VersionSourceFile vf5 = new VersionSourceFile(5, ver2, file0);
		versionSourceFileDao.create(vf3);
		versionSourceFileDao.create(vf4);
		versionSourceFileDao.create(vf5);

		final FileChangeDao daotest = new FileChangeDao();
		final List<FileChange> retrievedWithOldSourceFile = daotest
				.getWithOldSourceFile(file1);
		final List<FileChange> retrievedWithVersion = daotest.getWithVersion(ver2);
		final List<FileChange> retrievedWithType = daotest.getWithType(Type.MODIFY);
		
		dbManager.closeConnection();
	}
}
