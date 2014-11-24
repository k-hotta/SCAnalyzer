package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;

public class VolatileSourceFileRetriever<E extends IProgramElement> implements
		IRetriever<E, DBSourceFile, SourceFile<E>> {

	@Override
	public SourceFile<E> retrieveElement(DBSourceFile dbElement) {
		// TODO Auto-generated method stub
		return null;
	}

}
