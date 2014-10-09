package jp.ac.osaka_u.ist.sdl.c20r.ui.controller;

import java.util.Collection;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import jp.ac.osaka_u.ist.sdl.c20r.ui.data.CSVCloneSetInfo;

public class CloneSetListViewerController {

	private final JTable table;

	private DefaultTableModel tableModel;

	private CloneSetListSelectionActionListener previousListener = null;

	public CloneSetListViewerController(final JTable table,
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
		sorter.setComparator(0, new LongComparator());
		sorter.setComparator(1, new IntegerComparator());
		sorter.setComparator(2, new IntegerComparator());
		table.setRowSorter(sorter);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public void setDefaultRenderer(DisappearedCloneSetRenderer renderer) {
		table.setDefaultRenderer(Object.class, renderer);
	}

	public void makeRows(final Collection<CSVCloneSetInfo> cloneSets) {
		for (final CSVCloneSetInfo cloneSet : cloneSets) {
			Long ID = cloneSet.getId();
			Integer elementsCount = cloneSet.getCount();
			Integer movedCount = cloneSet.getMovedCount();

			Object[] row = new Object[] { ID, elementsCount, movedCount };
			tableModel.addRow(row);
		}
	}

	public void addListSelectionListener(
			CloneSetListSelectionActionListener listener) {
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

	public void removeAll() {
		DefaultTableModel model = new DefaultTableModel(new Object[][] {},
				new String[] { "ID", "# of elements", "# of moved" });
		table.setModel(model);
		this.tableModel = model;
		initialize();
	}

	public void update(final Collection<CSVCloneSetInfo> cloneSets) {
		removeAll();
		makeRows(cloneSets);
	}

}
