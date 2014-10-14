package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;

/**
 * The DAO for {@link RawCloneClass}.
 * 
 * @author k-hotta
 * 
 * @see RawCloneClass
 */
public class RawCloneClassDao extends AbstractDataDao<RawCloneClass> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(RawCloneClassDao.class);

	/**
	 * The DAO for versions. <br>
	 * This is for refreshing
	 */
	private final Dao<Version, Long> versionDao;

	/**
	 * The DAO for raw cloned fragments. <br>
	 * This is for refreshing.
	 */
	private final Dao<RawClonedFragment, Long> rawClonedFragmentDao;

	@SuppressWarnings("unchecked")
	public RawCloneClassDao() throws SQLException {
		super((Dao<RawCloneClass, Long>) DBManager.getInstance().getDao(
				RawCloneClass.class));
		this.versionDao = this.manager.getDao(Version.class);
		this.rawClonedFragmentDao = this.manager
				.getDao(RawClonedFragment.class);
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	public RawCloneClass refresh(RawCloneClass element) throws SQLException {
		versionDao.refresh(element.getVersion());
		for (final RawClonedFragment rawClonedFragment : element.getElements()) {
			rawClonedFragmentDao.refresh(rawClonedFragment);
		}

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
	public List<RawCloneClass> getWithVersion(final Version version)
			throws SQLException {
		return refreshAll(originalDao.queryForEq(
				RawCloneClass.VERSION_COLUMN_NAME, version));
	}

}
