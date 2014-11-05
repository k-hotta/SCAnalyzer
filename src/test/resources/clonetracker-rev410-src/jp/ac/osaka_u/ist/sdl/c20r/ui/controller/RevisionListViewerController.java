package jp.ac.osaka_u.ist.sdl.c20r.ui.controller;

import java.util.Collection;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import jp.ac.osaka_u.ist.sdl.c20r.ui.data.CSVRevisionInfo;

public class RevisionListViewerController {

	private final JTable table;

	private final DefaultTableModel tableModel;

	public RevisionListViewerController(final JTable table,
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
		sorter.setComparator(0, new IntegerComparator());
		sorter.setComparator(1, new IntegerComparator());
		sorter.setComparator(2, new IntegerComparator());
		sorter.setComparator(3, new IntegerComparator());
		table.setRowSorter(sorter);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public void makeRows(final Collection<CSVRevisionInfo> revisions) {
		for (final CSVRevisionInfo revision : revisions) {
			Integer revisionNum = revision.getRevisionNum();
			Integer nextRevisionNum = revision.getNextRevisionNum();
			Integer disappeared = revision.getDisappeared();
			Integer moved = revision.getMoved();

			Object[] row = new Object[] { revisionNum, nextRevisionNum,
					disappeared, moved };
			tableModel.addRow(row);
		}
	}

	public JTable getTable() {
		return table;
	}

	public void addListSelectionListener(
			final RevisionListSelectionActionListener listener) {
		table.getSelectionModel().addListSelectionListener(listener);
	}

	public void setDefaultRenderer(RevisionRenderer renderer) {
		table.setDefaultRenderer(Object.class, renderer);
	}

}
