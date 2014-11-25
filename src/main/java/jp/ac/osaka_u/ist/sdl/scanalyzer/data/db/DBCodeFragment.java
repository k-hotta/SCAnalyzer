package jp.ac.osaka_u.ist.sdl.scanalyzer.data.db;

import java.util.Collection;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class represents a code fragment, which is a set of {@link DBSegment}.
 * 
 * @author k-hotta
 * 
 */
@DatabaseTable(tableName = TableName.CODE_FRAGMENT)
public class DBCodeFragment implements IDBElement {

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
	 * The column name for ghost
	 */
	public static final String GHOST_NAME = "GHOST";

	/**
	 * The id of this fragment
	 */
	@DatabaseField(id = true, columnName = ID_COLUMN_NAME)
	private long id;

	/**
	 * The segments in this fragment
	 */
	@ForeignCollectionField(eager = true, columnName = SEGMENTS_COLUMN_NAME)
	private Collection<DBSegment> segments;

	/**
	 * The owner clone class of this fragment
	 */
	@DatabaseField(canBeNull = false, foreign = true, columnName = CLONE_CLASS_COLUMN_NAME)
	private DBCloneClass cloneClass;

	/**
	 * Whether this fragment is ghost or not
	 */
	@DatabaseField(canBeNull = false, columnName = GHOST_NAME)
	private boolean ghost;

	/**
	 * The default constructor
	 */
	public DBCodeFragment() {

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
	 * @param ghost
	 *            whether this fragment is ghost or not
	 */
	public DBCodeFragment(final long id, final Collection<DBSegment> segments,
			final DBCloneClass cloneClass, final boolean ghost) {
		this.id = id;
		this.segments = segments;
		this.cloneClass = cloneClass;
		this.ghost = ghost;
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
	public Collection<DBSegment> getSegments() {
		return segments;
	}

	/**
	 * Set the segments in this fragment with the specified one.
	 * 
	 * @param segments
	 *            the segments to be set
	 */
	public void setSegments(final Collection<DBSegment> segments) {
		this.segments = segments;
	}

	/**
	 * Get the owner clone class of this fragment.
	 * 
	 * @return the owner clone class of this fragment
	 */
	public DBCloneClass getCloneClass() {
		return cloneClass;
	}

	/**
	 * Set the owner clone class of this fragment with the specified one.
	 * 
	 * @param cloneClass
	 *            the clone class to be set
	 */
	public void setCloneClass(final DBCloneClass cloneClass) {
		this.cloneClass = cloneClass;
	}

	/**
	 * Get whether this fragment is ghost or not.
	 * 
	 * @return <code>true</code> if this fragment is a ghost fragment,
	 *         <code>false</code> otherwise
	 */
	public boolean isGhost() {
		return ghost;
	}

	/**
	 * Set whether this fragment is ghost or not with the specified value.
	 * 
	 * @param ghost
	 *            the boolean value to be set
	 */
	public void setGhost(final boolean ghost) {
		this.ghost = ghost;
	}

	/**
	 * Judge whether the given object equals to this object. <br>
	 * 
	 * @return <code>true</code> if the given object is an instance of
	 *         {@link DBCodeFragment} and the id values of the two objects are
	 *         the same to each other, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DBCodeFragment)) {
			return false;
		}

		final DBCodeFragment another = (DBCodeFragment) obj;

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

		for (final DBSegment segment : segments) {
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
