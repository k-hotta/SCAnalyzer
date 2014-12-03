package jp.ac.osaka_u.ist.sdl.scanalyzer.mining;

import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneGenealogy;

/**
 * This is an implementation of {@link MiningStrategy} to get how similar a
 * clone class is during its evolution.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class CloneGenealogySimilarityStrategy<E extends IProgramElement>
		implements MiningStrategy<DBCloneGenealogy, CloneGenealogy<E>> {

	@Override
	public boolean requiresVolatileObjects() {
		return false;
	}

	@Override
	public void mine(Collection<CloneGenealogy<E>> genealogies)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeResult() throws Exception {
		// TODO Auto-generated method stub

	}

}
