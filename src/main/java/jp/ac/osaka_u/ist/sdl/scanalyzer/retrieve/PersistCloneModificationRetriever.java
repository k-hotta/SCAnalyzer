package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneModification;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneModification;

/**
 * This is a retriever for {@link CloneModification} without volatile
 * information.
 * 
 * @author k-hotta
 *
 * @param <E>
 */
public class PersistCloneModificationRetriever<E extends IProgramElement>
		extends
		PersistObjectRetriever<E, DBCloneModification, CloneModification<E>> {

	public PersistCloneModificationRetriever(RetrievedObjectManager<E> manager) {
		super(manager);
	}

	@Override
	protected CloneModification<E> make(DBCloneModification dbElement) {
		return new CloneModification<E>(dbElement);
	}

	@Override
	protected void add(CloneModification<E> element) {
		manager.add(element);
	}
}
