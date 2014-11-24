package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;

/**
 * This is a retriever for {@link SourceFile} without volatile information.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class PersistSourceFileRetriever<E extends IProgramElement> extends
		PersistObjectRetriever<E, DBSourceFile, SourceFile<E>> {

	public PersistSourceFileRetriever(RetrievedObjectManager<E> manager) {
		super(manager);
	}

	@Override
	protected SourceFile<E> make(DBSourceFile dbElement) {
		return new SourceFile<E>(dbElement);
	}

	@Override
	protected void add(SourceFile<E> element) {
		manager.add(element);
	}

}
