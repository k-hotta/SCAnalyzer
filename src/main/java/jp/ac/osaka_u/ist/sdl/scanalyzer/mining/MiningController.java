package jp.ac.osaka_u.ist.sdl.scanalyzer.mining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDataElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.IDBElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.AbstractDataDao;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.DBManager;
import jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve.IRetriever;
import jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve.ParallelRetriever;
import jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve.RetrievedObjectManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is for controlling the mining procedure.
 * 
 * @author k-hotta
 *
 */
public class MiningController<E extends IProgramElement, D extends IDBElement, T extends IDataElement<D>> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(MiningController.class);

	/**
	 * The number of genealogies processed at a time.
	 */
	private final int maximumGenealogiesCount;

	/**
	 * The strategy of mining
	 */
	private final MiningStrategy<D, T> strategy;

	/**
	 * The DAO
	 */
	private final AbstractDataDao<D, ?> dao;

	/**
	 * The object retriever
	 */
	private final IRetriever<E, D, T> retriever;

	/**
	 * The manager for retrieved objects
	 */
	private final RetrievedObjectManager<E> manager;

	public MiningController(final int maximumGenealogiesCount,
			final MiningStrategy<D, T> strategy,
			final AbstractDataDao<D, ?> dao,
			final IRetriever<E, D, T> retriever,
			final RetrievedObjectManager<E> manager) {
		this.maximumGenealogiesCount = maximumGenealogiesCount;
		this.strategy = strategy;
		this.dao = dao;
		this.retriever = retriever;
		this.manager = manager;
	}

	public void performMining() throws Exception {
		final List<Long> ids = dao.getAllIds();
		int count = 0;
		int miningRunCount = 0;

		final Set<Long> waitingIds = new TreeSet<Long>();

		logger.info("the number of elements to be mined: " + ids.size());

		while (count < ids.size()) {
			final Long id = ids.get(count++);
			waitingIds.add(id);
			// persistElementsToBeMined.add(dao.get(id));

			if (count % maximumGenealogiesCount == 0) {
				logger.info("perform mining a part of the set of elements to be mined");
				logger.info(waitingIds.size() + " elements will be mined");
				performMining(waitingIds);
				miningRunCount++;
				logger.info("performed mining for " + miningRunCount
						+ maximumGenealogiesCount + " elements out of "
						+ ids.size() + " elements in total");

				prepareToContinue();
				waitingIds.clear();
			}
		}

		if (!waitingIds.isEmpty()) {
			performMining(waitingIds);
			logger.info("perform mining for all the elements");
		}

		logger.info("writing results ... ");
		strategy.writeResult();
		logger.info("complete writing");
	}

	private void performMining(final Collection<Long> ids) throws Exception {
		logger.info("retrieving " + ids.size() + " elements from database");
		final Map<Long, D> retrieved = retrieveFromDatabase(ids);
		logger.info("complete retrieving elements from database");

		final List<T> toBeMined = new ArrayList<>();
		// int count = 0;
		// for (final long id : ids) {
		// logger.info("[" + (++count) + "/" + ids.size()
		// + "] retrieving element " + id);
		// toBeMined.add(retriever.retrieveElement(retrieved.get(id)));
		// }
		logger.info("retrieving volatile information ...");
		final ParallelRetriever<E, D, T> parallelRetriever = new ParallelRetriever<>(
				retriever);
		toBeMined.addAll(parallelRetriever.retrieveAll(retrieved.values())
				.values());
		logger.info("complete retrieving volatile information");

		logger.info("start mining ...");
		strategy.mine(toBeMined);
		logger.info("complete mining for " + ids.size() + " elements");
	}

	private Map<Long, D> retrieveFromDatabase(final Collection<Long> ids)
			throws Exception {
		return dao.get(ids);
	}

	private void prepareToContinue() {
		this.manager.clear();
		DBManager.getInstance().clearDaos();
	}

}
