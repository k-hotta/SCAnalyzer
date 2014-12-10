package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.util.ArrayList;
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
				mappingDao.queryRaw("select * from CLONE_CLASS_MAPPING", (
						columns, results) -> {
					return new DBCloneClassMapping(Long.parseLong(results[0]),
							null, null, null, null, null);
				});
			}, "select *: ");
			mappingDao.clearObjectCache();

			run(() -> {
				final List<DBCloneClassMapping> result = new ArrayList<>();
				for (final long id : ids) {
					final GenericRawResults<DBCloneClassMapping> rawResult = mappingDao.queryRaw(
							"select * from CLONE_CLASS_MAPPING where ID = "
									+ id,
							(columns, results) -> {
								return new DBCloneClassMapping(Long
										.parseLong(results[0]), null, null,
										null, null, null);
							});
					result.add(rawResult.getFirstResult());
				}
			}, "select (for each): ");
			mappingDao.clearObjectCache();

			run(() -> {
				final List<DBCloneClassMapping> result = new ArrayList<>();
				for (final long id : ids) {
					final GenericRawResults<DBCloneClassMapping> rawResult = mappingDao
							.queryRaw(
									"select * from CLONE_CLASS_MAPPING inner join CLONE_CLASS on CLONE_CLASS_MAPPING.NEW_CLONE_CLASS = CLONE_CLASS.ID where CLONE_CLASS_MAPPING.ID = "
											+ id,
									(columns, results) -> {
										return new DBCloneClassMapping(Long
												.parseLong(results[0]), null,
												null, null, null, null);
									});
					result.add(rawResult.getFirstResult());
				}
			}, "innner join: ");

			run(() -> {
				final List<DBCloneClassMapping> result = new ArrayList<>();
				mappingDao.callBatchTasks(new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						for (final long id : ids) {
							final GenericRawResults<DBCloneClassMapping> rawResult = mappingDao
									.queryRaw(
											"select * from CLONE_CLASS_MAPPING where ID = "
													+ id,
											(columns, results) -> {
												return new DBCloneClassMapping(
														Long.parseLong(results[0]),
														null, null, null, null,
														null);
											});
							result.add(rawResult.getFirstResult());
						}

						return null;
					}

				});

			}, "select (batch): ");
			mappingDao.clearObjectCache();

			run(() -> {
				final StringBuilder builder = new StringBuilder();
				builder.append("select * from CLONE_CLASS_MAPPING where ID in (");
				for (final long id : ids) {
					builder.append(id + ",");
				}
				builder.deleteCharAt(builder.length() - 1);
				builder.append(")");

				mappingDao.queryRaw(builder.toString(), (columns, results) -> {
					return new DBCloneClassMapping(Long.parseLong(results[0]),
							null, null, null, null, null);
				});
			}, "select (with IN): ");
			mappingDao.clearObjectCache();

			// run(() -> mappingDao.queryForAll(), "queryForAll: ");
			// mappingDao.clearObjectCache();
			//
			// run(() -> {
			// for (final Long id : ids) {
			// mappingDao.queryForId(id);
			// }
			// }, "queryForId (for each): ");
			// mappingDao.clearObjectCache();
			//
			// run(() -> {
			// mappingDao.callBatchTasks(new Callable<Void>() {
			// @Override
			// public Void call() throws Exception {
			// for (final long id : ids) {
			// mappingDao.queryForId(id);
			// }
			// return null;
			// }
			// });
			// }, "queryForId (batch): ");
			// mappingDao.clearObjectCache();
			//
			// final List<DBCloneClassMapping> elements =
			// mappingDao.queryForAll();
			//
			// run(() -> {
			// for (DBCloneClassMapping element : elements) {
			// mappingDao.refresh(element);
			// }
			// }, "refresh (for each): ");
			// mappingDao.clearObjectCache();
			//
			// run(() -> {
			// mappingDao.callBatchTasks(new Callable<Void>() {
			// @Override
			// public Void call() throws Exception {
			// for (DBCloneClassMapping element : elements) {
			// mappingDao.refresh(element);
			// }
			// return null;
			// }
			// });
			// }, "refresh (batch): ");
			// mappingDao.clearObjectCache();

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
