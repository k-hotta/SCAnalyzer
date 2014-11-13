package jp.ac.osaka_u.ist.sdl.scanalyzer;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.WorkerManager;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.VersionProvider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is responsible for the main procedure.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class Analyzer<E extends IProgramElement> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager.getLogger(Analyzer.class);

	/**
	 * The manager of workers
	 */
	private final WorkerManager<E> workerManager;

	public Analyzer(final WorkerManager<E> workerManager) {
		this.workerManager = workerManager;
	}

	/**
	 * This is the responsible method to perform the main procedure of
	 * SCAnalyzer.
	 * 
	 * @throws Exception
	 *             if any error occurred during the analysis
	 */
	public void analyze() throws Exception {
		// the version provider
		final VersionProvider<E> versionProvider = workerManager
				.getVersionProvider();

		// the previously analyzed revision
		Version<E> previous = null;

		// the revision under analysis
		logger.info("preparing the next version ... ");
		Version<E> next = versionProvider.getNextVersion(previous);

		if (next == null) {
			logger.info("no target version is found");
			return;
		}

		while (next != null) {
			logger.info("a new version " + next.getId()
					+ " is provided for revision "
					+ next.getRevision().getIdentifier());
			logger.info("analyzing the new version, version " + next.getId()
					+ " ... ");

			previous = next;

			logger.info("preparing the next version ... ");
			next = versionProvider.getNextVersion(previous);
		}

		logger.info("no other version is found");
		logger.info("all the versions have been analyzed");
	}

}
