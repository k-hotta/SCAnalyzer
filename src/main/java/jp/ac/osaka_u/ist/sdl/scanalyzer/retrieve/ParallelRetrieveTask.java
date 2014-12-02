package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import java.util.concurrent.Callable;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDataElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.IDBElement;

public class ParallelRetrieveTask<E extends IProgramElement, D extends IDBElement, T extends IDataElement<D>>
		implements Callable<T> {

	private final D dbElement;

	private final IRetriever<E, D, T> retriever;

	public ParallelRetrieveTask(final D dbElement,
			final IRetriever<E, D, T> retriever) {
		this.dbElement = dbElement;
		this.retriever = retriever;
	}

	@Override
	public T call() throws Exception {
		return retriever.retrieveElement(dbElement);
	}

}
