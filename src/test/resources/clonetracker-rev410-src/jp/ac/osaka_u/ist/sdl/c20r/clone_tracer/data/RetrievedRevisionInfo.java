package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data;

/**
 * DB������o�������r�W��������\���N���X
 * 
 * @author k-hotta
 * 
 */
public class RetrievedRevisionInfo extends AbstractRetrievedElementInfo {

	/**
	 * ���r�W�����ԍ� (���r�W����ID�Ƃ͕ʕ�)
	 */
	private final int revisionNum;

	/**
	 * �t�@�C����
	 */
	private final int filesCount;

	/**
	 * �u���b�N��
	 */
	private final int blocksCount;

	/**
	 * �N���[���Z�b�g��
	 */
	private final int cloneSetsCount;

	public RetrievedRevisionInfo(final long id, final int revisionNum,
			final int filesCount, final int blocksCount,
			final int cloneSetsCount) {
		super(id);
		this.revisionNum = revisionNum;
		this.filesCount = filesCount;
		this.blocksCount = blocksCount;
		this.cloneSetsCount = cloneSetsCount;
	}
	
	/**
	 * ���r�W�����ԍ����擾����
	 * @return
	 */
	public final int getRevisionNum() {
		return revisionNum;
	}

	/**
	 * �t�@�C�������擾����
	 * @return
	 */
	public final int getFilesCount() {
		return filesCount;
	}
	
	/**
	 * �u���b�N�����擾����
	 * @return
	 */
	public final int getBlocksCount() {
		return blocksCount;
	}
	
	/**
	 * �N���[���Z�b�g�����擾����
	 * @return
	 */
	public final int getCloneSetsCount() {
		return cloneSetsCount;
	}
	
}
