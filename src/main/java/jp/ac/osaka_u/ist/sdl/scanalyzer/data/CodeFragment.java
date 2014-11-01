package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;

/**
 * This class represents code fragment.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class CodeFragment<E extends IProgramElement> implements
		IDataElement<DBCodeFragment> {

	/**
	 * The id of this fragment
	 */
	private final long id;

	/**
	 * The core of this fragment
	 */
	private final DBCodeFragment core;

	/**
	 * The segments in this fragment
	 */
	private final SortedMap<Long, Segment<E>> segments;

	/**
	 * The owner clone class of this fragment
	 */
	private CloneClass cloneClass;

	/**
	 * The constructor with core
	 * 
	 * @param core
	 *            the core
	 */
	public CodeFragment(final DBCodeFragment core) {
		this.id = core.getId();
		this.core = core;
		this.segments = new TreeMap<Long, Segment<E>>();
		this.cloneClass = null;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public DBCodeFragment getCore() {
		return core;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CodeFragment)) {
			return false;
		}

		final CodeFragment<?> another = (CodeFragment<?>) obj;

		return this.core.equals(another.getCore());
	}

	@Override
	public int hashCode() {
		return this.core.hashCode();
	}

	@Override
	public String toString() {
		return this.core.toString();
	}

	/**
	 * Get the segments in this fragment as an unmodifiable map.
	 * 
	 * @return the map having the segments in this fragment, each of whose key
	 *         is the id of a segment, each of whose value is the segment
	 * 
	 * @throws IllegalStateException
	 *             if segments are empty
	 */
	public SortedMap<Long, Segment<E>> getSegments() {
		if (segments.isEmpty()) {
			// segments must not be empty
			throw new IllegalStateException("there are no segments");
		}
		return Collections.unmodifiableSortedMap(segments);
	}

	/**
	 * Add the given segment to this fragment.
	 * 
	 * @param segment
	 *            the segment to be added
	 * @throws IllegalArgumentException
	 *             if the given segment is not included in the segments in the
	 *             core, or the given segment is null
	 */
	public void addSegment(final Segment<E> segment) {
		if (segment == null) {
			throw new IllegalArgumentException("the given segment is null");
		}
		if (!this.core.getSegments().contains(segment.getCore())) {
			throw new IllegalArgumentException(
					"the given segment is not included in the segments in the core");
		}

		this.segments.put(segment.getId(), segment);
	}

	/**
	 * Get the owner clone class of this fragment.
	 * 
	 * @return the owner clone class of this fragment
	 * @throws IllegalStateException
	 *             if the clone class has not been set
	 */
	public CloneClass getCloneClass() {
		if (cloneClass == null) {
			throw new IllegalStateException("the clone class has not been set");
		}
		return cloneClass;
	}

	/**
	 * Set the owner clone class of this fragment with the specified one. The
	 * core of the given clone class must match to that in the core of this
	 * fragment.
	 * 
	 * @param cloneClass
	 *            the clone class to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if the given clone class doesn't match to that in the core of
	 *             this fragment, or the given clone class is <code>null</code>
	 */
	public void setCloneClass(final CloneClass cloneClass) {
		if (cloneClass == null) {
			throw new IllegalArgumentException("the given clone class is null");
		}

		if (!this.core.getCloneClass().equals(cloneClass.getCore())) {
			throw new IllegalArgumentException(
					"the given clone class doesn't match to that in the core");
		}

		this.cloneClass = cloneClass;
	}

}
