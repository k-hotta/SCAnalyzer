package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;

/**
 * This is a retriever for {@link CloneClassMapping} without volatile
 * information.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class PersistCloneClassMappingRetriever<E extends IProgramElement>
		extends
		PersistObjectRetriever<E, DBCloneClassMapping, CloneClassMapping<E>> {

	public PersistCloneClassMappingRetriever(RetrievedObjectManager<E> manager) {
		super(manager);
	}

	@Override
	protected CloneClassMapping<E> make(DBCloneClassMapping dbElement) {
		return new CloneClassMapping<E>(dbElement);
	}

	@Override
	protected void add(CloneClassMapping<E> element) {
		manager.add(element);
	}

}
