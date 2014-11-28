package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

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
	protected String getTableName() {
		return TableName.CODE_FRAGMENT_MAPPING;
	}

	@Override
	protected String getIdColumnName() {
		return DBCodeFragmentMapping.ID_COLUMN_NAME;
	}

	@Override
	protected DBCodeFragmentMapping refreshChildren(
			DBCodeFragmentMapping element) throws Exception {
		if (element.getOldCodeFragment() != null) {
			codeFragmentDao.refresh(element.getOldCodeFragment());
		}

		if (element.getNewCodeFragment() != null) {
			codeFragmentDao.refresh(element.getNewCodeFragment());
		}

		if (deepRefresh) {
			cloneClassMappingDao.refresh(element.getCloneClassMapping());
		}

		return element;
	}

	@Override
	protected Collection<DBCodeFragmentMapping> refreshChildrenForAll(
			Collection<DBCodeFragmentMapping> elements) throws Exception {
		final Set<DBCodeFragment> codeFragmentsToBeRefreshed = new HashSet<>();
		for (final DBCodeFragmentMapping element : elements) {
			if (element.getOldCodeFragment() != null) {
				codeFragmentsToBeRefreshed.add(element.getOldCodeFragment());
			}
			if (element.getNewCodeFragment() != null) {
				codeFragmentsToBeRefreshed.add(element.getNewCodeFragment());
			}
		}
		codeFragmentDao.refreshAll(codeFragmentsToBeRefreshed);
		for (final DBCodeFragmentMapping element : elements) {
			if (element.getOldCodeFragment() != null) {
				element.setOldCodeFragment(codeFragmentDao.get(element
						.getOldCodeFragment().getId()));
			}
			if (element.getNewCodeFragment() != null) {
				element.setNewCodeFragment(codeFragmentDao.get(element
						.getNewCodeFragment().getId()));
			}
		}

		if (deepRefresh) {
			final Set<DBCloneClassMapping> cloneClassMappingsToBeRetrieved = new HashSet<>();
			for (final DBCodeFragmentMapping element : elements) {
				cloneClassMappingsToBeRetrieved.add(element
						.getCloneClassMapping());
			}
			cloneClassMappingDao.refreshAll(cloneClassMappingsToBeRetrieved);
			for (final DBCodeFragmentMapping element : elements) {
				element.setCloneClassMapping(cloneClassMappingDao.get(element
						.getCloneClassMapping().getId()));
			}
		}

		return elements;
	}

	public Map<Long, DBCodeFragmentMapping> getWithCloneClassMappingIds(
			final Collection<Long> cloneClassMappingIds) {
		// TODO implement
		return null;
	}

}
