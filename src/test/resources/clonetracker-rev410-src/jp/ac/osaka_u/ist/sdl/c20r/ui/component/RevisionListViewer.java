package jp.ac.osaka_u.ist.sdl.c20r.ui.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jp.ac.osaka_u.ist.sdl.c20r.ui.controller.RevisionListViewerController;

public class RevisionListViewer extends JPanel {
	private JTable table;
	private RevisionListViewerController controller;

	/**
	 * Create the panel.
	 */
	public RevisionListViewer() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblListOfRevisions = new JLabel("List of Revisions");
		GridBagConstraints gbc_lblListOfRevisions = new GridBagConstraints();
		gbc_lblListOfRevisions.insets = new Insets(0, 0, 5, 0);
		gbc_lblListOfRevisions.gridx = 0;
		gbc_lblListOfRevisions.gridy = 0;
		add(lblListOfRevisions, gbc_lblListOfRevisions);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		add(scrollPane, gbc_scrollPane);

		table = new JTable();
		DefaultTableModel model = new DefaultTableModel(new Object[][] {},
				new String[] { "Before", "After", "# of disappeared",
						"# of moved" });
		table.setModel(model);
		scrollPane.setViewportView(table);

		controller = new RevisionListViewerController(table, model);
	}

	public RevisionListViewerController getController() {
		return controller;
	}

}
