package jp.ac.osaka_u.ist.sdl.scanalyzer.data.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class represents modifications on CLONES.
 * 
 * @author k-hotta
 *
 */
@DatabaseTable(tableName = TableName.CLONE_MODIFICATION)
public class DBCloneModification implements IDBElement {

	/**
	 * The type of modification, which is ADD or REMOVE
	 * 
	 * @author k-hotta
	 * 
	 */
	public enum Type {
		ADD, REMOVE
	};

	/**
	 * The column name for id
	 */
	public static final String ID_COLUMN_NAME = "ID";

	/**
	 * The column name for oldStartPosition
	 */
	public static final String OLD_START_POSITION_COLUMN_NAME = "OLD_START_POSITION";

	/**
	 * The column name for newStartPosition
	 */
	public static final String NEW_START_POSITION_COLUMN_NAME = "NEW_START_POSITION";

	/**
	 * The column name for length
	 */
	public static final String LENGTH_COLUMN_NAME = "LENGTH";

	/**
	 * The column name for type
	 */
	public static final String TYPE_COLUMN_NAME = "TYPE";

	/**
	 * The column name for contentHash
	 */
	public static final String CONTENT_HASH_COLUMN_NAME = "CONTENT_HASH";

	/**
	 * The column name for codeFragmentMapping
	 */
	public static final String CODE_FRAGMENT_MAPPING_COLUMN_NAME = "CODE_FRAGMENT_MAPPING";

	/**
	 * The column name for relatedOldSegment
	 */
	public static final String RELATED_OLD_SEGMENT_COLUMN_NAME = "RELATED_OLD_SEGMENT";

	/**
	 * The column name for relatedNewSegment
	 */
	public static final String RELATED_NEW_SEGMENT_COLUMN_NAME = "RELATED_NEW_SEGMENT";

	/**
	 * The id of this modification
	 */
	@DatabaseField(id = true, columnName = ID_COLUMN_NAME)
	private long id;

	/**
	 * The start position of elements affected by this modification in the
	 * before version
	 */
	@DatabaseField(canBeNull = false, columnName = OLD_START_POSITION_COLUMN_NAME)
	private int oldStartPosition;

	/**
	 * The start position of elements affected by this modification in the after
	 * version
	 */
	@DatabaseField(canBeNull = false, columnName = NEW_START_POSITION_COLUMN_NAME)
	private int newStartPosition;

	/**
	 * The length of added/deleted elements
	 */
	@DatabaseField(canBeNull = false, columnName = LENGTH_COLUMN_NAME)
	private int length;

	/**
	 * The type of this modification
	 */
	@DatabaseField(canBeNull = false)
	private Type type;

	/**
	 * The hash value created from added/deleted elements
	 */
	@DatabaseField(canBeNull = false)
	private int contentHash;

	/**
	 * The owner code fragment mapping of this modification
	 */
	@DatabaseField(canBeNull = true, foreign = true, columnName = CODE_FRAGMENT_MAPPING_COLUMN_NAME)
	private DBCodeFragmentMapping codeFragmentMapping;

	/**
	 * The related segment in the old version
	 */
	@DatabaseField(canBeNull = true, foreign = true, columnName = RELATED_OLD_SEGMENT_COLUMN_NAME)
	private DBSegment relatedOldSegment;

	/**
	 * The related segment in the new version
	 */
	@DatabaseField(canBeNull = true, foreign = true, columnName = RELATED_NEW_SEGMENT_COLUMN_NAME)
	private DBSegment relatedNewSegment;

	public DBCloneModification() {
		// default constructor mandatory for ORMLite
	}

	public DBCloneModification(final long id, final int oldStartPosition,
			final int newStartPosition, final int length, final Type type,
			final int contentHash,
			final DBCodeFragmentMapping codeFragmentMapping,
			final DBSegment relatedOldSegment, final DBSegment relatedNewSegment) {
		this.id = id;
		this.oldStartPosition = oldStartPosition;
		this.newStartPosition = newStartPosition;
		this.length = length;
		this.type = type;
		this.contentHash = contentHash;
		this.codeFragmentMapping = codeFragmentMapping;
		this.relatedOldSegment = relatedOldSegment;
		this.relatedNewSegment = relatedNewSegment;
	}

