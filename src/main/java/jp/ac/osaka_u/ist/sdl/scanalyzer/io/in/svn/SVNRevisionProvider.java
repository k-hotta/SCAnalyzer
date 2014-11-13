package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.svn;

import java.util.Collection;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.IRevisionProvider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;

/**
 * This class is an implementation of {@link IRevisionProvider} for SVN
 * repositories.
 * 
 * @author k-hotta
 * 
 * @see IRevisionProvider
 * @see jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.VersionProvider VersionProvider
 */
public class SVNRevisionProvider implements IRevisionProvider {

	/**
	 * the logger
	 */
	private static final Logger logger = LogManager
			.getLogger(SVNRevisionProvider.class);

	/**
	 * the logger for errors
	 */
	private static final Logger eLogger = LogManager.getLogger("error");

	/**
	 * the repository manager
	 */
	private final SVNRepositoryManager repositoryManager;

	/**
	 * the number of revision where the period of interest starts
	 */
	private final long startRevisionNum;

	/**
	 * the number of revision where the period of interest ends
	 */
	private final long endRevisionNum;

	/**
	 * The constructor.
	 * 
	 * @param repositoryManager
	 *            the repository manager
	 * @param startRevisionNum
	 *            the revision number from which the analysis starts
	 * @param endRevisionNum
	 *            the revision number at which the analysis ends
	 * @throws SVNException
	 *             If failed to connect the repository
	 * @throws IllegalArgumentException
	 *             If the given endRevisionNum is smaller than the given
	 *             startRevisionNum
	 */
	public SVNRevisionProvider(final SVNRepositoryManager repositoryManager,
			final Long startRevisionNum, final Long endRevisionNum)
			throws SVNException {
		if (startRevisionNum != null && endRevisionNum != null
				&& endRevisionNum < startRevisionNum) {
			eLogger.fatal("endRevisionNum is smaller than startRevisionNum");
			throw new IllegalArgumentException(
					"end revision must be greater or equal to start revision");
		}

		this.repositoryManager = repositoryManager;

		if (startRevisionNum == null || startRevisionNum < 1) {
			this.startRevisionNum = 1;
		} else {
			this.startRevisionNum = startRevisionNum;
		}

		final long latestRevisionNum = repositoryManager.getRepository()
				.getLatestRevision();

		if (latestRevisionNum < this.startRevisionNum) {
			eLogger.fatal("startRevisionNum is greater than the latest revision");
			throw new IllegalArgumentException(
					"start revision must be smaller than or equals to the latest revision "
							+ latestRevisionNum);
		}

		if (endRevisionNum == null || endRevisionNum > latestRevisionNum) {
			this.endRevisionNum = latestRevisionNum;
		} else {
			this.endRevisionNum = endRevisionNum;
		}

		logger.trace("the SVN revision provider has been initialized");
		logger.trace("start revision: " + this.startRevisionNum);
		logger.trace("end revision: " + this.endRevisionNum);
	}

	@Override
	public Revision getFirstRevision() {
		return getNextRevision(this.startRevisionNum, this.endRevisionNum);
	}

	@Override
	public Revision getNextRevision(Revision currentRevision) {
		final long currentRevisionNum = Long.parseLong(currentRevision
				.getIdentifier());

		// increment currentRevisionNum to avoid processing the same revision
		return getNextRevision(currentRevisionNum + 1, endRevisionNum);
	}

	/**
	 * Get the next earliest revision in the range from the given start revision
	 * and to the given end revision.
	 * 
	 * @param startRevisionNum
	 *            the revision where the target range starts
	 * @param endRevisionNum
	 *            the revision where the target range ends
	 * @return the next earliest revision in the specified range if found,
	 *         <code>null</code> if not found
	 */
	private Revision getNextRevision(final long startRevisionNum,
			final long endRevisionNum) {
		for (long rev = startRevisionNum; rev <= endRevisionNum; rev++) {
			try {
				final Collection<SVNLogEntry> logEntries = this.repositoryManager
						.getLog(rev);

				for (final SVNLogEntry logEntry : logEntries) {
					for (SVNLogEntryPath path : logEntry.getChangedPaths()
							.values()) {
						if (isTarget(rev, path, logEntry)) {
							final DBRevision dbRevision = new DBRevision(
									IDGenerator.generate(DBRevision.class),
									String.valueOf(rev), logEntry.getDate());
							return new Revision(dbRevision);
						}
					}
				}
			} catch (SVNException e) {
				eLogger.warn("fail to connect repository when processing "
						+ rev, e);
				eLogger.warn(rev + " will be ignored");
			}
		}

		return null;
	}

	/**
	 * Check whether the given changed path is relevant.
	 * 
	 * @param revisionNum
	 *            the revision number under consideration
	 * @param path
	 *            the changed path
	 * @param logEntry
	 *            the owner log entry
	 * @return <code>true</code> if the given path is relevant,
	 *         <code>false</code> otherwise
	 * @throws SVNException
	 *             If unable to solve the kind of the node of this change
	 */
	private boolean isTarget(final long revisionNum,
			final SVNLogEntryPath path, final SVNLogEntry logEntry)
			throws SVNException {
		final String relativePath = path.getPath();

		SVNNodeKind kind = path.getKind();

		if (kind == SVNNodeKind.UNKNOWN) {
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
			final char type = path.getType();

			if (type == 'D') {
				// directory deletion
				final List<String> relevantFilesInDeletedDir = this.repositoryManager
						.getListOfRelevantFiles(revisionNum - 1, relativePath);

				return !relevantFilesInDeletedDir.isEmpty();
			} else {
				// in this case each files in this directory must be reported
				return false;
			}
		} else if (kind == SVNNodeKind.FILE) {
			return this.repositoryManager.getLanguage().isRelevantFile(
					relativePath);
		} else {
			return false;
		}
	}
}
