package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;

/**
 * The DAO for {@link DBSegment}.
 * 
 * @author k-hotta
 * 
 * @see DBSegment
 */
public class SegmentDao extends AbstractDataDao<DBSegment> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager.getLogger(SegmentDao.class);

	/**
	 * The DAO for source files. <br>
	 * This is for refreshing.
	 */
	private SourceFileDao sourceFileDao;

	/**
	 * The DAO for code fragments. <br>
	 * This is for refreshing.
	 */
	private CodeFragmentDao codeFragmentDao;

	@SuppressWarnings("unchecked")
	public SegmentDao() throws SQLException {
		super((Dao<DBSegment, Long>) DBManager.getInstance().getNativeDao(
				DBSegment.class));
		this.sourceFileDao = null;
		this.codeFragmentDao = null;
	}

	/**
	 * Set the DAO for SourceFile with the specified one
	 * 
	 * @param sourceFileDao
	 *            the DAO to be set
	 */
	void setSourceFileDao(final SourceFileDao sourceFileDao) {
		this.sourceFileDao = sourceFileDao;
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

	@Override
	protected void trace(String msg) {
		logger.trace(msg);
	}

	@Override
	protected DBSegment refreshChildren(DBSegment element) throws Exception {
		if (deepRefresh) {
			sourceFileDao.refresh(element.getSourceFile());
			codeFragmentDao.refresh(element.getCodeFragment());
		}
		return element;
	}

	@Override
	protected Collection<DBSegment> refreshChildrenForAll(
			Collection<DBSegment> elements) throws Exception {
		if (deepRefresh) {
			final Set<DBSourceFile> sourceFilesToBeRefreshed = new HashSet<>();
			final Set<DBCodeFragment> codeFragmentsToBeRefreshed = new HashSet<>();

			for (final DBSegment element : elements) {
				sourceFilesToBeRefreshed.add(element.getSourceFile());
				codeFragmentsToBeRefreshed.add(element.getCodeFragment());
			}

			sourceFileDao.refreshAll(sourceFilesToBeRefreshed);
			codeFragmentDao.refreshAll(codeFragmentsToBeRefreshed);

			for (final DBSegment element : elements) {
				element.setSourceFile(sourceFileDao.get(element.getSourceFile()
						.getId()));
				element.setCodeFragment(codeFragmentDao.get(element
						.getCodeFragment().getId()));
			}
		}

		return elements;
	}

	/**
	 * Get the elements whose source files are the specified one.
	 * 
	 * @param sourceFile
	 *            source file as a key
	 * @return a list of the elements whose source files are the specified one
	 * @throws Exception
	 *             If any error occurred when connecting the database
	 */
	public Collection<DBSegment> getWithSourceFile(final DBSourceFile sourceFile)
			throws Exception {
		return refreshAll(originalDao.queryForEq(
				DBSegment.SOURCE_FILE_COLUMN_NAME, sourceFile));
	}

}
