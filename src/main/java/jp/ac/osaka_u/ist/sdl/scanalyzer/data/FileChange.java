package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class represents a change to an individual file from one version to the
 * next. <br>
 * There are four types of changes that are supported: ADD, DELETE, MODIFY, and
 * RELOCATE.
 * 
 * @author k-hotta
 * 
 */
@DatabaseTable(tableName = "FILE_CHANGES")
public class FileChange implements IDBElement {

	/**
	 * Enumerates types of file changes
	 * 
	 * @author k-hotta
	 * 
	 */
	public enum Type {
		ADD('A'), DELETE('D'), MODIFY('M'), RELOCATE('R');

		private final char typeChar;

		private Type(final char typeChar) {
			this.typeChar = typeChar;
		}

		private char getChar() {
			return this.typeChar;
		}

		public static Type getTypeByChar(final char typeChar) {
			for (Type type : Type.values()) {
				if (type.getChar() == typeChar) {
					return type;
				}
			}

			return null;
		}
	}

	/**
	 * The column name for id
	 */
	public final static String ID_COLUMN_NAME = "ID";

	/**
	 * The column name for oldSourceFile
	 */
	public final static String OLD_SOURCE_FILE_COLUMN_NAME = "OLD_SOURCE_FILE";

	/**
	 * The column name for newSourceFile
	 */
	public final static String NEW_SOURCE_FILE_COLUMN_NAME = "NEW_SOURCE_FILE";

	/**
	 * The column name for type
	 */
	public final static String TYPE_COLUMN_NAME = "TYPE";

	/**
	 * The column name for version
	 */
	public final static String VERSION_COLUMN_NAME = "VERSION";

	/**
	 * The id of this file change
	 */
	@DatabaseField(id = true, columnName = ID_COLUMN_NAME)
	private long id;

	/**
	 * The source file before changed, <code>null</code> if the type of this
	 * file change is {@link Type#ADD}
	 */
	@DatabaseField(canBeNull = true, foreign = true, columnName = OLD_SOURCE_FILE_COLUMN_NAME)
	private SourceFile oldSourceFile;

	/**
	 * The source file after changed, <code>null</code> if the type of this file
	 * change is {@link Type#DELETE}
	 */
	@DatabaseField(canBeNull = true, foreign = true, columnName = NEW_SOURCE_FILE_COLUMN_NAME)
	private SourceFile newSourceFile;

	/**
	 * The type of this file change
	 */
	@DatabaseField(canBeNull = false, columnName = TYPE_COLUMN_NAME)
	private Type type;

	/**
	 * The owner version of this file change. This file change occurred between
	 * the owner version and its last (previous) version.
	 */
	@DatabaseField(canBeNull = false, foreign = true, columnName = VERSION_COLUMN_NAME)
	private Version version;

	/**
	 * The default constructor
	 */
	public FileChange() {

	}

	/**
	 * The constructor with all values specified
	 * 
	 * @param id
	 *            the id
	 * @param oldSourceFile
	 *            the source file before changed, must be <code>null</code> if
	 *            the type is {@link Type#ADD}, otherwise must not be
	 *            <code>null</code>
	 * @param newSourceFile
	 *            the source file after changed, must be <code>null</code> if
	 *            the type is {@link Type#DELETE}, otherwise must not be
	 *            <code>null</code>
	 * @param type
	 *            the type of this file change
	 * @param version
	 *            the owner version of this file change, that is, this file
	 *            change occurred between the owner version and its last
	 *            (previous) one
	 */
	public FileChange(final long id, final SourceFile oldSourceFile,
			final SourceFile newSourceFile, final Type type,
			final Version version) {
		assert ((type == Type.ADD && oldSourceFile == null && newSourceFile != null)
				|| (type == Type.DELETE && oldSourceFile != null && newSourceFile == null) || (oldSourceFile != null && newSourceFile != null));

		this.id = id;
		this.oldSourceFile = oldSourceFile;
		this.newSourceFile = newSourceFile;
		this.type = type;
		this.version = version;
	}

	/**
	 * Get the id of this file change.
	 * 
	 * @return the id of this file change
	 */
	@Override
	public long getId() {
		return id;
	}

	/**
	 * Set the id of this file change with the specified value.
	 * 
	 * @param id
	 *            the id to be set
	 */
	@Override
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get the source file before changed.
	 * 
	 * @return the source file before changed, the returned value is expected to
	 *         be <code>null</code> if the type of this file change is
	 *         {@link Type#ADD}.
	 */
	public SourceFile getOldSourceFile() {
		return oldSourceFile;
	}

	/**
	 * Set the source file before changed with the specified one.
	 * 
	 * @param oldSourceFile
	 *            the source file before changed to be set
	 */
	public void setOldSourceFile(SourceFile oldSourceFile) {
		this.oldSourceFile = oldSourceFile;
	}

	/**
	 * Get the source file after changed.
	 * 
	 * @return the source file after changed, the returned value is expected to
	 *         be <code>null</code> if the type of this file change is
	 *         {@link Type#DELETE}.
	 */
	public SourceFile getNewSourceFile() {
		return newSourceFile;
	}

	/**
	 * Set the source file after changed with the specified one.
	 * 
	 * @param newSourceFile
	 *            the source file after changed to be set
	 */
	public void setNewSourceFile(SourceFile newSourceFile) {
		this.newSourceFile = newSourceFile;
	}

	/**
	 * Get the type of this file change
	 * 
	 * @return the type of this file change
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Set the type of this file change
	 * 
	 * @param type
	 *            the type to be set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * Get the owner version of this file change
	 * 
	 * @return the owner version of this file change
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * Set the owner version of this file change with the specified one
	 * 
	 * @param version
	 *            the owner version of this file change
	 */
	public void setVersion(Version version) {
		this.version = version;
	}

	/**
	 * Judge whether the given object equals to this object. <br>
	 * 
	 * @return <code>true</code> if the given object is an instance of
	 *         {@link FileChange} and the id values of the two objects are the
	 *         same to each other, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof FileChange)) {
			return false;
		}
		final FileChange another = (FileChange) obj;

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

		switch (type) {
		case ADD:
			builder.append("ADD " + newSourceFile.getPath() + "("
					+ newSourceFile.getId() + ")");
			break;
		case DELETE:
			builder.append("DELETE " + oldSourceFile.getPath() + "("
					+ oldSourceFile.getId() + ")");
			break;
		case MODIFY:
			builder.append("MODIFY " + oldSourceFile.getPath() + "("
					+ oldSourceFile.getId() + ") => " + newSourceFile.getPath()
					+ "(" + newSourceFile.getId() + ")");
			break;
		case RELOCATE:
			builder.append("RELOCATE " + oldSourceFile.getPath() + "("
					+ oldSourceFile.getId() + ") => " + newSourceFile.getPath()
					+ "(" + newSourceFile.getId() + ")");
			break;
		default:
			builder.append("UNKNOWN\n");
			break;
		}

		return builder.toString();
	}

}
