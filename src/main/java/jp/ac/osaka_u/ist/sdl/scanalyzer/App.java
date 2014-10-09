package jp.ac.osaka_u.ist.sdl.scanalyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.DBManager;

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

		dbManager.initializeTable(Revision.class);
		dbManager.initializeTable(SourceFile.class);
		dbManager.initializeTable(Version.class);
		dbManager.initializeTable(RawCloneClass.class);
		dbManager.initializeTable(RawClonedFragment.class);

		final Dao<Revision, Long> revisionDao = dbManager
				.getDao(Revision.class);
		final Dao<Version, Long> versionDao = dbManager.getDao(Version.class);
		final Dao<SourceFile, Long> sourceFileDao = dbManager
				.getDao(SourceFile.class);
		final Dao<RawCloneClass, Long> rawCloneClassDao = dbManager
				.getDao(RawCloneClass.class);
		final Dao<RawClonedFragment, Long> rawClonedFragmentDao = dbManager
				.getDao(RawClonedFragment.class);

		Revision newRevision = new Revision(1, "init", new Date());
		revisionDao.create(newRevision);

		Revision retrieved = revisionDao.queryForId((long) 1);
		logger.info(retrieved.getId() + "," + retrieved.getIdentifier());

		Collection<SourceFile> sourceFiles = new ArrayList<SourceFile>();

		Version version = new Version(1, retrieved, sourceFiles);

		SourceFile file1 = new SourceFile(1, "A.java", version);
		sourceFileDao.create(file1);
		SourceFile file2 = new SourceFile(2, "B.java", version);
		sourceFileDao.create(file2);

		sourceFiles.add(file1);
		sourceFiles.add(file2);

		versionDao.create(version);

		Version retrievedVersion = versionDao.queryForId((long) 1);
		revisionDao.refresh(retrievedVersion.getRevision());

		RawCloneClass cloneClass = new RawCloneClass();
		cloneClass.setId(1);
		cloneClass.setRevision(newRevision);

		RawClonedFragment frag1 = new RawClonedFragment(1, newRevision, file1,
				2, 5, cloneClass);
		RawClonedFragment frag2 = new RawClonedFragment(2, newRevision, file2,
				10, 5, cloneClass);
		Collection<RawClonedFragment> elements = new ArrayList<RawClonedFragment>();
		elements.add(frag1);
		elements.add(frag2);

		cloneClass.setElements(elements);

		dbManager.closeConnection();

		rawCloneClassDao.create(cloneClass);
		rawClonedFragmentDao.create(frag1);
		rawClonedFragmentDao.create(frag2);

	}
}
