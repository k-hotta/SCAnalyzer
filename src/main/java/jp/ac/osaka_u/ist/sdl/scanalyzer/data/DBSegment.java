package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class represents segments, which is a set of continuous elements in a
 * single file. Note that this class has only surface-level information. The
 * contents of the segments would be stored in {@link SegmentWithContent}.
 * 
 * @author k-hotta
 * 
 * @see SegmentWithContent
 */
@DatabaseTable(tableName = "SEGMENTS")
public class DBSegment implements IDBElement {

	/**
	 * The column name for id
	 */
	public static final String ID_COLUMN_NAME = "ID";

	/**
	 * The column name for sourceFile
	 */
	public static final String SOURCE_FILE_COLUMN_NAME = "SOURCE_FILE";

	/**
	 * The column name for startPosition
	 */
	public static final String START_POSITION_COLUMN_NAME = "START_POSITION";

	/**
	 * The column name for endPosition
	 */
	public static final String END_POSITION_COLUMN_NAME = "END_POSITION";

	/**
	 * The column name for codeFragment
	 */
	public static final String CODE_FRAGMENT_COLUMN_NAME = "CODE_FRAGMENT";

	/**
	 * The id of this segment
	 */
	@DatabaseField(id = true, columnName = ID_COLUMN_NAME)
	private long id;

	/**
	 * The owner source file of this segment
	 */
	@DatabaseField(canBeNull = false, columnName = SOURCE_FILE_COLUMN_NAME, foreign = true)
	private DBSourceFile sourceFile;

	/**
	 * The start position within the file
	 */
	@DatabaseField(canBeNull = false, columnName = START_POSITION_COLUMN_NAME)
	private int startPosition;

	/**
	 * The end position within the file
	 */
	@DatabaseField(canBeNull = false, columnName = END_POSITION_COLUMN_NAME)
	private int endPosition;

	/**
	 * The owner code fragment of this segment
	 */
	@DatabaseField(canBeNull = false, foreign = true, columnName = CODE_FRAGMENT_COLUMN_NAME)
	private CodeFragment codeFragment;

	/**
	 * The default constructor
	 */
	public DBSegment() {
		// this is mandatory for ORMLite
	}

	/**
	 * The constructor with all the values to be set
	 * 
	 * @param id
	 *            the id of this segment
	 * @param sourceFile
	 *            the owner source file of this segment
	 * @param startPosition
	 *            the start position of this segment
	 * @param endPosition
	 *            the end position of this segment
	 * @param codeFragment
	 *            the owner code fragment of this segment
	 */
	public DBSegment(final long id, final DBSourceFile sourceFile,
			final int startPosition, final int endPosition,
			final CodeFragment codeFragment) {
		this.id = id;
		this.sourceFile = sourceFile;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.codeFragment = codeFragment;
	}

	/**
	 * Get the id of this segment.
	 * 
	 * @return the id of this segment
	 */
	@Override
	public long getId() {
		return id;
	}

	/**
	 * Set the id of this segment with the specified value.
	 * 
	 * @param id
	 *            the id to be set
	 */
	@Override
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get the owner source file of this segment.
	 * 
	 * @return the owner source file of this segment
	 */
	public final DBSourceFile getSourceFile() {
		return sourceFile;
	}

	/**
	 * Set the owner source file of this segment with the specified one.
	 * 
	 * @param sourceFile
	 *            the owner source file of this segment to be set
	 */
	public final void setSourceFile(DBSourceFile sourceFile) {
		this.sourceFile = sourceFile;
	}

	/**
	 * Get the start position of this segment.
	 * 
	 * @return the start position of this segment
	 */
	public final int getStartPosition() {
		return startPosition;
	}

	/**
	 * Set the start position of this segment with the specified value.
	 * 
	 * @param startPosition
	 *            the start position of this segment to be set
	 */
	public final void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	/**
	 * Get the end position of this segment.
	 * 
	 * @return the end position of this segment
	 */
	public final int getEndPosition() {
		return endPosition;
	}

	/**
	 * Set the end position of this segment with the specified value.
	 * 
	 * @param endPosition
	 *            the end position of this segment to be set
	 */
	public final void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}

	/**
	 * Get the owner code fragment of this segment
	 * 
	 * @return the owner code fragment
	 */
	public final CodeFragment getCodeFragment() {
		return codeFragment;
	}

	/**
	 * Set the owner code fragment of this segment with the specified one.
	 * 
	 * @param codeFragment
	 *            the owner code fragment to be set
	 */
	public final void setCodeFragment(final CodeFragment codeFragment) {
		this.codeFragment = codeFragment;
	}

	/**
	 * Judge whether the given object equals to this object. <br>
	 * 
	 * @return <code>true</code> if the given object is an instance of
	 *         {@link DBSegment} and the id values of the two objects are the same
	 *         to each other, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DBSegment)) {
			return false;
		}

		final DBSegment another = (DBSegment) obj;

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
		return this.id + " [" + this.sourceFile + " " + this.startPosition
				+ "-" + this.endPosition + "]";
	}

}
