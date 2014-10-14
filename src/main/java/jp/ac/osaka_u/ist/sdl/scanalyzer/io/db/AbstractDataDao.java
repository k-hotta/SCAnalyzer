package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDBElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;

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
	}

	/**
	 * Output given trace message
	 * 
	 * @param msg
	 *            the message to be output
	 */
	protected abstract void trace(final String msg);

	/**
	 * Get all the elements in the table as a list
	 * 
	 * @return all the instances
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<D> getAll() throws SQLException {
		trace("get all the elements of this table from database");
		return originalDao.queryForAll();
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
		trace("get the element whose id is " + id + " from database");
		final D result = originalDao.queryForId(id);

		if (result == null) {
			eLogger.warn("cannot find the corresponding element for id " + id);
		}

		return refresh(result);
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
		trace("register the element whose id is " + element.getId());
		originalDao.create(element);
	}

	/**
	 * Register all the given elements into the database.
	 * 
	 * @param elements
	 *            the elements to be stored
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public void register(final Collection<D> elements) throws SQLException {
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
	 * Perform refreshing on all the given elements.
	 * 
	 * @param elements
	 *            the elements to be refreshed
	 * @return elements after refreshed
	 * @throws SQLException
	 */
	public List<D> refreshAll(final List<D> elements) throws SQLException {
		for (final D element : elements) {
			refresh(element);
		}

		return elements;
	}

}
