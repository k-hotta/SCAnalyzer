package jp.ac.osaka_u.ist.sdl.scanalyzer.ui.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collection;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.control.CodeFragmentViewController;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.event.CloneClassChangeEvent;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.event.CloneClassChangeEventListener;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.event.CodeFragmentChangeEvent;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.event.CodeFragmentChangeEventListener;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.event.SegmentChangeEvent;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.event.SegmentChangeEventListener;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.model.CodeFragmentViewModel;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.model.SourceCodeViewModel;

/**
 * This is the view for code fragment.
 * 
 * @author k-hotta
 *
 */
public class CodeFragmentView extends JPanel implements
		SegmentChangeEventListener, CodeFragmentChangeEventListener,
		CloneClassChangeEventListener {

	private static final long serialVersionUID = -7327964619975311711L;

	private static final String[] COLUMNS = new String[] { "ID", "Path",
			"Start", "End" };

	/**
	 * The model
	 */
	private CodeFragmentViewModel model;

	/**
	 * The controller
	 */
	private CodeFragmentViewController controller = new CodeFragmentViewController(
			this);

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

	private JTable table;
	private SourceCodeView sourceCodeView;

	/**
	 * Create the panel.
	 */
	public CodeFragmentView() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 0;
		add(splitPane, gbc_splitPane);

		JScrollPane scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);

		table = new JTable();
		scrollPane.setViewportView(table);
		table.setModel(tableModel);

		sourceCodeView = new SourceCodeView();
		splitPane.setRightComponent(sourceCodeView);

		SourceCodeViewModel sourceCodeViewModel = new SourceCodeViewModel();
		sourceCodeView.setModel(sourceCodeViewModel);

		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						controller.segmentChanged(e, table);
					}
				});

		initializeTable();
	}

	private void initializeTable() {
		JTableHeader header = table.getTableHeader();
		header.setReorderingAllowed(false);
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
		sorter.setComparator(0, (v1, v2) -> Long.compare((Long) v1, (Long) v2));
		sorter.setComparator(1,
				(v1, v2) -> ((String) v1).compareTo((String) v2));
		sorter.setComparator(2,
				(v1, v2) -> Integer.compare((Integer) v1, (Integer) v2));
		sorter.setComparator(3,
				(v1, v2) -> Integer.compare((Integer) v1, (Integer) v2));
		table.setRowSorter(sorter);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public void makeRows(final Collection<? extends Segment<?>> segments) {
		for (final Segment<?> segment : segments) {
			final Long id = segment.getId();
			final String path = segment.getSourceFile().getPath();
			final Integer start = segment.getFirstElement().getLine();
			final Integer end = segment.getLastElement().getLine();

			Object[] row = new Object[] { id, path, start, end };
			tableModel.addRow(row);
		}
	}

	public void removeAll() {
		tableModel = new DefaultTableModel(new Object[][] {}, COLUMNS);
		table.setModel(tableModel);
		initializeTable();
	}

	public void update(final Collection<? extends Segment<?>> segments) {
		removeAll();
		makeRows(segments);
	}

	/**
	 * Set the model.
	 * 
	 * @param model
	 *            the model to be set
	 */
	public void setModel(final CodeFragmentViewModel model) {
		if (this.model != null) {
			this.model.removeListener(this);
		}

		if (model != null) {
			this.model = model;
			model.addListener(this);
			controller.setModel(model);
		}
	}

	@Override
	public void segmentChanged(SegmentChangeEvent e) {
		final Segment<?> segment = e.getNewSegment();

		sourceCodeView.notifySegmentChange(segment);
	}

	@Override
	public void fragmentChanged(CodeFragmentChangeEvent e) {
		controller.fragmentChanged(e.getNewCodeFragment());
		removeAll();
	}

	@Override
	public void cloneClassChanged(CloneClassChangeEvent e) {
		controller.cloneClassChanged();
	}

}
