package jp.ac.osaka_u.ist.sdl.scanalyzer.mining;

import java.io.File;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.Config;
import jp.ac.osaka_u.ist.sdl.scanalyzer.config.ConfigLoader;
import jp.ac.osaka_u.ist.sdl.scanalyzer.config.DBMS;
import jp.ac.osaka_u.ist.sdl.scanalyzer.config.ElementTypeSensitiveWorkerInitializer;
import jp.ac.osaka_u.ist.sdl.scanalyzer.config.TokenSensitiveWorkerInitializer;
import jp.ac.osaka_u.ist.sdl.scanalyzer.config.WorkerManager;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDataElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Token;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.IDBElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.AbstractDataDao;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.DBManager;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.DBUrlProvider;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.svn.SVNRepositoryManager;
import jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve.IRetriever;
import jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve.RetrieveMode;
import jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve.RetrievedObjectManager;
import jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve.RetrieverManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is the main class for mining the result of SCAnalyzer.
 * 
 * @author k-hotta
 *
 */
public class SCAnalyzerMiningMain {

	/**
	 * The logger
	 */
	private static Logger logger = LogManager
			.getLogger(SCAnalyzerMiningMain.class);

	/**
	 * The logger for errors
	 */
	private static Logger eLogger = LogManager.getLogger("error");

	/**
	 * This is the main method of SCAnalyzer.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * create a new instance of this class and then delegate all the
		 * necessary processing to the instance
		 */
		final SCAnalyzerMiningMain main = new SCAnalyzerMiningMain();

		/*
		 * Run the main procedure. The return value indicates whether the
		 * operations have successfully finished or not.
		 */
		boolean success = main.run(args);

