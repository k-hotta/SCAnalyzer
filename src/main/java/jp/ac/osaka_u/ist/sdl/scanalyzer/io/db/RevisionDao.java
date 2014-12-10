package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RawRowMapper;

/**
 * The DAO for {@link DBRevision}.
 * 
 * @author k-hotta
 * 
 * @see DBRevision
 */
public class RevisionDao extends
		AbstractDataDao<DBRevision, RevisionDao.InternalDBRevision> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(RevisionDao.class);

	@SuppressWarnings("unchecked")
	public RevisionDao() throws SQLException {
		super((Dao<DBRevision, Long>) DBManager.getInstance().getNativeDao(
				DBRevision.class));
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	protected String getTableName() {
		return TableName.REVISION;
	}

	@Override
	protected String getIdColumnName() {
		return DBRevision.ID_COLUMN_NAME;
	}

	@Override
	protected DBRevision refreshChildren(DBRevision element) throws Exception {
		// do nothing because Revision doesn't have any foreign field
		return element;
	}

	@Override
	protected Collection<DBRevision> refreshChildrenForAll(
			Collection<DBRevision> elements) throws Exception {
		// do nothing because Revision doesn't have any foreign field
		return elements;
	}

	@Override
	protected RawRowMapper<InternalDBRevision> getRowMapper() throws Exception {
		return new RowMapper();
	}

	@Override
	protected void initializeRelativeElementIds(
			Map<String, Set<Long>> relativeElementIds) {
		// do nothing
	}

	@Override
	protected void initializeForeignChildElementIds(
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds) {
		// do nothing
	}

	@Override
	protected void updateRelativeElementIds(
			Map<String, Set<Long>> relativeElementIds,
			InternalDBRevision rawResult) throws Exception {
		// do nothing
	}

	@Override
	protected void retrieveRelativeElements(
			Map<String, Set<Long>> relativeElementIds,
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds)
			throws Exception {
		// do nothing
	}

	@Override
	protected DBRevision makeInstance(InternalDBRevision rawResult,
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds)
			throws Exception {
		return new DBRevision(rawResult.getId(), rawResult.getIdentifier(),
				rawResult.getDate());
	}

	class InternalDBRevision implements InternalDataRepresentation<DBRevision> {

		private final Long id;

		private final String identifier;

		private final Date date;

		public InternalDBRevision(final Long id, final String identifier,
				final Date date) {
			this.id = id;
			this.identifier = identifier;
			this.date = date;
		}

		public final Long getId() {
			return id;
		}

		public final String getIdentifier() {
			return identifier;
		}

		public final Date getDate() {
			return date;
		}

	}

	class RowMapper implements RawRowMapper<InternalDBRevision> {

		@Override
		public InternalDBRevision mapRow(String[] columnNames,
				String[] resultColumns) throws SQLException {
			Long id = null;
			String identifier = null;
			Date date = null;

			for (int i = 0; i < columnNames.length; i++) {
				final String columnName = columnNames[i];
				final String resultColumn = resultColumns[i];

				if (resultColumn == null) {
					continue;
				}

				switch (columnName) {
				case DBRevision.ID_COLUMN_NAME:
					id = Long.parseLong(resultColumn);
					break;
				case DBRevision.IDENTIFIER_COLUMN_NAME:
					identifier = resultColumn;
					break;
				case DBRevision.DATE_COLUMN_NAME:
					final long dateMillSec = Long.parseLong(resultColumn);
					date = new Date(dateMillSec);
					break;
				}
			}

			return new InternalDBRevision(id, identifier, date);
		}

	}
}
