package jp.ac.osaka_u.ist.sdl.c20r.ui.controller;

import java.awt.Color;
import java.awt.Component;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import jp.ac.osaka_u.ist.sdl.c20r.ui.data.CSVCloneElementInfo;
import jp.ac.osaka_u.ist.sdl.c20r.ui.settings.UISettings;

public class DisappearedCloneElementRenderer extends DefaultTableCellRenderer {

	private static final Color emphasisForeground = UISettings.getInstance()
			.getFileDeletedForeground();

	private static final Color disappearedBackground = UISettings.getInstance()
			.getDisappearedBackground();

	private static final Color movedBackground = UISettings.getInstance()
			.getMovedBackground();

	private static final Color bothBackground = UISettings.getInstance()
			.getBothBackground();

	private final Set<CSVCloneElementInfo> elements;

	public DisappearedCloneElementRenderer(
			final Set<CSVCloneElementInfo> elements) {
		this.elements = elements;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);

		final String path = (String) table.getValueAt(row, 0);
		final int start = (Integer) table.getValueAt(row, 1);
		final int end = (Integer) table.getValueAt(row, 2);

		CSVCloneElementInfo selectedClone = null;
		for (CSVCloneElementInfo element : elements) {
			if (element.getPath().equals(path)
					&& element.getStartLine() == start
					&& element.getEndLine() == end) {
				selectedClone = element;
				break;
			}
		}

		if ((selectedClone != null) && (selectedClone.isInDeletedFile())) {
			setForeground(emphasisForeground);
		} else {
			setForeground(table.getForeground());
		}

		if ((selectedClone != null)
				&& (selectedClone.isDisappear() && (selectedClone.isMoved()))) {
			setBackground(bothBackground);
		} else if ((selectedClone != null) && (selectedClone.isDisappear())) {
			setBackground(disappearedBackground);
		} else if ((selectedClone != null) && (selectedClone.isMoved())) {
			setBackground(movedBackground);
		} else {
			setBackground(table.getBackground());
		}

		return this;
	}
}
