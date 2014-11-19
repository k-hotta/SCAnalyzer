package jp.ac.osaka_u.ist.sdl.scanalyzer.ui;

/**
 * This is an event that represents changes of segments.
 * 
 * @author k-hotta
 *
 */
public class SegmentChangeEvent extends ModelEvent {

	private static final long serialVersionUID = -8197661124115559895L;

	public SegmentChangeEvent(Object source) {
		super(source);
	}

}
