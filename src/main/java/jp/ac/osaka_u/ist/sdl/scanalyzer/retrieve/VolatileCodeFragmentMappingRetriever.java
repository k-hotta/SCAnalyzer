package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragmentMapping;

/**
 * This is a retriever for {@link CodeFragmentMapping} with the volatile
 * information.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class VolatileCodeFragmentMappingRetriever<E extends IProgramElement>
		implements IRetriever<E, DBCodeFragmentMapping, CodeFragmentMapping<E>> {

	/**
	 * The manager for retrieved objects
	 */
	private final RetrievedObjectManager<E> manager;

	/**
	 * The retriever for code fragments
	 */
	private VolatileCodeFragmentRetriever<E> codeFragmentRetriever;

	public VolatileCodeFragmentMappingRetriever(
			final RetrievedObjectManager<E> manager) {
		this.manager = manager;
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

	@Override
	public CodeFragmentMapping<E> retrieveElement(
			DBCodeFragmentMapping dbElement) {
		final CodeFragmentMapping<E> codeFragmentMapping = new CodeFragmentMapping<E>(
				dbElement);

		if (dbElement.getOldCodeFragment() != null) {
			CodeFragment<E> oldCodeFragment = manager.getCodeFragment(dbElement
					.getOldCodeFragment().getId());
			if (oldCodeFragment == null) {
				oldCodeFragment = codeFragmentRetriever
						.retrieveElement(dbElement.getOldCodeFragment());
			}
			codeFragmentMapping.setOldCodeFragment(oldCodeFragment);
		}

		if (dbElement.getNewCodeFragment() != null) {
			CodeFragment<E> newCodeFragment = manager.getCodeFragment(dbElement
					.getNewCodeFragment().getId());
			if (newCodeFragment == null) {
				newCodeFragment = codeFragmentRetriever
						.retrieveElement(dbElement.getNewCodeFragment());
			}
			codeFragmentMapping.setNewCodeFragment(newCodeFragment);
		}

		manager.add(codeFragmentMapping);

		return codeFragmentMapping;
	}
}
