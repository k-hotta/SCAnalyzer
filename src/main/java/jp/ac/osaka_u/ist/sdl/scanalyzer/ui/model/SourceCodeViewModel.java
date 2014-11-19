package jp.ac.osaka_u.ist.sdl.scanalyzer.ui.model;

import javax.swing.event.EventListenerList;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.SegmentChangeEvent;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.SegmentChangeEventListener;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.helper.FileContentProvideHelper;

/**
 * This is a model for
 * {@link jp.ac.osaka_u.ist.sdl.scanalyzer.ui.view.SourceCodeView}.
 * 
 * @author k-hotta
 *
 */
public class SourceCodeViewModel {

	/**
	 * The listeners of this model
	 */
	private EventListenerList listeners = new EventListenerList();

	/**
	 * The segment to be shown
	 */
	private Segment<?> segment = null;

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
	 * Notify the listeners that this model has been updated
	 */
	protected void fireModelChanged() {
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
	 * @param segment
	 *            a segment to be set
	 */
	public void setSegment(final Segment<?> segment) {
		this.segment = segment;
		fireModelChanged();
	}

	/**
	 * Get the path of the owner file of the segment to be shown.
	 * 
	 * @return the path of the owner file
	 */
	public String getPath() {
		if (segment == null) {
			return "";
		}

		return segment.getSourceFile().getPath();
	}

	/**
	 * Get the file content of the owner file of the segment to be shown.
	 * 
	 * @return the file content of the owner file
	 */
	public String getFileContent() {
		if (segment == null) {
			return "";
		}

		return FileContentProvideHelper.getFileContent(segment);
	}

	/**
	 * Get the start offset of the segment to be shown.
	 * 
	 * @return the start offset
	 */
	public int getStartOffset() {
		if (segment == null) {
			return -1;
		}

		return segment.getFirstElement().getOffset();
	}

	/**
	 * Get the end offset of the segment to be shown.
	 * 
	 * @return the end offset
	 */
	public int getEndOffset() {
		if (segment == null) {
			return -1;
		}

		return segment.getLastElement().getOffset();
	}

	/**
	 * Get the start line of the segment to be shown.
	 * 
	 * @return the start line
	 */
	public int getStartLine() {
		if (segment == null) {
			return -1;
		}

		return segment.getFirstElement().getLine();
	}

	/**
	 * Get the end line of the segment to be shown.
	 * 
	 * @return the end line
	 */
	public int getEndLine() {
		if (segment == null) {
			return -1;
		}

		return segment.getLastElement().getLine();
	}

}
