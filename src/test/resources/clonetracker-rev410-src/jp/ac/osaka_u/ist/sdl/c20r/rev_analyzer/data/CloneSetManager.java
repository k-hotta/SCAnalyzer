package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.clone.CloneSetInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.manager.AbstractDBElementManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.manager.DBCloneSetManager;

public class CloneSetManager extends AbstractElementManager<CloneSetInfo> {

	private static CloneSetManager SINGLETON = null;

	private final Map<Integer, Long> hashIdMap;

	private final Set<CloneSetInfo> newlyAddedElements;

	private CloneSetManager() {
		super();
		this.hashIdMap = new TreeMap<Integer, Long>();
		createHashIdMap();
		this.newlyAddedElements = new TreeSet<CloneSetInfo>();
		addAll(DBCloneSetManager.getInstance().getRegisteredElements());
	}

	private void createHashIdMap() {
		for (CloneSetInfo element : getAllElements()) {
			this.hashIdMap.put(element.getHash(), element.getId());
		}
	}

	public static CloneSetManager getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new CloneSetManager();
		}

		return SINGLETON;
	}

	@Override
	protected AbstractDBElementManager<CloneSetInfo> getDbManager() {
		return DBCloneSetManager.getInstance();
	}

	@Override
	void clear() {
		SINGLETON = null;
	}

	@Override
	public void add(CloneSetInfo element) {
		super.add(element);
		hashIdMap.put(element.getHash(), element.getId());
	}

	public CloneSetInfo getElement(final int hash) {
		if (hashIdMap.containsKey(hash)) {
			return getElement(hashIdMap.get(hash));
		}

		final CloneSetInfo newElement = new CloneSetInfo(getNextId(), hash);
		this.add(newElement);
		this.newlyAddedElements.add(newElement);

		return newElement;
	}

	public long getCorrepondentId(final int hash) {
		if (hashIdMap.containsKey(hash)) {
			return hashIdMap.get(hash);
		}

		return getNextId();
	}

	public Set<CloneSetInfo> getNewlyAddedElements() {
		return Collections.unmodifiableSet(newlyAddedElements);
	}

}
