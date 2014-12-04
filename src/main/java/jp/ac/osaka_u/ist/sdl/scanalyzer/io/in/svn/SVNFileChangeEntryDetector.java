package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.svn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.FileChangeEntry;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.IFileChangeEntryDetector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * This class provides versions for repositories managed by Subversion.
 * 
 * @author k-hotta
 * 
 */
public class SVNFileChangeEntryDetector implements IFileChangeEntryDetector {

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
	public SVNFileChangeEntryDetector(
			final SVNRepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
	}

	@Override
	public Collection<FileChangeEntry> detectFileChangeEntriesToFirstRevision(
			DBRevision revision) throws Exception {
		final List<String> sourceFiles = this.repositoryManager
				.getListOfRelevantFiles(Long.valueOf(revision.getIdentifier()));

		final List<FileChangeEntry> result = new ArrayList<>();

		for (final String sourceFile : sourceFiles) {
			result.add(new FileChangeEntry(null, sourceFile, 'A'));
		}

		return result;
	}

	@Override
	public Collection<FileChangeEntry> detectFileChangeEntriesToRevision(
			DBRevision revision) throws Exception {
		final Collection<SVNLogEntry> logEntriesToRevision = this.repositoryManager
				.getLog(Long.parseLong(revision.getIdentifier()));

		return detectFileChangeEntries(logEntriesToRevision);
	}

	private Collection<FileChangeEntry> detectFileChangeEntries(
			final Collection<SVNLogEntry> logEntries) {
		/*
		 * This maps each path of changed files to its corresponding file change
		 * entry.
		 */
		final Map<String, FileChangeEntry> fileChangeEntries = new HashMap<String, FileChangeEntry>();

		for (final SVNLogEntry logEntry : logEntries) {
			final List<SVNLogEntryPath> paths = new ArrayList<SVNLogEntryPath>(
					logEntry.getChangedPaths().values());
			// make sure deletions are processed first
			Collections.sort(paths,
					new LogComparator(this.repositoryManager.getRepository(),
							logEntry));

			// the paths of deleted directories
			final Set<String> deletedDirs = new HashSet<String>();

			for (final SVNLogEntryPath path : paths) {
				try {
					// changedRelativeFiles will be updated here
					processEntryPath(fileChangeEntries, path, logEntry,
							deletedDirs);
				} catch (SVNException e) {
					eLogger.warn(path.getPath() + " in revision "
							+ logEntry.getRevision()
							+ " will be ignored due to an error");
				}
			}
		}

		return fileChangeEntries.values();
	}

