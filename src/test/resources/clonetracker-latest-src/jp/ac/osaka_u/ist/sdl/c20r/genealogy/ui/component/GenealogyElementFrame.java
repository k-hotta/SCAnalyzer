package jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.component;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.GenealogyElementFrameController;

public class GenealogyElementFrame extends JFrame {

	private JPanel contentPane;

	private GenealogyElementFrameController controller;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GenealogyElementFrame frame = new GenealogyElementFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GenealogyElementFrame() throws Exception {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JSplitPane splitPane = new JSplitPane();
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 0;
		contentPane.add(splitPane, gbc_splitPane);

		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setLeftComponent(splitPane_1);

		GenealogyCloneSetElementListViewer upGenealogyCloneSetElementListViewer = new GenealogyCloneSetElementListViewer();
		splitPane_1.setLeftComponent(upGenealogyCloneSetElementListViewer);

		GenealogyCloneSetElementListViewer downGenealogyCloneSetElementListViewer = new GenealogyCloneSetElementListViewer();
		splitPane_1.setRightComponent(downGenealogyCloneSetElementListViewer);

		JSplitPane splitPane_2 = new JSplitPane();
		splitPane_2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setRightComponent(splitPane_2);

		GenealogySourceCodeViewer upGenealogySourceCodeViewer = new GenealogySourceCodeViewer();
		splitPane_2.setLeftComponent(upGenealogySourceCodeViewer);

		GenealogySourceCodeViewer downGenealogySourceCodeViewer = new GenealogySourceCodeViewer();
		splitPane_2.setRightComponent(downGenealogySourceCodeViewer);

		controller = new GenealogyElementFrameController(
				upGenealogyCloneSetElementListViewer,
				downGenealogyCloneSetElementListViewer,
				upGenealogySourceCodeViewer, downGenealogySourceCodeViewer);
	}

	public GenealogyElementFrameController getController() {
		return controller;
	}

}
