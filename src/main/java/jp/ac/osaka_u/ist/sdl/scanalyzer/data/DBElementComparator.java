package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.Comparator;

/**
 * This class sorts db elements with the ascending order of ids.
 * 
 * @author k-hotta
 * 
 */
public class DBElementComparator implements Comparator<IDBElement> {

	@Override
	public int compare(IDBElement element1, IDBElement element2) {
		return ((Long) element1.getId()).compareTo((Long) element2.getId());
	}

}
