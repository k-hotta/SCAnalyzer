package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragmentMapping;

/**
 * This is a retriever for {@link CodeFragmentMapping} without volatile
 * information.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class PersistCodeFragmentMappingRetriever<E extends IProgramElement>
		extends
		PersistObjectRetriever<E, DBCodeFragmentMapping, CodeFragmentMapping<E>> {

	public PersistCodeFragmentMappingRetriever(RetrievedObjectManager<E> manager) {
		super(manager);
	}

	@Override
	protected CodeFragmentMapping<E> make(DBCodeFragmentMapping dbElement) {
		return new CodeFragmentMapping<E>(dbElement);
	}

	@Override
	protected void add(CodeFragmentMapping<E> element) {
		manager.add(element);
	}

}
