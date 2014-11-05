package jp.ac.osaka_u.ist.sdl.c20r.ui.controller;

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
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
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

import jp.ac.osaka_u.ist.sdl.c20r.ui.repository.SVNRepositoryManager;
import jp.ac.osaka_u.ist.sdl.c20r.ui.settings.UISettings;

public class SourceCodeViewerController {

	private final JSplitPane rootSplitPane;

	private final JSplitPane upSplitPane;

	private final JSplitPane downSplitPane;

	private final JTextArea leftUpSourceArea;

	private final JScrollPane leftUpSourcePane;

	private final JTextArea leftUpCrdArea;

	private final JTextArea leftDownSourceArea;

	private final JScrollPane leftDownSourcePane;

	private final JTextArea leftDownCrdArea;

	private final JTextArea rightUpSourceArea;

	private final JScrollPane rightUpSourcePane;

	private final JTextArea rightUpCrdArea;

	private final JTextArea rightDownSourceArea;

	private final JScrollPane rightDownSourcePane;

	private final JTextArea rightDownCrdArea;

	private final JLabel upCrdSimilairtyLabel;

	private final JLabel downCrdSimilarityLabel;

	private final SVNRepositoryManager repoManager;

	private final int initDividerLocationOfRoot = 800;

	private final Color defaultHighlight = UISettings.getInstance()
			.getDefaultHighlight();

	private final Color disappearedHighlight = UISettings.getInstance()
			.getDisappearedHighlight();

	private final Color movedHighlight = UISettings.getInstance()
			.getMovedHighlight();

	private final Color bothHighlight = UISettings.getInstance()
			.getBothHighlight();

	private final Color anotherFileBackground = UISettings.getInstance()
			.getAnotherFileBackground();

	private final Color defaultBackground;

	public SourceCodeViewerController(final JSplitPane rootSplitPane,
			final JSplitPane upSplitPane, final JSplitPane downSplitPane,
			final JTextArea leftUpSourceArea,
			final JScrollPane leftUpSourcePane, final JTextArea leftUpCrdArea,
			final JTextArea leftDownSourceArea,
			final JScrollPane leftDownSourcePane,
			final JTextArea leftDownCrdArea, final JTextArea rightUpSourceArea,
			final JScrollPane rightUpSourcePane,
			final JTextArea rightUpCrdArea,
			final JTextArea rightDownSourceArea,
			final JScrollPane rightDownSourcePane,
			final JTextArea rightDownCrdArea,
			final JLabel upCrdSimilarityLabel,
			final JLabel downCrdSimilarityLabel) throws Exception {
		this.rootSplitPane = rootSplitPane;
		this.upSplitPane = upSplitPane;
		this.downSplitPane = downSplitPane;
		this.leftUpSourceArea = leftUpSourceArea;
		this.leftUpSourcePane = leftUpSourcePane;
		this.leftUpCrdArea = leftUpCrdArea;
		this.leftUpSourcePane.setRowHeaderView(new LineNumberView(
				leftUpSourceArea));
		this.leftDownSourceArea = leftDownSourceArea;
		this.leftDownSourcePane = leftDownSourcePane;
		this.leftDownCrdArea = leftDownCrdArea;
		this.leftDownSourcePane.setRowHeaderView(new LineNumberView(
				leftDownSourceArea));
		this.rightUpSourceArea = rightUpSourceArea;
		this.rightUpSourcePane = rightUpSourcePane;
		this.rightUpCrdArea = rightUpCrdArea;
		this.rightUpSourcePane.setRowHeaderView(new LineNumberView(
				rightUpSourceArea));
		this.rightDownSourceArea = rightDownSourceArea;
		this.rightDownSourcePane = rightDownSourcePane;
		this.rightDownCrdArea = rightDownCrdArea;
		this.rightDownSourcePane.setRowHeaderView(new LineNumberView(
				rightDownSourceArea));
		this.repoManager = SVNRepositoryManager.getInstance();
		this.rootSplitPane.setDividerLocation(initDividerLocationOfRoot);
		this.defaultBackground = leftUpSourceArea.getBackground();
		this.upCrdSimilairtyLabel = upCrdSimilarityLabel;
		this.downCrdSimilarityLabel = downCrdSimilarityLabel;
	}

	public void clear() {
		clearSourceArea(PanelDirection.LEFT_UP);
		clearSourceArea(PanelDirection.LEFT_DOWN);
		clearSourceArea(PanelDirection.RIGHT_UP);
		clearSourceArea(PanelDirection.RIGHT_DOWN);

		clearCrdArea(PanelDirection.LEFT_UP);
		clearCrdArea(PanelDirection.LEFT_DOWN);
		clearCrdArea(PanelDirection.RIGHT_UP);
		clearCrdArea(PanelDirection.RIGHT_DOWN);

		clearLabel(getCrdLabel(PanelDirection.RIGHT_UP));
		clearLabel(getCrdLabel(PanelDirection.RIGHT_DOWN));
	}

	private void clearSourceArea(final PanelDirection direction) {
		clearTextArea(getSourceArea(direction));
	}

	private void clearCrdArea(final PanelDirection direction) {
		clearTextArea(getCrdArea(direction));
	}

