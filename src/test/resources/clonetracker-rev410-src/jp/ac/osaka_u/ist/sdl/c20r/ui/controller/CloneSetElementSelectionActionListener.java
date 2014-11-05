package jp.ac.osaka_u.ist.sdl.c20r.ui.controller;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jp.ac.osaka_u.ist.sdl.c20r.ui.data.CSVCloneElementInfo;
import jp.ac.osaka_u.ist.sdl.c20r.ui.data.CSVCloneSetInfo;

public class CloneSetElementSelectionActionListener implements
		ListSelectionListener {

	private final SourceCodeViewerController controller;

	private final PanelDirection leftDirection;

	private final PanelDirection rightDirection;

	private final JTable table;

	private final CSVCloneSetInfo cloneSet;

	private final int beforeRevisionNum;

	private final int afterRevisionNum;

	public CloneSetElementSelectionActionListener(
			final SourceCodeViewerController controller,
			final PanelDirection leftDirection,
			final PanelDirection rightDirection, final JTable table,
			final CSVCloneSetInfo cloneSet, final int beforeRevisionNum,
			final int afterRevisionNum) {
		this.controller = controller;
		this.leftDirection = leftDirection;
		this.rightDirection = rightDirection;
		this.table = table;
		this.cloneSet = cloneSet;
		this.beforeRevisionNum = beforeRevisionNum;
		this.afterRevisionNum = afterRevisionNum;
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

		CSVCloneElementInfo selectedElement = null;
		for (CSVCloneElementInfo element : cloneSet.getElements()) {
			if (element.getPath().equals(path)
					&& element.getStartLine() == start
					&& element.getEndLine() == end) {
				selectedElement = element;
			}
		}

		if (selectedElement == null) {
			return;
		}

		String afterPath = selectedElement.getAfterPath();

		if (afterPath.equals("N/A")) {
			afterPath = path;
		}

		final boolean isAnotherFile = !afterPath.equals(path);

		final String beforeCrd = selectedElement.getBeforeCrd();
		final String afterCrd = selectedElement.getAfterCrd();
		final double similarity = selectedElement.getSimilarity();
		final int ld = selectedElement.getLd();

		controller.setSourceCode(leftDirection, beforeRevisionNum, path, start,
				end, selectedElement.isDisappear(), selectedElement.isMoved(),
				false, isAnotherFile);
		controller.setSourceCode(rightDirection, afterRevisionNum, afterPath,
				selectedElement.getAfterStartLine(),
				selectedElement.getAfterEndLine(),
				selectedElement.isDisappear(), selectedElement.isMoved(), true,
				isAnotherFile);

		controller.setCrd(leftDirection, beforeCrd, false, isAnotherFile, ld,
				similarity);
		controller.setCrd(rightDirection, afterCrd, true, isAnotherFile, ld,
				similarity);
	}
}
