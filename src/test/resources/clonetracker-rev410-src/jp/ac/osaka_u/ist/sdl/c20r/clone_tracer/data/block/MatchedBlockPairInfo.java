package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block;

public class MatchedBlockPairInfo extends AbstractBlockPairInfo {

	public MatchedBlockPairInfo(RetrievedBlockInfo beforeBlock,
			RetrievedBlockInfo afterBlock) {
		super(beforeBlock, afterBlock);
	}

	@Override
	public boolean containsIncrease() {
		return false;
	}

	@Override
	public boolean containsDecrease() {
		return false;
	}

}
