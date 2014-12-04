package jp.ac.osaka_u.ist.sdl.scanalyzer.ui.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.control.CloneClassListViewController;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.event.CodeFragmentChangeEventListener;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.model.CloneClassListViewModel;

public class CloneClassListView extends JPanel implements MouseListener,
		ActionListener {

	private static final long serialVersionUID = -3596186023235024862L;

	private static final String[] COLUMNS = new String[] { "ID",
			"# of segments" };

	private JTable table;

	private JPopupMenu popupMenu;
	private JMenuItem showLeft;
	private JMenuItem showRight;

	private CloneClassListViewController controller = new CloneClassListViewController();

	private CloneClassListViewModel model;

	/**
	 * The table model
	 */
	@SuppressWarnings("serial")
	private DefaultTableModel tableModel = new DefaultTableModel(
			new Object[][] {}, COLUMNS) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	/**
	 * Create the panel.
	 */
	public CloneClassListView() {
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

		table = new JTable();
		scrollPane.setViewportView(table);
		table.setModel(tableModel);
		table.addMouseListener(this);

		popupMenu = new JPopupMenu();
		showLeft = new JMenuItem("Show in Left");
		showRight = new JMenuItem("Show in Right");
		showLeft.addActionListener(this);
		showRight.addActionListener(this);
		popupMenu.add(showLeft);
		popupMenu.add(showRight);

		initializeTable();
	}

	private void initializeTable() {
		JTableHeader header = table.getTableHeader();
		header.setReorderingAllowed(false);
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
		sorter.setComparator(0, (v1, v2) -> Long.compare((Long) v1, (Long) v2));
		sorter.setComparator(1,
				(v1, v2) -> ((String) v1).compareTo((String) v2));
		table.setRowSorter(sorter);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	private void makeRows(final CloneClass<?> cloneClass) {
		final Collection<CodeFragment<?>> codeFragments = new ArrayList<>();
		codeFragments.addAll(cloneClass.getCodeFragments().values());
		codeFragments.addAll(cloneClass.getGhostFragments().values());
		for (final CodeFragment<?> codeFragment : codeFragments) {
			final Long id = codeFragment.getId();
			final Integer numSegments = codeFragment.getSegments().size();

			Object[] row = new Object[] { id, numSegments };
			tableModel.addRow(row);
		}
	}

	public void removeAll() {
		tableModel = new DefaultTableModel(new Object[][] {}, COLUMNS);
		table.setModel(tableModel);
		initializeTable();
	}

	public void update(final CloneClass<?> cloneClass) {
		removeAll();
		makeRows(cloneClass);
	}

	/**
	 * Set the model.
	 * 
	 * @param model
	 *            the model to be set
	 */
	public void setModel(final CloneClassListViewModel model) {
		this.model = model;
		this.controller.setModel(model);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.isMetaDown()) {
			popupMenu.show(this, e.getX(), e.getY());
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// do nothing
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == showLeft) {
			controller.leftCodeFragmentChanged(table);
		} else if (e.getSource() == showRight) {
			controller.rightCodeFragmentChanged(table);
		}
	}

	/**
	 * Add the given listener to this model as a left-click listener.
	 * 
	 * @param listener
	 *            a listener to be added
	 */
	public void addLeftListener(final CodeFragmentChangeEventListener listener) {
		model.addLeftListener(listener);
	}

	/**
	 * Remove the given listener from the list of left-click listeners of this
	 * model.
	 * 
	 * @param listener
	 *            a listener to be removed
	 */
	public void removeLeftListener(
			final CodeFragmentChangeEventListener listener) {
		model.removeLeftListener(listener);
	}

	/**
	 * Add the given listener to this model as a right-click listener.
	 * 
	 * @param listener
	 *            a listener to be added
	 */
	public void addRightListener(final CodeFragmentChangeEventListener listener) {
		model.addRightListener(listener);
	}

	/**
	 * Remove the given listener from the list of right-click listeners of this
	 * model.
	 * 
	 * @param listener
	 *            a listener to be removed
	 */
	public void removeRightListener(
			final CodeFragmentChangeEventListener listener) {
		model.removeRightListener(listener);
	}

	/**
	 * Set the clone class.
	 * 
	 * @param cloneClass
	 *            the clone class
	 */
	public void setCloneClass(final CloneClass<?> cloneClass) {
		controller.setCloneClass(cloneClass);
		update(cloneClass);
	}

}
