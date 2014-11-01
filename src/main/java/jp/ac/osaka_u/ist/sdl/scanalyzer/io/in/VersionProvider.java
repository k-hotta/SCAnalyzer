package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBElementComparator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange.Type;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFileWithContent;

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
	 * how to provide the contents of source files
	 */
	private IFileContentProvider contentProvider;

	/**
	 * how to parser the contents of source files
	 */
	private SourceFileContentBuilder<?> contentBuilder;

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

	/**
	 * Set the clone detector with the specified one.
	 * 
	 * @param cloneDetector
	 *            the clone detector to be set
	 */
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
	 * Get the content provider.
	 * 
	 * @return the content provider
	 */
	public IFileContentProvider getContentProvider() {
		return contentProvider;
	}

	/**
	 * Set the content provider with the specified one.
	 * 
	 * @param contentProvider
	 *            the content provider to be set
	 */
	public void setContentProvider(final IFileContentProvider contentProvider) {
		if (contentProvider == null) {
			eLogger.fatal("null is specified for contentProvider");
			throw new IllegalArgumentException(
					"contentProvider must not be null");
		}
		this.contentProvider = contentProvider;
		logger.trace("the content provider has been set:"
				+ contentProvider.getClass().getName());
	}

	/**
	 * Get the content builder.
	 * 
	 * @return the content builder
	 */
	public SourceFileContentBuilder<?> getContentBuilder() {
		return contentBuilder;
	}

	/**
	 * Set the content builder with the specified one.
	 * 
	 * @param contentBuilder
	 *            the content builder to be set
	 */
	public void setContentBuilder(
			final SourceFileContentBuilder<?> contentBuilder) {
		if (contentBuilder == null) {
			eLogger.fatal("null is specified for contentBuilder");
			throw new IllegalArgumentException(
					"contentBuilder must not be null");
		}
		this.contentBuilder = contentBuilder;
		logger.trace("the content builder has been set:"
				+ contentBuilder.getClass().getName());
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
	public DBVersion getNextVersion(final DBVersion currentVersion)
			throws Exception {
		// ready?
		if (!ready()) {
			throw new IllegalStateException(
					"the provider has not been ready to work");
		}

		if (currentVersion == null) {
			// this is the first call of this method
			return providePseudoInitialVersion();
		}

		// detect the next revision
		DBRevision nextRevision = detectNextRevision(currentVersion);

		// check if nextRevision is null
		if (nextRevision == null) {
			// all the versions should have been processed
			logger.trace("no next revision has been detected");
			return null;
		}

		// the instance of the next version which is under construction
		final DBVersion nextVersion = new DBVersion(
				IDGenerator.generate(DBVersion.class), null,
				new TreeSet<DBFileChange>(new DBElementComparator()),
				new TreeSet<DBRawCloneClass>(new DBElementComparator()),
				new TreeSet<DBCloneClass>(new DBElementComparator()),
				new TreeSet<DBSourceFile>(new DBElementComparator()),
				new TreeMap<Long, SourceFileWithContent<?>>());

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

		if (contentProvider == null) {
			eLogger.fatal("content provider has not been specified");
			ready = false;
		}

		if (contentBuilder == null) {
			eLogger.fatal("content builder has not been specified");
			ready = false;
		}

		return ready;
	}

	/**
	 * Provide the pseudo initial version
	 * 
	 * @return pseudo initial version
	 */
	private final DBVersion providePseudoInitialVersion() {
		logger.trace("the initial version will be provided");
		return new DBVersion(IDGenerator.generate(DBVersion.class), new DBRevision(
				IDGenerator.generate(DBRevision.class),
				"pseudo-initial-revision", null), new HashSet<DBFileChange>(),
				new HashSet<DBRawCloneClass>(), new HashSet<DBCloneClass>(),
				new HashSet<DBSourceFile>(),
				new TreeMap<Long, SourceFileWithContent<?>>());
	}

	/**
	 * Detect next revision of the given current version
	 * 
	 * @param currentVersion
	 *            current version
	 * @return the next revision if detected, <code>null</code> otherwise
	 */
	private DBRevision detectNextRevision(final DBVersion currentVersion) {
		final DBRevision currentRevision = currentVersion.getRevision();

		// the next revision
		DBRevision nextRevision = null;
		if (currentRevision.getDate() == null) {
			// the current revision is pseudo initial revision
			nextRevision = revisionProvider.getFirstRevision();
		} else {
			nextRevision = revisionProvider.getNextRevision(currentRevision);
		}

		return nextRevision;
	}

	/**
	 * Process all the file changes and create instances of {@link DBFileChange}
	 * and {@link DBSourceFile}
	 * 
	 * @param currentVersion
	 *            the current version
	 * @param nextVersion
	 *            the next version under construction
	 * @param fileChangeEntries
	 *            file change entries between the current version and the next
	 *            version
	 */
	private void processFileChanges(final DBVersion currentVersion,
			final DBVersion nextVersion,
			Collection<FileChangeEntry> fileChangeEntries) {
		/*
		 * Source files under consideration, which will be initialized with the
		 * source files in the current version. This map will be updated through
		 * processing file changes to the next version. This map will have
		 * source files in the NEXT version after all the file changes
		 * processed.
		 */
		final Map<String, DBSourceFile> sourceFilesUnderConsideration = getSourceFilesAsMap(currentVersion
				.getSourceFiles());

		// the contents of each source file in current revision
		final Map<Long, SourceFileWithContent<?>> sourceFileContentsUnderConsideration = new TreeMap<Long, SourceFileWithContent<?>>(
				currentVersion.getSourceFileContents());

		for (FileChangeEntry fileChangeEntry : fileChangeEntries) {
			final String oldPath = fileChangeEntry.getBeforePath();
			final String newPath = fileChangeEntry.getAfterPath();
			final Type type = Type.getTypeByChar(fileChangeEntry.getType());

			if (type == null) {
				eLogger.fatal("invalid character has been specified as a file change type");
				throw new IllegalStateException(fileChangeEntry.getType()
						+ " is not a valid type of file change");
			}

			DBSourceFile oldSourceFile = null;
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

			DBSourceFile newSourceFile = null;
			if (newPath != null) {
				// create new instance of source file
				newSourceFile = new DBSourceFile(
						IDGenerator.generate(DBSourceFile.class), newPath);
				logger.trace("create a new source file "
						+ newSourceFile.toString());

				SourceFileWithContent<?> content = parseFile(nextVersion,
						newSourceFile);

				sourceFilesUnderConsideration.put(newPath, newSourceFile);
				sourceFileContentsUnderConsideration.put(newSourceFile.getId(),
						content);
			}

			if (oldSourceFile == null && newSourceFile == null) {
				// this is an illegal case
				eLogger.fatal("at least one of the two source files in a file change must be non-null");
				throw new IllegalStateException(
						"both of the two source files in a file change are null");
			}

			final DBFileChange fileChange = new DBFileChange(
					IDGenerator.generate(DBFileChange.class), oldSourceFile,
					newSourceFile, type, nextVersion);
			logger.trace("create a new file change " + fileChange.toString());
			nextVersion.getFileChanges().add(fileChange);
		}

		for (final DBSourceFile sourceFile : sourceFilesUnderConsideration
				.values()) {
			logger.trace("add " + sourceFile.getPath() + " into version "
					+ nextVersion.getId());
			nextVersion.getSourceFiles().add(sourceFile);
			nextVersion.getSourceFileContents()
					.put(sourceFile.getId(),
							sourceFileContentsUnderConsideration.get(sourceFile
									.getId()));
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
	private final Map<String, DBSourceFile> getSourceFilesAsMap(
			final Collection<DBSourceFile> sourceFiles) {
		final Map<String, DBSourceFile> result = new HashMap<String, DBSourceFile>();

		for (final DBSourceFile sourceFile : sourceFiles) {
			result.put(sourceFile.getPath(), sourceFile);
		}

		return result;
	}

	/**
	 * Parse the given source file and get the contents in it.
	 * 
	 * @param version
	 *            the version in which the target source file exists
	 * @param sourceFile
	 *            the target source file
	 * @return the contents of the source file
	 */
	private final SourceFileWithContent<?> parseFile(final DBVersion version,
			final DBSourceFile sourceFile) {
		final String contentsStr = contentProvider.getFileContent(version,
				sourceFile);

		if (contentsStr == null) {
			eLogger.fatal("cannot find source code of " + sourceFile.getId()
					+ " in version " + version.getId());
			throw new IllegalStateException("source code of "
					+ sourceFile.getId() + " in version " + version.getId()
					+ " is null");
		}

		return contentBuilder.build(sourceFile, contentsStr);
	}

	/**
	 * Detect clones with the specified clone detector in the next version
	 * 
	 * @param nextVersion
	 *            the next version where clones are to be detected
	 */
	private void detectClones(final DBVersion nextVersion) {
		final Collection<DBRawCloneClass> rawCloneClasses = cloneDetector
				.detectClones(nextVersion);

		final ConcurrentMap<Long, SourceFileWithContent<? extends IProgramElement>> concurrentContents = new ConcurrentHashMap<Long, SourceFileWithContent<? extends IProgramElement>>();
		concurrentContents.putAll(nextVersion.getSourceFileContents());

		ExecutorService pool = Executors.newCachedThreadPool();
		final List<Future<DBCloneClass>> futures = new ArrayList<Future<DBCloneClass>>();

		try {
			for (final DBRawCloneClass rawCloneClass : rawCloneClasses) {
				nextVersion.getRawCloneClasses().add(rawCloneClass);
				rawCloneClass.setVersion(nextVersion);

				final CloneClassBuildTask task = new CloneClassBuildTask(
						concurrentContents, rawCloneClass, nextVersion);
				futures.add(pool.submit(task));
			}

			final List<DBCloneClass> results = new ArrayList<DBCloneClass>();
			for (final Future<DBCloneClass> future : futures) {
				try {
					results.add(future.get());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			for (final DBCloneClass result : results) {
				nextVersion.getCloneClasses().add(result);
				result.setVersion(nextVersion);
			}
		} finally {
			pool.shutdown();
		}
	}

}
