package jp.ac.osaka_u.ist.sdl.scanalyzer.ui.event;

import java.util.EventListener;

/**
 * This is a listener of {@link CloneClassChangeEvent}.
 * 
 * @author k-hotta
 *
 */
public interface CloneClassChangeEventListener extends EventListener {

	/**
	 * Process the clone class change event.
	 * 
	 * @param e
	 *            the event
	 */
	public void cloneClassChanged(final CloneClassChangeEvent e);

}
