package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.AbstractBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.RetrievedBlockInfo;

/**
 * 前後のリビジョン間でクローンセットの対応関係を表すクラス <br>
 * 片方のリビジョンのあるクローンセットがもう一方のリビジョンの複数のクローンセットに対応し得るので，
 * 一つのクローンセットが複数のクローンセットペアに含まれ得る
 * 
 * @author k-hotta
 * 
 */
public abstract class AbstractCloneSetPairInfo implements
		Comparable<AbstractCloneSetPairInfo> {

	/**
	 * これまでに生成した要素数
	 */
	private static AtomicLong count = new AtomicLong(0);

	/**
	 * クローンセットペアのID
	 */
	protected final long id;

	/**
	 * 前リビジョンにおけるクローンセットのID
	 */
	protected final long beforeCloneSetId;

	/**
	 * 前リビジョンにおけるクローンセット
	 */
	protected final RetrievedCloneSetInfo beforeCloneSet;

	/**
	 * 後リビジョンにおけるクローンセットのID
	 */
	protected final long afterCloneSetId;

	/**
	 * 後リビジョンにおけるクローンセット
	 */
	protected final RetrievedCloneSetInfo afterCloneSet;

	/**
	 * このクローンセットペアに関連するブロックペアのマップ
	 */
	protected final Map<Long, AbstractBlockPairInfo> blockPairs;

	/**
	 * 後のリビジョンからこのクローンセットに追加されたブロック
	 */
	protected final Set<RetrievedBlockInfo> addedBlocks;

	/**
	 * 前のリビジョンでこのクローンセットから消えたブロック
	 */
	protected final Set<RetrievedBlockInfo> deletedBlocks;

	/**
	 * コンストラクタ
	 * 
	 * @param beforeCloneSet
	 * @param afterCloneSet
	 */
	public AbstractCloneSetPairInfo(final RetrievedCloneSetInfo beforeCloneSet,
			final long beforeCloneSetId,
			final RetrievedCloneSetInfo afterCloneSet,
			final long afterCloneSetId) {
		this.id = count.getAndIncrement();
		this.beforeCloneSetId = beforeCloneSetId;
		this.beforeCloneSet = beforeCloneSet;
		this.afterCloneSetId = afterCloneSetId;
		this.afterCloneSet = afterCloneSet;
		this.blockPairs = new TreeMap<Long, AbstractBlockPairInfo>();
		this.addedBlocks = new TreeSet<RetrievedBlockInfo>();
		this.deletedBlocks = new TreeSet<RetrievedBlockInfo>();
	}

	public static AbstractCloneSetPairInfo createInstance(final long beforeId,
			final Map<Long, RetrievedCloneSetInfo> beforeCloneSets,
			final long afterId,
			final Map<Long, RetrievedCloneSetInfo> afterCloneSets) {
		final RetrievedCloneSetInfo beforeCloneSet = (beforeId >= 0) ? beforeCloneSets
				.get(beforeId) : null;
		final RetrievedCloneSetInfo afterCloneSet = (afterId >= 0) ? afterCloneSets
				.get(afterId) : null;

		if (beforeCloneSet == null) {
			if (afterCloneSet == null) {
				return null;
			} else {
				return new AddedCloneSetPairInfo(afterCloneSet);
			}
		} else {
			if (afterCloneSet == null) {
				return new DeletedCloneSetPairInfo(beforeCloneSet);
			} else {
				return new MatchedCloneSetPairInfo(beforeCloneSet,
						afterCloneSet);
			}
		}
	}

	/**
	 * IDを取得
	 * 
	 * @return
	 */
	public final long getId() {
		return this.id;
	}

	/**
	 * 前リビジョンにおけるクローンセットのIDを取得
	 * 
	 * @return
	 */
	public final long getBeforeCloneSetId() {
		return this.beforeCloneSetId;
	}

	/**
	 * 前リビジョンにおけるクローンセットを取得
	 * 
	 * @return
	 */
	public final RetrievedCloneSetInfo getBeforeCloneSet() {
		return this.beforeCloneSet;
	}

	/**
	 * 後リビジョンにおけるクローンセットのIDを取得
	 * 
	 * @return
	 */
	public final long getAfterCloneSetId() {
		return this.afterCloneSetId;
	}

	/**
	 * 後リビジョンにおけるクローンセットを取得
	 * 
	 * @return
	 */
	public final RetrievedCloneSetInfo getAfterCloneSet() {
		return this.afterCloneSet;
	}

	/**
	 * 登録されているブロックペアを全て取得
	 * 
	 * @return
	 */
	public final Set<AbstractBlockPairInfo> getAllBlockPairs() {
		final Set<AbstractBlockPairInfo> result = new TreeSet<AbstractBlockPairInfo>();
		result.addAll(blockPairs.values());
		return Collections.unmodifiableSet(result);
	}

	public final Map<Long, AbstractBlockPairInfo> getBlockPairMap() {
		return Collections.unmodifiableMap(blockPairs);
	}

	/**
	 * IDを指定してブロックペアを取得
	 * 
	 * @param id
	 * @return
	 */
	public final AbstractBlockPairInfo getBlockPair(final long id) {
		if (blockPairs.containsKey(id)) {
			return blockPairs.get(id);
		}

		return null;
	}

	/**
	 * ブロックペアを追加
	 * 
	 * @param blockPair
	 */
	public void addBlockPair(final AbstractBlockPairInfo blockPair) {
		blockPairs.put(blockPair.getId(), blockPair);
	}

	/**
	 * ブロックペアを一括で追加
	 * 
	 * @param blockPairs
	 */
	public final void addBlockPairs(
			final Collection<AbstractBlockPairInfo> blockPairs) {
		for (final AbstractBlockPairInfo blockPair : blockPairs) {
			addBlockPair(blockPair);
		}
	}

	/**
	 * 追加されたブロックを取得
	 * 
	 * @return
	 */
	public Set<RetrievedBlockInfo> getAddedBlocks() {
		return Collections.unmodifiableSet(this.addedBlocks);
	}

	/**
	 * 追加されたブロックを追加
	 * 
	 * @param addedBlock
	 */
	public void addAddedBlock(final RetrievedBlockInfo addedBlock) {
		this.addedBlocks.add(addedBlock);
	}

	/**
	 * 削除されたブロックを取得
	 * 
	 * @return
	 */
	public Set<RetrievedBlockInfo> getDeletedBlocks() {
		return Collections.unmodifiableSet(deletedBlocks);
	}

	/**
	 * 削除されたブロックを追加
	 * 
	 * @param deletedBlock
	 */
	public void addDeletedBlock(final RetrievedBlockInfo deletedBlock) {
		this.deletedBlocks.add(deletedBlock);
	}

	@Override
	public final int compareTo(AbstractCloneSetPairInfo anotherPair) {
		return ((Long) id).compareTo(anotherPair.getId());
	}

	public final boolean isCorrespondent(final long beforeCloneId,
			final long afterCloneId) {
		if (beforeCloneId == -1) {
			if (afterCloneId == -1) {
				return false;
			} else {
				return this.afterCloneSetId == afterCloneId;
			}
		} else if (afterCloneId == -1) {
			return this.beforeCloneSetId == beforeCloneId;
		} else {
			// return this.beforeCloneSetId == beforeCloneId
			// && this.afterCloneSetId == afterCloneId;]
			return this.beforeCloneSetId == beforeCloneId
					|| this.afterCloneSetId == afterCloneId;
		}
	}

	public final Set<RetrievedBlockInfo> getDeletedBlocksInDeletedFiles() {
		final Set<RetrievedBlockInfo> result = new TreeSet<RetrievedBlockInfo>();
		for (final RetrievedBlockInfo deletedBlock : deletedBlocks) {
			if (deletedBlock.isInDeletedFile()) {
				result.add(deletedBlock);
			}
		}
		return Collections.unmodifiableSet(result);
	}

	public void finalize() {
		// do nothing on default
	}

	public abstract boolean containsHashChange();

}
