package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;

/**
 * This is a retriever for {@link Version} without volatile information.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class PersistVersionRetriever<E extends IProgramElement> extends
		PersistObjectRetriever<E, DBVersion, Version<E>> {

	public PersistVersionRetriever(RetrievedObjectManager<E> manager) {
		super(manager);
	}

	@Override
	protected Version<E> make(DBVersion dbElement) {
		return new Version<E>(dbElement);
	}

	@Override
	protected void add(Version<E> element) {
		manager.add(element);
	}

}
