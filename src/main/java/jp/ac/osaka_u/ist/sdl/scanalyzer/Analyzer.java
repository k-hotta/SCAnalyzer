package jp.ac.osaka_u.ist.sdl.scanalyzer;

import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.WorkerManager;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.VersionProvider;
import jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.ICloneClassMapper;
import jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.IProgramElementMapper;

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

	/**
	 * The version provider
	 */
	private final VersionProvider<E> versionProvider;

	/**
	 * The element mapper
	 */
	private final IProgramElementMapper<E> elementMapper;

	/**
	 * The clone mapper
	 */
	private final ICloneClassMapper<E> cloneMapper;

	public Analyzer(final WorkerManager<E> workerManager) {
		this.workerManager = workerManager;
		this.versionProvider = workerManager.getVersionProvider();
		this.elementMapper = workerManager.getElementMapper();
		this.cloneMapper = workerManager.getCloneMapper();
	}

	/**
	 * This is the responsible method to perform the main procedure of
	 * SCAnalyzer.
	 * 
	 * @throws Exception
	 *             if any error occurred during the analysis
	 */
	public void analyze() throws Exception {
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

			// if previous == null, the next version is the first one
			// there is no need to detect mapping in this case
			if (previous != null) {
				// mapping elements between two versions
				logger.info("mapping elements between two versions ... ");
				elementMapper.prepare(previous, next);
				logger.info("complete mapping elements");

				// mapping clones between two versions
				logger.info("mapping clone classes between two versions ...");
				final Collection<CloneClassMapping<E>> cloneClassMappings = cloneMapper
						.detectMapping(previous, next);
				logger.info("complete mapping clone classes: "
						+ cloneClassMappings.size()
						+ " mappings have been found");
			}

			// finished analyzing the new version
			// the followings are for preparing the next version
			previous = next;

			logger.info("preparing the next version ... ");
			next = versionProvider.getNextVersion(previous);
		}

		logger.info("no other version is found");
		logger.info("all the versions have been analyzed");
	}

}
