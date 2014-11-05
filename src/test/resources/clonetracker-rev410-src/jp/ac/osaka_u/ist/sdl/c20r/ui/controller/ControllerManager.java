package jp.ac.osaka_u.ist.sdl.c20r.ui.controller;

public class ControllerManager {

	private static ControllerManager SINGLETON = null;

	private RevisionListViewerController revisionViewerController;

	private CloneSetListViewerController listViewerController;

	private CloneSetElementsViewerController upElementsViewerController;

	private CloneSetElementsViewerController downElementsViewerController;

	private SourceCodeViewerController codeViewerController;

	private ControllerManager() {

	}

	public static ControllerManager getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new ControllerManager();
		}

		return SINGLETON;
	}

	public RevisionListViewerController getRevisionViewerController() {
		return revisionViewerController;
	}

	public void setRevisionListViewerController(
			final RevisionListViewerController revisionViewerController) {
		this.revisionViewerController = revisionViewerController;
	}

	public CloneSetListViewerController getListViewerController() {
		return listViewerController;
	}

	public void setListViewerController(
			CloneSetListViewerController listViewerController) {
		this.listViewerController = listViewerController;
	}

	public CloneSetElementsViewerController getUpElementsViewerController() {
		return upElementsViewerController;
	}

	public void setUpElementsViewerController(
			CloneSetElementsViewerController upElementsViewerController) {
		this.upElementsViewerController = upElementsViewerController;
	}

	public CloneSetElementsViewerController getDownElementsViewerController() {
		return downElementsViewerController;
	}

	public void setDownElementsViewerController(
			CloneSetElementsViewerController downElementsViewerController) {
		this.downElementsViewerController = downElementsViewerController;
	}

	public SourceCodeViewerController getCodeViewerController() {
		return codeViewerController;
	}

	public void setCodeViewerController(
			SourceCodeViewerController codeViewerController) {
		this.codeViewerController = codeViewerController;
	}

}
