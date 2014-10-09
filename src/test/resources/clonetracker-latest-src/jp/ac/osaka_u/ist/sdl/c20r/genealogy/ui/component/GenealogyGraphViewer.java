package jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.GenealogyGraphViewerController;

public class GenealogyGraphViewer extends JPanel {

	private final GenealogyGraphViewerController controller;

	/**
	 * Create the panel.
	 */
	public GenealogyGraphViewer() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		add(scrollPane, gbc_scrollPane);

		JLabel label = new JLabel("");
		scrollPane.setViewportView(label);

		this.controller = new GenealogyGraphViewerController(label);
	}

	public final GenealogyGraphViewerController getController() {
		return controller;
	}

}
