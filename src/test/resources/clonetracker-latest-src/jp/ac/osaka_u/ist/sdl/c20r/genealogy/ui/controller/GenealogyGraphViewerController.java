package jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class GenealogyGraphViewerController {

	private final JLabel label;

	public GenealogyGraphViewerController(final JLabel label) {
		this.label = label;
	}

	/**
	 * �O���t���Z�b�g����
	 * 
	 * @param graphFilePath
	 *            �@�O���t�̉摜�t�@�C���̐�΃p�X
	 */
	public void setGraph(final String graphFilePath) {
		ImageIcon icon = new ImageIcon(graphFilePath);
		
		label.removeAll();
		label.setIcon(icon);
	}

	public void clear() {
		label.removeAll();
		label.setText("");
	}

	public void update(final String graphFilePath) {
		//clear();
		setGraph(graphFilePath);
	}

}
