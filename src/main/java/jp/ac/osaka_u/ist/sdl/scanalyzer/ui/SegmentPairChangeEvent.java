package jp.ac.osaka_u.ist.sdl.scanalyzer.ui;

/**
 * This class represents an event that notifies a change of segment pairs.
 * 
 * @author k-hotta
 *
 */
public class SegmentPairChangeEvent extends ModelEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6786830982073752685L;

	/**
	 * The direction
	 */
	private final Direction direction;

	/**
	 * The direction of the changed segment.
	 * 
	 * @author k-hotta
	 *
	 */
	public enum Direction {
		RIGHT, LEFT;
	}

	public SegmentPairChangeEvent(Object source, final Direction direction) {
		super(source);
		this.direction = direction;
	}

	/**
	 * Get the direction.
	 * 
	 * @return the direction
	 */
	public Direction getDirection() {
		return direction;
	}

}
