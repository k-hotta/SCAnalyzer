package jp.ac.osaka_u.ist.sdl.scanalyzer.ui;

import java.util.EventListener;

/**
 * This interface represents listeners of models.
 * 
 * @author k-hotta
 *
 */
public interface ModelListener extends EventListener {

	/**
	 * This method is expected to process the given model event.
	 * 
	 * @param e
	 *            the event
	 */
	public void modelChanged(final ModelEvent e);

}
