package jp.ac.osaka_u.ist.sdl.scanalyzer.ui;

/**
 * This class is a default model listener, which throws
 * {@link UnsupportedOperationException} in all the cases.
 * 
 * @author k-hotta
 *
 */
public class DefaultModelListener implements ModelListener {

	@Override
	public void modelChanged(SegmentChangeEvent e) {
		throw new UnsupportedOperationException("the operation for "
				+ e.getClass().getSimpleName() + " is not supported");
	}

}
