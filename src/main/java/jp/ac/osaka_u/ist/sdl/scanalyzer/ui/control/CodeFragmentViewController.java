package jp.ac.osaka_u.ist.sdl.scanalyzer.ui.control;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.model.CodeFragmentViewModel;

/**
 * This is a controller for the view of code fragments.
 * 
 * @author k-hotta
 * 
 * @see jp.ac.osaka_u.ist.sdl.scanalyzer.ui.view.CodeFragmentView
 */
public class CodeFragmentViewController {

	/**
	 * The model
	 */
	private CodeFragmentViewModel model;

	/**
	 * Set the model.
	 * 
	 * @param model
	 *            the model to be set
	 */
	public void setModel(final CodeFragmentViewModel model) {
		this.model = model;
	}

	/**
	 * Get the model.
	 * 
	 * @return the model
	 */
	public CodeFragmentViewModel getModel() {
		return model;
	}

	public void segmentChanged(final ListSelectionEvent e, final JTable table) {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			return;
		}

		Long id = (Long) table.getValueAt(selectedRow, 0);
		model.setSegment(id);
	}

	/**
	 * Notify that the code fragment has been changed
	 * 
	 * @param codeFragment
	 *            the new code fragment
	 */
	public void fragmentChanged(final CodeFragment<?> codeFragment) {
		model.setCodeFragment(codeFragment);
	}

	/**
	 * Notify that the clone class has been changed
	 */
	public void cloneClassChanged() {
		model.setCodeFragment(null);
	}

}
