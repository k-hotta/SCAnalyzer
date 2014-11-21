package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBElementComparator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;

/**
 * The DAO for {@link DBCloneClass}.
 * 
 * @author k-hotta
 * 
 * @see DBCloneClass
 */
public class CloneClassDao extends AbstractDataDao<DBCloneClass> {

	/**
	 * The logger
	 */
	private static Logger logger = LogManager.getLogger(CloneClassDao.class);

	/**
	 * The DAO for version
	 */
	private VersionDao versionDao;

	/**
	 * The DAO for CodeFragment
	 */
	private CodeFragmentDao codeFragmentDao;

	@SuppressWarnings("unchecked")
	public CloneClassDao() throws SQLException {
		super((Dao<DBCloneClass, Long>) DBManager.getInstance().getNativeDao(
				DBCloneClass.class));
		codeFragmentDao = null;
		versionDao = null;
	}

	/**
	 * Set the DAO for CodeFragment with the specified one.
	 * 
	 * @param codeFragmentDao
	 *            the DAO to be set
	 */
	void setCodeFragmentDao(final CodeFragmentDao codeFragmentDao) {
		this.codeFragmentDao = codeFragmentDao;
	}

	/**
	 * Set the DAO for Version with the specified one.
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
	public DBCloneClass refresh(DBCloneClass element) throws SQLException {
		final Collection<DBCodeFragment> codeFragments = new TreeSet<DBCodeFragment>(
				new DBElementComparator());
		for (final DBCodeFragment codeFragment : element.getCodeFragments()) {
			codeFragments.add(codeFragmentDao.get(codeFragment.getId()));
		}
		element.setCodeFragments(codeFragments);

		if (deepRefresh) {
			element.setVersion(versionDao.get(element.getVersion().getId()));
		}

		return element;
	}

}
