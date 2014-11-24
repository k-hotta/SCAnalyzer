package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDataElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.IDBElement;

/**
 * This is a retriever which does not retrieve any volatile information.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public abstract class PersistObjectRetriever<E extends IProgramElement, D extends IDBElement, T extends IDataElement<D>>
		implements IRetriever<E, D, T> {

	/**
	 * The manager for retrieved objects
	 */
	protected final RetrievedObjectManager<E> manager;

	public PersistObjectRetriever(final RetrievedObjectManager<E> manager) {
		this.manager = manager;
	}

	@Override
	public T retrieveElement(D dbElement) {
		final T result = make(dbElement);
		add(result);
		return result;
	}

	protected abstract T make(final D dbElement);

	protected abstract void add(final T element);
}
