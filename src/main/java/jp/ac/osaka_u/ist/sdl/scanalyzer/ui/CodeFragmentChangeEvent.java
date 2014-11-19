package jp.ac.osaka_u.ist.sdl.scanalyzer.ui;

/**
 * This is an event that represents changes of code fragment.
 * 
 * @author k-hotta
 *
 */
public class CodeFragmentChangeEvent extends ModelEvent {

	private static final long serialVersionUID = -2040857680588427312L;

	public CodeFragmentChangeEvent(Object source) {
		super(source);
	}

}
