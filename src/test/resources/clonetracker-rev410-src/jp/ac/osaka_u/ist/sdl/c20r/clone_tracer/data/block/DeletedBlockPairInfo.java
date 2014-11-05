package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block;

public class DeletedBlockPairInfo extends AbstractBlockPairInfo {

	public DeletedBlockPairInfo(RetrievedBlockInfo beforeBlock) {
		super(beforeBlock, null);
		setDeleted(true);
	}

	@Override
	public boolean containsIncrease() {
		return false;
	}

	@Override
	public boolean containsDecrease() {
		return true;
	}

}
