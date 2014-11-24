package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;

public class VolatileCodeFragmentRetriever<E extends IProgramElement>
		implements IRetriever<E, DBCodeFragment, CodeFragment<E>> {

	@Override
	public CodeFragment<E> retrieveElement(DBCodeFragment dbElement) {
		// TODO Auto-generated method stub
		return null;
	}

}
