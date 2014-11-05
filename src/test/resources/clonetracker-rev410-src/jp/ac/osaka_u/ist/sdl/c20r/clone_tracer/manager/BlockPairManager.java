package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.manager;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.AbstractBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.AddedBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.DeletedBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.MatchedBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.RetrievedBlockInfo;

public class BlockPairManager {
	
	private final Map<Long, AbstractBlockPairInfo> allPairs;

	private final Map<Long, AddedBlockPairInfo> addedPairs;

	private final Map<Long, DeletedBlockPairInfo> deletedPairs;

	private final Map<Long, MatchedBlockPairInfo> matchedPairs;

	public BlockPairManager() {
		this.allPairs = new TreeMap<Long, AbstractBlockPairInfo>();
		this.addedPairs = new TreeMap<Long, AddedBlockPairInfo>();
		this.deletedPairs = new TreeMap<Long, DeletedBlockPairInfo>();
		this.matchedPairs = new TreeMap<Long, MatchedBlockPairInfo>();
	}
	
	public Map<Long, AbstractBlockPairInfo> getAllPairsAsMap() {
		return Collections.unmodifiableMap(allPairs);
	}

	public Set<AbstractBlockPairInfo> getAllPairs() {
		Set<AbstractBlockPairInfo> result = new TreeSet<AbstractBlockPairInfo>();
		result.addAll(matchedPairs.values());
		result.addAll(addedPairs.values());
		result.addAll(deletedPairs.values());
		return Collections.unmodifiableSet(result);
	}

	public Set<AddedBlockPairInfo> getAddedPairs() {
		Set<AddedBlockPairInfo> result = new TreeSet<AddedBlockPairInfo>();
		result.addAll(addedPairs.values());
		return Collections.unmodifiableSet(result);
	}

	public Set<DeletedBlockPairInfo> getDeletedPairs() {
		Set<DeletedBlockPairInfo> result = new TreeSet<DeletedBlockPairInfo>();
		result.addAll(deletedPairs.values());
		return Collections.unmodifiableSet(result);
	}

	public Set<MatchedBlockPairInfo> getMatchedBlockPairInfo() {
		Set<MatchedBlockPairInfo> result = new TreeSet<MatchedBlockPairInfo>();
		result.addAll(matchedPairs.values());
		return Collections.unmodifiableSet(result);
	}

	public void add(final AddedBlockPairInfo addedPair) {
		final RetrievedBlockInfo afterBlock = addedPair.getAfterBlock();
		afterBlock.setBlockPairId(addedPair.getId());
		this.addedPairs.put(addedPair.getId(), addedPair);
		this.allPairs.put(addedPair.getId(), addedPair);
	}

	public void add(final DeletedBlockPairInfo deletedPair) {
		final RetrievedBlockInfo beforeBlock = deletedPair.getBeforeBlock();
		beforeBlock.setBlockPairId(deletedPair.getId());
		this.deletedPairs.put(deletedPair.getId(), deletedPair);
		this.allPairs.put(deletedPair.getId(), deletedPair);
	}

	public void add(final MatchedBlockPairInfo matchedPair) {
		final RetrievedBlockInfo beforeBlock = matchedPair.getBeforeBlock();
		final RetrievedBlockInfo afterBlock = matchedPair.getAfterBlock();
		beforeBlock.setBlockPairId(matchedPair.getId());
		afterBlock.setBlockPairId(matchedPair.getId());
		this.matchedPairs.put(matchedPair.getId(), matchedPair);
		this.allPairs.put(matchedPair.getId(), matchedPair);
	}
	
}
