package jp.ac.osaka_u.ist.sdl.scanalyzer.ui.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.RevisionChangeEvent;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.RevisionChangeEventListener;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.model.CloneClassListViewModel;

public class RevisionView extends JPanel implements RevisionChangeEventListener {

	private static final long serialVersionUID = -6530106056898436057L;

	private CodeFragmentView leftCodeFragmentView;
	private JTabbedPane tabbedPane;
	private JSplitPane outerSplitPane;
	private CodeFragmentView rightCodeFragmentView;

	/**
	 * Create the panel.
	 */
	public RevisionView() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		outerSplitPane = new JSplitPane();
		GridBagConstraints gbc_outerSplitPane = new GridBagConstraints();
		gbc_outerSplitPane.fill = GridBagConstraints.BOTH;
		gbc_outerSplitPane.gridx = 0;
		gbc_outerSplitPane.gridy = 0;
		add(outerSplitPane, gbc_outerSplitPane);

		JSplitPane innerSplitPane = new JSplitPane();
		outerSplitPane.setRightComponent(innerSplitPane);

		leftCodeFragmentView = new CodeFragmentView();
		innerSplitPane.setLeftComponent(leftCodeFragmentView);

		rightCodeFragmentView = new CodeFragmentView();
		innerSplitPane.setRightComponent(rightCodeFragmentView);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		outerSplitPane.setLeftComponent(tabbedPane);
	}

	private CloneClassListView constructCloneClassListView(
			final CloneClass<?> cloneClass) {
		final CloneClassListView result = new CloneClassListView();
		final CloneClassListViewModel model = new CloneClassListViewModel();
		result.setModel(model);

		result.addLeftListener(leftCodeFragmentView);
		result.addRightListener(rightCodeFragmentView);

		result.setCloneClass(cloneClass);

		return result;
	}

	@Override
	public void revisionChanged(RevisionChangeEvent e) {
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		outerSplitPane.setLeftComponent(tabbedPane);

		for (final CloneClass<?> cloneClass : e.getNewCloneClasses()) {
			tabbedPane.add(constructCloneClassListView(cloneClass));
		}
	}

}