	/**
	 * Process the given change for a file or a directory. The result of this
	 * method call will be stored in the map given as the first argument.
	 * 
	 * @param fileChangeEntries
	 *            the map from a relative path to its corresponding entry of
	 *            change
	 * @param path
	 *            the path of changed file/directory
	 * @param logEntry
	 *            the log entry having the changed file/directory
	 * @param deletedDirs
	 *            a set having the paths of deleted files
	 * @throws SVNException
	 *             If fail to get information about the path from the SVN
	 *             repository
	 * @throws IllegalStateException
	 *             If the kind of the given path is unknown
	 */
	private final void processEntryPath(
			final Map<String, FileChangeEntry> fileChangeEntries,
			final SVNLogEntryPath path, final SVNLogEntry logEntry,
			final Set<String> deletedDirs) throws SVNException,
			IllegalStateException {
		// relative path of changed file/directory
		final String relativePath = path.getPath();

		// type of changed node (unknown, file, directory
		SVNNodeKind kind = path.getKind();

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
			 * directory
			 */
			processDirectoryChange(fileChangeEntries, path, logEntry,
					deletedDirs);
		} else if (kind == SVNNodeKind.FILE
				|| this.repositoryManager.getLanguage().isRelevantFile(
						relativePath)) {
			/*
			 * file
			 */
			processFileChange(fileChangeEntries, path, logEntry);
		} else {
			eLogger.warn("unknown kind of change for " + relativePath
					+ " in revision " + logEntry.getRevision());
			throw new IllegalStateException("unknown kind of change");
		}
	}

	/**
	 * Process the given directory change. The result of this method call will
	 * be stored in the map given as the first argument.
	 * 
	 * @param fileChangeEntries
	 *            the map from a relative path to its corresponding entry of
	 *            change
	 * @param path
	 *            the path of changed directory
	 * @param logEntry
	 *            the log entry having the changed directory
	 * @param deletedDirs
	 *            a set having paths of deleted directories
	 * @throws SVNException
	 *             If fail to get information about the path from the SVN
	 *             repository
	 */
	private void processDirectoryChange(
			final Map<String, FileChangeEntry> fileChangeEntries,
			final SVNLogEntryPath path, final SVNLogEntry logEntry,
			final Set<String> deletedDirs) throws SVNException {
		// relative path of changed file/directory
		final String relativePath = path.getPath();

		if (!relativePath.startsWith(this.repositoryManager.getRelativePath())) {
			return; // not in the relative path
		}

		// type of change (A, D, M, R)
		final char type = path.getType();

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
			final String repositoryRelativePath = this.repositoryManager
					.getRelativePath();
			if (copyPath != null && copyPath.startsWith(repositoryRelativePath)
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

				// true the directory is moved, false it is copied
				// if the directory is moved, the original directory must be
				// deleted
				final boolean moved = deletedDirs.contains(copyPath);

				for (final String oldRelevantFile : oldRelevantFiles) {
					final String newRelevantFile = relativePath
							+ oldRelevantFile.substring(copyPath.length());

					if (moved) {
						// this is file move
						fileChangeEntries.remove(oldRelevantFile);
						final FileChangeEntry fileChangeEntry = new FileChangeEntry(
								oldRelevantFile, newRelevantFile, 'R');

						// overwrite previous file change entry for
						// newRelevantFile if exists
						fileChangeEntries.put(newRelevantFile, fileChangeEntry);
					} else {
						// this is file copy, hence this will be regarded as a
						// file addition
						final FileChangeEntry fileChangeEntry = new FileChangeEntry(
								oldRelevantFile, newRelevantFile, 'A');

						// overwrite previous file change entry for
						// newRelevantFile if exists
						fileChangeEntries.put(newRelevantFile, fileChangeEntry);
					}
				}
			} else if (copyPath != null
					&& !copyPath.startsWith(repositoryRelativePath)
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
			deletedDirs.add(relativePath);

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

	/**
	 * Process the given file change. The result of this method call will be
	 * stored in the map given as the first argument.
	 * 
	 * @param fileChangeEntries
	 *            the map from a relative path to its corresponding entry of
	 *            change
	 * @param path
	 *            the path of changed file
	 * @param logEntry
	 *            the log entry having the changed file
	 * @throws SVNException
	 *             If fail to get information about the path from the SVN
	 *             repository
	 */
	private void processFileChange(
			final Map<String, FileChangeEntry> fileChangeEntries,
			final SVNLogEntryPath path, final SVNLogEntry logEntry) {
		// relative path of changed file/directory
		final String relativePath = path.getPath();

		// type of change (A, D, M, R)
		final char type = path.getType();

		if (!this.repositoryManager.getLanguage().isRelevantFile(relativePath)) {
			return; // not relevant file
		}
		if (!relativePath.startsWith(this.repositoryManager.getRelativePath())) {
			return; // not in the relative path
		}

		// make sure this method doesn't overwrite the result detected in any
		// directory change
		if (!fileChangeEntries.containsKey(relativePath)) {
			FileChangeEntry fileChangeEntry = null;
			if (type == 'A') {
				final String copyPath = path.getCopyPath();
				if (copyPath != null) {
					// this is file move/copy
					final FileChangeEntry copyPathEntry = fileChangeEntries
							.get(copyPath);
					if (copyPathEntry != null && copyPathEntry.getType() == 'D') {
						fileChangeEntries.remove(copyPath);
						fileChangeEntry = new FileChangeEntry(copyPath,
								relativePath, 'R');
					} else {
						fileChangeEntry = new FileChangeEntry(copyPath,
								relativePath, 'A');
					}
				} else {
					// this is just a new file addition
					fileChangeEntry = new FileChangeEntry(null, relativePath,
							'A');
				}
			} else if (type == 'D') {
				fileChangeEntry = new FileChangeEntry(relativePath, null, 'D');
			} else if (type == 'M') {
				fileChangeEntry = new FileChangeEntry(relativePath,
						relativePath, 'M');
			} else if (type == 'R') {
				if (path.getCopyPath() == null) {
					fileChangeEntry = new FileChangeEntry(relativePath,
							relativePath, 'R');
				} else {
					fileChangeEntry = new FileChangeEntry(path.getCopyPath(),
							relativePath, 'R');
				}
			}

			if (fileChangeEntry != null) {
				fileChangeEntries.put(relativePath, fileChangeEntry);
			} else {
				// in case type is unknown
				eLogger.warn("unknown kind of change for " + relativePath
						+ " in revision " + logEntry.getRevision());
				throw new IllegalStateException("unknown kind of change");
			}
		}
	}

	/**
	 * This class compares SVNLogEntryPath.
	 * 
	 * @author k-hotta
	 * 
	 */
	private class LogComparator implements Comparator<SVNLogEntryPath> {

		private final SVNRepository repo;
		private final SVNLogEntry logEntry;

		public LogComparator(SVNRepository repository, SVNLogEntry logEntry) {
			this.repo = repository;
			this.logEntry = logEntry;
		}

		@Override
		public int compare(SVNLogEntryPath left, SVNLogEntryPath right) {

			if (left.getType() == 'D' && right.getType() != 'D') {
				return -1;
			} else if (left.getType() != 'D' && right.getType() == 'D') {
				return 1;
			} else if (left.getType() == 'D' && right.getType() == 'D') {
				SVNNodeKind leftKind = left.getKind();
				SVNNodeKind rightKind = right.getKind();
				if (leftKind == SVNNodeKind.UNKNOWN) {
					try {
						leftKind = this.repo.checkPath(left.getPath(),
								this.logEntry.getRevision());
					} catch (SVNException e) {
						System.err.println("\nWARNING: " + e.getMessage());
					}
				}
				if (rightKind == SVNNodeKind.UNKNOWN) {
					try {
						rightKind = this.repo.checkPath(left.getPath(),
								this.logEntry.getRevision());
					} catch (SVNException e) {
						System.err.println("\nWARNING: " + e.getMessage());
					}
				}
				if (leftKind == SVNNodeKind.DIR && rightKind == SVNNodeKind.DIR) {
					return left.getPath().compareTo(right.getPath());
				}
				return leftKind == SVNNodeKind.DIR ? -1 : 1;
			} else if (left.getType() == 'A') {
				return -1;
			} else if (right.getType() == 'A') {
				return 1;
			}

			return 0;
		}
	}

}
