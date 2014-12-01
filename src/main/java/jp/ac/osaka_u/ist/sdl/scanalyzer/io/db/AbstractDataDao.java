package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.IDBElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.PreparedQuery;

/**
 * This abstract class represents DAOs for each data class and provides some
 * functions that are common in all the data classes.
 * 
 * @author k-hotta
 * 
 * @param <D>
 *            the data class under consideration of the instance of the child
 *            class of this abstract class
 */
public abstract class AbstractDataDao<D extends IDBElement, R extends InternalDataRepresentation<D>> {

	/**
	 * The logger for errors
	 */
	private static final Logger eLogger = LogManager.getLogger("error");

	/**
	 * If this field is true, the related elements to a given db element will be
	 * refreshed automatically.
	 */
	protected static boolean autoRefresh = true;

	/**
	 * If this field is true, ALL the related elements to a given db element
	 * will be refreshed. If false, only the child elements of the db element
	 * will be refreshed. <br>
	 * For instance, suppose that you are about to refresh a clone class. A
	 * clone class has code fragments as its children, and a version as its
	 * parent. If deep refresh is false, only the code fragments will be
	 * refreshed. On the other hand, if true, not only the code fragments but
	 * also the version will be refreshed.
	 */
	protected static boolean deepRefresh = false;

	/**
	 * The DB manager
	 */
	protected final DBManager manager;

	/**
	 * The DAO instance of the data class provided by ORMLite
	 */
	protected final Dao<D, Long> originalDao;

	/**
	 * The retrieved elements
	 */
	protected final ConcurrentMap<Long, D> retrievedElements;

	/**
	 * * The constructor.
	 * 
	 * <p>
	 * NOTE: {@link DBManager} must be set up before calling this constructor,
	 * which can be done by calling {@link DBManager#setup(String)}.
	 * </p>
	 * 
	 * @param originalDao
	 *            the instance of DAO provided by ORMLite which corresponds to
	 *            the data class
	 */
	public AbstractDataDao(final Dao<D, Long> originalDao) {
		this.manager = DBManager.getInstance();
		this.originalDao = originalDao;
		this.retrievedElements = new ConcurrentSkipListMap<Long, D>();
	}

	/**
	 * Set the value of auto refresh.
	 * 
	 * @param autoRefresh
	 *            the boolean value to be set
	 */
	public static void setAutoRefresh(final boolean autoRefresh) {
		AbstractDataDao.autoRefresh = autoRefresh;
	}

	/**
	 * Set the value of deep refresh.
	 * 
	 * @param deepRefresh
	 *            the boolean value to be set
	 */
	public static void setDeepRefresh(final boolean deepRefresh) {
		AbstractDataDao.deepRefresh = deepRefresh;
	}

	/**
	 * Output given trace message
	 * 
	 * @param msg
	 *            the message to be output
	 */
	protected abstract void trace(final String msg);

	/**
	 * Get the name of the table is which the DAO is interested
	 * 
	 * @return the name of the table
	 */
	protected abstract String getTableName();

	/**
	 * Get the name of the column that represents ID.
	 * 
	 * @return the name of the ID column
	 */
	protected abstract String getIdColumnName();

	/**
	 * Retrieve the elements from database whose id equals to the given value.
	 * 
	 * @param id
	 *            the id of the element to be retrieved
	 * @return the retrieved element if exists, <code>null</code> otherwise
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	private synchronized D retrieve(final long id) throws Exception {
		trace("get the element whose id is " + id + " from database");
		final D result = originalDao.queryForId(id);

		if (result == null) {
			eLogger.warn("cannot find the corresponding element for id " + id);
		}

		return result;
	}

	/**
	 * Get all the elements in the table as a list.
	 * 
	 * @return all the instances
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Collection<D> getAll() throws Exception {
		trace("get all the elements of this table from database");
		return refreshAll(originalDao.queryForAll());
	}

	/**
	 * Get the element that has the specified id value
	 * 
	 * @param id
	 *            the id as a query of search
	 * @return the instance that has the specified id if found,
	 *         <code>null</code> otherwise
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public D get(final long id) throws Exception {
		D result = retrievedElements.get(id);
		if (result == null) {
			result = retrieve(id);
			refresh(result);
		}
		return result;
	}

	/**
	 * Clear all the stored elements.
	 */
	public void clear() {
		this.retrievedElements.clear();
	}

	/**
	 * Put the given element into the map. <br>
	 * If the number of the elements in the map in this operation, it also
	 * removes half of stored elements from the map.
	 * 
	 * @param element
	 *            the element to be stored
	 * @return the put element
	 */
	protected D put(final D element) {
		D result = retrievedElements.get(element.getId());
		if (result == null) {
			result = element;
			this.retrievedElements.put(element.getId(), element);
			trace("the element " + element.getId() + " was put");
		}

		return result;
	}

