package jp.ac.osaka_u.ist.sdl.c20r.ui.controller;

import java.awt.Color;
import java.awt.Component;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import jp.ac.osaka_u.ist.sdl.c20r.ui.data.CSVCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.c20r.ui.settings.UISettings;

public class DisappearedCloneSetRenderer extends DefaultTableCellRenderer {

	private static final Color emphasisForeground = UISettings.getInstance()
			.getFileDeletedForeground();

	private static final Color disappearedBackground = UISettings.getInstance()
			.getDisappearedBackground();

	private static final Color movedBackground = UISettings.getInstance()
			.getMovedBackground();

	private static final Color bothBackground = UISettings.getInstance()
			.getBothBackground();

	private final Map<Long, CSVCloneSetInfo> map;

	public DisappearedCloneSetRenderer(final Map<Long, CSVCloneSetInfo> map) {
		this.map = map;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		final long id = (Long) table.getValueAt(row, 0);

		final CSVCloneSetInfo selectedClone = map.get(id);

		if (selectedClone.isInDeletedFile()) {
			setForeground(emphasisForeground);
		} else {
			setForeground(table.getForeground());
		}

		if ((selectedClone != null)
				&& (selectedClone.isContainsDisappear() && (selectedClone
						.isContainsMoved()))) {
			setBackground(bothBackground);
		} else if ((selectedClone != null)
				&& (selectedClone.isContainsDisappear())) {
			setBackground(disappearedBackground);
		} else if ((selectedClone != null) && (selectedClone.isContainsMoved())) {
			setBackground(movedBackground);
		} else {
			setBackground(table.getBackground());
		}

		return this;
	}

}
