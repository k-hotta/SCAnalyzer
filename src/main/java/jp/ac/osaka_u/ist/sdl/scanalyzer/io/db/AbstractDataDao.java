package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDBElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
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
public abstract class AbstractDataDao<D extends IDBElement> {

	/**
	 * The logger for errors
	 */
	private static final Logger eLogger = LogManager.getLogger("error");

	/**
	 * The DB manager
	 */
	protected final DBManager manager;

	/**
	 * The DAO instance of the data class provided by ORMLite
	 */
	protected final Dao<D, Long> originalDao;

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
	 * Output given trace message
	 * 
	 * @param msg
	 *            the message to be output
	 */
	protected abstract void trace(final String msg);

	/**
	 * Retrieve the elements from database whose id equals to the given value.
	 * 
	 * @param id
	 *            the id of the element to be retrieved
	 * @return the retrieved element if exists, <code>null</code> otherwise
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	private synchronized D retrieve(final long id) throws SQLException {
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
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<D> getAll() throws SQLException {
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
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public D get(final long id) throws SQLException {
		if (retrievedElements.containsKey(id)) {
			return retrievedElements.get(id);
		} else {
			final D result = retrieve(id);
			return checkAndRefresh(result);
		}
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
	 * Get a list of the elements in the table whose id is specified as the
	 * argument.
	 * 
	 * @param ids
	 *            the values of id to be retrieved
	 * @return a list of the elements
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<D> get(final long... ids) throws SQLException {
		final List<D> result = new ArrayList<D>();

		for (final long id : ids) {
			final D element = get(id);
			if (element != null) {
				result.add(element);
			}
		}

		return result;
	}

	/**
	 * Get a list of the elements in the table whose id is specified as the
	 * argument.
	 * 
	 * @param ids
	 *            a collection of the values of id to be retrieved
	 * @return a list of the elements
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<D> get(final Collection<Long> ids) throws SQLException {
		final List<D> result = new ArrayList<D>();

		for (final long id : ids) {
			final D element = get(id);
			if (element != null) {
				result.add(element);
			}
		}

		return result;
	}

	/**
	 * Register the given element into the database.
	 * 
	 * @param element
	 *            the element to be stored
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public void register(final D element) throws SQLException {
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
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public void registerAll(final Collection<D> elements) throws SQLException {
		for (final D element : elements) {
			register(element);
		}
	}

	/**
	 * Perform refreshing on the given element. This operation is necessary for
	 * elements having foreign objects.
	 * 
	 * @param element
	 *            the element to be refreshed
	 * @return the element after refreshed
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public abstract D refresh(final D element) throws SQLException;

	/**
	 * Check whether the given element is already stored. If so, this method
	 * returns the stored element. Otherwise, this method stores the new element
	 * refreshed.
	 * 
	 * @param element
	 *            element to be checked
	 * @return <code>null</code> in case element is null, the already stored
	 *         element if exists, otherwise newly stored element.
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	private D checkAndRefresh(final D element) throws SQLException {
		if (element == null) {
			return null;
		}

		if (this.retrievedElements.containsKey(element.getId())) {
			return this.retrievedElements.get(element.getId());
		}

		put(element);
		return refresh(element);
	}

	/**
	 * Perform refreshing on all the given elements.
	 * 
	 * @param elements
	 *            the elements to be refreshed
	 * @return elements after refreshed
	 * @throws SQLException
	 */
	public List<D> refreshAll(final List<D> elements) throws SQLException {
		final List<D> result = new ArrayList<D>();

		for (final D element : elements) {
			result.add(checkAndRefresh(element));
		}

		return result;
	}

	/**
	 * Query the given prepared query.
	 * 
	 * @param preparedQuery
	 *            the prepared query
	 * @return the result of the given query
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<D> query(final PreparedQuery<D> preparedQuery)
			throws SQLException {
		if (preparedQuery == null) {
			eLogger.warn("the specified prepared query is null, so nothing will be done");
			return new ArrayList<D>();
		}

		trace("query " + preparedQuery.getStatement());
		return refreshAll(originalDao.query(preparedQuery));
	}

}
