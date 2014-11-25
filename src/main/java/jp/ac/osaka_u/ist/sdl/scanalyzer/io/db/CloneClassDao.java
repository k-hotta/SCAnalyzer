package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

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
	protected String getTableName() {
		return TableName.CLONE_CLASS;
	}

	@Override
	protected String getIdColumnName() {
		return DBCloneClass.ID_COLUMN_NAME;
	}

	@Override
	protected DBCloneClass refreshChildren(DBCloneClass element)
			throws Exception {
		codeFragmentDao.refreshAll(element.getCodeFragments());

		if (deepRefresh) {
			versionDao.refresh(element.getVersion());
		}

		return element;
	}

	@Override
	protected Collection<DBCloneClass> refreshChildrenForAll(
			Collection<DBCloneClass> elements) throws Exception {
		final Set<DBCodeFragment> codeFragmentsToBeRefreshed = new HashSet<>();
		for (final DBCloneClass element : elements) {
			codeFragmentsToBeRefreshed.addAll(element.getCodeFragments());
		}
		codeFragmentDao.refreshAll(codeFragmentsToBeRefreshed);
		for (final DBCloneClass element : elements) {
			final List<DBCodeFragment> toBeStored = new ArrayList<>();
			for (final DBCodeFragment codeFragment : element.getCodeFragments()) {
				toBeStored.add(codeFragmentDao.get(codeFragment.getId()));
			}
			element.setCodeFragments(toBeStored);
		}

		if (deepRefresh) {
			final Set<DBVersion> versionsToBeRefreshed = new HashSet<>();
			for (final DBCloneClass element : elements) {
				versionsToBeRefreshed.add(element.getVersion());
			}
			versionDao.refreshAll(versionsToBeRefreshed);
			for (final DBCloneClass element : elements) {
				element.setVersion(versionDao.get(element.getVersion().getId()));
			}
		}

		return elements;
	}

}
