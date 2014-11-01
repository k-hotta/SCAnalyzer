package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;

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
	public DBSourceFile getOwnerSourceFile();

	/**
	 * Get the position of this element within the owner source file.
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

}
