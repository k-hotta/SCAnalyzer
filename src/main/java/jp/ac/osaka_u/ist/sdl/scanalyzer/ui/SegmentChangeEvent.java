package jp.ac.osaka_u.ist.sdl.scanalyzer.ui;

/**
 * This class represents an event that notifies a change of segments to be
 * shown.
 * 
 * @author k-hotta
 *
 */
public class SegmentChangeEvent extends ModelEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8197661124115559895L;

	public SegmentChangeEvent(Object source) {
		super(source);
	}

}
