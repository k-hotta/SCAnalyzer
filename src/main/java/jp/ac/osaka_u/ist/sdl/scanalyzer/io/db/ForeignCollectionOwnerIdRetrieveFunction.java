package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.IDBElement;

/**
 * This is a functional interface that represents how to get corresponding ID of
 * the given element. This interface is expected to be used to retrieve elements
 * in foreign collection with raw queries. The database table of owner data
 * class of foreign collection field does not have the information about which
 * foreign elements the object of the owner class, which is specified by the
 * raw, has. For retrieving foreign collections, it is necessary to access other
 * database tables that contain the data about the foreign objects of interest. <br>
 * Let's take {@link jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion} and
 * {@link jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange} as an example.
 * DBVersion has a field of a collection contains all the objects of
 * DBFileChange related to the version. In other words, DBVersion has a foreign
 * collection field of DBFileChange. However, the database table of DBVersion
 * itself has no information about which objects of DBFileChange is included in
 * the version. On the other hand, the database table of DBFileChange has the
 * information about to which DBVersion each of the file changes belongs. To
 * know which file changes each version has, it is necessary to combine the data
 * of both of the two tables. <br>
 * The method {@link #getCorrespondingId(IDBElement)} is expected to provide the
 * ID value of OWNER data element in CHILD database tables. In the above
 * example, this method is expected to return the ID values of DBVersion in
 * DBFileChange.
 * 
 * 
 * @author k-hotta
 *
 * @param <D>
 */
@FunctionalInterface
interface ForeignCollectionOwnerIdRetrieveFunction<D extends IDBElement> {

	/**
	 * Provide the id value of the owner data element of the given foreign child
	 * element.
	 * 
	 * @param element
	 *            the foreign child element under consideration
	 * @return the id value of the owner data element
	 */
	long getCorrespondingId(D element);

}
