package jp.ac.osaka_u.ist.sdl.scanalyzer.mining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDataElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.IDBElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.AbstractDataDao;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.DBManager;
import jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve.IRetriever;
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
	private final AbstractDataDao<D> dao;

	/**
	 * The object retriever
	 */
	private final IRetriever<E, D, T> retriever;

	/**
	 * The manager for retrieved objects
	 */
	private final RetrievedObjectManager<E> manager;

	public MiningController(final int maximumGenealogiesCount,
			final MiningStrategy<D, T> stragety, final AbstractDataDao<D> dao,
			final IRetriever<E, D, T> retriever,
			final RetrievedObjectManager<E> manager) {
		this.maximumGenealogiesCount = maximumGenealogiesCount;
		this.strategy = stragety;
		this.dao = dao;
		this.retriever = retriever;
		this.manager = manager;
	}

	public void performMining() throws Exception {
		final List<Long> ids = dao.getAllIds();
		int count = 0;
		int miningRunCount = 0;

		final List<D> persistElementsToBeMined = new ArrayList<>();

		logger.info("the number of elements to be mined: " + ids.size());

		while (count <= ids.size()) {
			final Long id = ids.get(count++);
			persistElementsToBeMined.add(dao.get(id));

			if (count % maximumGenealogiesCount == 0) {
				performMining(persistElementsToBeMined);
				miningRunCount++;
				logger.info("performed mining for " + miningRunCount
						+ maximumGenealogiesCount + " elements out of "
						+ ids.size() + " elements");

				prepareToContinue();
				persistElementsToBeMined.clear();
			}
		}

		if (!persistElementsToBeMined.isEmpty()) {
			performMining(persistElementsToBeMined);
			logger.info("perform mining for all the elements");
		}

		logger.info("writing results ... ");
		strategy.writeResult();
		logger.info("complete writing");
	}

	private void performMining(final Collection<D> elements) throws Exception {
		final List<T> toBeMined = new ArrayList<>();
		for (final D element : elements) {
			toBeMined.add(retriever.retrieveElement(element));
		}

		strategy.mine(toBeMined);
	}

	private void prepareToContinue() {
		this.manager.clear();
		DBManager.getInstance().clearDaos();
	}

}
