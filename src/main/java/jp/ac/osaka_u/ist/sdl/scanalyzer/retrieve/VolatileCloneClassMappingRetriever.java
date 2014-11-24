package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;

public class VolatileCloneClassMappingRetriever<E extends IProgramElement>
		implements IRetriever<E, DBCloneClassMapping, CloneClassMapping<E>> {

	@Override
	public CloneClassMapping<E> retrieveElement(DBCloneClassMapping dbElement) {
		// TODO Auto-generated method stub
		return null;
	}

}
