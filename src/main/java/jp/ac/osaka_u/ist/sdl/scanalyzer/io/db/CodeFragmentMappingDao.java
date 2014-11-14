package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragmentMapping;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;

public class CodeFragmentMappingDao extends
		AbstractDataDao<DBCodeFragmentMapping> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(CodeFragmentMappingDao.class);

	/**
	 * The DAO for code fragments.
	 */
	private CodeFragmentDao codeFragmentDao;

	/**
	 * The DAO for clone class mappings.
	 */
	private CloneClassMappingDao cloneClassMappingDao;

	@SuppressWarnings("unchecked")
	public CodeFragmentMappingDao() throws SQLException {
		super((Dao<DBCodeFragmentMapping, Long>) DBManager.getInstance()
				.getNativeDao(DBCodeFragmentMapping.class));
		codeFragmentDao = null;
		cloneClassMappingDao = null;
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
	 * Set the DAO for CloneClassMapping with the specified one.
	 * 
	 * @param cloneClassMappingDao
	 *            the DAO to be set
	 */
	void setCloneClassMappingDao(final CloneClassMappingDao cloneClassMappingDao) {
		this.cloneClassMappingDao = cloneClassMappingDao;
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	public DBCodeFragmentMapping refresh(DBCodeFragmentMapping element)
			throws SQLException {
		if (element.getOldCodeFragment() != null) {
			element.setOldCodeFragment(codeFragmentDao.get(element
					.getOldCodeFragment().getId()));
		}

		if (element.getNewCodeFragment() != null) {
			element.setNewCodeFragment(codeFragmentDao.get(element
					.getNewCodeFragment().getId()));
		}

		element.setCloneClassMapping(cloneClassMappingDao.get(element
				.getCloneClassMapping().getId()));

		return element;
	}

}