package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.DBMS;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;

/**
 * Although this class is named "Test", it has no unit test cases. This is used
 * just for evaluating the performance of ORMLite.
 * 
 * @author k-hotta
 *
 */
public class DBPerformanceTest {

	public static void main(String[] args) throws Exception {
		try {
			final String dbUrl = DBUrlProvider.getUrl(DBMS.SQLITE,
					"src/test/resources/test.db");
			DBManager.setup(dbUrl);

			final Collection<Long> ids = DBManager.getInstance()
					.getCloneClassMappingDao().getAllIds();

			AbstractDataDao.setAutoRefresh(false);

			final Dao<DBCloneClassMapping, Long> mappingDao = DBManager
					.getInstance().getNativeDao(DBCloneClassMapping.class);

			run(() -> {
				GenericRawResults<DBCloneClassMapping> result = mappingDao.queryRaw("select * from CLONE_CLASS_MAPPING", (
						columns, results) -> {
					return new DBCloneClassMapping(Long.parseLong(results[0]),
							null, null, null, null);
				});
				int count = 0;
				for (DBCloneClassMapping tmp : result) {
					mappingDao.refresh(tmp);
					System.out.println(count++);
				}
			}, "select *: ");

			run(() -> mappingDao.queryForAll(), "queryForAll: ");
			mappingDao.clearObjectCache();

			run(() -> {
				for (final Long id : ids) {
					mappingDao.queryForId(id);
				}
			}, "queryForId (for each): ");
			mappingDao.clearObjectCache();

			run(() -> {
				mappingDao.callBatchTasks(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						for (final long id : ids) {
							mappingDao.queryForId(id);
						}
						return null;
					}
				});
			}, "queryForId (batch): ");
			mappingDao.clearObjectCache();

			final List<DBCloneClassMapping> elements = mappingDao.queryForAll();

			run(() -> {
				for (DBCloneClassMapping element : elements) {
					mappingDao.refresh(element);
				}
			}, "refresh (for each): ");
			mappingDao.clearObjectCache();

			run(() -> {
				mappingDao.callBatchTasks(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						for (DBCloneClassMapping element : elements) {
							mappingDao.refresh(element);
						}
						return null;
					}
				});
			}, "refresh (batch): ");
			mappingDao.clearObjectCache();

		} finally {
			DBManager.closeConnection();
		}
	}

	public static void run(final PerformanceRun func, final String str)
			throws Exception {
		final long t1 = System.nanoTime();
		func.run();
		final long t2 = System.nanoTime();

		System.out.println(str + (t2 - t1));
	}

}
