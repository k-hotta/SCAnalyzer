package jp.ac.osaka_u.ist.sdl.scanalyzer.mining;

import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDataElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.IDBElement;

/**
 * This interface represents how to mine the genealogies.
 * 
 * @author k-hotta
 *
 */
public interface MiningStrategy<D extends IDBElement, T extends IDataElement<D>> {

	/**
	 * Whether or not this strategy requires volatile information. If
	 * <code>true</code>, not only persist information but also volatile one
	 * will be retrieved from the database and the repository, which is much
	 * expensive.
	 * 
	 * @return whether or not this strategy requires volatile information
	 */
	public boolean requiresVolatileObjects();

	/**
	 * Mine information from the given collection of elements. It is expected
	 * that this method called several times to complete processing all the
	 * elements. That is, the given collection of elements might have only a
	 * part of elements to be processed. If
	 * {@link MiningStrategy#requiresVolatileObjects()} is <code>false</code>,
	 * the elements in the given collection have only their cores.
	 * 
	 * @param genealogies
	 *            the target elements
	 * @throws Exception
	 *             If any error occurred
	 */
	public void mine(final Collection<T> genealogies) throws Exception;

	/**
	 * Write the result of mining. This method is expected to be called after
	 * all the genealogies are processed by
	 * {@link MiningStrategy#mine(Collection)}.
	 * 
	 * @throws Exception
	 *             If any error occurred
	 */
	public void writeResult() throws Exception;

}
