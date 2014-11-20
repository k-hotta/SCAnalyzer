package jp.ac.osaka_u.ist.sdl.scanalyzer.ui.event;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;

/**
 * This is an event that represents a change of clone class.
 * 
 * @author k-hotta
 *
 */
public class CloneClassChangeEvent extends ModelEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -474680854353929510L;

	/**
	 * The new clone class set by this event
	 */
	private final CloneClass<?> newCloneClass;

	public CloneClassChangeEvent(Object source,
			final CloneClass<?> newCloneClass) {
		super(source);
		this.newCloneClass = newCloneClass;
	}

	/**
	 * Get the new clone class.
	 * 
	 * @return the new clone class
	 */
	public CloneClass<?> getNewCloneClass() {
		return newCloneClass;
	}

}
