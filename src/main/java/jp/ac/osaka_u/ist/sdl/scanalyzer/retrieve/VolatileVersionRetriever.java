package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;

public class VolatileVersionRetriever<E extends IProgramElement> implements
		IRetriever<E, DBVersion, Version<E>> {

	@Override
	public Version<E> retrieveElement(DBVersion dbElement) {
		// TODO Auto-generated method stub
		return null;
	}

}
