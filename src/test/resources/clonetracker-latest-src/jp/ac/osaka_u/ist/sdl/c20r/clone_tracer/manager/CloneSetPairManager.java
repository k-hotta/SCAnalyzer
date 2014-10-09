package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.manager;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone.AbstractCloneSetPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone.AddedCloneSetPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone.DeletedCloneSetPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone.MatchedCloneSetPairInfo;

/**
 * クローンセットペアを管理するクラス
 * 
 * @author k-hotta
 * 
 */
public class CloneSetPairManager {

	private final Map<Long, AddedCloneSetPairInfo> addedCloneSetPairs;

	private final Map<Long, DeletedCloneSetPairInfo> deletedCloneSetPairs;

	private final Map<Long, MatchedCloneSetPairInfo> matchedCloneSetPairs;

	public CloneSetPairManager() {
		this.addedCloneSetPairs = new TreeMap<Long, AddedCloneSetPairInfo>();
		this.deletedCloneSetPairs = new TreeMap<Long, DeletedCloneSetPairInfo>();
		this.matchedCloneSetPairs = new TreeMap<Long, MatchedCloneSetPairInfo>();
	}
	
	public final Set<AbstractCloneSetPairInfo> getAllCloneSetPairs() {
		final Set<AbstractCloneSetPairInfo> result = new TreeSet<AbstractCloneSetPairInfo>();
		result.addAll(addedCloneSetPairs.values());
		result.addAll(deletedCloneSetPairs.values());
		result.addAll(matchedCloneSetPairs.values());
		return Collections.unmodifiableSet(result);
	}

	public final Set<AddedCloneSetPairInfo> getAddedCloneSetPairs() {
		final Set<AddedCloneSetPairInfo> result = new TreeSet<AddedCloneSetPairInfo>();
		result.addAll(addedCloneSetPairs.values());
		return Collections.unmodifiableSet(result);
	}

	public final Set<DeletedCloneSetPairInfo> getDeletedCloneSetPairs() {
		final Set<DeletedCloneSetPairInfo> result = new TreeSet<DeletedCloneSetPairInfo>();
		result.addAll(deletedCloneSetPairs.values());
		return Collections.unmodifiableSet(result);
	}

	public final Set<MatchedCloneSetPairInfo> getMatchedCloneSetPairs() {
		final Set<MatchedCloneSetPairInfo> result = new TreeSet<MatchedCloneSetPairInfo>();
		result.addAll(matchedCloneSetPairs.values());
		return Collections.unmodifiableSet(result);
	}

	public final void add(final AbstractCloneSetPairInfo pair) {
		if (pair instanceof AddedCloneSetPairInfo) {
			add((AddedCloneSetPairInfo) pair);
		} else if (pair instanceof DeletedCloneSetPairInfo) {
			add((DeletedCloneSetPairInfo) pair);
		} else if (pair instanceof MatchedCloneSetPairInfo) {
			add((MatchedCloneSetPairInfo) pair);
		}
	}

	public final void add(final AddedCloneSetPairInfo addedPair) {
		this.addedCloneSetPairs.put(addedPair.getId(), addedPair);
	}

	public final void add(final DeletedCloneSetPairInfo deletedPair) {
		this.deletedCloneSetPairs.put(deletedPair.getId(), deletedPair);
	}

	public final void add(final MatchedCloneSetPairInfo matchedPair) {
		this.matchedCloneSetPairs.put(matchedPair.getId(), matchedPair);
	}

	public final void addAll(final Collection<AbstractCloneSetPairInfo> pairs) {
		for (AbstractCloneSetPairInfo pair : pairs) {
			add(pair);
		}
	}

}
