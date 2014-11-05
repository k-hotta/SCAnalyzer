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
 * �O��̃��r�W�����ԂŃN���[���Z�b�g�̑Ή��֌W��\���N���X <br>
 * �Е��̃��r�W�����̂���N���[���Z�b�g����������̃��r�W�����̕����̃N���[���Z�b�g�ɑΉ�������̂ŁC
 * ��̃N���[���Z�b�g�������̃N���[���Z�b�g�y�A�Ɋ܂܂꓾��
 * 
 * @author k-hotta
 * 
 */
public abstract class AbstractCloneSetPairInfo implements
		Comparable<AbstractCloneSetPairInfo> {

	/**
	 * ����܂łɐ��������v�f��
	 */
	private static AtomicLong count = new AtomicLong(0);

	/**
	 * �N���[���Z�b�g�y�A��ID
	 */
	protected final long id;

	/**
	 * �O���r�W�����ɂ�����N���[���Z�b�g��ID
	 */
	protected final long beforeCloneSetId;

	/**
	 * �O���r�W�����ɂ�����N���[���Z�b�g
	 */
	protected final RetrievedCloneSetInfo beforeCloneSet;

	/**
	 * �ナ�r�W�����ɂ�����N���[���Z�b�g��ID
	 */
	protected final long afterCloneSetId;

	/**
	 * �ナ�r�W�����ɂ�����N���[���Z�b�g
	 */
	protected final RetrievedCloneSetInfo afterCloneSet;

	/**
	 * ���̃N���[���Z�b�g�y�A�Ɋ֘A����u���b�N�y�A�̃}�b�v
	 */
	protected final Map<Long, AbstractBlockPairInfo> blockPairs;

	/**
	 * ��̃��r�W�������炱�̃N���[���Z�b�g�ɒǉ����ꂽ�u���b�N
	 */
	protected final Set<RetrievedBlockInfo> addedBlocks;

	/**
	 * �O�̃��r�W�����ł��̃N���[���Z�b�g����������u���b�N
	 */
	protected final Set<RetrievedBlockInfo> deletedBlocks;

	/**
	 * �R���X�g���N�^
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
	 * ID���擾
	 * 
	 * @return
	 */
	public final long getId() {
		return this.id;
	}

	/**
	 * �O���r�W�����ɂ�����N���[���Z�b�g��ID���擾
	 * 
	 * @return
	 */
	public final long getBeforeCloneSetId() {
		return this.beforeCloneSetId;
	}

	/**
	 * �O���r�W�����ɂ�����N���[���Z�b�g���擾
	 * 
	 * @return
	 */
	public final RetrievedCloneSetInfo getBeforeCloneSet() {
		return this.beforeCloneSet;
	}

	/**
	 * �ナ�r�W�����ɂ�����N���[���Z�b�g��ID���擾
	 * 
	 * @return
	 */
	public final long getAfterCloneSetId() {
		return this.afterCloneSetId;
	}

	/**
	 * �ナ�r�W�����ɂ�����N���[���Z�b�g���擾
	 * 
	 * @return
	 */
	public final RetrievedCloneSetInfo getAfterCloneSet() {
		return this.afterCloneSet;
	}

	/**
	 * �o�^����Ă���u���b�N�y�A��S�Ď擾
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
	 * ID���w�肵�ău���b�N�y�A���擾
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
	 * �u���b�N�y�A��ǉ�
	 * 
	 * @param blockPair
	 */
	public void addBlockPair(final AbstractBlockPairInfo blockPair) {
		blockPairs.put(blockPair.getId(), blockPair);
	}

	/**
	 * �u���b�N�y�A���ꊇ�Œǉ�
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
	 * �ǉ����ꂽ�u���b�N���擾
	 * 
	 * @return
	 */
	public Set<RetrievedBlockInfo> getAddedBlocks() {
		return Collections.unmodifiableSet(this.addedBlocks);
	}

	/**
	 * �ǉ����ꂽ�u���b�N��ǉ�
	 * 
	 * @param addedBlock
	 */
	public void addAddedBlock(final RetrievedBlockInfo addedBlock) {
		this.addedBlocks.add(addedBlock);
	}

	/**
	 * �폜���ꂽ�u���b�N���擾
	 * 
	 * @return
	 */
	public Set<RetrievedBlockInfo> getDeletedBlocks() {
		return Collections.unmodifiableSet(deletedBlocks);
	}

	/**
	 * �폜���ꂽ�u���b�N��ǉ�
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
