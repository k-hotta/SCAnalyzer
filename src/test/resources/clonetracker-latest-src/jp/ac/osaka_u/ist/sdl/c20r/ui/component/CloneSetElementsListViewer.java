package jp.ac.osaka_u.ist.sdl.c20r.ui.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import jp.ac.osaka_u.ist.sdl.c20r.ui.controller.CloneSetElementsViewerController;
import javax.swing.table.DefaultTableModel;

public class CloneSetElementsListViewer extends JPanel {
	private JTable table;
	private CloneSetElementsViewerController controller;

	/**
	 * Create the panel.
	 */
	public CloneSetElementsListViewer() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblElementsInThe = new JLabel("Elements in the Selected Clone Set");
		GridBagConstraints gbc_lblElementsInThe = new GridBagConstraints();
		gbc_lblElementsInThe.insets = new Insets(0, 0, 5, 0);
		gbc_lblElementsInThe.gridx = 0;
		gbc_lblElementsInThe.gridy = 0;
		add(lblElementsInThe, gbc_lblElementsInThe);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		add(scrollPane, gbc_scrollPane);
		
		table = new JTable();
		DefaultTableModel model = new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"Path", "Start Line", "End Line"
				}
			);
		table.setModel(model);
		scrollPane.setViewportView(table);

		controller = new CloneSetElementsViewerController(table, model);
	}

	public CloneSetElementsViewerController getController() {
		return controller;
	}
	
}
