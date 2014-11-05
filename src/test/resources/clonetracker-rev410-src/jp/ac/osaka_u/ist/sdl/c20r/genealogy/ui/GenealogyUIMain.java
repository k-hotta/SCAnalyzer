package jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.RetrievedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.AllRevisionInfoRetriever;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.CloneGenealogyInfoRetriever;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.RetrievedCloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.component.GenealogyElementFrame;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.component.GenealogyListingFrame;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.GenealogyListViewerController;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.listener.GenealogyListSelectionActionListener;

public class GenealogyUIMain {

	private final GenealogyListingFrame listingFrame;

	private final GenealogyElementFrame elementFrame;

	public GenealogyUIMain(final String dbPath) {
		this.listingFrame = new GenealogyListingFrame();
		this.elementFrame = new GenealogyElementFrame();
		DBConnection.createInstance(dbPath);
	}

	public static void main(String[] args) {
		GenealogyUIMain main = new GenealogyUIMain(
				"F:\\dbfiles\\ant-30-0419.db");
		main.run();
	}

	public void run() {
		try {
			// リビジョン情報をDBから回収
			final AllRevisionInfoRetriever revRetriever = new AllRevisionInfoRetriever();

			final Map<Long, Integer> revisions = new TreeMap<Long, Integer>();
			for (final RetrievedRevisionInfo rev : revRetriever.retrieveAll()) {
				revisions.put(rev.getId(), rev.getRevisionNum());
			}

			// Genealogy 情報をDBから回収し，テーブルにセット
			final CloneGenealogyInfoRetriever genealogyRetriever = new CloneGenealogyInfoRetriever();
			final Set<RetrievedCloneGenealogyInfo> genealogies = genealogyRetriever
					.retrieveAll();

			final GenealogyListViewerController listViewerController = listingFrame
					.getController().getGenealogyListViewerController();

			listViewerController.makeRows(genealogies, revisions);

			// Genealogy テーブルにリスナーを設定
			listViewerController
					.addListSelectionListener(new GenealogyListSelectionActionListener(
							genealogies,
							listViewerController.getTable(),
							listingFrame
									.getController()
									.getGenealogyCloneSetPairListViewerController(),
							revisions));

			listingFrame.setVisible(true);
			elementFrame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
