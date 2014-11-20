package jp.ac.osaka_u.ist.sdl.scanalyzer.ui.view;

import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneGenealogy;

public class CloneGenealogyView extends JFrame {

	private static final long serialVersionUID = -657481296943445572L;

	private JPanel contentPane;
	private CloneGenealogyElementsView upperElementsView;
	private CloneGenealogyElementsView lowerElementsView;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CloneGenealogyView frame = new CloneGenealogyView();
					frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
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
	public CloneGenealogyView() {
		setTitle("SCAnalyzer - Genealogy View -");
		try {
			// setting look and feel
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			System.err.println("can't find System's Look&Feel");
		}

		setExtendedState(Frame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 0;
		contentPane.add(splitPane, gbc_splitPane);

		upperElementsView = new CloneGenealogyElementsView();
		splitPane.setLeftComponent(upperElementsView);

		lowerElementsView = new CloneGenealogyElementsView();
		splitPane.setRightComponent(lowerElementsView);
	}

	public void setCloneGenealogy(final CloneGenealogy<?> genealogy) {
		upperElementsView.setCloneGenealogy(genealogy);
		lowerElementsView.setCloneGenealogy(genealogy);
	}

}