		if (success) {
			logger.info("operations have successfully finished");
		} else {
			eLogger.fatal("operaions failed");
		}
	}

	/**
	 * Run the main procedure. First this method loads configurations, and then
	 * runs the main procedure by delegating all the further procedure to the
	 * responsible private class.
	 * 
	 * @param args
	 * @return
	 */
	public boolean run(final String[] args) {
		logger.info("loading configurations ... ");
		final ConfigLoader configLoader = new ConfigLoader();
		Config config = null;
		try {
			config = configLoader.load(args);
		} catch (Exception e) {
			eLogger.fatal("fail to load configurations", e);
			return false;
		}

		logger.info("start setting up");
		// this is the responsible class for setting up and tearing down
		// which is element type sensitive
		final MainHelper<?> helper = setUpMainHelper(config);

		if (helper == null) {
			eLogger.fatal("fail to run the main procedure");
			return false;
		}

		try {
			/*
			 * set up
			 */
			helper.setUp();
			logger.info("complete setting up");

			/*
			 * run the main procedure
			 */
			logger.info("start the main procedure");
			helper.runMain();
			logger.info("complete the main procedure");

		} catch (Exception e) {

			/*
			 * fail to set up or run the main
			 */
			eLogger.fatal("fail to run the main procedure", e);
			return false;

		} finally {

			// the tear down process is surrounded by finally block
			// since it is necessary to tear down not only in success cases but
			// also in failure cases

			try {
				/*
				 * tear down
				 */
				logger.info("start tearing down");
				helper.tearDown();
				logger.info("complete tearing down");

			} catch (Exception e) {

				/*
				 * fail to tear down
				 */
				eLogger.fatal("fail to tear down", e);
				return false;

			}

		}

		return true;
	}

	/**
	 * Set up the main runner that corresponds to the type of program element of
	 * interest.
	 * 
	 * @param config
	 *            the configuration values
	 * @return an instance of MainRunner, <code>null</code> if failed any
	 *         operation
	 */
	private MainHelper<?> setUpMainHelper(final Config config) {
		switch (config.getElementType()) {
		case TOKEN:
			return new MainHelper<Token>(config);
		}

		return null;
	}

	/**
	 * This class is responsible for setting up and tearing down SCAnalyzer.
	 * 
	 * @author k-hotta
	 *
	 * @param <E>
	 *            the type of program element
	 */
	private class MainHelper<E extends IProgramElement> {

		/**
		 * The configurations
		 */
		private final Config config;

		/**
		 * The manager for workers
		 */
		private WorkerManager<E> workerManager;

		/**
		 * The manager for retrievers
		 */
		private RetrieverManager<E> retrieverManager;

		/**
		 * The helper for strategy
		 */
		private StrategyHelper<E, ?, ?> strategyHelper;

		/**
		 * The strategy
		 */
		private MiningStrategy<?, ?> strategy;

		private MainHelper(final Config config) {
			this.config = config;
			this.workerManager = null;
			this.retrieverManager = null;
			this.strategyHelper = null;
			this.strategy = null;
		}

		/**
		 * Setting up
		 * 
		 * @throws Exception
		 */
		private void setUp() throws Exception {
			setUpDatabase();
			setUpRepository();
			this.workerManager = setUpWorkers();
			this.strategy = setUpStrategy();
			this.retrieverManager = setUpRetrievers();
			this.strategyHelper = setUpStrategyHelper();
		}

		/**
		 * Set up the database connection
		 * 
		 * @throws Exception
		 */
		private void setUpDatabase() throws Exception {
			logger.info("setting up database connection ... ");
			if (config.getDbms() == DBMS.SQLITE) {
				final File dbLocalFile = new File(config.getDbPath());
				if (dbLocalFile.exists()) {
					if (config.isOverwriteDb()) {
						logger.info(dbLocalFile.getAbsolutePath()
								+ " already exists, hence all the data in it will be disposed");
					} else {
						throw new IllegalStateException(
								"overwriting database is prohibited, but "
										+ dbLocalFile.getAbsolutePath()
										+ " already exists");
					}
				}
			}

			final String dbUrl = DBUrlProvider.getUrl(config.getDbms(),
					config.getDbPath());
			logger.info("the URL is " + dbUrl);

			DBManager.setup(dbUrl);
			logger.info("complete creating the database connection");
		}

		/**
		 * Set up repository connection
		 * 
		 * @throws Exception
		 */
		private void setUpRepository() throws Exception {
			logger.info("setting up repository connection ...");
			switch (config.getVcs()) {
			case SVN:
				SVNRepositoryManager.setup(config.getRepository(),
						config.getRelativePath(), config.getLanguage());
			}
			logger.info("complete creating the repository connection");
		}

		/**
		 * Setting up workers
		 * 
		 * @return
		 * @throws Exception
		 */
		@SuppressWarnings("unchecked")
		private WorkerManager<E> setUpWorkers() throws Exception {
			logger.info("setting up workers ... ");

			ElementTypeSensitiveWorkerInitializer<E> sensitiveWorkerInitializer = null;
			switch (config.getElementType()) {
			case TOKEN:
				sensitiveWorkerInitializer = (ElementTypeSensitiveWorkerInitializer<E>) new TokenSensitiveWorkerInitializer();
				break;
			}

			if (sensitiveWorkerInitializer == null) {
				throw new IllegalStateException(
						"cannot initialize type sensitive workers");
			}

			final WorkerManager<E> result = new WorkerManager<>();
			result.setup(config, sensitiveWorkerInitializer);

			logger.info("complete initializing all the workers");
			logger.info("+ revision provider: "
					+ result.getRevisionProvider().getClass().getSimpleName());
			logger.info("+ file change entry detector: "
					+ result.getFileChangeEntryDetector().getClass()
							.getSimpleName());
			if (result.getRelocationFinder() == null) {
				logger.info("+ additional relocation finder: nothing");
			} else {
				logger.info("+ additional relocation finder: "
						+ result.getRelocationFinder().getClass()
								.getSimpleName());
			}
			logger.info("+ clone detector: "
					+ result.getCloneDetector().getClass().getSimpleName());
			logger.info("+ file content provider: "
					+ result.getFileContentProvider().getClass()
							.getSimpleName());
			logger.info("+ source file parser: "
					+ result.getFileParser().getClass().getSimpleName());
			logger.info("+ element equalizer: "
					+ result.getEqualizer().getClass().getSimpleName());
			logger.info("+ element mapper: "
					+ result.getElementMapper().getClass().getSimpleName());
			logger.info("+ clone mapper: "
					+ result.getCloneMapper().getClass().getSimpleName());

			return result;
		}

		private MiningStrategy<?, ?> setUpStrategy() {
			switch (config.getMiningStrategy()) {
			case GENEALOGY_PERSIST_PERIOD:
				new CloneGenealogyPersistPeriodFindStrategy<>(
						config.getOutputFilePath());
			case GENEALOGY_SIMILARITY_PERIOD:
				return new CloneGenealogySimilarityStrategy<>();
			}

			throw new IllegalStateException("cannot find strategy");
		}

		/**
		 * Set up the retrievers
		 * 
		 * @return
		 * @throws Exception
		 */
		private RetrieverManager<E> setUpRetrievers() throws Exception {
			if (strategy.requiresVolatileObjects()) {
				return new RetrieverManager<E>(RetrieveMode.VOLATILE,
						workerManager);
			} else {
				return new RetrieverManager<E>(RetrieveMode.PERSIST,
						workerManager);
			}
		}

		/**
		 * Set up the helper for strategy
		 * 
		 * @return
		 * @throws Exception
		 */
		@SuppressWarnings("unchecked")
		private StrategyHelper<E, ?, ?> setUpStrategyHelper() throws Exception {
			final RetrievedObjectManager<E> manager = new RetrievedObjectManager<E>();

			switch (config.getMiningStrategy()) {
			case GENEALOGY_PERSIST_PERIOD:
				return new StrategyHelper<E, DBCloneGenealogy, CloneGenealogy<E>>(
						manager,
						retrieverManager.getGenealogyRetriever(),
						DBManager.getInstance().getCloneGenealogyDao(),
						(MiningStrategy<DBCloneGenealogy, CloneGenealogy<E>>) strategy,
						config.getMaximumRetrieveCount());
			case GENEALOGY_SIMILARITY_PERIOD:
				return new StrategyHelper<E, DBCloneGenealogy, CloneGenealogy<E>>(
						manager,
						retrieverManager.getGenealogyRetriever(),
						DBManager.getInstance().getCloneGenealogyDao(),
						(MiningStrategy<DBCloneGenealogy, CloneGenealogy<E>>) strategy,
						config.getMaximumRetrieveCount());
			}

			throw new IllegalStateException(
					"the strategy of mining has not been correctly specified");
		}

		/**
		 * Run the main procedure.
		 * 
		 * @throws Exception
		 */
		private void runMain() throws Exception {
			strategyHelper.run();
		}

		/**
		 * Tearing down
		 * 
		 * @throws Exception
		 */
		private void tearDown() throws Exception {
			DBManager.closeConnection();
		}

	}

	private class StrategyHelper<E extends IProgramElement, D extends IDBElement, T extends IDataElement<D>> {

		private final RetrievedObjectManager<E> manager;

		private final IRetriever<E, D, T> retriever;

		private final AbstractDataDao<D, ?> dao;

		private final MiningStrategy<D, T> strategy;

		private final int maxRetrieved;

		public StrategyHelper(final RetrievedObjectManager<E> manager,
				final IRetriever<E, D, T> retriever,
				final AbstractDataDao<D, ?> dao,
				final MiningStrategy<D, T> strategy, final int maxRetrieved) {
			this.manager = manager;
			this.retriever = retriever;
			this.dao = dao;
			this.strategy = strategy;
			this.maxRetrieved = maxRetrieved;
		}

		private void run() throws Exception {
			logger.info("start mining with strategy: "
					+ strategy.getClass().getSimpleName());

			final MiningController<E, D, T> controller = new MiningController<>(
					maxRetrieved, strategy, dao, retriever, manager);
			controller.performMining();
		}

	}

}
