package jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.listener;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.GenealogySourceCodeViewerController;

public class GenealogyCloneSetElementSelectionActionListener implements
		ListSelectionListener {

	private final GenealogySourceCodeViewerController controller;

	private final JTable table;

	private final int revisionNum;

	public GenealogyCloneSetElementSelectionActionListener(
			final GenealogySourceCodeViewerController controller,
			final JTable table, final int revisionNum) {
		this.controller = controller;
		this.table = table;
		this.revisionNum = revisionNum;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			return;
		}

		final String path = (String) table.getValueAt(selectedRow, 0);
		final int start = (Integer) table.getValueAt(selectedRow, 1);
		final int end = (Integer) table.getValueAt(selectedRow, 2);

		controller.setSourceCode(revisionNum, path, start, end);
	}

}
