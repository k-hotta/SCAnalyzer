package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.AbstractBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.RetrievedBlockInfo;

/**
 * 前後のリビジョンで対応づけられたクローンセットペア <br>
 * 要素の追加，及び削除は同時に起こりえ得る
 * 
 * @author k-hotta
 * 
 */
public class MatchedCloneSetPairInfo extends AbstractCloneSetPairInfo {

	/**
	 * ハッシュ値に変動があったかどうか
	 */
	private final boolean containsHashChange;

	/**
	 * 要素の追加があったかどうか
	 */
	private boolean containsIncrease;

	/**
	 * 要素の削除があったかどうか
	 */
	private boolean containsDecrease;

	/**
	 * コンストラクタ
	 * 
	 * @param beforeCloneSet
	 * @param afterCloneSet
	 */
	public MatchedCloneSetPairInfo(RetrievedCloneSetInfo beforeCloneSet,
			RetrievedCloneSetInfo afterCloneSet) {
		super(beforeCloneSet, beforeCloneSet.getId(), afterCloneSet,
				afterCloneSet.getId());
		this.containsHashChange = beforeCloneSet.getHash() != afterCloneSet
				.getHash();
		this.containsIncrease = false;
		this.containsDecrease = false;
	}

	/**
	 * 要素の追加，削除の有無を判定する
	 */
	@Override
	public void finalize() {
		for (final AbstractBlockPairInfo blockPair : blockPairs.values()) {
			checkBlockPair(blockPair);
		}
	}

	private void checkBlockPair(final AbstractBlockPairInfo blockPair) {
		if (blockPair.containsIncrease()) {
			this.containsIncrease = true;
			addAddedBlock(blockPair.getAfterBlock());
			blockPair.setAdded(true);
		}

		if (blockPair.containsDecrease()) {
			this.containsDecrease = true;
			addDeletedBlock(blockPair.getBeforeBlock());
			blockPair.setDeleted(true);
		}

		final RetrievedBlockInfo beforeBlock = blockPair.getBeforeBlock();
		final RetrievedBlockInfo afterBlock = blockPair.getAfterBlock();

		if (beforeBlock != null && afterBlock != null) {
			final long beforeCloneSetId = beforeBlock.getCloneSetId();
			final long afterCloneSetId = afterBlock.getCloneSetId();

			if (beforeCloneSetId != this.beforeCloneSetId) {
				this.containsIncrease = true;
				addAddedBlock(afterBlock);
				blockPair.setAdded(true);
			}

			if (afterCloneSetId != this.afterCloneSetId) {
				this.containsDecrease = true;
				addDeletedBlock(beforeBlock);
				blockPair.setDeleted(true);
			}
		}
	}

	/**
	 * ハッシュ値の変動があったかどうかを取得する
	 * 
	 * @return
	 */
	public final boolean containsHashChange() {
		return this.containsHashChange;
	}

	/**
	 * 要素の追加があったかどうかを取得する
	 * 
	 * @return
	 */
	public final boolean containsIncrease() {
		return this.containsIncrease;
	}

	/**
	 * 要素の削除があったかどうかを取得する
	 * 
	 * @return
	 */
	public final boolean containsDecrease() {
		return this.containsDecrease;
	}

}
