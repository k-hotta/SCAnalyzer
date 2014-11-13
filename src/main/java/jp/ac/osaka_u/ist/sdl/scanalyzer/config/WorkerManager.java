package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.ICloneDetector;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.IFileChangeEntryDetector;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.IFileContentProvider;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.IRelocationFinder;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.IRevisionProvider;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.ISourceFileParser;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.ScorpioCloneResultReader;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.svn.SVNFileChangeEntryDetector;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.svn.SVNFileContentProvider;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.svn.SVNRepositoryManager;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.svn.SVNRevisionProvider;
import jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.ICloneClassMapper;
import jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.IProgramElementMapper;
import jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.iclones.IClonesCloneClassMapper;
import difflib.myers.Equalizer;

/**
 * This class manages <b>worker</b>s to which tasks will be assigned. The worker
 * will vary because of the configuration values.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of element
 * 
 * @see IProgramElement
 * @see IRevisionProvider
 * @see IFileChangeEntryDetector
 * @see IRelocationFinder
 * @see ICloneDetector
 * @see IFileContentProvider
 * @see ISourceFileParser
 * @see IProgramElementMapper
 * @see ICloneClassMapper
 */
public class WorkerManager<E extends IProgramElement> {

	/**
	 * The revision provider
	 */
	private IRevisionProvider revisionProvider;

	/**
	 * The file change entry detector
	 */
	private IFileChangeEntryDetector fileChangeEntryDetector;

	/**
	 * The additional relocation finder
	 */
	private IRelocationFinder relocationFinder;

	/**
	 * The clone detector
	 */
	private ICloneDetector<E> cloneDetector;

	/**
	 * The file content provider
	 */
	private IFileContentProvider<E> fileContentProvider;

	/**
	 * The source file parser
	 */
	private ISourceFileParser<E> fileParser;

	/**
	 * The equalizer for elements
	 */
	private Equalizer<E> equalizer;

	/**
	 * The element mapper
	 */
	private IProgramElementMapper<E> elementMapper;

	/**
	 * The clone mapper
	 */
	private ICloneClassMapper<E> cloneMapper;

	public final IRevisionProvider getRevisionProvider() {
		return revisionProvider;
	}

	public final IFileChangeEntryDetector getFileChangeEntryDetector() {
		return fileChangeEntryDetector;
	}

	public final IRelocationFinder getRelocationFinder() {
		return relocationFinder;
	}

	public final ICloneDetector<E> getCloneDetector() {
		return cloneDetector;
	}

	public final IFileContentProvider<E> getFileContentProvider() {
		return fileContentProvider;
	}

	public final ISourceFileParser<E> getFileParser() {
		return fileParser;
	}

	public final Equalizer<E> getEqualizer() {
		return equalizer;
	}

	public final IProgramElementMapper<E> getElementMapper() {
		return elementMapper;
	}

	public final ICloneClassMapper<E> getCloneMapper() {
		return cloneMapper;
	}

	/**
	 * Set up all the workers with the specified configuration
	 * 
	 * @param config
	 *            the configuration
	 */
	public void setup(final Config config,
			ElementTypeSensitiveWorkerInitializer<E> sensitiveInitializer) {
		revisionProvider = setupRevisionProvider(config.getVcs(),
				config.getStartRevisionIdentifier(),
				config.getEndRevisionIdentifier());
		fileChangeEntryDetector = setupFileChangeEntryDetector(config.getVcs());
		relocationFinder = setupRelocationFinder();
		cloneDetector = setupCloneDetector(config.getCloneDetector(),
				config.getCloneResultDirectory(),
				config.getCloneResultFileFormat());
		fileContentProvider = setupFileContentProvider(config.getVcs());
		fileParser = sensitiveInitializer.setupSourceFileParser(config
				.getLanguage());
		equalizer = sensitiveInitializer.setupEqualizer(config
				.getElementEqualizer());
		elementMapper = sensitiveInitializer.setupElementMapper(config
				.getElementMappingAlgorithm());
		cloneMapper = setupCloneClassMapper(config.getCloneMappingAlgorithm());
	}

	/**
	 * Set up the revision provider
	 * 
	 * @param vcs
	 * @param startRevisionIdentifier
	 * @param endRevisionIdentifier
	 * @return
	 */
	private IRevisionProvider setupRevisionProvider(
			final VersionControlSystem vcs,
			final String startRevisionIdentifier,
			final String endRevisionIdentifier) {
		switch (vcs) {
		case SVN:
			try {
				final long startRevisionNum = (startRevisionIdentifier == null) ? null
						: Long.parseLong(startRevisionIdentifier);
				final long endRevisionNum = (endRevisionIdentifier == null) ? null
						: Long.parseLong(endRevisionIdentifier);

				return new SVNRevisionProvider(
						SVNRepositoryManager.getInstance(), startRevisionNum,
						endRevisionNum);
			} catch (Exception e) {
				throw new IllegalStateException(
						"cannot initialize revision provider", e);
			}
		}

		throw new IllegalStateException("cannot initialize revision provider");
	}

	/**
	 * Set up file change entry detector
	 * 
	 * @param vcs
	 * @return
	 */
	private IFileChangeEntryDetector setupFileChangeEntryDetector(
			final VersionControlSystem vcs) {
		switch (vcs) {
		case SVN:
			return new SVNFileChangeEntryDetector(
					SVNRepositoryManager.getInstance());
		}

		throw new IllegalStateException(
				"cannot initialize file change entry detector");
	}

	/**
	 * Set up relocation finder
	 * 
	 * @return
	 */
	private IRelocationFinder setupRelocationFinder() {
		// currently, no additional relocation finder is provided
		return null;
	}

	/**
	 * Set up clone detector
	 * 
	 * @param cdt
	 * @param cloneResultDir
	 * @param fileNameFormat
	 * @return
	 */
	private ICloneDetector<E> setupCloneDetector(final CloneDetector cdt,
			final String cloneResultDir, final String fileNameFormat) {
		switch (cdt) {
		case SCORPIO:
			if (cloneResultDir == null) {
				throw new IllegalStateException(
						"the directory of clone result files is null");
			}
			if (fileNameFormat == null) {
				throw new IllegalStateException("the file name format is null");
			}
			return new ScorpioCloneResultReader<>(cloneResultDir,
					fileNameFormat);
		}
		throw new IllegalStateException("cannot initialize cloine detector");
	}

	/**
	 * Set up file content provider
	 * 
	 * @param vcs
	 * @return
	 */
	private IFileContentProvider<E> setupFileContentProvider(
			final VersionControlSystem vcs) {
		switch (vcs) {
		case SVN:
			return new SVNFileContentProvider<E>(
					SVNRepositoryManager.getInstance());
		}

		throw new IllegalStateException(
				"cannot initialize file content provider");
	}

	/**
	 * Set up clone class mapper
	 * 
	 * @param algorithm
	 * @return
	 */
	private ICloneClassMapper<E> setupCloneClassMapper(
			final CloneClassMappingAlgorithm algorithm) {
		if (elementMapper == null) {
			throw new IllegalStateException(
					"element mapper has not been initialized");
		}

		switch (algorithm) {
		case ICLONES:
			return new IClonesCloneClassMapper<E>(elementMapper);
		}

		throw new IllegalStateException("cannot initialize clone class mapper");
	}

}
