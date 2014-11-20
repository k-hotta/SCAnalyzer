package jp.ac.osaka_u.ist.sdl.scanalyzer.ui;

import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;

/**
 * This is an event that represents revision changed.
 * 
 * @author k-hotta
 *
 */
public class RevisionChangeEvent extends ModelEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7905902972189902492L;

	/**
	 * The clone classes updated by the event
	 */
	private final List<CloneClass<?>> newCloneClasses;

	public RevisionChangeEvent(Object source,
			final List<CloneClass<?>> newCloneClasses) {
		super(source);
		this.newCloneClasses = newCloneClasses;
	}

	/**
	 * Get the list of clone classes set by this event.
	 * 
	 * @return the list of clone classes
	 */
	public List<CloneClass<?>> getNewCloneClasses() {
		return newCloneClasses;
	}

}
