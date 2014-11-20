package jp.ac.osaka_u.ist.sdl.scanalyzer.ui.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collection;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.RevisionInternalRepresentation;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.control.CloneGenealogyElementsViewController;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.model.CloneGenealogyElementsViewModel;

public class CloneGenealogyElementsView extends JPanel {

	private static final long serialVersionUID = 2652127458999630838L;

	private static final String[] COLUMNS = new String[] { "ID", "Identifier",
			"# of clones", "# of having ghosts", "# of completely ghosts" };

	private JTable table;
	private RevisionView revisionView;

	/**
	 * The table model
	 */
	@SuppressWarnings("serial")
	private DefaultTableModel tableModel = new DefaultTableModel(
			new Object[][] {}, COLUMNS) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private CloneGenealogyElementsViewController controller;

	private CloneGenealogyElementsViewModel model = new CloneGenealogyElementsViewModel();

	/**
	 * Create the panel.
	 */
	public CloneGenealogyElementsView() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JSplitPane splitPane = new JSplitPane();
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 0;
		add(splitPane, gbc_splitPane);

		JScrollPane scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);

		table = new JTable();
		scrollPane.setViewportView(table);
		table.setModel(tableModel);
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						controller.revisionChanged(e, table);
					}
				});

		revisionView = new RevisionView();
		splitPane.setRightComponent(revisionView);

		controller = new CloneGenealogyElementsViewController(this);

		model.addListener(revisionView);
		controller.setModel(model);

		initializeTable();
	}

	private void initializeTable() {
		JTableHeader header = table.getTableHeader();
		header.setReorderingAllowed(false);
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
		sorter.setComparator(0, (v1, v2) -> Long.compare((Long) v1, (Long) v2));
		sorter.setComparator(1,
				(v1, v2) -> ((String) v1).compareTo((String) v2));
		sorter.setComparator(2,
				(v1, v2) -> Integer.compare((Integer) v1, (Integer) v2));
		sorter.setComparator(3,
				(v1, v2) -> Integer.compare((Integer) v1, (Integer) v2));
		sorter.setComparator(4,
				(v1, v2) -> Integer.compare((Integer) v1, (Integer) v2));
		table.setRowSorter(sorter);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public void makeRows(
			final Collection<RevisionInternalRepresentation> internalRevisions) {
		for (final RevisionInternalRepresentation internalRevision : internalRevisions) {
			final Long id = internalRevision.getId();
			final String identifier = internalRevision.getIdentifier();
			final Integer numOfClones = internalRevision.getNumOfClones();
			final Integer numOfClonesWithGhosts = internalRevision
					.getNumOfClonesWithGhosts();
			final Integer numOfClonesCompletelyGhost = internalRevision
					.getNumOfClonesCompletelyGhost();

			Object[] row = new Object[] { id, identifier, numOfClones,
					numOfClonesWithGhosts, numOfClonesCompletelyGhost };
			tableModel.addRow(row);
		}
	}

	public void setCloneGenealogy(final CloneGenealogy<?> genealogy) {
		controller.setCloneGenealogy(genealogy);
	}

}
