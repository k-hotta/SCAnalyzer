package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDataElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.IDBElement;

/**
 * This class is for retrieving multiple elements in parallel. This class reuses
 * a single retriever in all the threads. Hence, it is possible to try to
 * retrieve same objects even though they have already been retrieved by some
 * other threads. This might affect performance, but this doesn't affect the
 * validity of results.
 * 
 * @author k-hotta
 *
 * @param <E>
 * @param <D>
 * @param <T>
 */
public class ParallelRetriever<E extends IProgramElement, D extends IDBElement, T extends IDataElement<D>> {

	// private final Supplier<IRetriever<E, D, T>> retrieverSupplier;

	private final IRetriever<E, D, T> retriever;

	public ParallelRetriever(final IRetriever<E, D, T> retriever) {
		// final Supplier<IRetriever<E, D, T>> retrieverSupplier) {
		// this.retrieverSupplier = retrieverSupplier;
		this.retriever = retriever;
	}

	public final Map<Long, T> retrieveAll(final Collection<D> dbElements) {
		final ExecutorService pool = Executors.newCachedThreadPool();
		final Map<Long, T> result = new TreeMap<>();

		try {
			final List<Future<T>> futures = new ArrayList<>();
			for (final D dbElement : dbElements) {
				final ParallelRetrieveTask<E, D, T> task = new ParallelRetrieveTask<>(
						dbElement, retriever);
				futures.add(pool.submit(task));
			}

			for (final Future<T> future : futures) {
				try {
					final T retrievedObject = future.get();
					result.put(retrievedObject.getId(), retrievedObject);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} finally {
			pool.shutdown();
		}

		return result;
	}
}
