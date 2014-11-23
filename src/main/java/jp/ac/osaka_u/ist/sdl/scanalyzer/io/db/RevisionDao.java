package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;

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
	protected DBRevision refreshChildren(DBRevision element) throws Exception {
		// do nothing because Revision doesn't have any foreign field
		return element;
	}

	@Override
	protected Collection<DBRevision> refreshChildrenForAll(
			Collection<DBRevision> elements) throws Exception {
		// do nothing because Revision doesn't have any foreign field
		return elements;
	}

}
