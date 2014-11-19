package jp.ac.osaka_u.ist.sdl.scanalyzer.ui.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.StyleContext;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.SegmentChangeEvent;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.SegmentChangeEventListener;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.UIConstants;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.control.SourceCodeViewController;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.model.SourceCodeViewModel;

/**
 * This is a view for source code of selected code fragments.
 * 
 * @author k-hotta
 *
 */
public class SourceCodeView extends JPanel implements
		SegmentChangeEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8522368594919051632L;

	/**
	 * The model
	 */
	private SourceCodeViewModel model;

	/**
	 * The controller
	 */
	private final SourceCodeViewController controller = new SourceCodeViewController();

	private JTextArea textArea;
	private JLabel pathLabel;
	private JScrollPane scrollPane;

	/**
	 * Create the panel.
	 */
	public SourceCodeView() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		add(scrollPane, gbc_scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		pathLabel = new JLabel("");
		scrollPane.setColumnHeaderView(pathLabel);

		scrollPane.setRowHeaderView(new LineNumberView(textArea));
	}

	/**
	 * Set the model.
	 * 
	 * @param model
	 *            the model to be set
	 */
	public void setModel(final SourceCodeViewModel model) {
		if (this.model != null) {
			model.removeListener(this);
		}

		if (model != null) {
			this.model = model;
			model.addListener(this);
			controller.setModel(model);
		}
	}

	/**
	 * Notify the change of segments to the controller.
	 * 
	 * @param segment
	 *            the new segment
	 */
	public void notifySegmentChange(final Segment<?> segment) {
		controller.segmentChanged(segment);
	}
	
	@Override
	public void segmentChanged(SegmentChangeEvent e) {
		if (!(e.getSource() instanceof SourceCodeViewModel)) {
			return;
		}

		final SourceCodeViewModel model = (SourceCodeViewModel) e.getSource();
		setSourceCode(model.getFileContent(), model.getStartOffset(),
				model.getEndOffset(), model.getStartLine());
		setPath(model.getPath());
	}

	/**
	 * Set the source code to the panel.s
	 * 
	 * @param content
	 * @param startOffset
	 * @param endOffset
	 * @param startLine
	 */
	private void setSourceCode(final String content, final int startOffset,
			final int endOffset, final int startLine) {
		textArea.removeAll();

		if (content == null) {
			return;
		}

		try {
			StyleContext sc = new StyleContext();
			DefaultStyledDocument doc = new DefaultStyledDocument(sc);

			textArea.setText(content);

			doc.insertString(0, content,
					sc.getStyle(StyleContext.DEFAULT_STYLE));
			textArea.setDocument(doc);

			Highlighter highlighter = textArea.getHighlighter();
			highlight(highlighter, content, startOffset, endOffset);

			move(startLine);

		} catch (Exception e) {
			throw new IllegalStateException("cannot set source code", e);
		}
	}

	/**
	 * Add highlight at the given location.
	 * 
	 * @param highlighter
	 * @param src
	 * @param start
	 * @param end
	 * @throws Exception
	 */
	private void highlight(final Highlighter highlighter, final String src,
			final int start, final int end) throws Exception {
		highlighter.removeAllHighlights();

		if (start < 0 || end < 0) {
			return;
		}

		Highlighter.HighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(
				UIConstants.CLONED_SEGMENT_HIGHLIGHT_COLOR);

		highlighter.addHighlight(start, end, highlightPainter);
	}

	/**
	 * Move cursor to the specified line.
	 * 
	 * @param startLine
	 * @throws Exception
	 */
	private void move(final int startLine) throws Exception {
		final int toBeMovedLine = startLine - UIConstants.LINE_MERGIN;
		if (toBeMovedLine < -1) {
			return;
		}
		Document doc = textArea.getDocument();
		Element root = doc.getDefaultRootElement();
		Element elem = root.getElement(toBeMovedLine - 1);
		Rectangle rect = textArea.modelToView(elem.getStartOffset());
		Rectangle vr = scrollPane.getViewport().getViewRect();
		rect.setSize(10, vr.height);
		textArea.scrollRectToVisible(rect);
		textArea.setCaretPosition(elem.getStartOffset());
	}

	/**
	 * Set the path to the label
	 * 
	 * @param path
	 */
	private void setPath(final String path) {
		if (path == null) {
			pathLabel.setText("");
		} else {
			pathLabel.setText(path);
		}
	}

	class LineNumberView extends JComponent {
		private static final long serialVersionUID = 7954227385146163422L;
		private static final int MARGIN = 5;
		private final JTextArea text;
		private final FontMetrics fontMetrics;
		private final int topInset;
		private final int fontAscent;
		private final int fontHeight;

		public LineNumberView(JTextArea textArea) {
			text = textArea;
			Font font = text.getFont();
			fontMetrics = getFontMetrics(font);
			fontHeight = fontMetrics.getHeight();
			fontAscent = fontMetrics.getAscent();
			topInset = text.getInsets().top;
			text.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void insertUpdate(DocumentEvent e) {
					repaint();
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					repaint();
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
				}
			});
			text.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					revalidate();
					repaint();
				}
			});
			setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
			setOpaque(true);
			setBackground(Color.WHITE);
		}

		private int getComponentWidth() {
			Document doc = text.getDocument();
			Element root = doc.getDefaultRootElement();
			int lineCount = root.getElementIndex(doc.getLength());
			int maxDigits = Math.max(3, String.valueOf(lineCount).length());
			return maxDigits * fontMetrics.stringWidth("0") + MARGIN * 2;
		}

		public int getLineAtPoint(int y) {
			Element root = text.getDocument().getDefaultRootElement();
			int pos = text.viewToModel(new Point(0, y));
			return root.getElementIndex(pos);
		}

		public Dimension getPreferredSize() {
			return new Dimension(getComponentWidth(), text.getHeight());
		}

		@Override
		public void paintComponent(Graphics g) {
			Rectangle clip = g.getClipBounds();
			g.setColor(getBackground());
			g.fillRect(clip.x, clip.y, clip.width, clip.height);
			g.setColor(getForeground());
			int base = clip.y - topInset;
			int start = getLineAtPoint(base);
			int end = getLineAtPoint(base + clip.height);
			int y = topInset - fontHeight + fontAscent + start * fontHeight;
			for (int i = start; i <= end; i++) {
				String text = String.valueOf(i + 1);
				int x = getComponentWidth() - MARGIN
						- fontMetrics.stringWidth(text);
				y = y + fontHeight;
				g.drawString(text, x, y);
			}
		}
	}

}
