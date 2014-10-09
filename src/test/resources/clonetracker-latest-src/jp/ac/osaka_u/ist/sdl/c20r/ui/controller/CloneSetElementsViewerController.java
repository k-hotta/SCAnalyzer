package jp.ac.osaka_u.ist.sdl.c20r.ui.controller;

import java.util.Collection;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import jp.ac.osaka_u.ist.sdl.c20r.ui.data.CSVCloneElementInfo;

public class CloneSetElementsViewerController {

	private final JTable table;

	private DefaultTableModel tableModel;

	private CloneSetElementSelectionActionListener previousListener;

	public CloneSetElementsViewerController(final JTable table,
			final DefaultTableModel tableModel) {
		this.table = table;
		this.tableModel = tableModel;
		initialize();
	}

	private void initialize() {
		JTableHeader header = table.getTableHeader();
		header.setReorderingAllowed(false);
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(
				tableModel);
		sorter.setComparator(1, new IntegerComparator());
		sorter.setComparator(2, new IntegerComparator());
		table.setRowSorter(sorter);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public void makeRows(final Collection<CSVCloneElementInfo> elements) {
		for (final CSVCloneElementInfo element : elements) {
			final String path = element.getPath();
			final int start = element.getStartLine();
			final int end = element.getEndLine();

			Object[] row = new Object[] { path, start, end };
			tableModel.addRow(row);
		}
	}

	public void removeAll() {
		DefaultTableModel model = new DefaultTableModel(new Object[][] {},
				new String[] { "Path", "Start Line", "End Line" });
		table.setModel(model);
		this.tableModel = model;
		initialize();
	}

	public void setDefaultRenderer(DisappearedCloneElementRenderer renderer) {
		table.setDefaultRenderer(Object.class, renderer);
	}

	public void update(final Collection<CSVCloneElementInfo> elements) {
		removeAll();
		makeRows(elements);
	}

	public void addListSelectionListener(
			CloneSetElementSelectionActionListener listener) {
		if (previousListener != null) {
			table.getSelectionModel().removeListSelectionListener(
					previousListener);
		}
		table.getSelectionModel().addListSelectionListener(listener);
		this.previousListener = listener;
	}

	public JTable getTable() {
		return table;
	}

}
