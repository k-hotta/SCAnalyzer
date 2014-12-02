package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.IDBElement;

/**
 * This class provides helper methods to retrieve foreign collection fields with
 * raw queries.
 * 
 * @author k-hotta
 *
 */
public class ForeignCollectionRetrieveHelper {

	/**
	 * Sort the IDs of the given children (foreign) objects based on their
	 * parent objects. The result will be stored into the map given as the first
	 * argument.
	 * 
	 * @param children
	 *            the target foreign elements
	 * @param func
	 *            a function that provides the ID value of the parent element
	 *            for each child element
	 * 
	 * @return a map whose key is the id of parent and whose value is a set of
	 *         IDs of children
	 */
	public static <D extends IDBElement> Map<Long, Set<Long>> sortIdsByParentElement(
			final Collection<D> children,
			final ForeignCollectionOwnerIdRetrieveFunction<D> func) {
		final Map<Long, Set<Long>> result = new TreeMap<>();

		for (final D child : children) {
			final long parentId = func.getCorrespondingId(child);
			Set<Long> childrenIdsInParent = result.get(parentId);

			if (childrenIdsInParent == null) {
				childrenIdsInParent = new TreeSet<>();
				result.put(parentId, childrenIdsInParent);
			}

			childrenIdsInParent.add(child.getId());
		}

		return result;
	}

	/**
	 * Get a set of IDs of RIGHT elements that should be retrieved. In addition,
	 * the map given as the first argument will be updated to store which right
	 * elements each of left ones has.
	 * 
	 * @param rightIdsByLeft
	 * @param rawIntermediateResults
	 * @return
	 */
	public static <R extends InternalIntermediateDataRepresentation<?>> Set<Long> getRightIdsAndUpdate(
			final Map<Long, Set<Long>> rightIdsByLeft,
			final Collection<R> rawIntermediateResults) {
		final Set<Long> result = new TreeSet<>();

		for (final R rawIntermedaiteResult : rawIntermediateResults) {
			final long leftId = rawIntermedaiteResult.getLeftId();
			final long rightId = rawIntermedaiteResult.getRightId();

			result.add(rightId);
			Set<Long> rightIdsInLeft = rightIdsByLeft.get(leftId);

			if (rightIdsInLeft == null) {
				rightIdsInLeft = new TreeSet<Long>();
				rightIdsByLeft.put(leftId, rightIdsInLeft);
			}

			rightIdsInLeft.add(rightId);
		}

		return result;
	}

}
