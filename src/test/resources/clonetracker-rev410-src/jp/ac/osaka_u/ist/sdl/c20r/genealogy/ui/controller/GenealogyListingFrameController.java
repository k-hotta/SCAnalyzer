package jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller;

import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.component.GenealogyCloneSetPairListViewer;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.component.GenealogyGraphViewer;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.component.GenealogyListViewer;

public class GenealogyListingFrameController {

	private final GenealogyListViewer genealogyListViewer;

	private final GenealogyCloneSetPairListViewer cloneSetPairListViewer;

	private final GenealogyGraphViewer graphViewer;

	public GenealogyListingFrameController(
			final GenealogyListViewer genealogyListViewer,
			final GenealogyCloneSetPairListViewer cloneSetPairListViewer,
			final GenealogyGraphViewer graphViewer) {
		this.genealogyListViewer = genealogyListViewer;
		this.cloneSetPairListViewer = cloneSetPairListViewer;
		this.graphViewer = graphViewer;
	}

	public final GenealogyListViewerController getGenealogyListViewerController() {
		return this.genealogyListViewer.getController();
	}

	public final GenealogyCloneSetPairListViewerController getGenealogyCloneSetPairListViewerController() {
		return this.cloneSetPairListViewer.getController();
	}

}
