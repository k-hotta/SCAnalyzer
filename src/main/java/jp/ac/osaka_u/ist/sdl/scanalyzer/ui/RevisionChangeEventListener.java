package jp.ac.osaka_u.ist.sdl.scanalyzer.ui;

import java.util.EventListener;

/**
 * This is a listener of {@link RevisionChangeEvent}.
 * 
 * @author k-hotta
 *
 */
public interface RevisionChangeEventListener extends EventListener {

	/**
	 * Process the revision change event.
	 * 
	 * @param e
	 *            the event
	 */
	public void revisionChanged(final RevisionChangeEvent e);

}
