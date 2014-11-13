package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBElementComparator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;

/**
 * The DAO for {@link DBCloneClassMapping}.
 * 
 * @author k-hotta
 *
 * @see DBCloneClassMapping
 */
public class CloneClassMappingDao extends AbstractDataDao<DBCloneClassMapping> {

	/**
	 * The logger
	 */
	private static Logger logger = LogManager
			.getLogger(CloneClassMappingDao.class);

	/**
	 * The DAO for clone classes.
	 */
	private CloneClassDao cloneClassDao;

	/**
	 * The DAO for code fragment mappings.
	 */
	private CodeFragmentMappingDao codeFragmentMappingDao;

	@SuppressWarnings("unchecked")
	public CloneClassMappingDao() throws SQLException {
		super((Dao<DBCloneClassMapping, Long>) DBManager.getInstance()
				.getNativeDao(DBCloneClassMapping.class));
		cloneClassDao = null;
		codeFragmentMappingDao = null;
	}

	/**
	 * Set the DAO for CloneClass with the specified one.
	 * 
	 * @param cloneClassDao
	 *            the DAO to be set
	 */
	void setCloneClassDao(final CloneClassDao cloneClassDao) {
		this.cloneClassDao = cloneClassDao;
	}

	/**
	 * Set the DAO for CodeFragmentMapping with the specified one.
	 * 
	 * @param codeFragmentMappingDao
	 *            the DAO to be set
	 */
	void setCodeFragmentMappingDao(
			final CodeFragmentMappingDao codeFragmentMappingDao) {
		this.codeFragmentMappingDao = codeFragmentMappingDao;
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	public DBCloneClassMapping refresh(DBCloneClassMapping element)
			throws SQLException {
		if (element.getOldCloneClass() != null) {
			element.setOldCloneClass(cloneClassDao.get(element
					.getOldCloneClass().getId()));
		}

		if (element.getNewCloneClass() != null) {
			element.setNewCloneClass(cloneClassDao.get(element
					.getNewCloneClass().getId()));
		}

		final Collection<DBCodeFragmentMapping> codeFragmentMappings = new TreeSet<>(
				new DBElementComparator());
		for (final DBCodeFragmentMapping codeFragmentMapping : element
				.getCodeFragmentMappings()) {
			codeFragmentMappings.add(codeFragmentMappingDao
					.get(codeFragmentMapping.getId()));
		}
		element.setCodeFragmentMappings(codeFragmentMappings);

		return element;
	}

}
