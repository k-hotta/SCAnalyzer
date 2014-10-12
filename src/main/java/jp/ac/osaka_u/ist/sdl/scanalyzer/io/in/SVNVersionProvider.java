package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.Language;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;

/**
 * This class provides versions for repositories managed by Subversion.
 * 
 * @author k-hotta
 * 
 */
public class SVNVersionProvider implements IVersionProvider {

	/**
	 * The logger for errors
	 */
	private static final Logger eLogger = LogManager.getLogger("error");

	/**
	 * The repository manager
	 */
	private final SVNRepositoryManager repositoryManager;

	/**
	 * The constructor with the instance of SVN repository manager
	 * 
	 * @param repositoryManager
	 *            the repository manager
	 */
	public SVNVersionProvider(final SVNRepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
	}

	/**
	 * Process the first version, which returns a pseudo initial version
	 * 
	 * @return a pseudo initial version with a pseudo initial revision and empty
	 *         source files, empty file changes, and empty clone classes
	 */
	private Version processFirstVersion() {
		return new Version(IDGenerator.generate(Version.class), new Revision(
				IDGenerator.generate(Revision.class), "init(pseudo)", null),
				new HashSet<SourceFile>(), new HashSet<FileChange>(),
				new HashSet<RawCloneClass>());
	}

	@Override
	public Version getNextVersion(Version currentVersion)
			throws IllegalStateException {
		if (currentVersion == null) {
			// the first call
			return processFirstVersion();
		}

		try {
			final Revision currentRevision = currentVersion.getRevision();
			final Date currentDate = currentRevision.getDate();
			final long currentRevisionNum = (currentDate == null) ? 1 : Long
					.parseLong(currentRevision.getIdentifier());
			final long nextRevisionNum = currentRevisionNum + 1;

			if (nextRevisionNum > this.repositoryManager.getRepository()
					.getLatestRevision()) {
				eLogger.warn("the next revision exceeds the latest revision");
				return null;
			}

			final Collection<SVNLogEntry> logEntriesToNextRevision = this.repositoryManager
					.getLog(nextRevisionNum);

			final Collection<FileChangeEntry> fileChangeEntries = detectFileChangeEntries(logEntriesToNextRevision);

			return null;
		} catch (SVNException svne) {
			eLogger.fatal("an error occurred when processing "
					+ currentVersion.getRevision().getIdentifier());
			throw new IllegalStateException(svne);
		} catch (NumberFormatException ne) {
			eLogger.fatal(currentVersion.getRevision().getIdentifier()
					+ " is not valid revision number");
			throw new IllegalStateException(ne);
		}
	}

	private Collection<FileChangeEntry> detectFileChangeEntries(
			final Collection<SVNLogEntry> logEntries) {
		/*
		 * This maps each path of changed files to its corresponding file change
		 * entry.
		 */
		final Map<String, FileChangeEntry> fileChangeEntries = new HashMap<String, FileChangeEntry>();

		for (final SVNLogEntry logEntry : logEntries) {
			for (final SVNLogEntryPath path : logEntry.getChangedPaths()
					.values()) {
				try {
					// changedRelativeFiles will be updated here
					processEntryPath(fileChangeEntries, path, logEntry);
				} catch (SVNException e) {
					eLogger.warn(path.getPath() + " in revision "
							+ logEntry.getRevision()
							+ " will be ignored due to an error");
				}
			}
		}

		return fileChangeEntries.values();
	}

	private final void processEntryPath(
			final Map<String, FileChangeEntry> fileChangeEntries,
			final SVNLogEntryPath path, final SVNLogEntry logEntry)
			throws SVNException, IllegalStateException {
		// relative path of changed file/directory
		final String relativePath = path.getPath();

		// type of changed node (unknown, file, directory
		SVNNodeKind kind = path.getKind();

		// type of change (A, D, M, R)
		final char type = path.getType();

		// unknown
		if (kind == SVNNodeKind.UNKNOWN) {
			// try to identify the kind of node
			try {
				kind = this.repositoryManager.getRepository().checkPath(
						relativePath, logEntry.getRevision());
			} catch (SVNException e) {
				eLogger.warn("unable to determine node kind of " + relativePath
						+ " in revision " + logEntry.getRevision());
				throw e;
			}
		}

		if (kind == SVNNodeKind.DIR) {
			/*
			 *  directory
			 */
			processDirectoryChange(fileChangeEntries, path, logEntry,
					relativePath, type);
		} else if (kind == SVNNodeKind.FILE
				|| this.repositoryManager.getLanguage().isRelevantFile(
						relativePath)) {
			/*
			 *  file
			 */
			processFileChange(fileChangeEntries, path, logEntry, relativePath,
					type);
		} else {
			eLogger.warn("unknown kind of change for " + relativePath
					+ " in revision " + logEntry.getRevision());
			throw new IllegalStateException("unknown kind of change");
		}
	}

