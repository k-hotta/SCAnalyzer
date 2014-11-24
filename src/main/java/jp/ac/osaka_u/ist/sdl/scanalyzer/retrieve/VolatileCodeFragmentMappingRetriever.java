package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragmentMapping;

public class VolatileCodeFragmentMappingRetriever<E extends IProgramElement>
		implements IRetriever<E, DBCodeFragmentMapping, CodeFragmentMapping<E>> {

	@Override
	public CodeFragmentMapping<E> retrieveElement(DBCodeFragmentMapping dbElement) {
		// TODO Auto-generated method stub
		return null;
	}

}
