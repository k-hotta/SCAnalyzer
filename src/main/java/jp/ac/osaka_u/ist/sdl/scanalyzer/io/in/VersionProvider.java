package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class provides the next version for a given version.
 * 
 * @author k-hotta
 * 
 */
public class VersionProvider {

	/**
	 * the logger
	 */
	private final static Logger logger = LogManager
			.getLogger(VersionProvider.class);

	/**
	 * the logger for errors
	 */
	private final static Logger eLogger = LogManager.getLogger("error");

	/**
	 * how to detect next revisions
	 */
	private IRevisionProvider revisionProvider;

	/**
	 * how to detect file changes
	 */
	private IFileChangeEntryDetector fileChangeDetector;

	/**
	 * how to detect file relocations <br>
	 * Note that this field can be null, if this is the case no additional file
	 * relocations will be detected to those reported by file change detector.
	 */
	private IRelocationFinder relocationFinder;

	/**
	 * Get the revision provider
	 * 
	 * @return the revision provider
	 */
	public IRevisionProvider getRevisionProvider() {
		return revisionProvider;
	}

	/**
	 * Set the revision provider with the specified one
	 * 
	 * @param revisionProvider
	 *            the revision provider to be set
	 */
	public void setRevisionProvider(final IRevisionProvider revisionProvider) {
		if (revisionProvider == null) {
			eLogger.fatal("null is specified for revisionProvider");
			throw new IllegalArgumentException(
					"revisionProvider must not be null");
		}
		this.revisionProvider = revisionProvider;
		logger.trace("the revision provider has been set: "
				+ revisionProvider.getClass().getName());
	}

	/**
	 * Get the file change detector.
	 * 
	 * @return the file change detector
	 */
	public IFileChangeEntryDetector getFileChangeDetector() {
		return fileChangeDetector;
	}

	/**
	 * Set the file change detector with the specified one
	 * 
	 * @param fileChangeDetector
	 *            the file change detector to be set
	 */
	public void setFileChangeDetector(
			final IFileChangeEntryDetector fileChangeDetector) {
		if (fileChangeDetector == null) {
			eLogger.fatal("null is specified for fileChangeDetector");
			throw new IllegalArgumentException(
					"fileChangeDetector must not be null");
		}
		this.fileChangeDetector = fileChangeDetector;
		logger.trace("the file change detector has been set: "
				+ fileChangeDetector.getClass().getName());
	}

	/**
	 * Get the relocation finder.
	 * 
	 * @return the relocation finder
	 */
	public IRelocationFinder getRelocationFinder() {
		return relocationFinder;
	}

	/**
	 * Set the relocation finder with the specified one.
	 * 
	 * @param relocationFinder
	 *            the relocation finder to be set
	 */
	public void setRelocationFinder(final IRelocationFinder relocationFinder) {
		this.relocationFinder = relocationFinder;
		if (relocationFinder == null) {
			logger.trace("the empty relocation finder has been set");
		} else {
			logger.trace("the relocation finder has been set: "
					+ relocationFinder.getClass().getName());
		}
	}

	/**
	 * Get the next version of the given current version.
	 * 
	 * @param currentVersion
	 *            the current version
	 * @return the detected next version if it has been detected,
	 *         <code>null</code> if no next revision has been detected
	 * @throws Exception
	 *             If any error occurred
	 */
	public Version getNextVersion(final Version currentVersion)
			throws Exception {
		if (!ready()) {
			// ready?
			throw new IllegalStateException(
					"the provider has not been ready to work");
		}

		if (currentVersion == null) {
			// this is the first call of this method
			return providePseudoInitialVersion();
		}

		final Revision currentRevision = currentVersion.getRevision();

		// the next revision
		Revision nextRevision = null;
		if (currentRevision.getDate() == null) {
			// the current revision is pseudo initial revision
			nextRevision = revisionProvider.getFirstRevision();
		} else {
			nextRevision = revisionProvider.getNextRevision(currentRevision);
		}

		// check if nextRevision is null
		if (nextRevision == null) {
			logger.trace("no next revision has been detected");
			return null;
		}

		// detecting file changes
		Collection<FileChangeEntry> fileChangeEntries = fileChangeDetector
				.detectFileChangeEntriesToRevision(nextRevision);
		if (relocationFinder != null) {
			fileChangeEntries = relocationFinder
					.fildRelocations(fileChangeEntries);
		}

		// the source files in the current revision
		//final Map<String, SourceFile> sourceFilesInCurrentVersion = getSourceFilesAsMap(sourceFiles);

		for (FileChangeEntry fileChangeEntry : fileChangeEntries) {

		}

		return null; // TODO implement
	}

	/**
	 * Check whether this version provider has been ready to work.
	 * 
	 * @return <code>true</code> if it has been ready which means that all the
	 *         necessary fields have been specified, <code>false</code>
	 *         otherwise
	 */
	private final boolean ready() {
		boolean ready = true;

		if (revisionProvider == null) {
			eLogger.fatal("revision provider has not been specified");
			ready = false;
		}

		if (fileChangeDetector == null) {
			eLogger.fatal("file change detector has not been specified");
			ready = false;
		}

		return ready;
	}

	/**
	 * Provide the pseudo initial version
	 * 
	 * @return pseudo initial version
	 */
	private final Version providePseudoInitialVersion() {
		return new Version(IDGenerator.generate(Version.class), new Revision(
				IDGenerator.generate(Revision.class),
				"pseudo-initial-revision", null), new HashSet<FileChange>(),
				new HashSet<RawCloneClass>());
	}

	/**
	 * Transform the given collection of source files to a map whose key is the
	 * path of the source file and whose value is the source file itself.
	 * 
	 * @param sourceFiles
	 *            a collection of source files
	 * @return a map generated from the given collection
	 */
	private final Map<String, SourceFile> getSourceFilesAsMap(
			final Collection<SourceFile> sourceFiles) {
		final Map<String, SourceFile> result = new HashMap<String, SourceFile>();

		for (final SourceFile sourceFile : sourceFiles) {
			result.put(sourceFile.getPath(), sourceFile);
		}

		return result;
	}

}
