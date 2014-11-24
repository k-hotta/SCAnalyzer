package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.DBManager;

/**
 * This is a retriever for {@link Segment} with the volatile information.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class VolatileSegmentRetriever<E extends IProgramElement> implements
		IRetriever<E, DBSegment, Segment<E>> {

	/**
	 * The manager for retrieved objects
	 */
	private final RetrievedObjectManager<E> manager;

	/**
	 * The manager of database connection
	 */
	private final DBManager dbManager;

	/**
	 * The retriever for source files
	 */
	private VolatileSourceFileRetriever<E> sourceFileRetriever;

	/**
	 * The retriever for versions
	 */
	private VolatileVersionRetriever<E> versionRetriever;

	public VolatileSegmentRetriever(final RetrievedObjectManager<E> manager) {
		this.manager = manager;
		this.dbManager = DBManager.getInstance();
		sourceFileRetriever = null;
		versionRetriever = null;
	}

	/**
	 * Set the retriever for source files
	 * 
	 * @param sourceFileRetriever
	 *            the retriever to be set
	 */
	public void setSourceFileRetriever(
			final VolatileSourceFileRetriever<E> sourceFileRetriever) {
		this.sourceFileRetriever = sourceFileRetriever;
	}

	/**
	 * Set the retriever for versions
	 * 
	 * @param versionRetriever
	 *            the retriever to be set
	 */
	public void setVersionRetriever(
			final VolatileVersionRetriever<E> versionRetriever) {
		this.versionRetriever = versionRetriever;
	}

	@Override
	public Segment<E> retrieveElement(DBSegment dbElement) {
		final Segment<E> segment = new Segment<E>(dbElement);

		// refresh the parent attributes of the segment
		// this is required because these attributes might not be refreshed
		// if the deep refreshing is OFF
		try {
			dbManager.getNativeDao(DBCodeFragment.class).refresh(
					dbElement.getCodeFragment());
			dbManager.getNativeDao(DBCloneClass.class).refresh(
					dbElement.getCodeFragment().getCloneClass());
			dbManager.getNativeDao(DBSourceFile.class).refresh(
					dbElement.getSourceFile());
		} catch (Exception e) {
			throw new IllegalStateException("fail to refresh the segment "
					+ dbElement.getId(), e);
		}

		SourceFile<E> sourceFile = manager.getSourceFile(dbElement
				.getSourceFile().getId());

		if (sourceFile == null) {
			final DBSourceFile dbSourceFile = dbElement.getSourceFile();

			final DBVersion dbOwnerVersion = dbElement.getCodeFragment()
					.getCloneClass().getVersion();

			Version<E> ownerVersion = manager
					.getVersion(dbOwnerVersion.getId());
			if (ownerVersion == null) {
				ownerVersion = versionRetriever.retrieveElement(dbOwnerVersion);
			}

			sourceFile = sourceFileRetriever.retrieveElement(dbSourceFile,
					dbOwnerVersion);
		}

		segment.setSourceFile(sourceFile);
		segment.setContents(sourceFile
				.getContents()
				.subMap(dbElement.getStartPosition(),
						dbElement.getEndPosition() + 1).values());

		manager.add(segment);

		return segment;
	}

}
