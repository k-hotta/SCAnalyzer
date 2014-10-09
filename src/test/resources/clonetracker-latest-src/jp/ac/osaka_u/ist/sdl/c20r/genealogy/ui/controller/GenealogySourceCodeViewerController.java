package jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.FileNotFoundException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.StyleContext;

import jp.ac.osaka_u.ist.sdl.c20r.ui.controller.LineSeparatorChecker;
import jp.ac.osaka_u.ist.sdl.c20r.ui.repository.SVNRepositoryManager;
import jp.ac.osaka_u.ist.sdl.c20r.ui.settings.UISettings;

public class GenealogySourceCodeViewerController {

	private final JTextArea sourceArea;

	private final JScrollPane sourcePane;

	private final SVNRepositoryManager repoManager;

	private final Color defaultHighlight = UISettings.getInstance()
			.getDefaultHighlight();

	public GenealogySourceCodeViewerController(final JTextArea sourceArea,
			final JScrollPane sourcePane) throws Exception {
		this.sourceArea = sourceArea;
		this.sourcePane = sourcePane;
		this.repoManager = SVNRepositoryManager.getInstance();
		this.sourcePane.setRowHeaderView(new LineNumberView(
				sourceArea));
	}

	public void clear() {
		sourceArea.removeAll();
		sourceArea.setText("");
	}

	public void setSourceCode(final int revisionNum, final String filePath,
			final int startLine, final int endLine) {
		try {
			StyleContext sc = new StyleContext();
			DefaultStyledDocument doc = new DefaultStyledDocument(sc);

			String content = null;
			try {
				content = repoManager.getFileContent(revisionNum, filePath);
			} catch (Exception e) {
				throw new FileNotFoundException();
			}

			sourceArea.setText(content);

			doc.insertString(0, content,
					sc.getStyle(StyleContext.DEFAULT_STYLE));
			sourceArea.setDocument(doc);

			Highlighter highlighter = sourceArea.getHighlighter();
			highlight(content, highlighter, startLine, endLine);

			move(startLine);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void highlight(final String str, final Highlighter highlighter,
			final int start, final int end) {
		highlighter.removeAllHighlights();

		if (start < 0 || end < 0) {
			return;
		}

		Color highlightColor = defaultHighlight;

		Highlighter.HighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(
				highlightColor);

		int indexOfStart = LineSeparatorChecker.getIndexOfLineStart(str, start);
		int indexOfEnd = LineSeparatorChecker.getIndexOfLineEnd(str, end);

		try {
			highlighter
					.addHighlight(indexOfStart, indexOfEnd, highlightPainter);
		} catch (BadLocationException e) {
			// TODO Ž©“®¶¬‚³‚ê‚½ catch ƒuƒƒbƒN
			e.printStackTrace();
		}
	}

	private void move(final int startLine) {
		try {
			final int toBeMovedLine = startLine
					- UISettings.getInstance().getLineMargin();
			if (toBeMovedLine < 1) {
				return;
			}
			Document doc = sourceArea.getDocument();
			Element root = doc.getDefaultRootElement();
			Element elem = root.getElement(toBeMovedLine - 1);
			Rectangle rect = sourceArea.modelToView(elem.getStartOffset());
			Rectangle vr = sourcePane.getViewport().getViewRect();
			rect.setSize(10, vr.height);
			sourceArea.scrollRectToVisible(rect);
			sourceArea.setCaretPosition(elem.getStartOffset());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	class LineNumberView extends JComponent {
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
