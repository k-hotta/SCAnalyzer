package jp.ac.osaka_u.ist.sdl.scanalyzer.ui;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;

/**
 * This is an event that represents changes of code fragment.
 * 
 * @author k-hotta
 *
 */
public class CodeFragmentChangeEvent extends ModelEvent {

	private static final long serialVersionUID = -2040857680588427312L;

	private final CodeFragment<?> newCodeFragment;

	public CodeFragmentChangeEvent(Object source,
			final CodeFragment<?> newCodeFragment) {
		super(source);
		this.newCodeFragment = newCodeFragment;
	}

	public CodeFragment<?> getNewCodeFragment() {
		return newCodeFragment;
	}

}
