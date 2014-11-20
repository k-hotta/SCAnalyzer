package jp.ac.osaka_u.ist.sdl.scanalyzer.ui.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.event.EventListenerList;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.RevisionChangeEvent;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.RevisionChangeEventListener;
import jp.ac.osaka_u.ist.sdl.scanalyzer.ui.view.CloneGenealogyElementsView;

/**
 * This is a model for {@link CloneGenealogyElementsView}.
 * 
 * @author k-hotta
 *
 */
public class CloneGenealogyElementsViewModel {

	/**
	 * The listeners
	 */
	private EventListenerList listeners = new EventListenerList();

	/**
	 * The genealogy to be shown
	 */
	private CloneGenealogy<?> genealogy;

	/**
	 * The revision id to be shown
	 */
	private long revisionId;

	/**
	 * The map between revision id and clones in the revision
	 */
	private final Map<Long, List<CloneClass<?>>> revisionClones = new TreeMap<>();

	public void addListener(final RevisionChangeEventListener listener) {
		listeners.add(RevisionChangeEventListener.class, listener);
	}

	public void removeListener(final RevisionChangeEventListener listener) {
		listeners.remove(RevisionChangeEventListener.class, listener);
	}

	/**
	 * Set the genealogy to be shown.
	 * 
	 * @param genealogy
	 *            the genealogy to be set
	 */
	public void setCloneGenealogy(final CloneGenealogy<?> genealogy) {
		this.genealogy = genealogy;
		revisionClones.clear();

		for (final CloneClass<?> cloneClass : genealogy.getCloneClasses()
				.values()) {
			final Revision revision = cloneClass.getVersion().getRevision();
			final long revisionId = revision.getId();

			if (revisionClones.containsKey(revisionId)) {
				revisionClones.get(revisionId).add(cloneClass);
			} else {
				final List<CloneClass<?>> newList = new ArrayList<>();
				newList.add(cloneClass);
				revisionClones.put(revisionId, newList);
			}
		}
	}

	/**
	 * Set revision id to be shown
	 * 
	 * @param id
	 *            the id of the revision to be shown
	 */
	public void setRevisionId(final long id) {
		this.revisionId = id;
		fireRevisionChanged();
	}

	protected void fireRevisionChanged() {
		RevisionChangeEventListener[] listenersArray = listeners
				.getListeners(RevisionChangeEventListener.class);

		final RevisionChangeEvent event = new RevisionChangeEvent(this,
				revisionClones.get(revisionId));
		for (final RevisionChangeEventListener l : listenersArray) {
			l.revisionChanged(event);
		}
	}

}
