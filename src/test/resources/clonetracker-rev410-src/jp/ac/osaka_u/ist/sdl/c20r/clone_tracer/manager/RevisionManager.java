package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.manager;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.RetrievedRevisionInfo;

/**
 * �O��̃��r�W�����Ƃ����̃f�[�^���Ǘ����邽�߂̃N���X
 * 
 * @author k-hotta
 * 
 */
public class RevisionManager {

	/**
	 * �O���r�W����
	 */
	private final RetrievedRevisionInfo beforeRevision;

	/**
	 * �O���r�W�����̃f�[�^
	 */
	private final RetrievedDataManager beforeDataManager;

	/**
	 * �ナ�r�W����
	 */
	private final RetrievedRevisionInfo afterRevision;

	/**
	 * �ナ�r�W�����̃f�[�^
	 */
	private final RetrievedDataManager afterDataManager;

	/**
	 * �u���b�N�y�A�̃}�l�[�W���[
	 */
	private final BlockPairManager blockPairManager;

	/**
	 * �N���[���Z�b�g�΂̃}�l�[�W���[
	 */
	private final CloneSetPairManager cloneManager;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param beforeRevision
	 * @param afterRevision
	 */
	public RevisionManager(final RetrievedRevisionInfo beforeRevision,
			final RetrievedRevisionInfo afterRevision) {
		this.beforeRevision = beforeRevision;
		this.afterRevision = afterRevision;
		this.beforeDataManager = new RetrievedDataManager();
		this.afterDataManager = new RetrievedDataManager();
		this.blockPairManager = new BlockPairManager();
		this.cloneManager = new CloneSetPairManager();
	}

	/**
	 * �O���r�W�������擾
	 * 
	 * @return
	 */
	public RetrievedRevisionInfo getBeforeRevision() {
		return beforeRevision;
	}

	/**
	 * �O���r�W�����̃f�[�^�}�l�[�W���[���擾
	 * 
	 * @return
	 */
	public RetrievedDataManager getBeforeDataManager() {
		return beforeDataManager;
	}

	/**
	 * �ナ�r�W�������擾
	 * 
	 * @return
	 */
	public RetrievedRevisionInfo getAfterRevision() {
		return afterRevision;
	}

	/**
	 * �ナ�r�W�����̃f�[�^�}�l�[�W���[���擾
	 * 
	 * @return
	 */
	public RetrievedDataManager getAfterDataManager() {
		return afterDataManager;
	}

	/**
	 * �u���b�N�y�A�̃}�l�[�W���[���擾
	 * 
	 * @return
	 */
	public BlockPairManager getBlockPairManager() {
		return blockPairManager;
	}

	/**
	 * �N���[���Z�b�g�y�A�̃}�l�[�W���[���擾
	 * 
	 * @return
	 */
	public CloneSetPairManager getCloneManager() {
		return cloneManager;
	}

}
