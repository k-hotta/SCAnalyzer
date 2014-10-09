package jp.ac.osaka_u.ist.sdl.c20r.ui.controller;

import java.util.Map;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jp.ac.osaka_u.ist.sdl.c20r.ui.data.CSVRevisionInfo;

public class RevisionListSelectionActionListener implements
		ListSelectionListener {

	private final Map<Integer, CSVRevisionInfo> revisions;

	private final JTable table;

	private final CloneSetListViewerController cloneViewerController;

	private final ControllerManager manager;

	public RevisionListSelectionActionListener(
			final Map<Integer, CSVRevisionInfo> revisions, final JTable table,
			final CloneSetListViewerController cloneViewerController) {
		this.revisions = revisions;
		this.table = table;
		this.cloneViewerController = cloneViewerController;
		this.manager = ControllerManager.getInstance();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			return;
		}

		final int revisionNum = (Integer) table.getValueAt(selectedRow, 0);
		final CSVRevisionInfo revision = revisions.get(revisionNum);
		this.cloneViewerController.update(revision.getCloneSets());
		this.cloneViewerController
				.setDefaultRenderer(new DisappearedCloneSetRenderer(revision
						.getCloneSetsMap()));
		manager.getCodeViewerController().clear();
		manager.getUpElementsViewerController().removeAll();
		manager.getDownElementsViewerController().removeAll();

		final CloneSetListSelectionActionListener cloneListener = new CloneSetListSelectionActionListener(
				revision, manager.getListViewerController().getTable(),
				manager.getUpElementsViewerController(),
				manager.getDownElementsViewerController(),
				manager.getCodeViewerController(), revision.getRevisionNum(),
				revision.getNextRevisionNum());
		cloneViewerController.addListSelectionListener(cloneListener);
	}

}
