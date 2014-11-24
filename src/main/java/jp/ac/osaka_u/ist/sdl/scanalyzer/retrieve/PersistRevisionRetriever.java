package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;

/**
 * This is a retriever for {@link Revision} without volatile information.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class PersistRevisionRetriever<E extends IProgramElement> extends
		PersistObjectRetriever<E, DBRevision, Revision> {

	public PersistRevisionRetriever(RetrievedObjectManager<E> manager) {
		super(manager);
	}

	@Override
	protected Revision make(DBRevision dbElement) {
		return new Revision(dbElement);
	}

	@Override
	protected void add(Revision element) {
		manager.add(element);
	}

}
