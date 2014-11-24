package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;

public class VolatileRevisionRetriever<E extends IProgramElement> implements
		IRetriever<E, DBRevision, Revision> {

	@Override
	public Revision retrieveElement(DBRevision dbElement) {
		// TODO Auto-generated method stub
		return null;
	}

}
