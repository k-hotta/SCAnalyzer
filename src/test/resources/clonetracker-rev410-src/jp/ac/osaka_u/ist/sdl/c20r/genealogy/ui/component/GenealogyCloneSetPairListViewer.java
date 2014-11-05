package jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.GenealogyCloneSetPairListViewerController;

public class GenealogyCloneSetPairListViewer extends JPanel {
	private JTable table;

	private GenealogyCloneSetPairListViewerController controller;

	/**
	 * Create the panel.
	 */
	public GenealogyCloneSetPairListViewer() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblListOfClone = new JLabel("List of Clone Set Pairs");
		GridBagConstraints gbc_lblListOfClone = new GridBagConstraints();
		gbc_lblListOfClone.insets = new Insets(0, 0, 5, 0);
		gbc_lblListOfClone.gridx = 0;
		gbc_lblListOfClone.gridy = 0;
		add(lblListOfClone, gbc_lblListOfClone);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		add(scrollPane, gbc_scrollPane);

		table = new JTable();
		DefaultTableModel model = new DefaultTableModel(new Object[][] {},
				new String[] { "ID", "Before Rev", "After Rev" });
		table.setModel(model);
		scrollPane.setViewportView(table);

		controller = new GenealogyCloneSetPairListViewerController(table, model);
	}

	public final GenealogyCloneSetPairListViewerController getController() {
		return controller;
	}

}
