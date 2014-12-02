package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.DBManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VolatileVersionRetriever<E extends IProgramElement> implements
		IRetriever<E, DBVersion, Version<E>> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(VolatileVersionRetriever.class);

	/**
	 * The manager for retrieved objects
	 */
	private final RetrievedObjectManager<E> manager;

	/**
	 * The manager for database connection
	 */
	private final DBManager dbManager;

	/**
	 * The retriever for revisions
	 */
	private VolatileRevisionRetriever<E> revisionRetriever;

	public VolatileVersionRetriever(final RetrievedObjectManager<E> manager) {
		this.manager = manager;
		this.dbManager = DBManager.getInstance();
		this.revisionRetriever = null;
	}

	/**
	 * Set the revision retriever
	 * 
	 * @param revisionRetriever
	 *            the retriever to be set
	 */
	public void setRevisionRetriever(
			final VolatileRevisionRetriever<E> revisionRetriever) {
		this.revisionRetriever = revisionRetriever;
	}

	@Override
	public Version<E> retrieveElement(DBVersion dbElement) {
		logger.debug("start retrieving " + dbElement.getId());

		final Version<E> version = new Version<E>(dbElement);

		// refresh the db version
		// this is required because it might not be refreshed
		// when deep refreshing is OFF
		try {
			dbManager.getNativeDao(DBVersion.class).refresh(dbElement);
		} catch (Exception e) {
			throw new IllegalStateException("cannot refresh the version "
					+ dbElement.getId(), e);
		}

		Revision revision = manager
				.getRevision(dbElement.getRevision().getId());

		if (revision == null) {
			try {
				dbManager.getNativeDao(DBRevision.class).refresh(
						dbElement.getRevision());
			} catch (Exception e) {
				throw new IllegalStateException("cannot refresh the revision "
						+ dbElement.getRevision().getIdentifier(), e);
			}

			revision = revisionRetriever.retrieveElement(dbElement
					.getRevision());
		}

		version.setRevision(revision);

		manager.add(version);

		return version;
	}

}
