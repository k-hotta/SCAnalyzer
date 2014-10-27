package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

/**
 * This interface describes the atomic element of source code which is of
 * interest in the investigation. <br>
 * The example of atomic elements would be, Tokens, Lines, and Statements.
 * 
 * @author k-hotta
 * 
 */
public interface IAtomicElement {

	/**
	 * Get the owner source file of this element
	 * 
	 * @return the owner source file
	 */
	public SourceFile<?> getOwnerSourceFile();

	/**
	 * Get the position of this element within the owner source file.
	 * 
	 * @return the position
	 */
	public int getPosition();

}
