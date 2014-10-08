package jp.ac.osaka_u.ist.sdl.scanalyzer;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.DBManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) throws Exception {
		Logger logger = LogManager.getLogger(App.class);
		Logger eLogger = LogManager.getLogger("error");

		String dbUrl = "jdbc:sqlite:C:/Users/k-hotta/git/SCAnalyzer/src/test/resources/test.db";
//		ConnectionSource connectionSource = new JdbcConnectionSource(dbUrl);
//
//		Dao<Revision, Long> revisionDao = DaoManager.createDao(
//				connectionSource, Revision.class);
//		TableUtils.createTable(connectionSource, Revision.class);
//
//		Revision newRevision = new Revision(1, "init");
//		revisionDao.create(newRevision);
//
//		Revision retrieved = revisionDao.queryForId((long) 1);
//		logger.info(retrieved.getId() + "," + retrieved.getIdentifier());
//
//		connectionSource.close();
		
		DBManager.setup(dbUrl);
		final DBManager dbManager = DBManager.getInstance();
		
		dbManager.initializeTable(Revision.class);
		final Dao<Revision, Long> revisionDao = dbManager.getDao(Revision.class);
		
		Revision newRevision = new Revision(1, "init");
		revisionDao.create(newRevision);

		Revision retrieved = revisionDao.queryForId((long) 1);
		logger.info(retrieved.getId() + "," + retrieved.getIdentifier());		
		
		dbManager.closeConnection();
		
	}
}
