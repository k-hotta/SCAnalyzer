package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersionSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.TableName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RawRowMapper;

/**
 * The DAO for {@link DBSourceFile}.
 * 
 * @author k-hotta
 * 
 * @see DBSourceFile
 * @see DBVersionSourceFile
 */
public class SourceFileDao extends
		AbstractDataDao<DBSourceFile, SourceFileDao.InternalDBSourceFile> {

	/**
	 * The logger
	 */
	private static Logger logger = LogManager.getLogger(SourceFileDao.class);

	@SuppressWarnings("unchecked")
	public SourceFileDao() throws SQLException {
		super((Dao<DBSourceFile, Long>) DBManager.getInstance().getNativeDao(
				DBSourceFile.class));
	}

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	protected String getTableName() {
		return TableName.SOURCE_FILE;
	}

	@Override
	protected String getIdColumnName() {
		return DBSourceFile.ID_COLUMN_NAME;
	}

	@Override
	protected DBSourceFile refreshChildren(DBSourceFile element)
			throws Exception {
		// do nothing because Revision doesn't have any foreign field
		return element;
	}

	@Override
	protected Collection<DBSourceFile> refreshChildrenForAll(
			Collection<DBSourceFile> elements) throws Exception {
		// do nothing because Revision doesn't have any foreign field
		return elements;
	}

	/**
	 * Get the elements whose paths are the specified one.
	 * 
	 * @param path
	 *            path as a query
	 * @return a list of elements whose paths are the specified one
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Collection<DBSourceFile> getWithPath(final String path)
			throws Exception {
		return refreshAll(originalDao.queryForEq(DBSourceFile.PATH_COLUMN_NAME,
				path));
	}

	class InternalDBSourceFile implements
			InternalDataRepresentation<DBSourceFile> {

		private final Long id;

		private final String path;

		public InternalDBSourceFile(final Long id, final String path) {
			this.id = id;
			this.path = path;
		}

		@Override
		public final Long getId() {
			return id;
		}

		public final String getPath() {
			return path;
		}

	}

	class RowMapper implements RawRowMapper<InternalDBSourceFile> {

		@Override
		public InternalDBSourceFile mapRow(String[] columnNames,
				String[] resultColumns) throws SQLException {
			Long id = null;
			String path = null;

			for (int i = 0; i < columnNames.length; i++) {
				final String columnName = columnNames[i];
				final String resultColumn = resultColumns[i];

				switch (columnName) {
				case DBSourceFile.ID_COLUMN_NAME:
					id = Long.parseLong(resultColumn);
					break;
				case DBSourceFile.PATH_COLUMN_NAME:
					path = resultColumn;
					break;
				}
			}

			return new InternalDBSourceFile(id, path);
		}

	}

	@Override
	protected RawRowMapper<InternalDBSourceFile> getRowMapper()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void updateRelativeElementIds(InternalDBSourceFile rawResult)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void retrieveRelativeElements(
			Map<String, Set<Long>> relativeElementIds) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected DBSourceFile makeInstance(InternalDBSourceFile rawResult) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<Long, DBSourceFile> queryRaw(String query) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
