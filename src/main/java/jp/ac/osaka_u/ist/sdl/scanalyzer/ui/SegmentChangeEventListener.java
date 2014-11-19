package jp.ac.osaka_u.ist.sdl.scanalyzer.ui;

import java.util.EventListener;

/**
 * This is a listener of {@link SegmentChangeEvent}.
 * 
 * @author k-hotta
 *
 */
public interface SegmentChangeEventListener extends EventListener {

	/**
	 * Process the event of segment change.
	 * 
	 * @param e
	 *            the event
	 */
	public void segmentChanged(final SegmentChangeEvent e);

}
