package jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller;

import java.util.Collection;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import jp.ac.osaka_u.ist.sdl.c20r.genealogy.RetrievedCloneSetPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.ui.controller.IntegerComparator;
import jp.ac.osaka_u.ist.sdl.c20r.ui.controller.LongComparator;

public class GenealogyCloneSetPairListViewerController {

	private final JTable table;

	private DefaultTableModel tableModel;

	public GenealogyCloneSetPairListViewerController(final JTable table,
			final DefaultTableModel tableModel) {
		this.table = table;
		this.tableModel = tableModel;
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

	public void makeRows(final Collection<RetrievedCloneSetPairInfo> pairs,
			final Map<Long, Integer> revisions) {
		for (final RetrievedCloneSetPairInfo pair : pairs) {
			final Long Id = pair.getId();
			final Integer beforeRev = revisions.get(pair.getBeforeRevId());
			final Integer afterRev = revisions.get(pair.getAfterRevId());

			final Object[] row = new Object[] { Id, beforeRev, afterRev };
			tableModel.addRow(row);
		}
	}

	public void removeAll() {
		DefaultTableModel model = new DefaultTableModel(new Object[][] {},
				new String[] { "ID", "Before Rev", "After Rev" });
		table.setModel(model);
		this.tableModel = model;
		initialize();
	}

	public void update(final Collection<RetrievedCloneSetPairInfo> pairs,
			final Map<Long, Integer> revisions) {
		clear();
		makeRows(pairs, revisions);
	}

	public void clear() {
		removeAll();
	}
	
}
