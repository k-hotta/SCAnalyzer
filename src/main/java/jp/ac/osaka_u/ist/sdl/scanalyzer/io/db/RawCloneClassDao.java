package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;

/**
 * The DAO for {@link DBRawCloneClass}.
 * 
 * @author k-hotta
 * 
 * @see DBRawCloneClass
 */
public class RawCloneClassDao extends AbstractDataDao<DBRawCloneClass> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(RawCloneClassDao.class);

	/**
	 * The DAO for versions. <br>
	 * This is for refreshing
	 */
	private VersionDao versionDao;

	/**
	 * The DAO for raw cloned fragments. <br>
	 * This is for refreshing.
	 */
	private RawClonedFragmentDao rawClonedFragmentDao;

	@SuppressWarnings("unchecked")
	public RawCloneClassDao() throws SQLException {
		super((Dao<DBRawCloneClass, Long>) DBManager.getInstance().getNativeDao(
				DBRawCloneClass.class));
		this.versionDao = null;
		this.rawClonedFragmentDao = null;
	}

	/**
	 * Set the DAO for SourceFile with the specified one
	 * 
	 * @param rawClonedFragmentDao
	 *            the DAO to be set
	 */
	void setRawClonedFragmentDao(final RawClonedFragmentDao rawClonedFragmentDao) {
		this.rawClonedFragmentDao = rawClonedFragmentDao;
	}

	/**
	 * Set the DAO for Version with the specified one
	 * 
	 * @param versionDao
	 *            the DAO to be set
	 */
	void setVersionDao(final VersionDao versionDao) {
		this.versionDao = versionDao;
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	public DBRawCloneClass refresh(DBRawCloneClass element) throws SQLException {
		element.setVersion(versionDao.get(element.getVersion().getId()));
		final Collection<DBRawClonedFragment> rawClonedFragments = new ArrayList<DBRawClonedFragment>();
		for (final DBRawClonedFragment rawClonedFragment : element.getElements()) {
			rawClonedFragments.add(rawClonedFragmentDao.get(rawClonedFragment
					.getId()));
		}
		element.setElements(rawClonedFragments);

		return element;
	}

	/**
	 * Get the elements whose versions are the specified one.
	 * 
	 * @param version
	 *            version as a query
	 * @return a list of the elements whose versions are the specified one
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<DBRawCloneClass> getWithVersion(final DBVersion version)
			throws SQLException {
		return refreshAll(originalDao.queryForEq(
				DBRawCloneClass.VERSION_COLUMN_NAME, version));
	}

}