	/**
	 * Put all the given elements into the map.
	 * 
	 * @param elements
	 *            the collection of elements to be added
	 */
	protected void putAll(final Collection<D> elements) {
		for (final D element : elements) {
			put(element);
		}
	}

	/**
	 * Get a list of the elements in the table whose id is specified as the
	 * argument.
	 * 
	 * @param ids
	 *            the values of id to be retrieved
	 * @return a list of the elements
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Map<Long, D> get(final long... ids) throws Exception {
		final List<Long> idsList = new ArrayList<>();
		for (final long id : ids) {
			idsList.add(id);
		}

		return get(idsList);
	}

	/**
	 * Get a list of the elements in the table whose id is specified as the
	 * argument.
	 * 
	 * @param ids
	 *            a collection of the values of id to be retrieved
	 * @return a list of the elements
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Map<Long, D> get(final Collection<Long> ids) throws Exception {
		// final List<D> result = new ArrayList<D>();
		//
		// for (final long id : ids) {
		// final D element = get(id);
		// if (element != null) {
		// result.add(element);
		// }
		// }
		// return result;
		final Map<Long, D> result = new TreeMap<>();

		final Collection<Long> idsToBeRetrieved = new HashSet<>();
		for (final Long id : ids) {
			if (retrievedElements.containsKey(id)) {
				result.put(id, retrievedElements.get(id));
			} else {
				idsToBeRetrieved.add(id);
			}
		}

		if (!idsToBeRetrieved.isEmpty()) {
			result.putAll(runRawQuery(QueryHelper.querySelectIdIn(
					getTableName(), getIdColumnName(), ids)));
		}

		return result;
	}

	public Map<Long, D> runRawQuery(final String query) throws Exception {
		final GenericRawResults<R> rawResults = originalDao.queryRaw(query,
				getRowMapper());

		final SortedMap<Long, D> result = new TreeMap<>();
		final SortedMap<String, Set<Long>> relativeElementIds = new TreeMap<>();
		final SortedMap<String, Map<Long, Set<Long>>> foreignChildElementIds = new TreeMap<>();

		initializeRelativeElementIds(relativeElementIds);
		initializeForeignChildElementIds(foreignChildElementIds);

		for (final R rawResult : rawResults) {
			final long id = rawResult.getId();
			if (!retrievedElements.containsKey(id)) {
				updateRelativeElementIds(relativeElementIds, rawResult);
			}
		}

		retrieveRelativeElements(relativeElementIds, foreignChildElementIds);

		for (final R rawResult : rawResults) {
			final long id = rawResult.getId();

			D element = retrievedElements.get(id);
			if (element == null) {
				element = makeInstance(rawResult, foreignChildElementIds);
				retrievedElements.put(id, element);
			}
			result.put(id, element);
		}

		return Collections.unmodifiableSortedMap(result);
	}

	protected abstract RawRowMapper<R> getRowMapper() throws Exception;

	protected abstract void initializeRelativeElementIds(
			final Map<String, Set<Long>> relativeElementIds);

	protected abstract void initializeForeignChildElementIds(
			final Map<String, Map<Long, Set<Long>>> foreignChildElementIds);

	protected abstract void updateRelativeElementIds(
			final Map<String, Set<Long>> relativeElementIds, final R rawResult)
			throws Exception;

	protected abstract void retrieveRelativeElements(
			final Map<String, Set<Long>> relativeElementIds,
			final Map<String, Map<Long, Set<Long>>> foreignChildElementIds)
			throws Exception;

	protected abstract D makeInstance(final R rawResult,
			final Map<String, Map<Long, Set<Long>>> foreignChildElementIds)
			throws Exception;

	/**
	 * Register the given element into the database.
	 * 
	 * @param element
	 *            the element to be stored
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public void register(final D element) throws Exception {
		if (element == null) {
			eLogger.fatal("null is specified as an argument of register method");
			throw new IllegalStateException("null is attemptted to be register");
		} else {
			trace("register the element whose id is " + element.getId());
			originalDao.create(element);
		}
	}

	/**
	 * Register all the given elements into the database.
	 * 
	 * @param elements
	 *            the elements to be stored
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public void registerAll(final Collection<D> elements) throws Exception {
		originalDao.callBatchTasks(new Callable<Void>() {
			public Void call() throws Exception {
				for (D element : elements) {
					originalDao.create(element);
				}
				return null;
			}
		});
	}

	/**
	 * Perform refreshing children of the given element. This operation is
	 * necessary for elements having foreign objects.
	 * 
	 * @param element
	 *            the element to be refreshed
	 * @return the element after refreshed
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	protected abstract D refreshChildren(final D element) throws Exception;

	/**
	 * Perform refreshing for the children of all the given elements.
	 * 
	 * @param elements
	 *            elements to be refreshed
	 * @return the elements after refreshed
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	protected abstract Collection<D> refreshChildrenForAll(
			final Collection<D> elements) throws Exception;

	/**
	 * Check whether the given element is already stored. If so, this method
	 * returns the stored element. Otherwise, this method stores the new element
	 * refreshed.
	 * 
	 * @param element
	 *            element to be checked
	 * @return <code>null</code> in case element is null, the already stored
	 *         element if exists, otherwise newly stored element.
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public D refresh(final D element) throws Exception {
		if (element == null) {
			return null;
		}

		if (!autoRefresh) {
			// if auto refresh is OFF,
			// returns the element as it is
			return element;
		}

		if (this.retrievedElements.containsKey(element.getId())) {
			return this.retrievedElements.get(element.getId());
		}

		// perform native refreshing
		originalDao.refresh(element);
		put(element);

		// put(element);
		return refreshChildren(element);
	}

	/**
	 * Perform refreshing on all the given elements.
	 * 
	 * @param elements
	 *            the elements to be refreshed
	 * @return elements after refreshed
	 * @throws Exception
	 */
	public Collection<D> refreshAll(final Collection<D> elements)
			throws Exception {
		if (!autoRefresh) {
			return elements;
		}

		final Collection<D> elementsToBeRetrieved = new ArrayList<>();
		for (final D element : elements) {
			if (!retrievedElements.containsKey(element.getId())) {
				elementsToBeRetrieved.add(element);
			}
		}

		// refresh the elements themselves in the native way
		refreshThemselves(elementsToBeRetrieved);

		for (final D element : elementsToBeRetrieved) {
			put(element);
		}

		// refresh all the children
		// note: the returned value will be ignored
		refreshChildrenForAll(elementsToBeRetrieved);

		// return not elementsToBeRetrieved but elements
		return elements;
	}

