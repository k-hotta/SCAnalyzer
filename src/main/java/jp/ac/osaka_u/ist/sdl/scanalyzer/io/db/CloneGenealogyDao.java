package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneGenealogyCloneClassMapping;

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
	public DBCloneGenealogy refresh(DBCloneGenealogy element)
			throws SQLException {
		if (deepRefresh) {
			element.setStartVersion(versionDao.get(element.getStartVersion()
					.getId()));
			element.setEndVersion(versionDao.get(element.getEndVersion()
					.getId()));
		}

		element.setCloneClassMappings(getCorrespondingCloneClassMappings(element));

		return element;
	}

	public List<DBCloneClassMapping> getCorrespondingCloneClassMappings(
			final DBCloneGenealogy cloneGenealogy) throws SQLException {
		if (cloneClassMappingsForCloneGenealogyQuery == null) {
			cloneClassMappingsForCloneGenealogyQuery = makeCloneClassMappingsForCloneGenealogyQuery();
		}
		cloneClassMappingsForCloneGenealogyQuery.setArgumentHolderValue(0,
				cloneGenealogy);

		return cloneClassMappingDao
				.query(cloneClassMappingsForCloneGenealogyQuery);
	}

	private PreparedQuery<DBCloneClassMapping> makeCloneClassMappingsForCloneGenealogyQuery()
			throws SQLException {
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