	/**
	 * Get the id of this modification.
	 * 
	 * @return the id of this modification
	 */
	@Override
	public long getId() {
		return id;
	}

	/**
	 * Set the id of this modification with the specified value.
	 * 
	 * @param id
	 *            the id to be set
	 */
	@Override
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get the position of the first element affected by this modification in
	 * the before version.
	 * 
	 * @return the position of the first element
	 */
	public int getOldStartPosition() {
		return oldStartPosition;
	}

	/**
	 * Set the position of the first element in the before version with the
	 * specified value.
	 * 
	 * @param oldStartPosition
	 *            the integer value to be set
	 */
	public void setOldStartPosition(int oldStartPosition) {
		this.oldStartPosition = oldStartPosition;
	}

	/**
	 * Get the position of the first element affected by this modification in
	 * the after version.
	 * 
	 * @return the position of the first element
	 */
	public int getNewStartPosition() {
		return newStartPosition;
	}

	/**
	 * Set the position of the first element in the after version with the
	 * specified value.
	 * 
	 * @param newStartPosition
	 *            the integer value to be set
	 */
	public void setNewStartPosition(int newStartPosition) {
		this.newStartPosition = newStartPosition;
	}

	/**
	 * Get the length of this modification.
	 * 
	 * @return the length of this modification
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Set the length of this modification with the specified value
	 * 
	 * @param length
	 *            the integer value to be set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * Get the type of this modification.
	 * 
	 * @return the type of this modification
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Set the type of modification with the specified one.
	 * 
	 * @param type
	 *            the type to be set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * Get the hash value of affected elements by this modification.
	 * 
	 * @return the hash value generated from affected elements
	 */
	public int getContentHash() {
		return contentHash;
	}

	/**
	 * Set the hash value of affected elements by this modification with the
	 * specified value.
	 * 
	 * @param contentHash
	 *            the integer value to be set
	 */
	public void setContentHash(int contentHash) {
		this.contentHash = contentHash;
	}

	/**
	 * Get the owner code fragment mapping of this modification.
	 * 
	 * @return the owner code fragment mapping
	 */
	public DBCodeFragmentMapping getCodeFragmentMapping() {
		return codeFragmentMapping;
	}

	/**
	 * Set the owner code fragment mapping of this modification with the
	 * specified one.
	 * 
	 * @param codeFragmentMapping
	 *            the code fragment mapping to be set
	 */
	public void setCodeFragmentMapping(
			final DBCodeFragmentMapping codeFragmentMapping) {
		this.codeFragmentMapping = codeFragmentMapping;
	}

	/**
	 * Get the related segment in the old version.
	 * 
	 * @return the related segment in the old version
	 */
	public DBSegment getRelatedOldSegment() {
		return relatedOldSegment;
	}

	/**
	 * Set the related segment in the old version with the specified one.
	 * 
	 * @param relatedOldSegment
	 *            the segment to be set
	 */
	public void setRelatedOldSegment(final DBSegment relatedOldSegment) {
		this.relatedOldSegment = relatedOldSegment;
	}

	/**
	 * Get the related segment in the new version.
	 * 
	 * @return the related segment in the new version
	 */
	public DBSegment getRelatedNewSegment() {
		return relatedNewSegment;
	}

	/**
	 * Set the related segment in the new version with the specified one.
	 * 
	 * @param relatedNewSegment
	 *            the segment to be set
	 */
	public void setRelatedNewSegment(final DBSegment relatedNewSegment) {
		this.relatedNewSegment = relatedNewSegment;
	}

	/**
	 * Judge whether the given object equals to this object. <br>
	 * 
	 * @return <code>true</code> if the given object is an instance of
	 *         {@link DBCloneModification} and the id values of the two objects
	 *         are the same to each other, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DBCloneModification)) {
			return false;
		}

		final DBCloneModification another = (DBCloneModification) obj;

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
		return this.type.toString() + ": length = " + this.length + " hash = "
				+ this.contentHash;
	}

}
