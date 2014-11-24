package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;

public class VolatileSegmentRetriever<E extends IProgramElement> implements
		IRetriever<E, DBSegment, Segment<E>> {

	@Override
	public Segment<E> retrieveElement(DBSegment dbElement) {
		// TODO Auto-generated method stub
		return null;
	}

}
