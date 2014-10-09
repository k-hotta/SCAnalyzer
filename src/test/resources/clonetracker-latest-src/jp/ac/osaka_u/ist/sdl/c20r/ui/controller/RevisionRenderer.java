package jp.ac.osaka_u.ist.sdl.c20r.ui.controller;

import java.awt.Color;
import java.awt.Component;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import jp.ac.osaka_u.ist.sdl.c20r.ui.data.CSVRevisionInfo;
import jp.ac.osaka_u.ist.sdl.c20r.ui.settings.UISettings;

public class RevisionRenderer extends DefaultTableCellRenderer {

	private static final Color emphasisForeground = UISettings.getInstance()
			.getFileDeletedForeground();

	private static final Color disappearedBackground = UISettings.getInstance()
			.getDisappearedBackground();

	private static final Color movedBackground = UISettings.getInstance()
			.getMovedBackground();

	private static final Color bothBackground = UISettings.getInstance()
			.getBothBackground();

	private final Map<Integer, CSVRevisionInfo> map;

	public RevisionRenderer(final Map<Integer, CSVRevisionInfo> map) {
		this.map = map;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		final int id = (Integer) table.getValueAt(row, 0);

		final CSVRevisionInfo selectedRevision = map.get(id);

		if (selectedRevision.containsFileDeletion()) {
			setForeground(emphasisForeground);
		} else {
			setForeground(table.getForeground());
		}

		if ((selectedRevision != null)
				&& (selectedRevision.containsDisappeared() && (selectedRevision
						.containsMoved()))) {
			setBackground(bothBackground);
		} else if ((selectedRevision != null)
				&& (selectedRevision.containsDisappeared())) {
			setBackground(disappearedBackground);
		} else if ((selectedRevision != null)
				&& (selectedRevision.containsMoved())) {
			setBackground(movedBackground);
		} else {
			setBackground(table.getBackground());
		}

		if (isSelected) {
			setBackground(getBackground().darker());
		}

		return this;
	}

}
