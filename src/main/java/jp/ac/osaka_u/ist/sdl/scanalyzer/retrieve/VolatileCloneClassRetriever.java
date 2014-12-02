package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is a retriever for {@link CloneClass} with the volatile information.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program elements
 */
public class VolatileCloneClassRetriever<E extends IProgramElement> implements
		IRetriever<E, DBCloneClass, CloneClass<E>> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(VolatileCloneClassRetriever.class);

	/**
	 * The manager for retrieved objects
	 */
	private final RetrievedObjectManager<E> manager;

	/**
	 * The retriever for code fragments
	 */
	private VolatileCodeFragmentRetriever<E> codeFragmentRetriever;

	/**
	 * The retriever for versions
	 */
	private VolatileVersionRetriever<E> versionRetriever;

	public VolatileCloneClassRetriever(final RetrievedObjectManager<E> manager) {
		this.manager = manager;
		this.codeFragmentRetriever = null;
		this.versionRetriever = null;
	}

	/**
	 * Set the code fragment retriever
	 * 
	 * @param codeFragmentRetriever
	 *            the retriever to be set
	 */
	public void setCodeFragmentRetriever(
			final VolatileCodeFragmentRetriever<E> codeFragmentRetriever) {
		this.codeFragmentRetriever = codeFragmentRetriever;
	}

	/**
	 * Set the version retriever
	 * 
	 * @param versionRetriever
	 *            the retriever to be set
	 */
	public void setVersionRetriever(
			final VolatileVersionRetriever<E> versionRetriever) {
		this.versionRetriever = versionRetriever;
	}

	@Override
	public CloneClass<E> retrieveElement(DBCloneClass dbElement) {
		logger.debug("start retrieving " + dbElement.getId());

		final CloneClass<E> cloneClass = new CloneClass<E>(dbElement);

		// set cloned & ghost code fragments
		for (final DBCodeFragment dbCodeFragment : dbElement.getCodeFragments()) {
			CodeFragment<E> codeFragment = manager
					.getCodeFragment(dbCodeFragment.getId());

			if (codeFragment == null) {
				codeFragment = codeFragmentRetriever
						.retrieveElement(dbCodeFragment);
			}

			cloneClass.addCodeFragment(codeFragment);
			codeFragment.setCloneClass(cloneClass);
		}

		// set version
		Version<E> version = manager.getVersion(dbElement.getVersion().getId());

		if (version == null) {
			version = versionRetriever.retrieveElement(dbElement.getVersion());
		}

		cloneClass.setVersion(version);

		manager.add(cloneClass);

		return cloneClass;
	}

}
