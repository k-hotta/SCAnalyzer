package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegmentComparator;

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
	 * The segments in this fragment. The key is the file path, and the value is
	 * the list of segments in the file. The list must be sorted based on the
	 * positions of segments.
	 */
	private final SortedMap<String, SortedSet<Segment<E>>> segments;

	/**
	 * The owner clone class of this fragment
	 */
	private CloneClass<E> cloneClass;

	/**
	 * The map has start positions of this fragment. If this fragment is
	 * separated into multiple files, this map contains the start position
	 * within each of the files. The segments in a single file will be treated
	 * as being in a single chunk, so if there are two or more segments in a
	 * single file, the elements between the segments (which is not included in
	 * any of the segments) will be treated as a gap within the chunk.
	 */
	private final SortedMap<String, Integer> startPositions;

	/**
	 * The map has end positions of this fragment for each of files.
	 */
	private final SortedMap<String, Integer> endPositions;

	/**
	 * The constructor with core
	 * 
	 * @param core
	 *            the core
	 */
	public CodeFragment(final DBCodeFragment core) {
		this.id = core.getId();
		this.core = core;
		this.segments = new TreeMap<String, SortedSet<Segment<E>>>();
		this.cloneClass = null;
		this.startPositions = new TreeMap<String, Integer>();
		this.endPositions = new TreeMap<String, Integer>();
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
	 * @return the list having the segments in this fragment, which is sorted
	 *         based on the positions of segments
	 * 
	 * @throws IllegalStateException
	 *             if segments are empty
	 */
	public List<Segment<E>> getSegments() {
		if (segments.isEmpty()) {
			// segments must not be empty
			throw new IllegalStateException("there are no segments");
		}

		final List<Segment<E>> result = new ArrayList<Segment<E>>();
		for (SortedSet<Segment<E>> segmentsInFile : segments.values()) {
			result.addAll(segmentsInFile);
		}
		return Collections.unmodifiableList(result);
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

		if (this.segments.containsKey(segment.getSourceFile().getPath())) {
			final SortedSet<Segment<E>> segmentsInFile = this.segments
					.get(segment.getSourceFile().getPath());
			segmentsInFile.add(segment);
			this.startPositions.put(segment.getSourceFile().getPath(),
					segmentsInFile.first().getFirstElement().getPosition());
			this.endPositions.put(segment.getSourceFile().getPath(),
					segmentsInFile.last().getLastElement().getPosition());
		} else {
			final SortedSet<Segment<E>> newSet = new TreeSet<Segment<E>>(
					new SegmentComaparator());
			newSet.add(segment);
			this.segments.put(segment.getSourceFile().getPath(), newSet);
			this.startPositions.put(segment.getSourceFile().getPath(), segment
					.getFirstElement().getPosition());
			this.endPositions.put(segment.getSourceFile().getPath(), segment
					.getLastElement().getPosition());
		}
	}

	/**
	 * Get the owner clone class of this fragment.
	 * 
	 * @return the owner clone class of this fragment
	 * @throws IllegalStateException
	 *             if the clone class has not been set
	 */
	public CloneClass<E> getCloneClass() {
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
	public void setCloneClass(final CloneClass<E> cloneClass) {
		if (cloneClass == null) {
			throw new IllegalArgumentException("the given clone class is null");
		}

		if (!this.core.getCloneClass().equals(cloneClass.getCore())) {
			throw new IllegalArgumentException(
					"the given clone class doesn't match to that in the core");
		}

		this.cloneClass = cloneClass;
	}

	/**
	 * Get the start positions as an unmodifiable sorted map.
	 * 
	 * @return the start positions
	 * 
	 * @throws IllegalStateException
	 *             if the segments have not been set
	 */
	public SortedMap<String, Integer> getStartPositions() {
		if (this.startPositions.isEmpty()) {
			throw new IllegalStateException("the segments have not been set");
		}

		return Collections.unmodifiableSortedMap(this.startPositions);
	}

	/**
	 * Get the end positions as an unmodifiable sorted map.
	 * 
	 * @return the end positions
	 * 
	 * @throws IllegalStateException
	 *             if segments have not been set
	 */
	public SortedMap<String, Integer> getEndPositions() {
		if (this.endPositions.isEmpty()) {
			throw new IllegalStateException("the segments have not been set");
		}

		return Collections.unmodifiableSortedMap(this.endPositions);
	}

	/**
	 * A comparator for segments
	 * 
	 * @author k-hotta
	 *
	 */
	private class SegmentComaparator implements Comparator<Segment<E>> {

		private final DBSegmentComparator dbSegmentComparator = new DBSegmentComparator();

		@Override
		public int compare(Segment<E> o1, Segment<E> o2) {
			return dbSegmentComparator.compare(o1.getCore(), o2.getCore());
		}

	}

}
