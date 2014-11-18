package jp.ac.osaka_u.ist.sdl.scanalyzer.ui;

import java.util.EventListener;

/**
 * This interface represents listeners of models.
 * 
 * @author k-hotta
 *
 */
public interface ModelListener<V extends ModelEvent> extends EventListener {

	/**
	 * This method is expected to process the given model event. This method is
	 * expected to be called when any change occurred in the corresponding
	 * model.
	 * 
	 * @param e
	 *            the model event
	 */
	public void modelChanged(final V e);

}
