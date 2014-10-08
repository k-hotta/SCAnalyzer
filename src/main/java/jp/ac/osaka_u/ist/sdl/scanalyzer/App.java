package jp.ac.osaka_u.ist.sdl.scanalyzer;

import java.util.ArrayList;
import java.util.Collection;

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

		final Dao<Revision, Long> revisionDao = dbManager
				.getDao(Revision.class);
		final Dao<Version, Long> versionDao = dbManager.getDao(Version.class);
		final Dao<SourceFile, Long> sourceFileDao = dbManager
				.getDao(SourceFile.class);

		Revision newRevision = new Revision(1, "init");
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
		System.out.println(retrievedVersion.getSourceFiles().size());
		revisionDao.refresh(retrievedVersion.getRevision());

		dbManager.closeConnection();

	}
}
