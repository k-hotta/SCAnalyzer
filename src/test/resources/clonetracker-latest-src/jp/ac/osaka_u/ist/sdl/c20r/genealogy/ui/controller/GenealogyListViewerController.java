package jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller;

import java.util.Collection;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import jp.ac.osaka_u.ist.sdl.c20r.genealogy.RetrievedCloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.listener.GenealogyListSelectionActionListener;
import jp.ac.osaka_u.ist.sdl.c20r.ui.controller.IntegerComparator;
import jp.ac.osaka_u.ist.sdl.c20r.ui.controller.LongComparator;

public class GenealogyListViewerController {

	private final JTable table;

	private final DefaultTableModel tableModel;

	public GenealogyListViewerController(final JTable table,
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
		sorter.setComparator(3, new IntegerComparator());
		sorter.setComparator(4, new IntegerComparator());
		sorter.setComparator(5, new IntegerComparator());
		sorter.setComparator(6, new IntegerComparator());
		table.setRowSorter(sorter);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public void makeRows(
			final Collection<RetrievedCloneGenealogyInfo> genealogies,
			final Map<Long, Integer> revisions) {
		for (final RetrievedCloneGenealogyInfo genealogy : genealogies) {
			final Long id = genealogy.getId();
			final Long beforeRevId = genealogy.getStartRev();
			final Long endRevId = genealogy.getEndRev();
			final Integer revisionCount = (int) (endRevId - beforeRevId + 1);
			final Integer changed = genealogy.getHashChanged();
			final Integer added = genealogy.getAddedRevs();
			final Integer deleted = genealogy.getDeletedRevs()
					- genealogy.getDeletedFileDelRevs();

			Object[] row = new Object[] { id, revisions.get(beforeRevId),
					revisions.get(endRevId), revisionCount, changed, added,
					deleted };
			tableModel.addRow(row);
		}
	}

	public JTable getTable() {
		return table;
	}

	public void addListSelectionListener(
			final GenealogyListSelectionActionListener listener) {
		table.getSelectionModel().addListSelectionListener(listener);
	}

}
