package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneGenealogy;

/**
 * This is a retriever for {@link CloneGenealogy} without volatile information.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class PersistGenealogyRetriever<E extends IProgramElement> extends
		PersistObjectRetriever<E, DBCloneGenealogy, CloneGenealogy<E>> {

	public PersistGenealogyRetriever(RetrievedObjectManager<E> manager) {
		super(manager);
	}

	@Override
	protected CloneGenealogy<E> make(DBCloneGenealogy dbElement) {
		return new CloneGenealogy<E>(dbElement);
	}

	@Override
	protected void add(CloneGenealogy<E> element) {
		manager.add(element);
	}

}
