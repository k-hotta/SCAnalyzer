package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;

/**
 * The DAO for {@link Version}.
 * 
 * @author k-hotta
 * 
 * @see Version
 * @see VersionSourceFile
 */
public class VersionDao extends AbstractDataDao<Version> {

	private SourceFileDao sourceFileDataDao;

	public VersionDao() {
		super(null);
		// TODO Auto-generated constructor stub
		sourceFileDataDao = null;
	}

	/**
	 * Set the data DAO for SourceFile with the specified one.
	 * 
	 * @param sourceFileDataDao
	 *            the data DAO to be set
	 */
	void setSourceFileDataDao(final SourceFileDao sourceFileDataDao) {
		this.sourceFileDataDao = sourceFileDataDao;
	}

	@Override
	protected void trace(String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public Version refresh(Version element) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
