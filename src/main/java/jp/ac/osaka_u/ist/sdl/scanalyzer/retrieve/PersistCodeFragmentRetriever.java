package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;

/**
 * This is a retriever for {@link CodeFragment} without volatile information.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class PersistCodeFragmentRetriever<E extends IProgramElement> extends
		PersistObjectRetriever<E, DBCodeFragment, CodeFragment<E>> {

	public PersistCodeFragmentRetriever(RetrievedObjectManager<E> manager) {
		super(manager);
	}

	@Override
	protected CodeFragment<E> make(DBCodeFragment dbElement) {
		return new CodeFragment<E>(dbElement);
	}

	@Override
	protected void add(CodeFragment<E> element) {
		manager.add(element);
	}

}
