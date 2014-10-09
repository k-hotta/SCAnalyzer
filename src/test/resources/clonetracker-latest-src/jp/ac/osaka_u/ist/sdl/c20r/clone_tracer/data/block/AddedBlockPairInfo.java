package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block;

public class AddedBlockPairInfo extends AbstractBlockPairInfo {

	public AddedBlockPairInfo(RetrievedBlockInfo afterBlock) {
		super(null, afterBlock);
		setAdded(true);
	}

	@Override
	public boolean containsIncrease() {
		return true;
	}

	@Override
	public boolean containsDecrease() {
		return false;
	}

}