	private void processFileChange(
			final Map<String, FileChangeEntry> fileChangeEntries,
			final SVNLogEntryPath path, final SVNLogEntry logEntry,
			final String relativePath, final char type) {
		if (!this.repositoryManager.getLanguage().isRelevantFile(
				relativePath)) {
			return; // not relevant file
		}
		if (!relativePath.startsWith(this.repositoryManager
				.getRelativePath())) {
			return; // not in the relative path
		}

		// ignore this if already checked in any directory changes
		if (!fileChangeEntries.containsKey(relativePath)) {
			FileChangeEntry fileChangeEntry = null;
			if (type == 'A') {
				fileChangeEntry = new FileChangeEntry(null, relativePath,
						'A');
			} else if (type == 'D') {
				fileChangeEntry = new FileChangeEntry(relativePath, null,
						'D');
			} else if (type == 'M') {
				fileChangeEntry = new FileChangeEntry(relativePath,
						relativePath, 'M');
			} else if (type == 'R') {
				fileChangeEntry = new FileChangeEntry(path.getCopyPath(),
						relativePath, 'R');
			}

			if (fileChangeEntry != null) {
				fileChangeEntries.put(relativePath, fileChangeEntry);
			} else {
				eLogger.warn("unknown kind of change for " + relativePath
						+ " in revision " + logEntry.getRevision());
				throw new IllegalStateException("unknown kind of change");
			}
		}
	}

	private void processDirectoryChange(
			final Map<String, FileChangeEntry> fileChangeEntries,
			final SVNLogEntryPath path, final SVNLogEntry logEntry,
			final String relativePath, final char type) throws SVNException {
		// modified or relocated
		if (type == 'M' || type == 'R') {
			/*
			 * ignore this because files contained in this directory will be
			 * treated independently
			 */
			return;
		}

		// added
		if (type == 'A') {
			final String copyPath = path.getCopyPath();
			if (copyPath != null
					&& copyPath.startsWith(this.repositoryManager
							.getRelativePath())
					&& !copyPath.equals(relativePath)) {
				/*
				 * copy path is not null, and it is in the relative svn path
				 * this case needs special treatments for detecting file
				 * relocations later
				 */

				// the list of relevant files IN BEFORE REVISION
				final List<String> oldRelevantFiles = this.repositoryManager
						.getListOfRelevantFiles(logEntry.getRevision() - 1,
								copyPath);

				for (final String oldRelevantFile : oldRelevantFiles) {
					final String newRelevantFile = relativePath
							+ oldRelevantFile.substring(copyPath.length());
					final FileChangeEntry fileChangeEntry = new FileChangeEntry(
							oldRelevantFile, newRelevantFile, 'A');

					// overwrite previous file change entry for
					// newRelevantFile if exists
					fileChangeEntries.put(newRelevantFile, fileChangeEntry);
				}
			} else if (copyPath != null
					&& !copyPath.startsWith(this.repositoryManager
							.getRelativePath())
					&& !copyPath.equals(relativePath)) {
				/*
				 * this directory was copied/moved from a location outside the
				 * relative path
				 */
				final List<String> newRelevantFiles = this.repositoryManager
						.getListOfRelevantFiles(logEntry.getRevision(),
								relativePath);
				for (final String newRelevantFile : newRelevantFiles) {
					final FileChangeEntry fileChangeEntry = new FileChangeEntry(
							null, newRelevantFile, 'A');
					// overwrite previous file change entry for
					// newRelevantFile if exists
					fileChangeEntries.put(newRelevantFile, fileChangeEntry);
				}
			} else {
				/*
				 * The directory was added without any copying/moving
				 * operations. We can safely ignore this case because the log
				 * must contain an addition for each file as well, which will be
				 * processed independently.
				 */
			}
		} else if (type == 'D') {
			// directory deleted
			final List<String> oldRelevantFiles = this.repositoryManager
					.getListOfRelevantFiles(logEntry.getRevision() - 1,
							relativePath);
			for (final String oldRelevantFile : oldRelevantFiles) {
				final FileChangeEntry fileChangeEntry = new FileChangeEntry(
						oldRelevantFile, null, 'D');
				fileChangeEntries.put(oldRelevantFile, fileChangeEntry);
			}
		}
	}
}
