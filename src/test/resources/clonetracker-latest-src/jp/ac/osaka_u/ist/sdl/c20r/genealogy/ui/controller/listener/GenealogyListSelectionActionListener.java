package jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.listener;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jp.ac.osaka_u.ist.sdl.c20r.genealogy.CloneSetPairInfoRetriever;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.RetrievedCloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.RetrievedCloneSetPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.GenealogyCloneSetPairListViewerController;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.GenealogyElementFrameController;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.GenealogyGraphViewerController;

public class GenealogyListSelectionActionListener implements
		ListSelectionListener {

	private final Map<Long, RetrievedCloneGenealogyInfo> genealogies;

	private final JTable table;

	private final GenealogyCloneSetPairListViewerController pairListController;

	private final GenealogyGraphViewerController graphController;

	private final CloneSetPairInfoRetriever pairRetriever;

	private final GenealogyElementFrameController elementFrameController;

	private final Map<Long, Integer> revisions;

	private final Map<Long, String> files;

	private final String graphDir;

	private final String workingDirPath;

	public GenealogyListSelectionActionListener(
			final Collection<RetrievedCloneGenealogyInfo> genealogies,
			final JTable table,
			final GenealogyCloneSetPairListViewerController pairListController,
			final GenealogyGraphViewerController graphController,
			final GenealogyElementFrameController elementFrameController,
			final Map<Long, Integer> revisions, final Map<Long, String> files,
			final String graphDir, final String workingDirPath) {
		this.genealogies = new TreeMap<Long, RetrievedCloneGenealogyInfo>();
		for (final RetrievedCloneGenealogyInfo genealogy : genealogies) {
			this.genealogies.put(genealogy.getId(), genealogy);
		}
		this.table = table;
		this.pairListController = pairListController;
		this.graphController = graphController;
		this.elementFrameController = elementFrameController;
		// beforeRevisionId は使わないけど仕様上指定しないと動かないから-1にしている
		this.pairRetriever = new CloneSetPairInfoRetriever(-1);
		this.revisions = revisions;
		this.files = files;
		this.graphDir = graphDir;
		this.workingDirPath = workingDirPath;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			return;
		}

		final long selectedGenealogyId = (Long) table
				.getValueAt(selectedRow, 0);
		final RetrievedCloneGenealogyInfo genealogy = genealogies
				.get(selectedGenealogyId);

		final Set<RetrievedCloneSetPairInfo> cloneSetPairs = pairRetriever
				.retrieve(genealogy.getPairs());

		final GenealogyCloneSetPairSelectionActionListener listener = new GenealogyCloneSetPairSelectionActionListener(
				cloneSetPairs, pairListController.getTable(),
				elementFrameController
						.getUpCloneSetElementListViewerController(),
				elementFrameController
						.getDownCloneSetElementListViewerController(),
				elementFrameController.getUpSourceCodeViewerController(),
				elementFrameController.getDownSourceCodeViewerController(),
				files, revisions, workingDirPath);
		pairListController.addListSelectionListener(listener);

		pairListController.update(cloneSetPairs, revisions);

		final String graphFilePath = detectGraphFilePath(selectedGenealogyId);
		graphController.update(graphFilePath);
	}

	private final String detectGraphFilePath(final long id) {
		final String path = graphDir + File.separator + id + ".png";
		final File file = new File(path);
		return file.getAbsolutePath();
	}

}
