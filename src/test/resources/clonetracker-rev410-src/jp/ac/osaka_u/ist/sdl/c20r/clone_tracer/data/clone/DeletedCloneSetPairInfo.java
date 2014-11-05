package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone;

import java.util.Set;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.AbstractBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.RetrievedBlockInfo;

/**
 * 前リビジョンで削除されたクローンセットペア
 * 
 * @author k-hotta
 * 
 */
public class DeletedCloneSetPairInfo extends AbstractCloneSetPairInfo {

	public DeletedCloneSetPairInfo(RetrievedCloneSetInfo beforeCloneSet) {
		super(beforeCloneSet, beforeCloneSet.getId(), null, -1);
	}

	@Override
	public void addBlockPair(final AbstractBlockPairInfo blockPair) {
		super.addBlockPair(blockPair);
		addDeletedBlock(blockPair.getBeforeBlock());
		blockPair.setDeleted(true);
	}

	@Override
	public boolean containsHashChange() {
		//return true;
		return false;
	}
	
	public Set<RetrievedBlockInfo> getDeletedBlocks() {
		return new TreeSet<RetrievedBlockInfo>();
	}
	
}
