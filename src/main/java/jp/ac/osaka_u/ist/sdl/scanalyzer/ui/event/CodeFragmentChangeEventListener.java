package jp.ac.osaka_u.ist.sdl.scanalyzer.ui.event;

import java.util.EventListener;

/**
 * This is a listener of {@link CodeFragmentChangeEvent}.
 * 
 * @author k-hotta
 *
 */
public interface CodeFragmentChangeEventListener extends EventListener {

	/**
	 * Process the code change event.
	 * 
	 * @param e
	 *            the event
	 */
	public void fragmentChanged(final CodeFragmentChangeEvent e);

}
