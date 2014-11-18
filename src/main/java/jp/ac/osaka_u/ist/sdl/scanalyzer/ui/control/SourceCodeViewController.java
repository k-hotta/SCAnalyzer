package jp.ac.osaka_u.ist.sdl.scanalyzer.ui.control;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.model.SourceCodeViewModel;

/**
 * This is a controller of the source code view.
 * 
 * @author k-hotta
 *
 */
public class SourceCodeViewController {

	/**
	 * The model
	 */
	private SourceCodeViewModel model;

	/**
	 * Set the model.
	 * 
	 * @param model
	 *            the model to be set
	 */
	public void setModel(final SourceCodeViewModel model) {
		this.model = model;
	}

	/**
	 * Get the model.
	 * 
	 * @return the model
	 */
	public SourceCodeViewModel getModel() {
		return model;
	}

	/**
	 * Notify that the segment has been changed.
	 * 
	 * @param segment
	 *            the new segment
	 */
	public void segmentChanged(final Segment<?> segment) {
		model.setSegment(segment);
	}

}
