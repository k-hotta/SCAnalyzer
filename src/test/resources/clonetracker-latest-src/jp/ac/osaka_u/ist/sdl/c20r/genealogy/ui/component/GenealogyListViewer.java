package jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.GenealogyListViewerController;

public class GenealogyListViewer extends JPanel {
	private JTable table;
	private GenealogyListViewerController controller;

	/**
	 * Create the panel.
	 */
	public GenealogyListViewer() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblListOfGenealogies = new JLabel("List of Genealogies");
		GridBagConstraints gbc_lblListOfGenealogies = new GridBagConstraints();
		gbc_lblListOfGenealogies.insets = new Insets(0, 0, 5, 0);
		gbc_lblListOfGenealogies.gridx = 0;
		gbc_lblListOfGenealogies.gridy = 0;
		add(lblListOfGenealogies, gbc_lblListOfGenealogies);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		add(scrollPane, gbc_scrollPane);

		table = new JTable();
		DefaultTableModel model = new DefaultTableModel(new Object[][] {},
				new String[] { "ID", "Start", "End", "# of Revisions", "# of Changed",
						"# of Added", "# of Deleted" });
		table.setModel(model);
		scrollPane.setViewportView(table);

		controller = new GenealogyListViewerController(table, model);
	}

	public GenealogyListViewerController getController() {
		return controller;
	}

}
