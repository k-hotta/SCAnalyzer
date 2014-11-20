package jp.ac.osaka_u.ist.sdl.scanalyzer.ui.event;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;

/**
 * This is an event that represents changes of segments.
 * 
 * @author k-hotta
 *
 */
public class SegmentChangeEvent extends ModelEvent {

	private static final long serialVersionUID = -8197661124115559895L;

	/**
	 * The new segment
	 */
	private final Segment<?> newSegment;

	public SegmentChangeEvent(Object source, final Segment<?> newSegment) {
		super(source);
		this.newSegment = newSegment;
	}

	/**
	 * Get the new segment.
	 * 
	 * @return the new segment
	 */
	public Segment<?> getNewSegment() {
		return newSegment;
	}

}
