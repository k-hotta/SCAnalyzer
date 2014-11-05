package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.AbstractBlockPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.RetrievedBlockInfo;

/**
 * �O��̃��r�W�����őΉ��Â���ꂽ�N���[���Z�b�g�y�A <br>
 * �v�f�̒ǉ��C�y�э폜�͓����ɋN���肦����
 * 
 * @author k-hotta
 * 
 */
public class MatchedCloneSetPairInfo extends AbstractCloneSetPairInfo {

	/**
	 * �n�b�V���l�ɕϓ������������ǂ���
	 */
	private final boolean containsHashChange;

	/**
	 * �v�f�̒ǉ������������ǂ���
	 */
	private boolean containsIncrease;

	/**
	 * �v�f�̍폜�����������ǂ���
	 */
	private boolean containsDecrease;

	/**
	 * �R���X�g���N�^
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
	 * �v�f�̒ǉ��C�폜�̗L���𔻒肷��
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
	 * �n�b�V���l�̕ϓ������������ǂ������擾����
	 * 
	 * @return
	 */
	public final boolean containsHashChange() {
		return this.containsHashChange;
	}

	/**
	 * �v�f�̒ǉ������������ǂ������擾����
	 * 
	 * @return
	 */
	public final boolean containsIncrease() {
		return this.containsIncrease;
	}

	/**
	 * �v�f�̍폜�����������ǂ������擾����
	 * 
	 * @return
	 */
	public final boolean containsDecrease() {
		return this.containsDecrease;
	}

}
