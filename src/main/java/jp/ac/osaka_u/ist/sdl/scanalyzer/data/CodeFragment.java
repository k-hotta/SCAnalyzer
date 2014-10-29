package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.Collection;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class represents a code fragment, which is a set of {@link Segment}.
 * 
 * @author k-hotta
 * 
 */
@DatabaseTable(tableName = "CODE_FRAGMENTS")
public class CodeFragment implements IDBElement {

	/**
	 * The column name for id
	 */
	public static final String ID_COLUMN_NAME = "ID";

	/**
	 * The column name for segments
	 */
	public static final String SEGMENTS_COLUMN_NAME = "SEGMENTS";

	/**
	 * The column name for cloneClass
	 */
	public static final String CLONE_CLASS_COLUMN_NAME = "CLONE_CLASS";

	/**
	 * The id of this fragment
	 */
	@DatabaseField(id = true, columnName = ID_COLUMN_NAME)
	private long id;

	/**
	 * The segments in this fragment
	 */
	@ForeignCollectionField(eager = true, columnName = SEGMENTS_COLUMN_NAME)
	private Collection<Segment> segments;

	/**
	 * The owner clone class of this fragment
	 */
	@DatabaseField(canBeNull = false, columnName = CLONE_CLASS_COLUMN_NAME)
	private CloneClass cloneClass;

	/**
	 * The default constructor
	 */
	public CodeFragment() {

	}

	/**
	 * The constructor with all the values specified
	 * 
	 * @param id
	 *            the id of this fragment
	 * @param segments
	 *            the segments in this fragment
	 * @param cloneClass
	 *            the owner clone class of this fragment
	 */
	public CodeFragment(final long id, final Collection<Segment> segments,
			final CloneClass cloneClass) {
		this.id = id;
		this.segments = segments;
		this.cloneClass = cloneClass;
	}

	/**
	 * Get the id of this fragment.
	 * 
	 * @return the id of this fragment
	 */
	@Override
	public long getId() {
		return id;
	}

	/**
	 * Set the id of this fragment with the specified value.
	 * 
	 * @param id
	 *            the id to be set
	 */
	@Override
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get the segments in this fragment.
	 * 
	 * @return the segments in this fragment
	 */
	public Collection<Segment> getSegments() {
		return segments;
	}

	/**
	 * Set the segments in this fragment with the specified one.
	 * 
	 * @param segments
	 *            the segments to be set
	 */
	public void setSegments(final Collection<Segment> segments) {
		this.segments = segments;
	}

	/**
	 * Get the owner clone class of this fragment.
	 * 
	 * @return the owner clone class of this fragment
	 */
	public CloneClass getCloneClass() {
		return cloneClass;
	}

	/**
	 * Set the owner clone class of this fragment with the specified one.
	 * 
	 * @param cloneClass
	 *            the clone class to be set
	 */
	public void setCloneClass(final CloneClass cloneClass) {
		this.cloneClass = cloneClass;
	}

	/**
	 * Judge whether the given object equals to this object. <br>
	 * 
	 * @return <code>true</code> if the given object is an instance of
	 *         {@link CodeFragment} and the id values of the two objects are the
	 *         same to each other, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CodeFragment)) {
			return false;
		}

		final CodeFragment another = (CodeFragment) obj;

		return this.id == another.getId();
	}

	/**
	 * Return a hash code value of this object. <br>
	 * The hash value of this object is just the value of the id. <br>
	 * 
	 * @return the hash value, which equals to the value of id of this object
	 */
	@Override
	public int hashCode() {
		return (int) this.id;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();

		builder.append(id + " (");

		for (final Segment segment : segments) {
			builder.append(segment.toString() + ", ");
		}
		if (segments.size() > 0) {
			builder.deleteCharAt(builder.length() - 1);
			builder.deleteCharAt(builder.length() - 1);
		}
		builder.append(")");

		return builder.toString();
	}

}
