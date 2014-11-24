package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;

/**
 * The retriever for {@link CodeFragment} with the volatile information.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class VolatileCodeFragmentRetriever<E extends IProgramElement>
		implements IRetriever<E, DBCodeFragment, CodeFragment<E>> {

	/**
	 * The manager for retrieved objects
	 */
	private final RetrievedObjectManager<E> manager;

	/**
	 * The retriever for segments
	 */
	private VolatileSegmentRetriever<E> segmentRetriever;

	public VolatileCodeFragmentRetriever(final RetrievedObjectManager<E> manager) {
		this.manager = manager;
		segmentRetriever = null;
	}

	/**
	 * Set the segment retriever
	 * 
	 * @param segmentRetriever
	 *            the retriever to be set
	 */
	public void setSegmentRetriever(
			final VolatileSegmentRetriever<E> segmentRetriever) {
		this.segmentRetriever = segmentRetriever;
	}

	@Override
	public CodeFragment<E> retrieveElement(DBCodeFragment dbElement) {
		final CodeFragment<E> codeFragment = new CodeFragment<E>(dbElement);

		for (final DBSegment dbSegment : dbElement.getSegments()) {
			Segment<E> segment = manager.getSegment(dbSegment.getId());

			if (segment == null) {
				segment = segmentRetriever.retrieveElement(dbSegment);
			}

			codeFragment.addSegment(segment);
			segment.setCodeFragment(codeFragment);
		}

		manager.add(codeFragment);

		return codeFragment;
	}

}
