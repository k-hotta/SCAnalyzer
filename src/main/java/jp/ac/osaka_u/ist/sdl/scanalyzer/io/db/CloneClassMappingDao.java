package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

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

	/**
	 * The DAO for version
	 */
	private VersionDao versionDao;

	@SuppressWarnings("unchecked")
	public CloneClassMappingDao() throws SQLException {
		super((Dao<DBCloneClassMapping, Long>) DBManager.getInstance()
				.getNativeDao(DBCloneClassMapping.class));
		cloneClassDao = null;
		codeFragmentMappingDao = null;
		versionDao = null;
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
		return TableName.CLONE_CLASS_MAPPING;
	}

	@Override
	protected String getIdColumnName() {
		return DBCloneClassMapping.ID_COLUMN_NAME;
	}

	@Override
	protected DBCloneClassMapping refreshChildren(DBCloneClassMapping element)
			throws Exception {
		if (element.getOldCloneClass() != null) {
			cloneClassDao.refresh(element.getOldCloneClass());
		}

		if (element.getNewCloneClass() != null) {
			cloneClassDao.refresh(element.getNewCloneClass());
		}

		codeFragmentMappingDao.refreshAll(element.getCodeFragmentMappings());

		if (deepRefresh) {
			versionDao.refresh(element.getVersion());
		}

		return element;
	}

	@Override
	protected Collection<DBCloneClassMapping> refreshChildrenForAll(
			Collection<DBCloneClassMapping> elements) throws Exception {
		final Set<DBCloneClass> cloneClassesToBeRefreshed = new HashSet<>();
		for (final DBCloneClassMapping element : elements) {
			if (element.getOldCloneClass() != null) {
				cloneClassesToBeRefreshed.add(element.getOldCloneClass());
			}
			if (element.getNewCloneClass() != null) {
				cloneClassesToBeRefreshed.add(element.getNewCloneClass());
			}
		}
		cloneClassDao.refreshAll(cloneClassesToBeRefreshed);
		for (final DBCloneClassMapping element : elements) {
			if (element.getOldCloneClass() != null) {
				element.setOldCloneClass(cloneClassDao.get(element
						.getOldCloneClass().getId()));
			}
			if (element.getNewCloneClass() != null) {
				element.setOldCloneClass(cloneClassDao.get(element
						.getNewCloneClass().getId()));
			}
		}

		final Set<DBCodeFragmentMapping> fragmentMappingsToBeRefreshed = new HashSet<>();
		final Map<Long, Collection<DBCodeFragmentMapping>> fragmentMappingsInElements = new TreeMap<>();
		for (final DBCloneClassMapping element : elements) {
			final Collection<DBCodeFragmentMapping> fragmentMappingsInElement = element
					.getCodeFragmentMappings();
			fragmentMappingsToBeRefreshed.addAll(fragmentMappingsInElement);
			fragmentMappingsInElements.put(element.getId(),
					fragmentMappingsInElement);
		}
		codeFragmentMappingDao.refreshAll(fragmentMappingsToBeRefreshed);
		for (final DBCloneClassMapping element : elements) {
			final List<DBCodeFragmentMapping> toBeStored = new ArrayList<>();
			for (final DBCodeFragmentMapping fragmentMapping : fragmentMappingsInElements
					.get(element.getId())) {
				toBeStored.add(codeFragmentMappingDao.get(fragmentMapping
						.getId()));
			}
			element.setCodeFragmentMappings(toBeStored);
		}

		if (deepRefresh) {
			final Set<DBVersion> versionsToBeRefreshed = new HashSet<>();
			for (final DBCloneClassMapping element : elements) {
				versionsToBeRefreshed.add(element.getVersion());
			}
			versionDao.refreshAll(versionsToBeRefreshed);
			for (final DBCloneClassMapping element : elements) {
				element.setVersion(versionDao.get(element.getVersion().getId()));
			}
		}

		return elements;
	}

	private class InternalDBCloneClassMapping {

		private final long id;

		private final Long oldCloneClassId;

		private final Long newCloneClassId;

		private final Long versionId;

		private InternalDBCloneClassMapping(final long id,
				final Long oldCloneClassId, final Long newCloneClassId,
				final Long versionId) {
			this.id = id;
			this.oldCloneClassId = oldCloneClassId;
			this.newCloneClassId = newCloneClassId;
			this.versionId = versionId;
		}

		private final long getId() {
			return id;
		}

		private final Long getOldCloneClassId() {
			return oldCloneClassId;
		}

		private final Long getNewCloneClassId() {
			return newCloneClassId;
		}

		private final Long getVersionId() {
			return versionId;
		}

	}

}
