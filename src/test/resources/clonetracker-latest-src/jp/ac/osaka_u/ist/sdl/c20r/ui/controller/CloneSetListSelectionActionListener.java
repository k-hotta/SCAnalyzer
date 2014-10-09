package jp.ac.osaka_u.ist.sdl.c20r.ui.controller;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jp.ac.osaka_u.ist.sdl.c20r.ui.data.CSVCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.c20r.ui.data.CSVRevisionInfo;
import jp.ac.osaka_u.ist.sdl.c20r.ui.settings.UISettings;

public class CloneSetListSelectionActionListener implements
		ListSelectionListener {

	private final CSVRevisionInfo revisionInfo;

	private final JTable table;

	private final CloneSetElementsViewerController upController;

	private final CloneSetElementsViewerController downController;

	private final SourceCodeViewerController codeController;

	private final int beforeRevisionNum;

	private final int afterRevisionNum;

	public CloneSetListSelectionActionListener(
			final CSVRevisionInfo revisionInfo, final JTable table,
			final CloneSetElementsViewerController upController,
			final CloneSetElementsViewerController downController,
			final SourceCodeViewerController codeController,
			final int beforeRevisionNum, final int afterRevisionNum) {
		this.revisionInfo = revisionInfo;
		this.table = table;
		this.upController = upController;
		this.downController = downController;
		this.codeController = codeController;
		this.beforeRevisionNum = beforeRevisionNum;
		this.afterRevisionNum = afterRevisionNum;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			return;
		}

		long id = (Long) table.getValueAt(selectedRow, 0);
		final CSVCloneSetInfo selectedCloneSet = revisionInfo.getCloneSetsMap()
				.get(id);
		
		if (selectedCloneSet == null) {
			System.err.println("cannot find the clone set " + ((Long) id).toString() + " in the revision " + revisionInfo.getRevisionNum());
			return;
		}
		
		this.upController.update(selectedCloneSet.getElements());
		this.downController.update(selectedCloneSet.getElements());
		this.codeController.clear();
		upController.setDefaultRenderer(new DisappearedCloneElementRenderer(
				selectedCloneSet.getElements()));
		downController.setDefaultRenderer(new DisappearedCloneElementRenderer(
				selectedCloneSet.getElements()));

		ControllerManager manager = ControllerManager.getInstance();

		final CloneSetElementSelectionActionListener upElementListener = new CloneSetElementSelectionActionListener(
				manager.getCodeViewerController(), PanelDirection.LEFT_UP,
				PanelDirection.RIGHT_UP, manager
						.getUpElementsViewerController().getTable(),
				selectedCloneSet, beforeRevisionNum, afterRevisionNum);
		manager.getUpElementsViewerController().addListSelectionListener(
				upElementListener);

		final CloneSetElementSelectionActionListener downElementListener = new CloneSetElementSelectionActionListener(
				manager.getCodeViewerController(), PanelDirection.LEFT_DOWN,
				PanelDirection.RIGHT_DOWN, manager
						.getDownElementsViewerController().getTable(),
				selectedCloneSet, beforeRevisionNum, afterRevisionNum);
		manager.getDownElementsViewerController().addListSelectionListener(
				downElementListener);
	}
}
