package jp.ac.osaka_u.ist.sdl.scanalyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.WorkerManager;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.DBManager;
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

		// if next is null, all the versions have been analyzed
		// otherwise, continue to analyze the next version
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

			// store the version data
			logger.info("storing version " + next.getId()
					+ " into database ...");
			storeVersionData(next, previous);
			logger.info("complete storing version " + next.getId() + " (rev. "
					+ next.getRevision().getIdentifier() + ")");

			// finished analyzing the new version
			// the followings are for preparing the next version
			previous = next;

			logger.info("preparing the next version ... ");
			next = versionProvider.getNextVersion(previous);
		}

		logger.info("no other version is found");
		logger.info("all the versions have been analyzed");
	}

	/**
	 * Store all the data of the given version into the database.
	 * 
	 * @param version
	 *            the version to be stored
	 * @param previous
	 *            the previous version of the target one, note no data of this
	 *            previous version will be stored
	 * @throws Exception
	 *             If any error occurred
	 */
	public void storeVersionData(final Version<E> version,
			final Version<E> previous) throws Exception {
		if (version == null) {
			throw new IllegalArgumentException("the given version is null");
		}

		final DBManager dbManager = DBManager.getInstance();

		dbManager.getVersionDao().register(version.getCore());

		dbManager.getRevisionDao().register(version.getRevision().getCore());
		logger.info("revision " + version.getRevision().getIdentifier()
				+ " has been stored");

		// prepare source file ids to be stored
		// this operation is required to avoid duplicate registration of
		// unchanged files
		final Set<Long> sourceFileIds = new HashSet<>();
		sourceFileIds.addAll(version.getSourceFiles().keySet());
		if (previous != null) {
			sourceFileIds.removeAll(previous.getSourceFiles().keySet());
		}

		final List<DBSourceFile> sourceFilesToBeStored = new ArrayList<>();
		for (final long sourceFileId : sourceFileIds) {
			final SourceFile<E> sourceFile = version.getSourceFiles().get(
					sourceFileId);
			sourceFilesToBeStored.add(sourceFile.getCore());
		}
		dbManager.getSourceFileDao().registerAll(sourceFilesToBeStored);
		logger.info(sourceFilesToBeStored.size()
				+ " source files have been stored");

		final List<DBFileChange> fileChangesToBeStored = new ArrayList<>();
		for (final FileChange<E> fileChange : version.getFileChanges().values()) {
			fileChangesToBeStored.add(fileChange.getCore());
		}
		dbManager.getFileChangeDao().registerAll(fileChangesToBeStored);
		logger.info(fileChangesToBeStored.size()
				+ " file changes have been stored");

		final List<DBRawCloneClass> rawCloneClassesToBeStored = new ArrayList<>();
		final List<DBRawClonedFragment> rawClonedFragmentsToBeStored = new ArrayList<>();
		for (final RawCloneClass<E> rawCloneClass : version
				.getRawCloneClasses().values()) {
			for (final RawClonedFragment<E> rawClonedFragment : rawCloneClass
					.getRawClonedFragments().values()) {
				rawClonedFragmentsToBeStored.add(rawClonedFragment.getCore());
			}
			rawCloneClassesToBeStored.add(rawCloneClass.getCore());
		}

		dbManager.getRawCloneClassDao().registerAll(rawCloneClassesToBeStored);
		logger.info(rawCloneClassesToBeStored.size()
				+ " raw clone classes have been stored");

		dbManager.getRawClonedFragmentDao().registerAll(
				rawClonedFragmentsToBeStored);
		logger.info(rawClonedFragmentsToBeStored.size()
				+ " raw cloned fragments have been stored");

		final List<DBCloneClass> cloneClassesToBeStored = new ArrayList<>();
		final List<DBCodeFragment> codeFragmentsToBeStored = new ArrayList<>();
		final List<DBSegment> segmentsToBeStored = new ArrayList<>();
		for (final CloneClass<E> cloneClass : version.getCloneClasses()
				.values()) {
			for (final CodeFragment<E> codeFragment : cloneClass
					.getCodeFragments().values()) {
				for (final Segment<E> segment : codeFragment.getSegments()) {
					segmentsToBeStored.add(segment.getCore());
				}
				codeFragmentsToBeStored.add(codeFragment.getCore());
			}
			for (final CodeFragment<E> ghostFragment : cloneClass
					.getGhostFragments().values()) {
				for (final Segment<E> segment : ghostFragment.getSegments()) {
					segmentsToBeStored.add(segment.getCore());
				}
				codeFragmentsToBeStored.add(ghostFragment.getCore());
			}
			cloneClassesToBeStored.add(cloneClass.getCore());
		}

		dbManager.getCloneClassDao().registerAll(cloneClassesToBeStored);
		logger.info(cloneClassesToBeStored.size()
				+ " clone classes have been stored");

		dbManager.getCodeFragmentDao().registerAll(codeFragmentsToBeStored);
		logger.info(codeFragmentsToBeStored.size()
				+ " code fragments have been stored");

		dbManager.getSegmentDao().registerAll(segmentsToBeStored);
		logger.info(segmentsToBeStored.size() + " stored");
	}

}
