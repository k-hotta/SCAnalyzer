package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBRevision;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;

/**
 * The DAO for {@link DBRevision}.
 * 
 * @author k-hotta
 * 
 * @see DBRevision
 */
public class RevisionDao extends AbstractDataDao<DBRevision> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(RevisionDao.class);

	@SuppressWarnings("unchecked")
	public RevisionDao() throws SQLException {
		super((Dao<DBRevision, Long>) DBManager.getInstance().getNativeDao(
				DBRevision.class));
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	public DBRevision refresh(DBRevision element) throws SQLException {
		// do nothing because Revision doesn't have any foreign field
		return element;
	}

	/**
	 * Get the element whose identifier is the specified one. <br>
	 * <p>
	 * Note: At most one element should be detected by this operation since the
	 * identifier of {@link DBRevision} must be unique.
	 * </p>
	 * 
	 * @param identifier
	 *            identifier as a query
	 * @return the element whose identifier is the specified one if found,
	 *         <code>null</code> otherwise
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public DBRevision getWithIdentifier(final String identifier)
			throws SQLException {
		final List<DBRevision> resultAsList = refreshAll(originalDao.queryForEq(
				DBRevision.IDENTIFIER_COLUMN_NAME, identifier));

		if (resultAsList.isEmpty()) {
			return null; // nothing is found
		}

		// there must be only one element because the identifier field is unique
		assert resultAsList.size() == 1;

		return resultAsList.get(0);
	}

	/**
	 * Get the elements whose date is the specified one.
	 * 
	 * @param date
	 *            date as a query
	 * @return the elements whose date is the specified one
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<DBRevision> getWithDate(final Date date) throws SQLException {
		return refreshAll(originalDao.queryForEq(DBRevision.DATE_COLUMN_NAME,
				date));
	}

}
