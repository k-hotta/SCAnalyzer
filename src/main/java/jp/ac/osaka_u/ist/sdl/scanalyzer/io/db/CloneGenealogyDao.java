package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
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
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
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
public class CloneGenealogyDao
		extends
		AbstractDataDao<DBCloneGenealogy, CloneGenealogyDao.InternalDBCloneGenealogyRepresentation> {

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
	protected RawRowMapper<InternalDBCloneGenealogyRepresentation> getRowMapper()
			throws Exception {
		return new RowMapper();
	}

	@Override
	protected void initializeRelativeElementIds(
			Map<String, Set<Long>> relativeElementIds) {
		relativeElementIds.put(TableName.CLONE_GENEALOGY, new TreeSet<Long>());
		relativeElementIds.put(TableName.VERSION, new TreeSet<Long>());
	}

	@Override
	protected void initializeForeignChildElementIds(
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds) {
		foreignChildElementIds.put(TableName.CLONE_CLASS_MAPPING,
				new TreeMap<Long, Set<Long>>());
	}

	@Override
	protected void updateRelativeElementIds(
			Map<String, Set<Long>> relativeElementIds,
			InternalDBCloneGenealogyRepresentation rawResult) throws Exception {
		relativeElementIds.get(TableName.CLONE_GENEALOGY)
				.add(rawResult.getId());

		final Set<Long> versionIdsToBeRetrieved = relativeElementIds
				.get(TableName.VERSION);
		versionIdsToBeRetrieved.add(rawResult.getStartVersionId());
		versionIdsToBeRetrieved.add(rawResult.getEndVersionId());
	}

	@Override
	protected void retrieveRelativeElements(
			Map<String, Set<Long>> relativeElementIds,
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds)
			throws Exception {
		// retrieve clone class mappings
		final Set<Long> cloneGenealogyIdsToBeRetrieved = relativeElementIds
				.get(TableName.CLONE_GENEALOGY);

		// get IDs of relative clone class mapping
		// from CloneGenealogyCloneClassMapping table
		final String queryForCloneClassMappings = QueryHelper.querySelectIdIn(
				TableName.CLONE_GENEALOGY_CLONE_CLASS,
				DBCloneGenealogyCloneClassMapping.CLONE_GENEALOGY_COLUMN_NAME,
				cloneGenealogyIdsToBeRetrieved);
		final GenericRawResults<InternalDBCloneGenealogyCloneClassMapping> rawIntermediateResults = nativeCloneGenealogyCloneClassMappingDao
				.queryRaw(
						queryForCloneClassMappings,
						(columnNames, resultColumns) -> {
							Long id = null;
							Long cloneGenealogyId = null;
							Long cloneClassMappingId = null;

							for (int i = 0; i < columnNames.length; i++) {
								final String columnName = columnNames[i];
								final String resultColumn = resultColumns[i];

								switch (columnName) {
								case DBCloneGenealogyCloneClassMapping.ID_COLUMN_NAME:
									id = Long.parseLong(resultColumn);
									break;
								case DBCloneGenealogyCloneClassMapping.CLONE_GENEALOGY_COLUMN_NAME:
									cloneGenealogyId = Long
											.parseLong(resultColumn);
									break;
								case DBCloneGenealogyCloneClassMapping.CLONE_CLASS_MAPPING_COLUMN_NAME:
									cloneClassMappingId = Long
											.parseLong(resultColumn);
									break;
								}
							}

							return new InternalDBCloneGenealogyCloneClassMapping(
									id, cloneGenealogyId, cloneClassMappingId);
						});

		final Set<Long> cloneClassMappingIdsToBeRetrieved = new TreeSet<Long>();
		final Map<Long, Set<Long>> cloneClassMappingIdsByGenealogyIds = foreignChildElementIds
				.get(TableName.CLONE_CLASS_MAPPING);
		for (final InternalDBCloneGenealogyCloneClassMapping rawIntermediateResult : rawIntermediateResults) {
			final long genealogyId = rawIntermediateResult
					.getCloneGenealogyId();
			final long mappingId = rawIntermediateResult
					.getCloneClassMappingId();

			cloneClassMappingIdsToBeRetrieved.add(mappingId);
			Set<Long> cloneClassMappingIdsInGenealogy = cloneClassMappingIdsByGenealogyIds
					.get(genealogyId);

			if (cloneClassMappingIdsInGenealogy == null) {
				cloneClassMappingIdsInGenealogy = new TreeSet<Long>();
				cloneClassMappingIdsByGenealogyIds.put(genealogyId,
						cloneClassMappingIdsInGenealogy);
			}

			cloneClassMappingIdsInGenealogy.add(mappingId);
		}

		// perform retrieving clone class mappings
		cloneClassMappingDao.get(cloneClassMappingIdsToBeRetrieved);

		// retrieve versions if deep refreshing is ON
		if (deepRefresh) {
			final Set<Long> versionIdsToBeRetrieved = relativeElementIds
					.get(TableName.VERSION);
			versionDao.get(versionIdsToBeRetrieved);
		}
	}

	@Override
	protected DBCloneGenealogy makeInstance(
			InternalDBCloneGenealogyRepresentation rawResult,
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds)
			throws Exception {
		final DBCloneGenealogy newInstance = new DBCloneGenealogy(
				rawResult.getId(), null, null, null);

		if (autoRefresh) {
			if (deepRefresh) {
				newInstance.setStartVersion(versionDao.get(rawResult
						.getStartVersionId()));
				newInstance.setEndVersion(versionDao.get(rawResult
						.getEndVersionId()));
			} else {
				newInstance.setStartVersion(new DBVersion(rawResult
						.getStartVersionId(), null, null, null, null, null,
						null));
				newInstance
						.setEndVersion(new DBVersion(rawResult
								.getEndVersionId(), null, null, null, null,
								null, null));
			}

			final Map<Long, Set<Long>> cloneClassMappingIdsByGenealogyId = foreignChildElementIds
					.get(TableName.CLONE_CLASS_MAPPING);
			final Set<Long> cloneClassMappingIdsInGenealogy = cloneClassMappingIdsByGenealogyId
					.get(rawResult.getId());

			newInstance.setCloneClassMappings(new ArrayList<>());

			if (cloneClassMappingIdsInGenealogy != null
					&& !cloneClassMappingIdsInGenealogy.isEmpty()) {
				final Collection<DBCloneClassMapping> cloneClassMappings = cloneClassMappingDao
						.get(cloneClassMappingIdsInGenealogy).values();
				newInstance.getCloneClassMappings().addAll(cloneClassMappings);
			}
		}

		return newInstance;
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

	class InternalDBCloneGenealogyRepresentation implements
			InternalDataRepresentation<DBCloneGenealogy> {

		private final Long id;

		private final Long startVersionId;

		private final Long endVersionId;

		public InternalDBCloneGenealogyRepresentation(final Long id,
				final Long startVersionId, final Long endVersionId) {
			this.id = id;
			this.startVersionId = startVersionId;
			this.endVersionId = endVersionId;
		}

		@Override
		public final Long getId() {
			return id;
		}

		public final Long getStartVersionId() {
			return startVersionId;
		}

		public final Long getEndVersionId() {
			return endVersionId;
		}

	}

	class RowMapper implements
			RawRowMapper<InternalDBCloneGenealogyRepresentation> {

		@Override
		public InternalDBCloneGenealogyRepresentation mapRow(
				String[] columnNames, String[] resultColumns)
				throws SQLException {
			Long id = null;
			Long startVersionId = null;
			Long endVersionId = null;

			for (int i = 0; i < columnNames.length; i++) {
				final String columnName = columnNames[i];
				final String resultColumn = resultColumns[i];

				switch (columnName) {
				case DBCloneGenealogy.ID_COLUMN_NAME:
					id = Long.parseLong(resultColumn);
					break;
				case DBCloneGenealogy.START_VERSION_COLUMN_NAME:
					startVersionId = Long.parseLong(resultColumn);
					break;
				case DBCloneGenealogy.END_VERSION_COLUMN_NAME:
					endVersionId = Long.parseLong(resultColumn);
					break;
				}
			}

			return new InternalDBCloneGenealogyRepresentation(id,
					startVersionId, endVersionId);
		}

	}

	class InternalDBCloneGenealogyCloneClassMapping
			implements
			InternalIntermediateDataRepresentation<DBCloneGenealogyCloneClassMapping> {

		private final Long id;

		private final Long cloneGenealogyId;

		private final Long cloneClassMappingId;

		public InternalDBCloneGenealogyCloneClassMapping(final Long id,
				final Long cloneGenealogyId, final Long cloneClassMappingId) {
			this.id = id;
			this.cloneGenealogyId = cloneGenealogyId;
			this.cloneClassMappingId = cloneClassMappingId;
		}

		@Override
		public final Long getId() {
			return id;
		}

		@Override
		public final Long getLeftId() {
			return cloneGenealogyId;
		}

		@Override
		public final Long getRightId() {
			return cloneClassMappingId;
		}

		public final Long getCloneGenealogyId() {
			return cloneGenealogyId;
		}

		public final Long getCloneClassMappingId() {
			return cloneClassMappingId;
		}

	}

}
