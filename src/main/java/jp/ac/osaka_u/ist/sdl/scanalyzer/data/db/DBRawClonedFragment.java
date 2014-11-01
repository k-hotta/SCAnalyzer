package jp.ac.osaka_u.ist.sdl.scanalyzer.data.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * A class that represents <b>raw</b> fragment in <b>raw</b> clone classes. <br>
 * Here, the term raw indicates that the data as they are reported by clone
 * detectors. <br>
 * SCAnalyzer has its own another definition of clone classes and cloned
 * fragments. <br>
 * Compared to the own definition of cloned fragments, this class has fewer
 * information including owner revision, owner source file, start line, and
 * length in number of lines. <br>
 * This class is supposed to be used for reading and storing clone information
 * reported by any clone detectors as a pre-process of SCAnalyzer. <br>
 * 
 * @author k-hotta
 * 
 * @see DBRawCloneClass
 * 
 */
@DatabaseTable(tableName = "RAW_CLONED_FRAGMENTS")
public class DBRawClonedFragment implements IDBElement {

	/**
	 * The column name for id
	 */
	public static final String ID_COLUMN_NAME = "ID";

	/**
	 * The column name for version
	 */
	public static final String VERSION_COLUMN_NAME = "VERSION";

	/**
	 * The column name for sourceFile
	 */
	public static final String SOURCE_FILE_COLUMN_NAME = "SOURCE_FILE";

	/**
	 * The column name for cloneClass
	 */
	public static final String CLONE_CLASS_COLUMN_NAME = "CLONE_CLASS";

	/**
	 * The column name for startLine
	 */
	public static final String START_LINE_COLUMN_NAME = "START_LINE";

	/**
	 * The column name for length
	 */
	public static final String LENGTH_COLUMN_NAME = "LENGTH";

	/**
	 * The id of this fragment
	 */
	@DatabaseField(id = true, columnName = ID_COLUMN_NAME)
	private long id;

	/**
	 * The owner version of this fragment
	 */
	@DatabaseField(canBeNull = false, foreign = true, index = true, columnName = VERSION_COLUMN_NAME)
	private DBVersion version;

	/**
	 * The the owner source file of this fragment
	 */
	@DatabaseField(canBeNull = false, foreign = true, index = true, columnName = SOURCE_FILE_COLUMN_NAME)
	private DBSourceFile sourceFile;

	/**
	 * The owner clone class of this fragment
	 */
	@DatabaseField(canBeNull = false, foreign = true, columnName = CLONE_CLASS_COLUMN_NAME)
	private DBRawCloneClass cloneClass;

	/**
	 * The line number within the file where this fragment starts
	 */
	@DatabaseField(canBeNull = false, index = true, columnName = START_LINE_COLUMN_NAME)
	private int startLine;

	/**
	 * The length of this fragment in the number of lines
	 */
	@DatabaseField(canBeNull = false, index = true, columnName = LENGTH_COLUMN_NAME)
	private int length;

	/**
	 * The default constructor
	 */
	public DBRawClonedFragment() {

	}

	/**
	 * The constructor with all the values specified
	 * 
	 * @param id
	 *            the id
	 * @param version
	 *            the owner version
	 * @param sourceFile
	 *            the owner source file
	 * @param startLine
	 *            the line number where this fragment starts
	 * @param length
	 *            the length in terms of line numbers
	 * @param cloneClass
	 *            the owner clone class of this fragment
	 */
	public DBRawClonedFragment(final long id, final DBVersion version,
			final DBSourceFile sourceFile, final int startLine, final int length,
			final DBRawCloneClass cloneClass) {
		this.id = id;
		this.version = version;
		this.sourceFile = sourceFile;
		this.startLine = startLine;
		this.length = length;
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
	 * Get the owner version of this fragment.
	 * 
	 * @return the owner version of this fragment
	 */
	public DBVersion getVersion() {
		return version;
	}

	/**
	 * Set the owner version of this fragment with the specified one.
	 * 
	 * @param version
	 *            the owner version to be set
	 */
	public void setVersion(DBVersion version) {
		this.version = version;
	}

	/**
	 * Get the owner source file of this fragment.
	 * 
	 * @return the owner source file of this fragment
	 */
	public DBSourceFile getSourceFile() {
		return sourceFile;
	}

	/**
	 * Set the owner source file of this fragment with the specified one.
	 * 
	 * @param sourceFile
	 *            the owner source file to be set
	 */
	public void setSourceFile(DBSourceFile sourceFile) {
		this.sourceFile = sourceFile;
	}

	/**
	 * Get the start line number of this fragment.
	 * 
	 * @return the start line number of this fragment
	 */
	public int getStartLine() {
		return startLine;
	}

	/**
	 * Set the start line number of this fragment with the specified value.
	 * 
	 * @param startLine
	 *            the start line number to be set
	 */
	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	/**
	 * Get the length of this fragment.
	 * 
	 * @return the length of this fragment
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Set the length of this fragment with the specified value.
	 * 
	 * @param length
	 *            the length of this fragment to be set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * Get the owner clone class of this fragment
	 * 
	 * @return the owner clone class of this fragment
	 */
	public DBRawCloneClass getCloneClass() {
		return cloneClass;
	}

	/**
	 * Set the owner clone class of this fragment with the specified one
	 * 
	 * @param cloneClass
	 *            the owner clone class to be set
	 */
	public void setCloneClass(DBRawCloneClass cloneClass) {
		this.cloneClass = cloneClass;
	}

	/**
	 * Judge whether the given object equals to this object. <br>
	 * 
	 * @return <code>true</code> if the given object is an instance of
	 *         {@link DBRawClonedFragment} and the id values of the two objects
	 *         are the same to each other, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DBRawClonedFragment)) {
			return false;
		}
		final DBRawClonedFragment another = (DBRawClonedFragment) obj;

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
		return sourceFile.getPath() + "(" + sourceFile.getId() + ") "
				+ startLine + "-" + (startLine + length - 1);
	}

}
