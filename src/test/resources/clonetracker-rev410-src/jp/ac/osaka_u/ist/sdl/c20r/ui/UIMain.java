package jp.ac.osaka_u.ist.sdl.c20r.ui;

import java.util.Map;

import jp.ac.osaka_u.ist.sdl.c20r.ui.component.MainFrame;
import jp.ac.osaka_u.ist.sdl.c20r.ui.controller.ControllerManager;
import jp.ac.osaka_u.ist.sdl.c20r.ui.controller.RevisionListSelectionActionListener;
import jp.ac.osaka_u.ist.sdl.c20r.ui.controller.RevisionRenderer;
import jp.ac.osaka_u.ist.sdl.c20r.ui.csv.CSVDirectoryReader;
import jp.ac.osaka_u.ist.sdl.c20r.ui.data.CSVRevisionInfo;
import jp.ac.osaka_u.ist.sdl.c20r.ui.settings.UISettings;

public class UIMain {

	private final MainFrame frame;

	private final ControllerManager manager;

	public UIMain(String repositoryLocation) throws Exception {
		UISettings.getInstance().setRepository(repositoryLocation);
		this.frame = new MainFrame();
		this.manager = ControllerManager.getInstance();
	}

	public void start(final String csvPath) {
		try {
			final Map<Integer, CSVRevisionInfo> revisions = CSVDirectoryReader
					.parseDirectory(csvPath, false);

			manager.getRevisionViewerController().makeRows(revisions.values());

			final RevisionListSelectionActionListener listener = new RevisionListSelectionActionListener(
					revisions,
					manager.getRevisionViewerController().getTable(),
					manager.getListViewerController());
			manager.getRevisionViewerController().addListSelectionListener(
					listener);
			manager.getRevisionViewerController().setDefaultRenderer(
					new RevisionRenderer(revisions));

			// final CSVRevisionInfo targetRevision = CSVReader.parse(csvPath,
			// false);

			// final int revisionNum = targetRevision.getRevisionNum();
			// final int nextRevisionNum = targetRevision.getNextRevisionNum();

			// SVNRepositoryManager.getInstance().checkout(revisionNum);
			// SVNRepositoryManager.getInstance().checkout(nextRevisionNum,
			// UISettings.getInstance().getAnotherWorkingDir());

			// manager.getListViewerController().makeRows(
			// targetRevision.getCloneSets());
			//
			// final CloneSetListSelectionActionListener listener = new
			// CloneSetListSelectionActionListener(
			// targetRevision, manager.getListViewerController()
			// .getTable(),
			// manager.getUpElementsViewerController(),
			// manager.getDownElementsViewerController(),
			// manager.getCodeViewerController(), revisionNum,
			// nextRevisionNum);
			// manager.getListViewerController()
			// .addListSelectionListener(listener);
			// manager.getListViewerController().setDefaultRenderer(
			// new DisappearedCloneSetRenderer(targetRevision
			// .getCloneSetsMap()));

			frame.setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
