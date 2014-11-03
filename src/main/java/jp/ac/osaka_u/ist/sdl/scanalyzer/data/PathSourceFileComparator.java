package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.Comparator;

/**
 * This class compares source files with their paths.
 * 
 * @author k-hotta
 *
 */
public class PathSourceFileComparator implements Comparator<SourceFile<?>> {

	@Override
	public int compare(SourceFile<?> o1, SourceFile<?> o2) {
		return o1.getPath().compareTo(o2.getPath());
	}

}
