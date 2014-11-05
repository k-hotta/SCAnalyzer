package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone;

import java.util.Set;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.AbstractBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.RetrievedBlockInfo;

/**
 * 後のリビジョンで追加されたクローンセット
 * 
 * @author k-hotta
 * 
 */
public class AddedCloneSetPairInfo extends AbstractCloneSetPairInfo {

	public AddedCloneSetPairInfo(RetrievedCloneSetInfo afterCloneSet) {
		super(null, -1, afterCloneSet, afterCloneSet.getId());
	}

	@Override
	public void addBlockPair(final AbstractBlockPairInfo blockPair) {
		super.addBlockPair(blockPair);
		addAddedBlock(blockPair.getAfterBlock());
		blockPair.setAdded(true);
	}

	@Override
	public boolean containsHashChange() {
		//return true;
		return false;
	}
	
	public Set<RetrievedBlockInfo> getAddedBlocks() {
		return new TreeSet<RetrievedBlockInfo>();
	}
	
}
