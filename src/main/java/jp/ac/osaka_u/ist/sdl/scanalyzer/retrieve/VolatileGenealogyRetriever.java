package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragmentMapping;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is a retriever for {@link CloneGenealogy} with volatile
 * information.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class VolatileGenealogyRetriever<E extends IProgramElement> implements
		IRetriever<E, DBCloneGenealogy, CloneGenealogy<E>> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager.getLogger(VolatileGenealogyRetriever.class);
	
	/**
	 * The manager for retrieved objects
	 */
	private final RetrievedObjectManager<E> manager;

	/**
	 * The retriever for versions
	 */
	private VolatileVersionRetriever<E> versionRetriever;

	/**
	 * The retriever for clone class mappings
	 */
	private VolatileCloneClassMappingRetriever<E> cloneClassMappingRetriever;

	/**
	 * The retriever for code fragment mappings
	 */
	private VolatileCodeFragmentMappingRetriever<E> codeFragmentMappingRetriever;

	public VolatileGenealogyRetriever(final RetrievedObjectManager<E> manager) {
		this.manager = manager;
		this.versionRetriever = null;
		this.cloneClassMappingRetriever = null;
		this.codeFragmentMappingRetriever = null;
	}

	/**
	 * Set the version retriever
	 * 
	 * @param versionRetriever
	 *            the retriever to be set
	 */
	public void setVersionRetriever(
			final VolatileVersionRetriever<E> versionRetriever) {
		this.versionRetriever = versionRetriever;
	}

	/**
	 * Set the clone class mapping retriever
	 * 
	 * @param cloneClassMappingRetriever
	 *            the retriever to be set
	 */
	public void setCloneClassMappingRetriever(
			final VolatileCloneClassMappingRetriever<E> cloneClassMappingRetriever) {
		this.cloneClassMappingRetriever = cloneClassMappingRetriever;
	}

	/**
	 * Set the code fragment mapping retriever
	 * 
	 * @param codeFragmentMappingRetriever
	 *            the retriever to be set
	 */
	public void setCodeFragmentMappingRetriever(
			final VolatileCodeFragmentMappingRetriever<E> codeFragmentMappingRetriever) {
		this.codeFragmentMappingRetriever = codeFragmentMappingRetriever;
	}

	@Override
	public CloneGenealogy<E> retrieveElement(DBCloneGenealogy dbElement) {
		logger.debug("start retrieving " + dbElement.getId());
		final CloneGenealogy<E> genealogy = new CloneGenealogy<E>(dbElement);

		// retrieve start version
		{
			Version<E> startVersion = manager.getVersion(dbElement
					.getStartVersion().getId());
			if (startVersion == null) {
				startVersion = versionRetriever.retrieveElement(dbElement
						.getStartVersion());
			}
			genealogy.setStartVersion(startVersion);
		}

		// retrieve end version
		{
			Version<E> endVersion = manager.getVersion(dbElement
					.getEndVersion().getId());
			if (endVersion == null) {
				endVersion = versionRetriever.retrieveElement(dbElement
						.getEndVersion());
			}
			genealogy.setEndVersion(endVersion);
		}

		// set clone class mappings
		for (final DBCloneClassMapping dbCloneClassMapping : dbElement
				.getCloneClassMappings()) {
			CloneClassMapping<E> cloneClassMapping = manager
					.getCloneClassMapping(dbCloneClassMapping.getId());

			if (cloneClassMapping == null) {
				cloneClassMapping = cloneClassMappingRetriever
						.retrieveElement(dbCloneClassMapping);
			}

			for (final DBCodeFragmentMapping dbCodeFragmentMapping : dbCloneClassMapping
					.getCodeFragmentMappings()) {
				CodeFragmentMapping<E> codeFragmentMapping = manager
						.getCodeFragmentMapping(dbCodeFragmentMapping.getId());

				if (codeFragmentMapping == null) {
					codeFragmentMapping = codeFragmentMappingRetriever
							.retrieveElement(dbCodeFragmentMapping);
				}

				cloneClassMapping.addCodeFragmentMappings(codeFragmentMapping);
				codeFragmentMapping.setCloneClassMapping(cloneClassMapping);
			}

			genealogy.addCloneClassMapping(cloneClassMapping);
		}

		// add the retrieved genealogy to the manager
		manager.add(genealogy);

		return genealogy;
	}

}
