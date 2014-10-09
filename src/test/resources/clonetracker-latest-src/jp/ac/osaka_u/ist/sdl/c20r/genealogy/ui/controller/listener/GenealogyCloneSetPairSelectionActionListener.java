package jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.listener;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.RetrievedBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.GenealogyBlockInfoRetriever;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.RetrievedCloneSetPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.GenealogyCloneSetElementListViewerController;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.GenealogySourceCodeViewerController;

public class GenealogyCloneSetPairSelectionActionListener implements
		ListSelectionListener {

	private final Map<Long, RetrievedCloneSetPairInfo> cloneSetPairs;

	private final JTable table;

	private final GenealogyCloneSetElementListViewerController upController;

	private final GenealogyCloneSetElementListViewerController downController;

	private final GenealogySourceCodeViewerController upSourceController;

	private final GenealogySourceCodeViewerController downSourceController;

	private final Map<Long, String> files;

	private final GenealogyBlockInfoRetriever blockRetriever;

	private final Map<Long, Integer> revisions;

	private final String workingDirPath;

	public GenealogyCloneSetPairSelectionActionListener(
			final Collection<RetrievedCloneSetPairInfo> cloneSetPairs,
			final JTable table,
			final GenealogyCloneSetElementListViewerController upController,
			final GenealogyCloneSetElementListViewerController downController,
			final GenealogySourceCodeViewerController upSourceController,
			final GenealogySourceCodeViewerController downSourceController,
			final Map<Long, String> files, final Map<Long, Integer> revisions,
			final String workingDirPath) {
		this.cloneSetPairs = new TreeMap<Long, RetrievedCloneSetPairInfo>();
		for (final RetrievedCloneSetPairInfo cloneSetPair : cloneSetPairs) {
			this.cloneSetPairs.put(cloneSetPair.getId(), cloneSetPair);
		}
		this.table = table;
		this.upController = upController;
		this.downController = downController;
		this.upSourceController = upSourceController;
		this.downSourceController = downSourceController;
		this.files = files;
		this.blockRetriever = new GenealogyBlockInfoRetriever();
		this.revisions = revisions;
		this.workingDirPath = workingDirPath;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			return;
		}

		final long selectedPairId = (Long) table.getValueAt(selectedRow, 0);
		final RetrievedCloneSetPairInfo cloneSetPair = cloneSetPairs
				.get(selectedPairId);

		final Set<RetrievedBlockInfo> blocksInBefore = blockRetriever
				.retrieveBlocksInSpecifiedClone(
						cloneSetPair.getBeforeCloneId(),
						cloneSetPair.getBeforeRevId());
		upController.update(blocksInBefore, files, workingDirPath);

		final Set<RetrievedBlockInfo> blocksInAfter = blockRetriever
				.retrieveBlocksInSpecifiedClone(cloneSetPair.getAfterCloneId(),
						cloneSetPair.getAfterRevId());
		downController.update(blocksInAfter, files, workingDirPath);

		final GenealogyCloneSetElementSelectionActionListener upListener = new GenealogyCloneSetElementSelectionActionListener(
				upSourceController, upController.getTable(),
				revisions.get(cloneSetPair.getBeforeRevId()));
		upController.addListSelectionListener(upListener);

		final GenealogyCloneSetElementSelectionActionListener downListener = new GenealogyCloneSetElementSelectionActionListener(
				downSourceController, downController.getTable(),
				revisions.get(cloneSetPair.getAfterRevId()));
		downController.addListSelectionListener(downListener);
	}
}
