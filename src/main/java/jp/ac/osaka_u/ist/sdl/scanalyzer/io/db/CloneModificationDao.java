package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneModification;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RawRowMapper;

/**
 * The DAO for {@link DBCloneModification}.
 * 
 * @author k-hotta
 *
 */
public class CloneModificationDao
		extends
		AbstractDataDao<DBCloneModification, CloneModificationDao.InternalDBCloneModification> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(CloneModificationDao.class);

	@SuppressWarnings("unchecked")
	public CloneModificationDao(Dao<DBCloneModification, Long> originalDao)
			throws SQLException {
		super((Dao<DBCloneModification, Long>) DBManager.getInstance()
				.getNativeDao(DBCloneModification.class));
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	protected String getTableName() {
		return TableName.CLONE_MODIFICATION;
	}

	@Override
	protected String getIdColumnName() {
		return DBCloneModification.ID_COLUMN_NAME;
	}

	@Override
	protected RawRowMapper<InternalDBCloneModification> getRowMapper()
			throws Exception {
		return new RowMapper();
	}

	@Override
	protected DBCloneModification refreshChildren(DBCloneModification element)
			throws Exception {
		// do nothing because there is no foreign field
		return element;
	}

	@Override
	protected Collection<DBCloneModification> refreshChildrenForAll(
			Collection<DBCloneModification> elements) throws Exception {
		// do nothing because there is no foreign field
		return elements;
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
			InternalDBCloneModification rawResult) throws Exception {
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
	protected DBCloneModification makeInstance(
			InternalDBCloneModification rawResult,
			Map<String, Map<Long, Set<Long>>> foreignChildElementIds)
			throws Exception {
		final DBCloneModification.Type type = DBCloneModification.Type
				.valueOf(rawResult.getType());

		if (type == null) {
			throw new IllegalStateException("cannot find type "
					+ rawResult.getType());
		}

		return new DBCloneModification(rawResult.getId(),
				rawResult.getOldStartPosition(),
				rawResult.getNewStartPosition(), rawResult.getLength(), type,
				rawResult.getContentHash());
	}

	class InternalDBCloneModification implements
			InternalDataRepresentation<DBCloneModification> {

		private final Long id;

		private final Integer oldStartPosition;

		private final Integer newStartPosition;

		private final Integer length;

		private final String type;

		private final Integer contentHash;

		public InternalDBCloneModification(final Long id,
				final Integer oldStartPosition, final Integer newStartPosition,
				final Integer length, final String type,
				final Integer contentHash) {
			this.id = id;
			this.oldStartPosition = oldStartPosition;
			this.newStartPosition = newStartPosition;
			this.length = length;
			this.type = type;
			this.contentHash = contentHash;
		}

		@Override
		public final Long getId() {
			return id;
		}

		public final Integer getOldStartPosition() {
			return oldStartPosition;
		}

		public final Integer getNewStartPosition() {
			return newStartPosition;
		}

		public final Integer getLength() {
			return length;
		}

		public final String getType() {
			return type;
		}

		public final Integer getContentHash() {
			return contentHash;
		}

	}

	class RowMapper implements RawRowMapper<InternalDBCloneModification> {

		@Override
		public InternalDBCloneModification mapRow(String[] columnNames,
				String[] resultColumns) throws SQLException {
			Long id = null;
			Integer oldStartPosition = null;
			Integer newStartPosition = null;
			Integer length = null;
			String type = null;
			Integer contentHash = null;

			for (int i = 0; i < columnNames.length; i++) {
				final String columnName = columnNames[i];
				final String resultColumn = resultColumns[i];

				switch (columnName) {
				case DBCloneModification.ID_COLUMN_NAME:
					id = Long.parseLong(resultColumn);
					break;
				case DBCloneModification.OLD_START_POSITION_COLUMN_NAME:
					oldStartPosition = Integer.parseInt(resultColumn);
					break;
				case DBCloneModification.NEW_START_POSITION_COLUMN_NAME:
					newStartPosition = Integer.parseInt(resultColumn);
					break;
				case DBCloneModification.LENGTH_COLUMN_NAME:
					length = Integer.parseInt(resultColumn);
					break;
				case DBCloneModification.TYPE_COLUMN_NAME:
					type = resultColumn;
					break;
				case DBCloneModification.CONTENT_HASH_COLUMN_NAME:
					contentHash = Integer.parseInt(resultColumn);
					break;
				}
			}

			return new InternalDBCloneModification(id, oldStartPosition,
					newStartPosition, length, type, contentHash);
		}

	}

}
