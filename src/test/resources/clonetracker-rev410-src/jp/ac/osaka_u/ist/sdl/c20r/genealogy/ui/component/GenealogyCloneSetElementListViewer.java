package jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.component;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JScrollPane;
import java.awt.Insets;
import javax.swing.JTable;

public class GenealogyCloneSetElementListViewer extends JPanel {
	private JTable table;

	/**
	 * Create the panel.
	 */
	public GenealogyCloneSetElementListViewer() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblCloneElements = new JLabel("Clone Elements");
		GridBagConstraints gbc_lblCloneElements = new GridBagConstraints();
		gbc_lblCloneElements.insets = new Insets(0, 0, 5, 0);
		gbc_lblCloneElements.gridx = 0;
		gbc_lblCloneElements.gridy = 0;
		add(lblCloneElements, gbc_lblCloneElements);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		add(scrollPane, gbc_scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);

	}

}
