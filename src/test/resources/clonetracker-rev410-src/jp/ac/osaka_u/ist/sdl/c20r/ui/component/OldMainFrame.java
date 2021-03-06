package jp.ac.osaka_u.ist.sdl.c20r.ui.component;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import jp.ac.osaka_u.ist.sdl.c20r.ui.controller.ControllerManager;

public class OldMainFrame extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OldMainFrame frame = new OldMainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws Exception 
	 */
	public OldMainFrame() throws Exception {
		try {
			// ルックアンドフィールをシステムのものに設定
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			System.err.println("can't find System's Look&Feel");
		}
		
		setTitle("C20R");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JSplitPane splitPane = new JSplitPane();
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 0;
		contentPane.add(splitPane, gbc_splitPane);
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane.setLeftComponent(splitPane_1);
		
		final ControllerManager manager = ControllerManager.getInstance();
		
		CloneSetListViewer cloneSetListViewer = new CloneSetListViewer();
		splitPane_1.setLeftComponent(cloneSetListViewer);
		manager.setListViewerController(cloneSetListViewer.getController());
		
		JSplitPane splitPane_2 = new JSplitPane();
		splitPane_2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_1.setRightComponent(splitPane_2);
		
		CloneSetElementsListViewer cloneSetElementsListViewer = new CloneSetElementsListViewer();
		splitPane_2.setLeftComponent(cloneSetElementsListViewer);
		manager.setUpElementsViewerController(cloneSetElementsListViewer.getController());
		
		CloneSetElementsListViewer cloneSetElementsListViewer_1 = new CloneSetElementsListViewer();
		splitPane_2.setRightComponent(cloneSetElementsListViewer_1);
		manager.setDownElementsViewerController(cloneSetElementsListViewer_1.getController());
		
		SourceCodeViewer sourceCodeViewer = new SourceCodeViewer();
		splitPane.setRightComponent(sourceCodeViewer);
		manager.setCodeViewerController(sourceCodeViewer.getController());
		
		setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

}
