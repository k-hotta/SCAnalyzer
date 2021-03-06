package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is a retriever for {@link Revision}.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class VolatileRevisionRetriever<E extends IProgramElement> implements
		IRetriever<E, DBRevision, Revision> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(VolatileRevisionRetriever.class);

	/**
	 * The manager for retrieved objects
	 */
	private final RetrievedObjectManager<E> manager;

	public VolatileRevisionRetriever(final RetrievedObjectManager<E> manager) {
		this.manager = manager;
	}

	@Override
	public Revision retrieveElement(DBRevision dbElement) {
		logger.debug("start retrieving " + dbElement.getId());
		final Revision revision = new Revision(dbElement);
		manager.add(revision);
		return revision;
	}

}
