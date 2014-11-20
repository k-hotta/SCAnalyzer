package jp.ac.osaka_u.ist.sdl.scanalyzer.ui.control;

import java.util.Map;
import java.util.TreeMap;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.RevisionInternalRepresentation;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.model.CloneGenealogyElementsViewModel;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.view.CloneGenealogyElementsView;

/**
 * This is a controller of {@link CloneGenealogyElementsView}.
 * 
 * @author k-hotta
 *
 */
public class CloneGenealogyElementsViewController {

	private CloneGenealogyElementsView view;

	private CloneGenealogyElementsViewModel model;

	public CloneGenealogyElementsViewController(
			final CloneGenealogyElementsView view) {
		this.view = view;
	}

	public void setModel(final CloneGenealogyElementsViewModel model) {
		this.model = model;
	}

	public void setCloneGenealogy(final CloneGenealogy<?> genealogy) {
		final Map<Long, RevisionInternalRepresentation> internalRevisions = detectInternalRevisions(genealogy);

		view.makeRows(internalRevisions.values());
		
		model.setCloneGenealogy(genealogy);
	}

	private Map<Long, RevisionInternalRepresentation> detectInternalRevisions(
			final CloneGenealogy<?> genealogy) {
		final Map<Long, RevisionInternalRepresentation> internalRevisions = new TreeMap<>();

		for (final CloneClass<?> cloneClass : genealogy.getCloneClasses()
				.values()) {
			final Revision revision = cloneClass.getVersion().getRevision();

			if (internalRevisions.containsKey(revision.getId())) {
				final RevisionInternalRepresentation internalRevision = internalRevisions
						.get(revision.getId());
				int numOfClones = internalRevision.getNumOfClones();
				int numOfClonesWithGhosts = internalRevision
						.getNumOfClonesWithGhosts();
				int numOfClonesCompletelyGhost = internalRevision
						.getNumOfClonesCompletelyGhost();

				internalRevision.setNumOfClones(numOfClones++);
				if (cloneClass.containsGhost()) {
					internalRevision
							.setNumOfClonesWithGhosts(numOfClonesWithGhosts++);
				}
				if (cloneClass.isCompletelyGhost()) {
					internalRevision
							.setNumOfClonesCompletelyGhost(numOfClonesCompletelyGhost++);
				}
			} else {
				final RevisionInternalRepresentation internalRevision = new RevisionInternalRepresentation();
				internalRevision.setId(revision.getId());
				internalRevision.setIdentifier(revision.getIdentifier());
				internalRevision.setNumOfClones(1);
				if (cloneClass.containsGhost()) {
					internalRevision.setNumOfClonesWithGhosts(1);
				} else {
					internalRevision.setNumOfClonesWithGhosts(0);
				}
				if (cloneClass.isCompletelyGhost()) {
					internalRevision.setNumOfClonesCompletelyGhost(1);
				} else {
					internalRevision.setNumOfClonesCompletelyGhost(0);
				}

				internalRevisions.put(revision.getId(), internalRevision);
			}
		}

		return internalRevisions;
	}

	public void revisionChanged(final ListSelectionEvent e, final JTable table) {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			return;
		}

		Long id = (Long) table.getValueAt(selectedRow, 0);
		model.setRevisionId(id);
	}

}
