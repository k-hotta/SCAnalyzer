package jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.RetrievedFileInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.RetrievedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.AllRevisionInfoRetriever;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.CloneGenealogyInfoRetriever;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.GenealogyFileInfoRetriever;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.RetrievedCloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.component.GenealogyElementFrame;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.component.GenealogyListingFrame;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.GenealogyElementFrameController;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.GenealogyListViewerController;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.GenealogyListingFrameController;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.listener.GenealogyListSelectionActionListener;
import jp.ac.osaka_u.ist.sdl.c20r.ui.settings.UISettings;

public class GenealogyUIMain {

	private final GenealogyListingFrame listingFrame;

	private final GenealogyElementFrame elementFrame;

	private final String graphDir;

	private final String workingDirPath;

	public GenealogyUIMain(final String dbPath, final String graphDir,
			final String repositoryLocation, final String workingDirPath)
			throws Exception {
		UISettings.getInstance().setRepository(repositoryLocation);
		this.listingFrame = new GenealogyListingFrame();
		this.elementFrame = new GenealogyElementFrame();
		DBConnection.createInstance(dbPath);
		this.graphDir = graphDir;
		this.workingDirPath = workingDirPath;
	}

	public static void main(String[] args) throws Exception {
		GenealogyUIMain main = new GenealogyUIMain(
				"F:\\dbfiles\\ant-30-0419.db", "F:\\graph\\ant",
				"F:/repositories/repository-ant/ant/core/trunk/src/main/", "F:\\work\\ant");
		main.run();
	}

	public void run() {
		try {
			final GenealogyListingFrameController listingController = listingFrame
					.getController();
			final GenealogyElementFrameController elementController = elementFrame
					.getController();

			// リビジョン情報をDBから回収
			final AllRevisionInfoRetriever revRetriever = new AllRevisionInfoRetriever();

			final Map<Long, Integer> revisions = new TreeMap<Long, Integer>();
			for (final RetrievedRevisionInfo rev : revRetriever.retrieveAll()) {
				revisions.put(rev.getId(), rev.getRevisionNum());
			}

			// ファイル情報をDBから回収
			final GenealogyFileInfoRetriever fileRetriever = new GenealogyFileInfoRetriever();

			final Map<Long, String> files = new TreeMap<Long, String>();
			for (final RetrievedFileInfo file : fileRetriever.retrieveAll()) {
				files.put(file.getId(), file.getPath());
			}

			// Genealogy 情報をDBから回収し，テーブルにセット
			final CloneGenealogyInfoRetriever genealogyRetriever = new CloneGenealogyInfoRetriever();
			final Set<RetrievedCloneGenealogyInfo> genealogies = genealogyRetriever
					.retrieveAll();

			final GenealogyListViewerController listViewerController = listingController
					.getGenealogyListViewerController();

			listViewerController.makeRows(genealogies, revisions);

			// Genealogy テーブルにリスナーを設定
			listViewerController
					.addListSelectionListener(new GenealogyListSelectionActionListener(
							genealogies,
							listViewerController.getTable(),
							listingController
									.getGenealogyCloneSetPairListViewerController(),
							listingController
									.getGenealogyGraphViewerController(),
							elementController, revisions, files, graphDir,
							workingDirPath));

			listingFrame.setVisible(true);
			elementFrame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
