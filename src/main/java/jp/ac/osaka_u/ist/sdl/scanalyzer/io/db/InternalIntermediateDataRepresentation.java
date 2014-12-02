package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.IDBElement;

/**
 * This is an interface for internal data representation for intermediate
 * database tables which connects an ID of a database table to another ID of
 * another database table.
 * 
 * @author k-hotta
 *
 * @param <D>
 */
public interface InternalIntermediateDataRepresentation<D extends IDBElement>
		extends InternalDataRepresentation<D> {

	public Long getLeftId();

	public Long getRightId();

}
