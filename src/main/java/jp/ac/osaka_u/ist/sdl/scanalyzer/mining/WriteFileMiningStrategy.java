package jp.ac.osaka_u.ist.sdl.scanalyzer.mining;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDataElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.IDBElement;

/**
 * This interface represents a mining strategy that write the results to a local
 * file.
 * 
 * @author k-hotta
 *
 * @param <D>
 * @param <T>
 */
public interface WriteFileMiningStrategy<D extends IDBElement, T extends IDataElement<D>>
		extends MiningStrategy<D, T> {

	/**
	 * Get the name of strategy.
	 * 
	 * @return
	 */
	public String getStrategyName();

	/**
	 * Get the name of database.
	 * 
	 * @return
	 */
	public String getDatabaseName();

}
