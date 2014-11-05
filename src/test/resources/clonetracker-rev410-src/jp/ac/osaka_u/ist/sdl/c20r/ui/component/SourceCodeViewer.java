package jp.ac.osaka_u.ist.sdl.c20r.ui.component;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import jp.ac.osaka_u.ist.sdl.c20r.ui.controller.SourceCodeViewerController;
import javax.swing.JLabel;

public class SourceCodeViewer extends JPanel {

	private SourceCodeViewerController controller;

	/**
	 * Create the panel.
	 * 
	 * @throws Exception
	 */
	public SourceCodeViewer() throws Exception {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JSplitPane rootSplitPane = new JSplitPane();
		rootSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		GridBagConstraints gbc_rootSplitPane = new GridBagConstraints();
		gbc_rootSplitPane.fill = GridBagConstraints.BOTH;
		gbc_rootSplitPane.gridx = 0;
		gbc_rootSplitPane.gridy = 0;
		add(rootSplitPane, gbc_rootSplitPane);

		JSplitPane upSplitPane = new JSplitPane();
		rootSplitPane.setLeftComponent(upSplitPane);

		JSplitPane leftUpSplitPane = new JSplitPane();
		leftUpSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		upSplitPane.setLeftComponent(leftUpSplitPane);

		JScrollPane leftUpSourcePane = new JScrollPane();
		leftUpSplitPane.setLeftComponent(leftUpSourcePane);

		JTextArea leftUpSourceArea = new JTextArea();
		leftUpSourcePane.setViewportView(leftUpSourceArea);

		JScrollPane leftUpCrdPane = new JScrollPane();
		leftUpSplitPane.setRightComponent(leftUpCrdPane);

		JTextArea leftUpCrdArea = new JTextArea();
		leftUpCrdPane.setViewportView(leftUpCrdArea);

		JSplitPane rightUpSplitPane = new JSplitPane();
		rightUpSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		upSplitPane.setRightComponent(rightUpSplitPane);

		JScrollPane rightUpSourcePane = new JScrollPane();
		rightUpSplitPane.setLeftComponent(rightUpSourcePane);

		JTextArea rightUpSourceArea = new JTextArea();
		rightUpSourcePane.setViewportView(rightUpSourceArea);

		JScrollPane rightUpCrdPane = new JScrollPane();
		rightUpSplitPane.setRightComponent(rightUpCrdPane);

		JTextArea rightUpCrdArea = new JTextArea();
		rightUpCrdPane.setViewportView(rightUpCrdArea);

		JSplitPane downSplitPane = new JSplitPane();
		rootSplitPane.setRightComponent(downSplitPane);

		JSplitPane leftDownSplitPane = new JSplitPane();
		leftDownSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		downSplitPane.setLeftComponent(leftDownSplitPane);

		JScrollPane leftDownSourcePane = new JScrollPane();
		leftDownSplitPane.setLeftComponent(leftDownSourcePane);

		JTextArea leftDownSourceArea = new JTextArea();
		leftDownSourcePane.setViewportView(leftDownSourceArea);

		JScrollPane leftDownCrdPane = new JScrollPane();
		leftDownSplitPane.setRightComponent(leftDownCrdPane);

		JTextArea leftDownCrdArea = new JTextArea();
		leftDownCrdPane.setViewportView(leftDownCrdArea);

		JSplitPane rightDownSplit = new JSplitPane();
		rightDownSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
		downSplitPane.setRightComponent(rightDownSplit);

		JScrollPane rightDownSourcePane = new JScrollPane();
		rightDownSplit.setLeftComponent(rightDownSourcePane);

		JTextArea rightDownSourceArea = new JTextArea();
		rightDownSourcePane.setViewportView(rightDownSourceArea);

		JScrollPane rightDownCrdPane = new JScrollPane();
		rightDownSplit.setRightComponent(rightDownCrdPane);

		JTextArea rightDownCrdArea = new JTextArea();
		rightDownCrdPane.setViewportView(rightDownCrdArea);

		JLabel upCrdSimilarityLabel = new JLabel("");
		rightUpCrdPane.setColumnHeaderView(upCrdSimilarityLabel);

		JLabel downCrdSimilarityLabel = new JLabel("");
		rightDownCrdPane.setColumnHeaderView(downCrdSimilarityLabel);

		this.controller = new SourceCodeViewerController(rootSplitPane,
				upSplitPane, downSplitPane, leftUpSourceArea, leftUpSourcePane,
				leftUpCrdArea, leftDownSourceArea, leftDownSourcePane,
				leftDownCrdArea, rightUpSourceArea, rightUpSourcePane,
				rightUpCrdArea, rightDownSourceArea, rightDownSourcePane,
				rightDownCrdArea, upCrdSimilarityLabel, downCrdSimilarityLabel);
	}

	public SourceCodeViewerController getController() {
		return controller;
	}

}
