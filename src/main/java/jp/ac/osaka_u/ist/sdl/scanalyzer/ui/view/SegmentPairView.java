package jp.ac.osaka_u.ist.sdl.scanalyzer.ui.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;

/**
 * This is a view for pairs of segments.
 * 
 * @author k-hotta
 *
 */
public class SegmentPairView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6687340593966602087L;
	private SourceCodeView leftSourceCodeView;
	private SourceCodeView rightSourceCodeView;

	/**
	 * Create the panel.
	 */
	public SegmentPairView() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JSplitPane splitPane = new JSplitPane();
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 0;
		add(splitPane, gbc_splitPane);

		leftSourceCodeView = new SourceCodeView();
		splitPane.setLeftComponent(leftSourceCodeView);

		rightSourceCodeView = new SourceCodeView();
		splitPane.setRightComponent(rightSourceCodeView);

	}

	/**
	 * Update the segment in the left side.
	 * 
	 * @param segment
	 *            the segment to be set
	 */
	public void updateLeftSegment(final Segment<?> segment) {
		leftSourceCodeView.segmentChanged(segment);
	}

	/**
	 * Update the segment int the right side.
	 * 
	 * @param segment
	 *            the segment to be set
	 */
	public void updateRightSegment(final Segment<?> segment) {
		rightSourceCodeView.segmentChanged(segment);
	}

}
