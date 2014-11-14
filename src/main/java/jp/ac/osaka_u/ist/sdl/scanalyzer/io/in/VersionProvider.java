package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBElementComparator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange.Type;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class provides the next version for a given version.
 * 
 * @author k-hotta
 * 
 * @param <E>
 *            the type of program element
 */
public class VersionProvider<E extends IProgramElement> {

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
	private ICloneDetector<E> cloneDetector;

	/**
	 * how to provide the contents of source files
	 */
	private IFileContentProvider<E> contentProvider;

	/**
	 * The parser of source files
	 */
	private ISourceFileParser<E> parser;

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
	public ICloneDetector<E> getCloneDetector() {
		return cloneDetector;
	}

	/**
	 * Set the clone detector with the specified one.
	 * 
	 * @param cloneDetector
	 *            the clone detector to be set
	 */
	public void setCloneDetector(final ICloneDetector<E> cloneDetector) {
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
	public IFileContentProvider<E> getContentProvider() {
		return contentProvider;
	}

	/**
	 * Set the content provider with the specified one.
	 * 
	 * @param contentProvider
	 *            the content provider to be set
	 */
	public void setContentProvider(final IFileContentProvider<E> contentProvider) {
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
	 * Get the file parser.
	 * 
	 * @return the file parser
	 */
	public ISourceFileParser<E> getFileParser() {
		return parser;
	}

	/**
	 * Set the file parser with the specified one.
	 * 
	 * @param parser
	 *            the parser to be set
	 */
	public void setFileParser(final ISourceFileParser<E> parser) {
		if (parser == null) {
			eLogger.fatal("null is specified for contentBuilder");
			throw new IllegalArgumentException(
					"contentBuilder must not be null");
		}
		this.parser = parser;
		logger.trace("the content builder has been set:"
				+ parser.getClass().getName());
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
	public Version<E> getNextVersion(final Version<E> currentVersion)
			throws Exception {
		// ready?
		if (!ready()) {
			throw new IllegalStateException(
					"the provider has not been ready to work");
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
		final DBVersion nextDBVersion = new DBVersion(
				IDGenerator.generate(DBVersion.class), null,
				new TreeSet<DBFileChange>(new DBElementComparator()),
				new TreeSet<DBRawCloneClass>(new DBElementComparator()),
				new TreeSet<DBCloneClass>(new DBElementComparator()),
				new TreeSet<DBSourceFile>(new DBElementComparator()));
		final Version<E> nextVersion = new Version<E>(nextDBVersion);

		// set the next revision to the next version
		logger.trace("create a new revision " + nextRevision.toString());
		nextDBVersion.setRevision(nextRevision.getCore());
		nextVersion.setRevision(nextRevision);

		// detect file changes
		Collection<FileChangeEntry> fileChangeEntries = fileChangeDetector
				.detectFileChangeEntriesToRevision(nextDBVersion.getRevision());
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

		if (parser == null) {
			eLogger.fatal("file parser has not been specified");
			ready = false;
		}

		return ready;
	}

	/**
	 * Detect next revision of the given current version
	 * 
	 * @param currentVersion
	 *            current version
	 * @return the next revision if detected, <code>null</code> otherwise
	 */
	private Revision detectNextRevision(final Version<E> currentVersion) {
		// the next revision
		Revision nextRevision = null;

		if (currentVersion == null) {
			// this is the first revision
			nextRevision = revisionProvider.getFirstRevision();
		} else {
			final Revision currentRevision = currentVersion.getRevision();
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
	private void processFileChanges(final Version<E> currentVersion,
			final Version<E> nextVersion,
			Collection<FileChangeEntry> fileChangeEntries) {
		/*
		 * Source files under consideration, which will be initialized with the
		 * source files in the current version. This map will be updated through
		 * processing file changes to the next version. This map will have
		 * source files in the NEXT version after all the file changes
		 * processed.
		 */
		final Map<String, SourceFile<E>> sourceFilesUnderConsideration = (currentVersion == null) ? new TreeMap<>()
				: getSourceFilesAsMap(currentVersion.getSourceFiles().values());

		for (FileChangeEntry fileChangeEntry : fileChangeEntries) {
			final String oldPath = fileChangeEntry.getBeforePath();
			final String newPath = fileChangeEntry.getAfterPath();
			final Type type = Type.getTypeByChar(fileChangeEntry.getType());

			if (type == null) {
				eLogger.fatal("invalid character has been specified as a file change type");
				throw new IllegalStateException(fileChangeEntry.getType()
						+ " is not a valid type of file change");
			}

			SourceFile<E> oldSourceFile = null;
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

			SourceFile<E> newSourceFile = null;
			if (newPath != null) {
				// create new instance of source file
				final DBSourceFile newDBSourceFile = new DBSourceFile(
						IDGenerator.generate(DBSourceFile.class), newPath);
				newSourceFile = new SourceFile<E>(newDBSourceFile);

				logger.trace("create a new source file "
						+ newSourceFile.toString());

				parseFile(nextVersion, newSourceFile);

				sourceFilesUnderConsideration.put(newPath, newSourceFile);
			}

			if (oldSourceFile == null && newSourceFile == null) {
				// this is an illegal case
				eLogger.fatal("at least one of the two source files in a file change must be non-null");
				throw new IllegalStateException(
						"both of the two source files in a file change are null");
			}

			final DBSourceFile oldDBSourceFile = (oldSourceFile == null) ? null
					: oldSourceFile.getCore();
			final DBSourceFile newDBSourceFile = (newSourceFile == null) ? null
					: newSourceFile.getCore();

			final DBFileChange dbFileChange = new DBFileChange(
					IDGenerator.generate(DBFileChange.class), oldDBSourceFile,
					newDBSourceFile, type, nextVersion.getCore());
			final FileChange<E> fileChange = new FileChange<E>(dbFileChange);
			fileChange.setVersion(nextVersion);
			fileChange.setOldSourceFile(oldSourceFile);
			fileChange.setNewSourceFile(newSourceFile);

			logger.trace("create a new file change " + fileChange.toString());
			nextVersion.getCore().getFileChanges().add(fileChange.getCore());
			nextVersion.addFileChange(fileChange);
		}

		for (final SourceFile<E> sourceFile : sourceFilesUnderConsideration
				.values()) {
			logger.trace("add " + sourceFile.getPath() + " into version "
					+ nextVersion.getId());
			nextVersion.getCore().getSourceFiles().add(sourceFile.getCore());
			nextVersion.addSourceFile(sourceFile);
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
	private final Map<String, SourceFile<E>> getSourceFilesAsMap(
			final Collection<SourceFile<E>> sourceFiles) {
		final Map<String, SourceFile<E>> result = new HashMap<String, SourceFile<E>>();

		for (final SourceFile<E> sourceFile : sourceFiles) {
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
	private final void parseFile(final Version<E> version,
			final SourceFile<E> sourceFile) {
		final String contentsStr = contentProvider.getFileContent(version,
				sourceFile);

		if (contentsStr == null) {
			eLogger.fatal("cannot find source code of " + sourceFile.getId()
					+ " in version " + version.getId());
			throw new IllegalStateException("source code of "
					+ sourceFile.getId() + " in version " + version.getId()
					+ " is null");
		}

		final SortedMap<Integer, E> contents = parser.parse(sourceFile,
				contentsStr);
		sourceFile.setContents(contents.values());
	}

	/**
	 * Detect clones with the specified clone detector in the next version
	 * 
	 * @param nextVersion
	 *            the next version where clones are to be detected
	 */
	private void detectClones(final Version<E> nextVersion) {
		final Collection<RawCloneClass<E>> rawCloneClasses = cloneDetector
				.detectClones(nextVersion);

		for (final RawCloneClass<E> rawCloneClass : rawCloneClasses) {
			nextVersion.getCore().getRawCloneClasses()
					.add(rawCloneClass.getCore());
			rawCloneClass.getCore().setVersion(nextVersion.getCore());
			rawCloneClass.setVersion(nextVersion);
			nextVersion.addRawCloneClass(rawCloneClass);
		}

		ExecutorService pool = Executors.newCachedThreadPool();
		final List<Future<CloneClass<E>>> futures = new ArrayList<Future<CloneClass<E>>>();

		try {
			for (final RawCloneClass<E> rawCloneClass : rawCloneClasses) {
				final CloneClassBuildTask<E> task = new CloneClassBuildTask<E>(
						rawCloneClass, nextVersion);
				futures.add(pool.submit(task));
			}

			final List<CloneClass<E>> results = new ArrayList<CloneClass<E>>();
			for (final Future<CloneClass<E>> future : futures) {
				try {
					results.add(future.get());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			for (final CloneClass<E> result : results) {
				nextVersion.getCore().getCloneClasses().add(result.getCore());
				nextVersion.addCloneClass(result);
			}
		} finally {
			pool.shutdown();
		}
	}

}
