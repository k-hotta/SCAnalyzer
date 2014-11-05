package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block;

import java.util.concurrent.atomic.AtomicLong;

/**
 * ブロックの対を表すクラス
 * 
 * @author k-hotta
 * 
 */
public abstract class AbstractBlockPairInfo implements
		Comparable<AbstractBlockPairInfo> {

	private static AtomicLong count = new AtomicLong(0);

	private final long id;

	private final RetrievedBlockInfo beforeBlock;

	private final RetrievedBlockInfo afterBlock;
	
	private boolean isAdded;
	
	private boolean isDeleted;

	public AbstractBlockPairInfo(final RetrievedBlockInfo beforeBlock,
			final RetrievedBlockInfo afterBlock) {
		this.id = count.getAndIncrement();
		this.beforeBlock = beforeBlock;
		this.afterBlock = afterBlock;
		this.isAdded = false;
		this.isDeleted = false;
	}

	public final long getId() {
		return this.id;
	}

	public final RetrievedBlockInfo getBeforeBlock() {
		return this.beforeBlock;
	}

	public final RetrievedBlockInfo getAfterBlock() {
		return this.afterBlock;
	}

	@Override
	public final int compareTo(AbstractBlockPairInfo anotherPair) {
		return ((Long) id).compareTo(anotherPair.getId());
	}

	public abstract boolean containsIncrease();

	public abstract boolean containsDecrease();
	
	public final void setAdded(boolean added) {
		this.isAdded = added;
	}
	
	public final void setDeleted(boolean deleted) {
		this.isDeleted = deleted;
	}
	
	public final boolean isAdded() {
		return this.isAdded;
	}

	public final boolean isDeleted() {
		return this.isDeleted;
	}
	
}
