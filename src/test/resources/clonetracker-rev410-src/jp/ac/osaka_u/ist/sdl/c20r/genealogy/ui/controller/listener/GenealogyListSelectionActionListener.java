package jp.ac.osaka_u.ist.sdl.c20r.genealogy.ui.controller.listener;

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

public class GenealogyListSelectionActionListener implements
		ListSelectionListener {

	private final Map<Long, RetrievedCloneGenealogyInfo> genealogies;

	private final JTable table;

	private final GenealogyCloneSetPairListViewerController pairListController;

	private final CloneSetPairInfoRetriever pairRetriever;

	private final Map<Long, Integer> revisions;

	public GenealogyListSelectionActionListener(
			final Collection<RetrievedCloneGenealogyInfo> genealogies,
			final JTable table,
			final GenealogyCloneSetPairListViewerController pairListController,
			final Map<Long, Integer> revisions) {
		this.genealogies = new TreeMap<Long, RetrievedCloneGenealogyInfo>();
		for (final RetrievedCloneGenealogyInfo genealogy : genealogies) {
			this.genealogies.put(genealogy.getId(), genealogy);
		}
		this.table = table;
		this.pairListController = pairListController;
		// beforeRevisionId は使わないけど仕様上指定しないと動かないから-1にしている
		this.pairRetriever = new CloneSetPairInfoRetriever(-1);
		this.revisions = revisions;
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
		pairListController.update(cloneSetPairs, revisions);
	}

}