	/**
	 * Perform refreshing in the native way for the given elements.
	 * 
	 * @param elements
	 *            the elements to be refreshed
	 * @throws Exception
	 *             if any error occurred
	 */
	protected void refreshThemselves(final Collection<D> elements)
			throws Exception {
		Class<?> clazz = null;
		for (D element : elements) {
			clazz = element.getClass();
			break;
		}

		long t1 = System.nanoTime();
		// for (D element : elements) {
		// originalDao.refresh(element);
		// }
		originalDao.callBatchTasks(new Callable<Void>() {
			public Void call() throws Exception {
				for (D element : elements) {
					originalDao.refresh(element);
				}
				return null;
			}
		});
		long t2 = System.nanoTime();
		String name = (clazz == null) ? "null" : clazz.getSimpleName();
		System.out.println("refreshing " + name + ": " + (t2 - t1));
	}

	/**
	 * Query the given prepared query.
	 * 
	 * @param preparedQuery
	 *            the prepared query
	 * @return the result of the given query
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Collection<D> query(final PreparedQuery<D> preparedQuery)
			throws Exception {
		return query(preparedQuery, true);
	}

	/**
	 * Query the given prepared query.
	 * 
	 * @param preparedQuery
	 *            the prepared query
	 * @param refresh
	 *            whether the elements should be refreshed
	 * @return the result of the given query
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	protected Collection<D> query(final PreparedQuery<D> preparedQuery,
			final boolean refresh) throws Exception {
		if (preparedQuery == null) {
			eLogger.warn("the specified prepared query is null, so nothing will be done");
			return new ArrayList<D>();
		}

		trace("query " + preparedQuery.getStatement());

		final Collection<D> result = originalDao.query(preparedQuery);

		if (refresh) {
			return refreshAll(result);
		} else {
			return result;
		}
	}

	/**
	 * Get all the ids stored in the database.
	 * 
	 * @return A collection contains all the ids
	 * @throws Exception
	 *             if any error occurred
	 */
	public List<Long> getAllIds() throws Exception {
		// final Collection<D> all = originalDao.queryForAll();

		final List<Long> result = new ArrayList<>();
		// for (final D element : all) {
		// result.add(element.getId());
		// }
		GenericRawResults<Long> rawResults = originalDao.queryRaw("select "
				+ getIdColumnName() + " from " + getTableName(), (columns,
				results) -> {
			return Long.parseLong(results[0]);
		});
		for (Long id : rawResults) {
			result.add(id);
		}

		Collections.sort(result);

		return Collections.unmodifiableList(result);
	}

}
