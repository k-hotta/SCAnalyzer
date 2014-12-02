package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is a retriever for {@link CloneClassMapping} with volatile information.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class VolatileCloneClassMappingRetriever<E extends IProgramElement>
		implements IRetriever<E, DBCloneClassMapping, CloneClassMapping<E>> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(VolatileCloneClassMappingRetriever.class);

	/**
	 * The manager for retrieved objects
	 */
	private final RetrievedObjectManager<E> manager;

	/**
	 * The retriever for clone classes
	 */
	private VolatileCloneClassRetriever<E> cloneClassRetriever;

	public VolatileCloneClassMappingRetriever(
			final RetrievedObjectManager<E> manager) {
		this.manager = manager;
		this.cloneClassRetriever = null;
	}

	/**
	 * Set the clone class retriever
	 * 
	 * @param cloneClassRetriever
	 *            the retriever to be set
	 */
	public void setCloneClassRetriever(
			final VolatileCloneClassRetriever<E> cloneClassRetriever) {
		this.cloneClassRetriever = cloneClassRetriever;
	}

	@Override
	public CloneClassMapping<E> retrieveElement(DBCloneClassMapping dbElement) {
		logger.debug("start retrieving " + dbElement.getId());

		final CloneClassMapping<E> cloneClassMapping = new CloneClassMapping<E>(
				dbElement);

		final DBCloneClass dbOldCloneClass = dbElement.getOldCloneClass();
		final DBCloneClass dbNewCloneClass = dbElement.getNewCloneClass();

		// retrieve old clone class
		if (dbOldCloneClass != null) {
			CloneClass<E> oldCloneClass = manager.getCloneClass(dbOldCloneClass
					.getId());

			if (oldCloneClass == null) {
				oldCloneClass = cloneClassRetriever
						.retrieveElement(dbOldCloneClass);
			}

			cloneClassMapping.setOldCloneClass(oldCloneClass);
		}

		// retrieve new clone class
		if (dbNewCloneClass != null) {
			CloneClass<E> newCloneClass = manager.getCloneClass(dbNewCloneClass
					.getId());

			if (newCloneClass == null) {
				newCloneClass = cloneClassRetriever
						.retrieveElement(dbNewCloneClass);
			}

			cloneClassMapping.setNewCloneClass(newCloneClass);
		}

		manager.add(cloneClassMapping);

		return cloneClassMapping;
	}

}
