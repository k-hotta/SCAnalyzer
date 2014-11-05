package jp.ac.osaka_u.ist.sdl.scanalyzer.data.db;

import java.util.Comparator;

public class DBSegmentComparator implements Comparator<DBSegment> {

	@Override
	public int compare(DBSegment o1, DBSegment o2) {
		int comparePath = o1.getSourceFile().getPath()
				.compareTo(o2.getSourceFile().getPath());
		if (comparePath != 0) {
			return comparePath;
		}

		int compareStart = ((Integer) o1.getStartPosition()).compareTo(o2
				.getStartPosition());
		if (compareStart != 0) {
			return compareStart;
		}

		int compareEnd = ((Integer) o1.getEndPosition()).compareTo(o2
				.getEndPosition());
		if (compareEnd != 0) {
			return compareEnd;
		}

		return ((Long) o1.getId()).compareTo(o2.getId());
	}

}
