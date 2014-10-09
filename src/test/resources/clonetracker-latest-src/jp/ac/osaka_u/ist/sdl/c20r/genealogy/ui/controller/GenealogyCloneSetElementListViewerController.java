package jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller;

import java.util.Collection;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.RetrievedBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.listener.GenealogyCloneSetElementSelectionActionListener;
import jp.ac.osaka_u.ist.sdl.c20r.ui.controller.IntegerComparator;

public class GenealogyCloneSetElementListViewerController {

	private final JTable table;

	private DefaultTableModel tableModel;

	private GenealogyCloneSetElementSelectionActionListener previousListener;

	public GenealogyCloneSetElementListViewerController(final JTable table,
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

	public void makeRows(final Collection<RetrievedBlockInfo> blocks,
			final Map<Long, String> files, final String workingDirPath) {
		for (final RetrievedBlockInfo block : blocks) {
			final String path = files.get(block.getFileId());
			final String convertedPath = path
					.substring(workingDirPath.length()).replaceAll("\\\\", "/");
			final int start = block.getStartLine();
			final int end = block.getEndLine();

			Object[] row = new Object[] { convertedPath, start, end };
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

	public void clear() {
		removeAll();
	}

	public void update(final Collection<RetrievedBlockInfo> blocks,
			final Map<Long, String> files, final String workingDirPath) {
		clear();
		makeRows(blocks, files, workingDirPath);
	}

	public JTable getTable() {
		return table;
	}

	public void addListSelectionListener(
			final GenealogyCloneSetElementSelectionActionListener listener) {
		if (previousListener != null) {
			table.getSelectionModel().removeListSelectionListener(
					previousListener);
		}
		table.getSelectionModel().addListSelectionListener(listener);
		this.previousListener = listener;
	}

}
