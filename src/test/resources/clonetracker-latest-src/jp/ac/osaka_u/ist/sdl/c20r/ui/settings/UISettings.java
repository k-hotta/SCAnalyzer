package jp.ac.osaka_u.ist.sdl.c20r.ui.settings;

import java.awt.Color;

public class UISettings {

	private static UISettings SINGLETON = null;

	private String repository;

	private final Color fileDeletedForeground = Color.WHITE;

	private final Color disappearedBackground = new Color(255, 100, 100)
			.brighter();

	private final Color movedBackground = new Color(100, 100, 255).brighter();

	private final Color bothBackground = new Color(100, 255, 100).brighter();

	private final Color defaultHighlight = Color.LIGHT_GRAY;

	private final Color disappearedHighlight = new Color(255, 100, 100);

	private final Color movedHighlight = new Color(100, 100, 255);

	private final Color bothHighlight = new Color(100, 255, 100);

	private final Color anotherFileBackground = new Color(220, 220, 220);
	
	private final int lineMargin = 3;

	private UISettings() {
		this.repository = null;
	}

	public static UISettings getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new UISettings();
		}

		return SINGLETON;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public Color getFileDeletedForeground() {
		return fileDeletedForeground;
	}

	public Color getDisappearedBackground() {
		return disappearedBackground;
	}

	public Color getMovedBackground() {
		return movedBackground;
	}

	public Color getBothBackground() {
		return bothBackground;
	}

	public Color getDefaultHighlight() {
		return defaultHighlight;
	}

	public Color getDisappearedHighlight() {
		return disappearedHighlight;
	}

	public Color getMovedHighlight() {
		return movedHighlight;
	}

	public Color getBothHighlight() {
		return bothHighlight;
	}

	public Color getAnotherFileBackground() {
		return anotherFileBackground;
	}
	
	public int getLineMargin() {
		return lineMargin;
	}

}
