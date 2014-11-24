package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;

public class VolatileCloneClassRetriever<E extends IProgramElement> implements
		IRetriever<E, DBCloneClass, CloneClass<E>> {

	@Override
	public CloneClass<E> retrieveElement(DBCloneClass dbElement) {
		// TODO Auto-generated method stub
		return null;
	}

}
