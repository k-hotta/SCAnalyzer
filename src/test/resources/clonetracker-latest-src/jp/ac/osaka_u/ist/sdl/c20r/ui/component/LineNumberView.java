package jp.ac.osaka_u.ist.sdl.c20r.ui.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class LineNumberView extends JComponent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6059957544662191983L;
	
	private final static Color DEFAULT_BACKGROUND = Color.lightGray;
	private final static Color DEFAULT_FOREGROUND = Color.darkGray;
	private final static Font DEFAULT_FONT = new Font("monospaced", Font.PLAIN, 12);
	
	// LineNumber height (abends when I use MAX_VALUE)
	private final static int HEIGHT = Integer.MAX_VALUE - 1000000;
	
	// Set right/left margin
	private final static int MARGIN = 5;
	
	// Line height of this LineNumber component
	private int lineHeight;
	
	// Line height of this LineNumber component
	private int fontLineHeight;
	
	//
	private int currentRowWidth;
	
	// Metrics of this LineNumber component
	private FontMetrics fontMetrics;
	
	/**
	 * Convenience constructor for Text Components
	 */
	public LineNumberView(JComponent component)
	{
		if (component == null)
		{
			setBackground( DEFAULT_BACKGROUND );
			setForeground( DEFAULT_FOREGROUND );
			setFont( DEFAULT_FONT );
		}
		else
		{
			setBackground( DEFAULT_BACKGROUND );
			setForeground( DEFAULT_FOREGROUND );
			setFont( component.getFont() );
		}
		
		setPreferredSize( 9999 );
	}
	
	public void setPreferredSize(int row)
	{
		int width = fontMetrics.stringWidth( String.valueOf(row) );
		
		if (currentRowWidth < width)
		{
			currentRowWidth = width;
			setPreferredSize( new Dimension(2 * MARGIN + width, HEIGHT) );
		}
	}
	
	public void setFont(Font font)
	{
		super.setFont(font);
		fontMetrics = getFontMetrics( getFont() );
		fontLineHeight = fontMetrics.getHeight();
	}
	
	/**
	 * The line height defaults to the line height of the font for this
	 * component. The line height can be overridden by setting it to a
	 * positive non-zero value.
	 */
	public int getLineHeight()
	{
		if (lineHeight == 0)
			return fontLineHeight;
		else
			return lineHeight;
	}
	
	public void setLineHeight(int lineHeight)
	{
		if (lineHeight > 0)
			this.lineHeight = lineHeight;
	}
	
	public int getStartOffset()
	{
		return 4;
	}
	
	public void paintComponent(Graphics g)
	{
		int lineHeight = getLineHeight();
		int startOffset = getStartOffset();
		Rectangle drawHere = g.getClipBounds();
		// System.out.println( drawHere );
		
		// Paint the background
		
		g.setColor( getBackground() );
		g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);
		
		// Determine the number of lines to draw in the foreground.
		
		g.setColor( getForeground() );
		int startLineNumber = (drawHere.y / lineHeight) + 1;
		int endLineNumber = startLineNumber + (drawHere.height / lineHeight);
		
		int start = (drawHere.y / lineHeight) * lineHeight + lineHeight - startOffset + lineHeight / 3;
		
		// System.out.println( startLineNumber + " : " + endLineNumber + " : " + start );
		
		for (int i = startLineNumber; i <= endLineNumber; i++)
		{
			String lineNumber = String.valueOf(i);
			int width = fontMetrics.stringWidth( lineNumber );
			g.drawString(lineNumber, MARGIN + currentRowWidth - width, start);
			start += lineHeight;
		}
		
		setPreferredSize( endLineNumber );
	}
	
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("LineNumberDemo");
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		JPanel panel = new JPanel();
		frame.setContentPane( panel );
		panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		panel.setLayout(new BorderLayout());
		
		JTextPane textPane = new JTextPane();
		
		JScrollPane scrollPane = new JScrollPane(textPane);
		panel.add(scrollPane);
		scrollPane.setPreferredSize(new Dimension(300, 250));
		
		LineNumberView lineNumber = new LineNumberView( textPane );
		lineNumber.setPreferredSize(99999);
		scrollPane.setRowHeaderView( lineNumber );
		
		frame.pack();
		frame.setVisible(true);
	}
}
