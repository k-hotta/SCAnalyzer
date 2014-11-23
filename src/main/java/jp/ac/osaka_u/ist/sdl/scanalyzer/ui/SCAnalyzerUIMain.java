package jp.ac.osaka_u.ist.sdl.scanalyzer.ui;

import java.awt.EventQueue;
import java.io.File;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.Config;
import jp.ac.osaka_u.ist.sdl.scanalyzer.config.ConfigLoader;
import jp.ac.osaka_u.ist.sdl.scanalyzer.config.DBMS;
import jp.ac.osaka_u.ist.sdl.scanalyzer.config.ElementTypeSensitiveWorkerInitializer;
import jp.ac.osaka_u.ist.sdl.scanalyzer.config.TokenSensitiveWorkerInitializer;
import jp.ac.osaka_u.ist.sdl.scanalyzer.config.WorkerManager;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Token;
import jp.ac.osaka_u.ist.sdl.scanalyzer.genealogy.CloneGenealogyRetriever;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.DBManager;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.DBUrlProvider;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.svn.SVNRepositoryManager;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.helper.FileContentProvideHelper;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.view.CloneGenealogyView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is the main class of UI of SCAnalyzer.
 * 
 * @author k-hotta
 *
 */
public class SCAnalyzerUIMain {

	/**
	 * The logger
	 */
	private static Logger logger = LogManager.getLogger(SCAnalyzerUIMain.class);

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
		final SCAnalyzerUIMain main = new SCAnalyzerUIMain();

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

		private MainHelper(final Config config) {
			this.config = config;
			this.workerManager = null;
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
				if (!dbLocalFile.exists()) {
					throw new IllegalStateException(
							"the database file does not exist");
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

		/**
		 * Run the main procedure.
		 * 
		 * @throws Exception
		 */
		private void runMain() throws Exception {
			// setup the helper for getting file contents
			FileContentProvideHelper.setProvider(workerManager
					.getFileContentProvider());

			final CloneGenealogyRetriever<E> retriever = new CloneGenealogyRetriever<>(
					DBManager.getInstance(),
					workerManager.getFileContentProvider(),
					workerManager.getFileParser());
			final long idToBeRetrieved = config.getGenealogyId();

			final CloneGenealogy<E> genealogy = retriever
					.retrieveCloneGenealogy(idToBeRetrieved);

			if (genealogy == null) {
				throw new IllegalStateException("cannot find genealogy "
						+ idToBeRetrieved);
			}

			showFrame(genealogy);
		}

		/**
		 * Show the frame.
		 * 
		 * @param genealogy
		 *            the genealogy to be shown
		 */
		private void showFrame(final CloneGenealogy<E> genealogy) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						CloneGenealogyView frame = new CloneGenealogyView();
						frame.setCloneGenealogy(genealogy);
						frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
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

}
