package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClassCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBElementComparator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

/**
 * The DAO for {@link CloneClass}.
 * 
 * @author k-hotta
 * 
 * @see CloneClass
 * @see CloneClassCodeFragment
 */
public class CloneClassDao extends AbstractDataDao<CloneClass> {

	/**
	 * The logger
	 */
	private static Logger logger = LogManager.getLogger(CloneClassDao.class);

	/**
	 * The DAO for CodeFragment. <br>
	 * This is for retrieving corresponding code fragments to each clone class.
	 */
	private final Dao<CodeFragment, Long> nativeCodeFragmentDao;

	/**
	 * The DAO for CloneClassCodeFragment. <br>
	 * This is for retrieving corresponding code fragments to each clone class.
	 */
	private final Dao<CloneClassCodeFragment, Long> nativeCloneClassCodeFragmentDao;

	/**
	 * The DAO for version
	 */
	private VersionDao versionDao;

	/**
	 * The DAO for CodeFragment
	 */
	private CodeFragmentDao codeFragmentDao;

	/**
	 * The query to get corresponding code fragments
	 */
	private PreparedQuery<CodeFragment> codeFragmentsForCloneClassQuery;

	@SuppressWarnings("unchecked")
	public CloneClassDao() throws SQLException {
		super((Dao<CloneClass, Long>) DBManager.getInstance().getNativeDao(
				CloneClass.class));
		this.nativeCodeFragmentDao = this.manager
				.getNativeDao(CodeFragment.class);
		this.nativeCloneClassCodeFragmentDao = this.manager
				.getNativeDao(CloneClassCodeFragment.class);
		codeFragmentDao = null;
		versionDao = null;
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
	public CloneClass refresh(CloneClass element) throws SQLException {
		final Collection<CodeFragment> codeFragments = new TreeSet<CodeFragment>(
				new DBElementComparator());
		for (final CodeFragment codeFragment : element.getCodeFragments()) {
			codeFragments.add(codeFragmentDao.get(codeFragment.getId()));
		}
		element.setCodeFragments(codeFragments);

		element.setVersion(versionDao.get(element.getVersion().getId()));

		return element;
	}

	/**
	 * Get code fragments corresponding to the given clone class as a list.
	 * 
	 * @param cloneClass
	 *            clone class as a query
	 * @return a list of corresponding code fragments
	 * @throws SQLException
	 *             If any error occurred when connecting the database
	 */
	public List<CodeFragment> getCorrespondingCodeFragments(
			final CloneClass cloneClass) throws SQLException {
		if (codeFragmentsForCloneClassQuery == null) {
			codeFragmentsForCloneClassQuery = makeCodeFragmentsForCloneClassQuery();
		}
		codeFragmentsForCloneClassQuery.setArgumentHolderValue(0, cloneClass);

		return codeFragmentDao.query(codeFragmentsForCloneClassQuery);
	}

	private PreparedQuery<CodeFragment> makeCodeFragmentsForCloneClassQuery()
			throws SQLException {
		QueryBuilder<CloneClassCodeFragment, Long> cloneClassCodeFragmentQb = nativeCloneClassCodeFragmentDao
				.queryBuilder();

		cloneClassCodeFragmentQb
				.selectColumns(CloneClassCodeFragment.CODE_FRAGMENT_COLUMN_NAME);
		SelectArg cloneClassSelectArg = new SelectArg();
		cloneClassCodeFragmentQb.where().eq(
				CloneClassCodeFragment.CLONE_CLASS_COLUMN_NAME,
				cloneClassSelectArg);

		QueryBuilder<CodeFragment, Long> codeFragmentQb = nativeCodeFragmentDao
				.queryBuilder();
		codeFragmentQb.where().in(CodeFragment.ID_COLUMN_NAME,
				cloneClassCodeFragmentQb);

		return codeFragmentQb.prepare();
	}

	@Override
	public void register(final CloneClass element) throws SQLException {
		super.register(element); // register CloneClass itself

		for (final CodeFragment codeFragment : element.getCodeFragments()) {
			final CloneClassCodeFragment cccf = new CloneClassCodeFragment(
					IDGenerator.generate(CloneClassCodeFragment.class),
					element, codeFragment);
			nativeCloneClassCodeFragmentDao.create(cccf);
		}
	}

}
