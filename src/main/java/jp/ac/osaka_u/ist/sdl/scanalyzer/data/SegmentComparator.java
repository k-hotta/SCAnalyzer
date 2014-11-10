package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.Comparator;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegmentComparator;

/**
 * A comparator for segments.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program elements
 */
public class SegmentComparator<E extends IProgramElement> implements
		Comparator<Segment<E>> {

	private final DBSegmentComparator dbSegmentComparator = new DBSegmentComparator();

	@Override
	public int compare(Segment<E> o1, Segment<E> o2) {
		return dbSegmentComparator.compare(o1.getCore(), o2.getCore());
	}

}
