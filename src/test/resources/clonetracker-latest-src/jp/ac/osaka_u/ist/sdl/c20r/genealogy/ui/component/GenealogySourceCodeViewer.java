package jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.GenealogySourceCodeViewerController;

public class GenealogySourceCodeViewer extends JPanel {

	private GenealogySourceCodeViewerController controller;

	/**
	 * Create the panel.
	 */
	public GenealogySourceCodeViewer() throws Exception {
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

		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		this.controller = new GenealogySourceCodeViewerController(textArea,
				scrollPane);
	}

	public GenealogySourceCodeViewerController getController() {
		return controller;
	}

}
