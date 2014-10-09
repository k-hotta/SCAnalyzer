package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

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
 * @see RawCloneClass
 * 
 */
@DatabaseTable(tableName = "raw_cloned_fragments")
public class RawClonedFragment implements IDBElement {

	/**
	 * The id of this fragment
	 */
	@DatabaseField(id = true, index = true)
	private long id;

	/**
	 * The owner version of this fragment
	 */
	@DatabaseField(canBeNull = false, foreign = true, index = true)
	private Version version;

	/**
	 * The the owner source file of this fragment
	 */
	@DatabaseField(canBeNull = false, foreign = true, index = true)
	private SourceFile sourceFile;
	
	/**
	 * The owner clone class of this fragment
	 */
	@DatabaseField(canBeNull = false, foreign = true)
	private RawCloneClass cloneClass;

	/**
	 * The line number within the file where this fragment starts
	 */
	@DatabaseField(canBeNull = false, index = true)
	private int startLine;

	/**
	 * The length of this fragment in the number of lines
	 */
	@DatabaseField(canBeNull = false, index = true)
	private int length;

	/**
	 * The default constructor
	 */
	public RawClonedFragment() {

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
	public RawClonedFragment(final long id, final Version version,
			final SourceFile sourceFile, final int startLine, final int length,
			final RawCloneClass cloneClass) {
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
	public Version getVersion() {
		return version;
	}

	/**
	 * Set the owner version of this fragment with the specified one.
	 * 
	 * @param version
	 *            the owner version to be set
	 */
	public void setVersion(Version version) {
		this.version = version;
	}

	/**
	 * Get the owner source file of this fragment.
	 * 
	 * @return the owner source file of this fragment
	 */
	public SourceFile getSourceFile() {
		return sourceFile;
	}

	/**
	 * Set the owner source file of this fragment with the specified one.
	 * 
	 * @param sourceFile
	 *            the owner source file to be set
	 */
	public void setSourceFile(SourceFile sourceFile) {
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
	public RawCloneClass getCloneClass() {
		return cloneClass;
	}

	/**
	 * Set the owner clone class of this fragment with the specified one
	 * 
	 * @param cloneClass
	 *            the owner clone class to be set
	 */
	public void setCloneClass(RawCloneClass cloneClass) {
		this.cloneClass = cloneClass;
	}

}
