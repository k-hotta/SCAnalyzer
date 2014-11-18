package jp.ac.osaka_u.ist.sdl.scanalyzer.ui;

import java.util.EventObject;

/**
 * This class represents an event related to a model.
 * 
 * @author k-hotta
 *
 */
public class ModelEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8060295575131642832L;

	public ModelEvent(Object source) {
		super(source);
	}

}
