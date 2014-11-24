package jp.ac.osaka_u.ist.sdl.scanalyzer.mining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDataElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.IDBElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.AbstractDataDao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is for controlling the mining procedure.
 * 
 * @author k-hotta
 *
 */
public class MiningController<D extends IDBElement, T extends IDataElement<D>> {

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

	public MiningController(final int maximumGenealogiesCount,
			final MiningStrategy<D, T> stragety, final AbstractDataDao<D> dao) {
		this.maximumGenealogiesCount = maximumGenealogiesCount;
		this.strategy = stragety;
		this.dao = dao;
	}

	public void performMining() throws Exception {
		final List<Long> ids = dao.getAllIds();
		int count = 0;
		final List<D> persistElementsToBeMined = new ArrayList<>();

		while (count <= ids.size()) {
			final Long id = ids.get(count++);
			persistElementsToBeMined.add(dao.get(id));

			if (count % maximumGenealogiesCount == 0) {
				performMining(persistElementsToBeMined);
				prepareToContinue();
				persistElementsToBeMined.clear();
			}
		}
		
		if (!persistElementsToBeMined.isEmpty()) {
			performMining(persistElementsToBeMined);
		}
	}

	private void performMining(final Collection<D> elements) {
		
	}

	private void prepareToContinue() {

	}

}
