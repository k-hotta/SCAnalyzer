package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBElementComparator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;

/**
 * The DAO for {@link CloneClass}.
 * 
 * @author k-hotta
 * 
 * @see CloneClass
 * @see CloneClassCodeFragment
 */
public class CloneClassDao extends AbstractDataDao<CloneClass> {

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
		super((Dao<CloneClass, Long>) DBManager.getInstance().getNativeDao(
				CloneClass.class));
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
	public CloneClass refresh(CloneClass element) throws SQLException {
		final Collection<CodeFragment> codeFragments = new TreeSet<CodeFragment>(
				new DBElementComparator());
		for (final CodeFragment codeFragment : element.getCodeFragments()) {
			codeFragments.add(codeFragmentDao.get(codeFragment.getId()));
		}
		element.setCodeFragments(codeFragments);

		element.setVersion(versionDao.get(element.getVersion().getId()));

		return element;
	}

}
