package jp.ac.osaka_u.ist.sdl.scanalyzer.ui.model;

import javax.swing.event.EventListenerList;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.event.SegmentChangeEvent;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.event.SegmentChangeEventListener;

/**
 * This is a model for code fragment view.
 * 
 * @author k-hotta
 *
 */
public class CodeFragmentViewModel {

	/**
	 * The listeners of this model
	 */
	private EventListenerList listeners = new EventListenerList();

	/**
	 * The segment to be shown
	 */
	private Segment<?> segment = null;

	/**
	 * The code fragment to be shown
	 */
	private CodeFragment<?> codeFragment = null;

	/**
	 * Add the given listener to this model.
	 * 
	 * @param listener
	 *            a listener to be added
	 */
	public void addListener(final SegmentChangeEventListener listener) {
		listeners.add(SegmentChangeEventListener.class, listener);
	}

	/**
	 * Remove the given listener from this model.
	 * 
	 * @param listener
	 *            a listener to be removed
	 */
	public void removeListener(final SegmentChangeEventListener listener) {
		listeners.remove(SegmentChangeEventListener.class, listener);
	}

	/**
	 * Notify the listeners that the segment has been updated
	 */
	protected void fireSegmentChanged() {
		SegmentChangeEventListener[] listenersArray = listeners
				.getListeners(SegmentChangeEventListener.class);
		final SegmentChangeEvent event = new SegmentChangeEvent(this, segment);
		for (final SegmentChangeEventListener l : listenersArray) {
			l.segmentChanged(event);
		}
	}

	/**
	 * Set the segment to be shown with the specified one, then notify the
	 * listeners that the segment has been replaced.
	 * 
	 * @param segmentId
	 *            the id of a segment to be set
	 */
	public void setSegment(final Long segmentId) {
		this.segment = null;
		if (segmentId != null) {
			for (final Segment<?> segment : this.codeFragment.getSegments()) {
				if (segment.getId() == segmentId) {
					this.segment = segment;
					break;
				}
			}
		}

		fireSegmentChanged();
	}

	/**
	 * Get the segment.
	 * 
	 * @return the segment
	 */
	public Segment<?> getSegment() {
		return segment;
	}

	/**
	 * Set the code fragment to be shown with the specified one, then notify the
	 * segment change listeners.
	 * 
	 * @param codeFragment
	 *            a code fragment to be set
	 */
	public void setCodeFragment(final CodeFragment<?> codeFragment) {
		this.codeFragment = codeFragment;
		setSegment(null);
	}

}
