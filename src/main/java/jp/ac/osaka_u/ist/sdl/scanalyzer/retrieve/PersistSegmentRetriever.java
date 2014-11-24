package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;

/**
 * This is a retriever for {@link Segment} without volatile information.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class PersistSegmentRetriever<E extends IProgramElement> extends
		PersistObjectRetriever<E, DBSegment, Segment<E>> {

	public PersistSegmentRetriever(RetrievedObjectManager<E> manager) {
		super(manager);
	}

	@Override
	protected Segment<E> make(DBSegment dbElement) {
		return new Segment<E>(dbElement);
	}

	@Override
	protected void add(Segment<E> element) {
		manager.add(element);
	}

}
