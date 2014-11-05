package jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.component;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.GenealogyListingFrameController;

public class GenealogyListingFrame extends JFrame {

	private JPanel contentPane;

	private GenealogyListingFrameController controller;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GenealogyListingFrame frame = new GenealogyListingFrame();
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
	public GenealogyListingFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JSplitPane splitPane = new JSplitPane();
		contentPane.add(splitPane, BorderLayout.CENTER);

		GenealogyListViewer genealogyListViewer = new GenealogyListViewer();
		splitPane.setLeftComponent(genealogyListViewer);

		JSplitPane splitPane_1 = new JSplitPane();
		splitPane.setRightComponent(splitPane_1);

		GenealogyCloneSetPairListViewer cloneSetPairListViewer = new GenealogyCloneSetPairListViewer();
		splitPane_1.setLeftComponent(cloneSetPairListViewer);

		GenealogyGraphViewer genealogyGraphViewer = new GenealogyGraphViewer();
		splitPane_1.setRightComponent(genealogyGraphViewer);

		controller = new GenealogyListingFrameController(genealogyListViewer,
				cloneSetPairListViewer, genealogyGraphViewer);
	}

	public final GenealogyListingFrameController getController() {
		return controller;
	}

}
