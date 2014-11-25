package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneGenealogyCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

/**
 * The DAO for {@link DBCloneGenealogy}.
 * 
 * @author k-hotta
 *
 * @see DBCloneGenealogy
 * @see DBCloneGenealogyCloneClassMapping
 */
public class CloneGenealogyDao extends AbstractDataDao<DBCloneGenealogy> {

	/**
	 * The logger
	 */
	private static Logger logger = LogManager
			.getLogger(CloneGenealogyDao.class);

	/**
	 * The DAO for Version
	 */
	private VersionDao versionDao;

	/**
	 * The DAO for CloneClassMapping
	 */
	private CloneClassMappingDao cloneClassMappingDao;

	/**
	 * The native DAO for CloneClassMapping.
	 */
	private final Dao<DBCloneClassMapping, Long> nativeCloneClassMappingDao;

	/**
	 * The native DAO for CloneGenealogyCloneClassMapping
	 */
	private final Dao<DBCloneGenealogyCloneClassMapping, Long> nativeCloneGenealogyCloneClassMappingDao;

	/**
	 * The query to get corresponding clone class mappings
	 */
	private PreparedQuery<DBCloneClassMapping> cloneClassMappingsForCloneGenealogyQuery;

	@SuppressWarnings("unchecked")
	public CloneGenealogyDao() throws SQLException {
		super((Dao<DBCloneGenealogy, Long>) DBManager.getInstance()
				.getNativeDao(DBCloneGenealogy.class));
		this.nativeCloneClassMappingDao = DBManager.getInstance().getNativeDao(
				DBCloneClassMapping.class);
		this.nativeCloneGenealogyCloneClassMappingDao = DBManager.getInstance()
				.getNativeDao(DBCloneGenealogyCloneClassMapping.class);
		this.versionDao = null;
		this.cloneClassMappingDao = null;
		this.cloneClassMappingsForCloneGenealogyQuery = null;
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	protected String getTableName() {
		return TableName.CLONE_GENEALOGY;
	}

	@Override
	protected String getIdColumnName() {
		return DBCloneGenealogy.ID_COLUMN_NAME;
	}

	/**
	 * Set the DAO for Version.
	 * 
	 * @param versionDao
	 *            the DAO to be set
	 */
	void setVersionDao(final VersionDao versionDao) {
		this.versionDao = versionDao;
	}

	/**
	 * Set the DAO for CloneClassMapping.
	 * 
	 * @param cloneClassMappingDao
	 *            the DAO to be set
	 */
	void setCloneClassMappingDao(final CloneClassMappingDao cloneClassMappingDao) {
		this.cloneClassMappingDao = cloneClassMappingDao;
	}

	@Override
	protected DBCloneGenealogy refreshChildren(DBCloneGenealogy element)
			throws Exception {
		if (deepRefresh) {
			final Set<DBVersion> versionsToBeRefreshed = new HashSet<>();
			versionsToBeRefreshed.add(element.getStartVersion());
			versionsToBeRefreshed.add(element.getEndVersion());
			versionDao.refreshAll(versionsToBeRefreshed);
			element.setStartVersion(versionDao.get(element.getStartVersion()
					.getId()));
			element.setEndVersion(versionDao.get(element.getEndVersion()
					.getId()));
		}

		final Collection<DBCloneClassMapping> mappingsInElement = getCorrespondingCloneClassMappings(element);

		cloneClassMappingDao.refreshAll(mappingsInElement);
		final Collection<DBCloneClassMapping> toBeStored = new ArrayList<>();
		for (final DBCloneClassMapping mappingInElement : mappingsInElement) {
			toBeStored.add(cloneClassMappingDao.get(mappingInElement.getId()));
		}
		element.setCloneClassMappings(toBeStored);

		return element;
	}

	@Override
	protected Collection<DBCloneGenealogy> refreshChildrenForAll(
			Collection<DBCloneGenealogy> elements) throws Exception {
		if (deepRefresh) {
			final Set<DBVersion> versionsToBeRefreshed = new HashSet<>();
			for (final DBCloneGenealogy element : elements) {
				versionsToBeRefreshed.add(element.getStartVersion());
				versionsToBeRefreshed.add(element.getEndVersion());
			}
			versionDao.refreshAll(versionsToBeRefreshed);
			for (final DBCloneGenealogy element : elements) {
				element.setStartVersion(versionDao.get(element
						.getStartVersion().getId()));
				element.setEndVersion(versionDao.get(element.getEndVersion()
						.getId()));
			}
		}

		final Set<DBCloneClassMapping> cloneClassMappingsToBeRetrieved = new HashSet<>();
		final Map<Long, Collection<DBCloneClassMapping>> cloneClassMappingInElements = new TreeMap<>();
		for (final DBCloneGenealogy element : elements) {
			final Collection<DBCloneClassMapping> mappingsInElement = getCorrespondingCloneClassMappings(element);
			cloneClassMappingsToBeRetrieved.addAll(mappingsInElement);
			cloneClassMappingInElements.put(element.getId(), mappingsInElement);
		}
		cloneClassMappingDao.refreshAll(cloneClassMappingsToBeRetrieved);
		for (final DBCloneGenealogy element : elements) {
			final Collection<DBCloneClassMapping> mappingsInElement = cloneClassMappingInElements
					.get(element.getId());
			final Collection<DBCloneClassMapping> toBeStored = new ArrayList<>();
			for (final DBCloneClassMapping mappingInElement : mappingsInElement) {
				toBeStored.add(cloneClassMappingDao.get(mappingInElement
						.getId()));
			}
			element.setCloneClassMappings(toBeStored);
		}

		return elements;
	}

	private Collection<DBCloneClassMapping> getCorrespondingCloneClassMappings(
			final DBCloneGenealogy cloneGenealogy) throws Exception {
		if (cloneClassMappingsForCloneGenealogyQuery == null) {
			cloneClassMappingsForCloneGenealogyQuery = makeCloneClassMappingsForCloneGenealogyQuery();
		}
		cloneClassMappingsForCloneGenealogyQuery.setArgumentHolderValue(0,
				cloneGenealogy);

		return cloneClassMappingDao.query(
				cloneClassMappingsForCloneGenealogyQuery, false);
	}

	private PreparedQuery<DBCloneClassMapping> makeCloneClassMappingsForCloneGenealogyQuery()
			throws Exception {
		QueryBuilder<DBCloneGenealogyCloneClassMapping, Long> cloneGenealogyCloneClassMappingQb = nativeCloneGenealogyCloneClassMappingDao
				.queryBuilder();

		cloneGenealogyCloneClassMappingQb
				.selectColumns(DBCloneGenealogyCloneClassMapping.CLONE_CLASS_MAPPING_COLUMN_NAME);
		SelectArg cloneGenealogySelectArg = new SelectArg();
		cloneGenealogyCloneClassMappingQb.where().eq(
				DBCloneGenealogyCloneClassMapping.CLONE_GENEALOGY_COLUMN_NAME,
				cloneGenealogySelectArg);

		QueryBuilder<DBCloneClassMapping, Long> cloneClassMappingQb = nativeCloneClassMappingDao
				.queryBuilder();
		cloneClassMappingQb.where().in(DBCloneClassMapping.ID_COLUMN_NAME,
				cloneGenealogyCloneClassMappingQb);

		return cloneClassMappingQb.prepare();
	}

	@Override
	public void register(final DBCloneGenealogy element) throws Exception {
		super.register(element);

		final List<DBCloneGenealogyCloneClassMapping> gMappings = new ArrayList<>();
		for (final DBCloneClassMapping mapping : element
				.getCloneClassMappings()) {
			final DBCloneGenealogyCloneClassMapping gMapping = new DBCloneGenealogyCloneClassMapping(
					IDGenerator
							.generate(DBCloneGenealogyCloneClassMapping.class),
					element, mapping);
			gMappings.add(gMapping);
		}

		nativeCloneGenealogyCloneClassMappingDao
				.callBatchTasks(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						for (final DBCloneGenealogyCloneClassMapping gMapping : gMappings) {
							nativeCloneGenealogyCloneClassMappingDao
									.create(gMapping);
						}
						return null;
					}
				});
	}

	@Override
	public void registerAll(final Collection<DBCloneGenealogy> elements)
			throws Exception {
		super.registerAll(elements);

		final List<DBCloneGenealogyCloneClassMapping> gMappings = new ArrayList<>();
		for (final DBCloneGenealogy element : elements) {
			for (final DBCloneClassMapping mapping : element
					.getCloneClassMappings()) {
				final DBCloneGenealogyCloneClassMapping gMapping = new DBCloneGenealogyCloneClassMapping(
						IDGenerator
								.generate(DBCloneGenealogyCloneClassMapping.class),
						element, mapping);
				gMappings.add(gMapping);
			}
		}

		nativeCloneGenealogyCloneClassMappingDao
				.callBatchTasks(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						for (final DBCloneGenealogyCloneClassMapping gMapping : gMappings) {
							nativeCloneGenealogyCloneClassMappingDao
									.create(gMapping);
						}
						return null;
					}
				});
	}

}
