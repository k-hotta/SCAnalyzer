package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;

/**
 * This is a retriever for {@link CloneClass} without volatile information.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class PersistCloneClassRetriever<E extends IProgramElement> extends
		PersistObjectRetriever<E, DBCloneClass, CloneClass<E>> {

	public PersistCloneClassRetriever(RetrievedObjectManager<E> manager) {
		super(manager);
	}

	@Override
	protected CloneClass<E> make(DBCloneClass dbElement) {
		return new CloneClass<E>(dbElement);
	}

	@Override
	protected void add(CloneClass<E> element) {
		manager.add(element);
	}

}
