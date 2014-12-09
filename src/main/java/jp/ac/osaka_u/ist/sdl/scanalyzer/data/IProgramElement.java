package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

/**
 * This interface describes the atomic element of source code which is of
 * interest in the investigation. <br>
 * The example of atomic elements would be, Tokens, Lines, and Statements.
 * 
 * @author k-hotta
 * 
 */
public interface IProgramElement {

	/**
	 * Get the owner source file of this element
	 * 
	 * @return the owner source file
	 */
	public SourceFile<? extends IProgramElement> getOwnerSourceFile();

	/**
	 * Get the position of this element within the owner source file, which is
	 * in terms of the elements.
	 * 
	 * @return the position
	 */
	public int getPosition();

	/**
	 * Get the line number where this element located
	 * 
	 * @return the line number where this element located
	 */
	public int getLine();

	/**
	 * Get the value of this element
	 * 
	 * @return the value
	 */
	public String getValue();

	/**
	 * Get the offset of this element within the owner source file, which is in
	 * terms of characters.
	 * 
	 * @return the offset
	 */
	public int getOffset();

	/**
	 * Get the hash code of this element to be used for analyzing changes
	 * between clone classes.
	 * 
	 * @return a hash code
	 */
	public int getHashForChangeAnalysis();

}