	private void clearTextArea(final JTextArea textArea) {
		textArea.removeAll();
		textArea.setBackground(defaultBackground);
		textArea.setText("");
		textArea.setToolTipText(null);
	}

	private void clearLabel(final JLabel label) {
		label.setText("");
	}

	public void setSourceCode(final PanelDirection direction,
			final int revisionNum, final String filePath, final int startLine,
			final int endLine, final boolean isDisappear,
			final boolean isMoved, final boolean isAfter,
			final boolean isAnotherFile) {

		final JTextArea target = getSourceArea(direction);
		target.removeAll();

		try {
			StyleContext sc = new StyleContext();
			DefaultStyledDocument doc = new DefaultStyledDocument(sc);

			String content = null;
			try {
				content = repoManager.getFileContent(revisionNum, filePath);
			} catch (Exception e) {
				throw new FileNotFoundException();
			}

			target.setText(content);

			doc.insertString(0, content,
					sc.getStyle(StyleContext.DEFAULT_STYLE));
			target.setDocument(doc);

			Highlighter highlighter = target.getHighlighter();
			highlight(content, highlighter, startLine, endLine, isDisappear,
					isMoved, isAfter);

			if (isAfter && isAnotherFile) {
				target.setBackground(anotherFileBackground);
			} else {
				target.setBackground(defaultBackground);
			}

			move(startLine, target, getSourceScrollPane(direction));

		} catch (FileNotFoundException fe) {
			if (isAfter) {
				// fe.printStackTrace();
				target.setText("");
			} else {
				fe.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void highlight(String str, Highlighter highlighter, int start,
			int end, boolean isDisappear, boolean isMoved, boolean isAfter) {
		highlighter.removeAllHighlights();

		if (start < 0 || end < 0) {
			return;
		}

		Color highlightColor = defaultHighlight;

		if (isDisappear && isMoved) {
			highlightColor = bothHighlight;
		} else if (isDisappear) {
			highlightColor = disappearedHighlight;
		} else if (isMoved) {
			highlightColor = movedHighlight;
		}

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

	public JTextArea getSourceArea(final PanelDirection direction) {
		if (direction == PanelDirection.LEFT_UP) {
			return leftUpSourceArea;
		} else if (direction == PanelDirection.LEFT_DOWN) {
			return leftDownSourceArea;
		} else if (direction == PanelDirection.RIGHT_UP) {
			return rightUpSourceArea;
		} else if (direction == PanelDirection.RIGHT_DOWN) {
			return rightDownSourceArea;
		} else {
			assert false;
			return null;
		}
	}

	public JScrollPane getSourceScrollPane(final PanelDirection direction) {
		if (direction == PanelDirection.LEFT_UP) {
			return leftUpSourcePane;
		} else if (direction == PanelDirection.LEFT_DOWN) {
			return leftDownSourcePane;
		} else if (direction == PanelDirection.RIGHT_UP) {
			return rightUpSourcePane;
		} else if (direction == PanelDirection.RIGHT_DOWN) {
			return rightDownSourcePane;
		} else {
			assert false;
			return null;
		}
	}

	public JTextArea getCrdArea(final PanelDirection direction) {
		if (direction == PanelDirection.LEFT_UP) {
			return leftUpCrdArea;
		} else if (direction == PanelDirection.LEFT_DOWN) {
			return leftDownCrdArea;
		} else if (direction == PanelDirection.RIGHT_UP) {
			return rightUpCrdArea;
		} else if (direction == PanelDirection.RIGHT_DOWN) {
			return rightDownCrdArea;
		} else {
			assert false;
			return null;
		}
	}

	public JLabel getCrdLabel(final PanelDirection direction) {
		if (direction == PanelDirection.RIGHT_UP) {
			return upCrdSimilairtyLabel;
		} else if (direction == PanelDirection.RIGHT_DOWN) {
			return downCrdSimilarityLabel;
		} else {
			return null;
		}
	}

	private void move(final int startLine, final JTextArea textArea,
			final JScrollPane scroll) {
		try {
			final int toBeMovedLine = startLine
					- UISettings.getInstance().getLineMargin();
			if (toBeMovedLine < 1) {
				return;
			}
			Document doc = textArea.getDocument();
			Element root = doc.getDefaultRootElement();
			Element elem = root.getElement(toBeMovedLine - 1);
			Rectangle rect = textArea.modelToView(elem.getStartOffset());
			Rectangle vr = scroll.getViewport().getViewRect();
			rect.setSize(10, vr.height);
			textArea.scrollRectToVisible(rect);
			textArea.setCaretPosition(elem.getStartOffset());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCrd(final PanelDirection direction, final String crd,
			final boolean isAfter, final boolean isAnotherFile, final int ld,
			final double similarity) {
		if (crd.equals("N/A")) {
			return;
		}
		final JTextArea target = getCrdArea(direction);
		target.removeAll();
		if (isAfter && isAnotherFile) {
			target.setBackground(anotherFileBackground);
		} else {
			target.setBackground(defaultBackground);
		}
		if (isAfter) {
			getCrdLabel(direction).setText(
					"ld = " + ld + " :: similarity = " + similarity);
		}
		target.setText(crd);
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
