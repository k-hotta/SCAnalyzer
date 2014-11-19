package jp.ac.osaka_u.ist.sdl.scanalyzer.ui.control;

import javax.swing.JTable;

import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.model.CloneClassListViewModel;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.view.CloneClassListView;

/**
 * This is a controller for {@link CloneClassListView}.
 * 
 * @author k-hotta
 *
 */
public class CloneClassListViewController {

	/**
	 * The model
	 */
	private CloneClassListViewModel model;

	/**
	 * Set the model.
	 * 
	 * @param model
	 *            the model to be set
	 */
	public void setModel(final CloneClassListViewModel model) {
		this.model = model;
	}

	/**
	 * Get the model.
	 * 
	 * @return the model
	 */
	public CloneClassListViewModel getModel() {
		return model;
	}

	/**
	 * Notify the left code fragment has been changed
	 * 
	 * @param table
	 *            the table
	 */
	public void leftCodeFragmentChanged(JTable table) {
		final Long id = findFragmentId(table);
		model.setLeftCodeFragment(id);
	}

	/**
	 * Notify the right code fragment has been changed
	 * 
	 * @param table
	 *            the table
	 */
	public void rightCodeFragmentChanged(JTable table) {
		final Long id = findFragmentId(table);
		model.setRightCodeFragment(id);
	}

	/**
	 * Get the id of the selected code fragment.
	 * 
	 * @param table
	 *            the table
	 * @return the id of the selected code fragment if found, otherwise
	 *         <code>null</code>
	 */
	private Long findFragmentId(final JTable table) {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			return null;
		}

		return (Long) table.getValueAt(selectedRow, 0);
	}

}
