package jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller;

import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.component.GenealogyCloneSetElementListViewer;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.component.GenealogySourceCodeViewer;

public class GenealogyElementFrameController {

	private final GenealogyCloneSetElementListViewer upCloneSetElementListViewer;

	private final GenealogyCloneSetElementListViewer downCloneSetElementListViewer;

	private final GenealogySourceCodeViewer upCodeViewer;

	private final GenealogySourceCodeViewer downCodeViewer;

	public GenealogyElementFrameController(
			final GenealogyCloneSetElementListViewer upCloneSetElementListViewer,
			final GenealogyCloneSetElementListViewer downCloneSetElementListViewer,
			final GenealogySourceCodeViewer upCodeViewer,
			final GenealogySourceCodeViewer downCodeViewer) {
		this.upCloneSetElementListViewer = upCloneSetElementListViewer;
		this.downCloneSetElementListViewer = downCloneSetElementListViewer;
		this.upCodeViewer = upCodeViewer;
		this.downCodeViewer = downCodeViewer;
	}

	public final GenealogyCloneSetElementListViewerController getUpCloneSetElementListViewerController() {
		return upCloneSetElementListViewer.getController();
	}

	public final GenealogyCloneSetElementListViewerController getDownCloneSetElementListViewerController() {
		return downCloneSetElementListViewer.getController();
	}

	public final GenealogySourceCodeViewerController getUpSourceCodeViewerController() {
		return upCodeViewer.getController();
	}

	public final GenealogySourceCodeViewerController getDownSourceCodeViewerController() {
		return downCodeViewer.getController();
	}

}
