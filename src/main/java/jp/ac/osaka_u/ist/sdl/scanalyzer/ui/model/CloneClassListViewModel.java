package jp.ac.osaka_u.ist.sdl.scanalyzer.ui.model;

import javax.swing.event.EventListenerList;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.CodeFragmentChangeEvent;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.CodeFragmentChangeEventListener;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.view.CloneClassListView;

/**
 * This is a model for {@link CloneClassListView}.
 * 
 * @author k-hotta
 *
 */
public class CloneClassListViewModel {

	/**
	 * The listeners for the left-click
	 */
	private EventListenerList leftListeners = new EventListenerList();

	/**
	 * The listeners for the right-click
	 */
	private EventListenerList rightListeners = new EventListenerList();

	/**
	 * The code fragment corresponding to the left-click
	 */
	private CodeFragment<?> leftCodeFragment;

	/**
	 * The code fragment corresponding to the right-click
	 */
	private CodeFragment<?> rightCodeFragment;

	/**
	 * The clone class to be shown
	 */
	private CloneClass<?> cloneClass;

	/**
	 * Add the given listener to this model as a left-click listener.
	 * 
	 * @param listener
	 *            a listener to be added
	 */
	public void addLeftListener(final CodeFragmentChangeEventListener listener) {
		leftListeners.add(CodeFragmentChangeEventListener.class, listener);
	}

	/**
	 * Remove the given listener from the list of left-click listeners of this
	 * model.
	 * 
	 * @param listener
	 *            a listener to be removed
	 */
	public void removeLeftListener(
			final CodeFragmentChangeEventListener listener) {
		leftListeners.remove(CodeFragmentChangeEventListener.class, listener);
	}

	/**
	 * Add the given listener to this model as a right-click listener.
	 * 
	 * @param listener
	 *            a listener to be added
	 */
	public void addRightListener(final CodeFragmentChangeEventListener listener) {
		rightListeners.add(CodeFragmentChangeEventListener.class, listener);
	}

	/**
	 * Remove the given listener from the list of right-click listeners of this
	 * model.
	 * 
	 * @param listener
	 *            a listener to be removed
	 */
	public void removeRightListener(
			final CodeFragmentChangeEventListener listener) {
		rightListeners.remove(CodeFragmentChangeEventListener.class, listener);
	}

	/**
	 * Notify the change of the code fragment to listeners
	 * 
	 * @param listeners
	 *            the listeners
	 * @param newCodeFragment
	 *            the new code fragment set by the change
	 */
	private void fireModelChanged(final EventListenerList listeners,
			final CodeFragment<?> newCodeFragment) {
		CodeFragmentChangeEventListener[] listenersArray = listeners
				.getListeners(CodeFragmentChangeEventListener.class);
		final CodeFragmentChangeEvent event = new CodeFragmentChangeEvent(this,
				newCodeFragment);
		for (final CodeFragmentChangeEventListener l : listenersArray) {
			l.fragmentChanged(event);
		}
	}

	/**
	 * Notify the change of the left code fragment to listeners
	 */
	protected void fireModelChangedToLeft() {
		fireModelChanged(leftListeners, leftCodeFragment);
	}

	/**
	 * Notify the change of the right code fragment to listeners
	 */
	protected void fireModelChangeToRight() {
		fireModelChanged(rightListeners, rightCodeFragment);
	}

	/**
	 * Set the given code fragment to the left code fragment.
	 * 
	 * @param codeFragmentId
	 *            the id of the fragment to be set
	 */
	public void setLeftCodeFragment(final Long codeFragmentId) {
		if (codeFragmentId != null) {
			this.leftCodeFragment = cloneClass.getCodeFragments().get(
					codeFragmentId);
		} else {
			this.leftCodeFragment = null;
		}
		fireModelChangedToLeft();
	}

	/**
	 * Set the given code fragment to the right code fragment.
	 * 
	 * @param codeFragmentId
	 *            the fragment to be set
	 */
	public void setRightCodeFragment(final Long codeFragmentId) {
		if (codeFragmentId != null) {
			this.rightCodeFragment = cloneClass.getCodeFragments().get(
					codeFragmentId);
		} else {
			this.rightCodeFragment = null;
		}
		fireModelChangeToRight();
	}

	/**
	 * Set the clone class.
	 * 
	 * @param cloneClass
	 *            the clone class to be set
	 */
	public void setCloneClass(final CloneClass<?> cloneClass) {
		this.cloneClass = cloneClass;
		setLeftCodeFragment(null);
		setRightCodeFragment(null);
	}

}
