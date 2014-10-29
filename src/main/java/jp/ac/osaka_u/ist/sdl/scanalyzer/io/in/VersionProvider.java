package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBElementComparator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange.Type;
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
	 * how to detect clones
	 */
	private ICloneDetector cloneDetector;

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
	 * Get the clone detector
	 * 
	 * @return the clone detector
	 */
	public ICloneDetector getCloneDetector() {
		return cloneDetector;
	}

	public void setCloneDetector(final ICloneDetector cloneDetector) {
		if (cloneDetector == null) {
			eLogger.fatal("null is specified for cloneDetector");
			throw new IllegalArgumentException("cloneDetector must not be null");
		}
		this.cloneDetector = cloneDetector;
		logger.trace("the clone detector has been set: "
				+ cloneDetector.getClass().getName());
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

		// detect the next revision
		Revision nextRevision = detectNextRevision(currentVersion);

		// check if nextRevision is null
		if (nextRevision == null) {
			// all the versions should have been processed
			logger.trace("no next revision has been detected");
			return null;
		}

		// the instance of the next version which is under construction
		final Version nextVersion = new Version(
				IDGenerator.generate(Version.class), null,
				new TreeSet<FileChange>(new DBElementComparator()),
				new TreeSet<RawCloneClass>(new DBElementComparator()),
				new TreeSet<CloneClass>(new DBElementComparator()),
				new TreeSet<SourceFile>(new DBElementComparator()));

		// set the next revision to the next version
		logger.trace("create a new revision " + nextRevision.toString());
		nextVersion.setRevision(nextRevision);

		// detect file changes
		Collection<FileChangeEntry> fileChangeEntries = fileChangeDetector
				.detectFileChangeEntriesToRevision(nextVersion.getRevision());
		if (relocationFinder != null) {
			fileChangeEntries = relocationFinder
					.fildRelocations(fileChangeEntries);
		}

		// instantiate file changes and detect source files
		processFileChanges(currentVersion, nextVersion, fileChangeEntries);

		// detect clones in the NEXT version
		detectClones(nextVersion);

		return nextVersion;
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

		if (cloneDetector == null) {
			eLogger.fatal("clone detector has not been specified");
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
		logger.trace("the initial version will be provided");
		return new Version(IDGenerator.generate(Version.class), new Revision(
				IDGenerator.generate(Revision.class),
				"pseudo-initial-revision", null), new HashSet<FileChange>(),
				new HashSet<RawCloneClass>(), new HashSet<CloneClass>(),
				new HashSet<SourceFile>());
	}

	/**
	 * Detect next revision of the given current version
	 * 
	 * @param currentVersion
	 *            current version
	 * @return the next revision if detected, <code>null</code> otherwise
	 */
	private Revision detectNextRevision(final Version currentVersion) {
		final Revision currentRevision = currentVersion.getRevision();

		// the next revision
		Revision nextRevision = null;
		if (currentRevision.getDate() == null) {
			// the current revision is pseudo initial revision
			nextRevision = revisionProvider.getFirstRevision();
		} else {
			nextRevision = revisionProvider.getNextRevision(currentRevision);
		}

		return nextRevision;
	}

	/**
	 * Process all the file changes and create instances of {@link FileChange}
	 * and {@link SourceFile}
	 * 
	 * @param currentVersion
	 *            the current version
	 * @param nextVersion
	 *            the next version under construction
	 * @param fileChangeEntries
	 *            file change entries between the current version and the next
	 *            version
	 */
	private void processFileChanges(final Version currentVersion,
			final Version nextVersion,
			Collection<FileChangeEntry> fileChangeEntries) {
		/*
		 * Source files under consideration, which will be initialized with the
		 * source files in the current version. This map will be updated through
		 * processing file changes to the next version. This map will have
		 * source files in the NEXT version after all the file changes
		 * processed.
		 */
		final Map<String, SourceFile> sourceFilesUnderConsideration = getSourceFilesAsMap(currentVersion
				.getSourceFiles());

		for (FileChangeEntry fileChangeEntry : fileChangeEntries) {
			final String oldPath = fileChangeEntry.getBeforePath();
			final String newPath = fileChangeEntry.getAfterPath();
			final Type type = Type.getTypeByChar(fileChangeEntry.getType());

			if (type == null) {
				eLogger.fatal("invalid character has been specified as a file change type");
				throw new IllegalStateException(fileChangeEntry.getType()
						+ " is not a valid type of file change");
			}

			SourceFile oldSourceFile = null;
			if (oldPath != null) {
				if (type == Type.ADD) {
					// this is a copy
					oldSourceFile = sourceFilesUnderConsideration.get(oldPath);
				} else {
					// this is a deletion, modification, or relocation
					oldSourceFile = sourceFilesUnderConsideration
							.remove(oldPath);
				}

				if (oldSourceFile == null) {
					eLogger.fatal("source file before changed does not exist in the previous version");
					throw new IllegalStateException(
							"cannot find source file before changed " + oldPath
									+ " in version " + currentVersion.getId());
				}
			}

			SourceFile newSourceFile = null;
			if (newPath != null) {
				// create new instance of source file
				newSourceFile = new SourceFile(
						IDGenerator.generate(SourceFile.class), newPath);
				logger.trace("create a new source file "
						+ newSourceFile.toString());
				sourceFilesUnderConsideration.put(newPath, newSourceFile);
			}

			if (oldSourceFile == null && newSourceFile == null) {
				// this is an illegal case
				eLogger.fatal("at least one of the two source files in a file change must be non-null");
				throw new IllegalStateException(
						"both of the two source files in a file change are null");
			}

			final FileChange fileChange = new FileChange(
					IDGenerator.generate(FileChange.class), oldSourceFile,
					newSourceFile, type, nextVersion);
			logger.trace("create a new file change " + fileChange.toString());
			nextVersion.getFileChanges().add(fileChange);
		}

		for (final SourceFile sourceFile : sourceFilesUnderConsideration
				.values()) {
			logger.trace("add " + sourceFile.getPath() + " into version "
					+ nextVersion.getId());
			nextVersion.getSourceFiles().add(sourceFile);
		}
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

	/**
	 * Detect clones with the specified clone detector in the next version
	 * 
	 * @param nextVersion
	 *            the next version where clones are to be detected
	 */
	private void detectClones(final Version nextVersion) {
		final Collection<RawCloneClass> rawCloneClasses = cloneDetector
				.detectClones(nextVersion);
		for (final RawCloneClass rawCloneClass : rawCloneClasses) {
			nextVersion.getRawCloneClasses().add(rawCloneClass);
			rawCloneClass.setVersion(nextVersion);
		}

	}

}
