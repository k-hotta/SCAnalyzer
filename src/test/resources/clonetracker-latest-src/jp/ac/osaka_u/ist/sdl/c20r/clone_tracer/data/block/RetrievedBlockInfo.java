package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block;

import java.util.ArrayList;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.AbstractRetrievedElementInfo;

public class RetrievedBlockInfo extends AbstractRetrievedElementInfo {

	/**
	 * �t�@�C��ID
	 */
	private final long fileId;

	/**
	 * �N���[���Z�b�gID
	 */
	private long cloneSetId;

	/**
	 * �ЂƂO���̃��j�b�g��ID
	 */
	private final long parentUnitId;

	/**
	 * �J�n���r�W����ID
	 */
	private final long startRevId;

	/**
	 * �I�����r�W����ID
	 */
	private final long endRevId;

	/**
	 * �J�n�s�ԍ�
	 */
	private final int startLine;

	/**
	 * �I���s�ԍ�
	 */
	private final int endLine;

	/**
	 * �n�b�V���l
	 */
	private final int hash;

	/**
	 * CRD�̕�����\�L
	 */
	private String crdStr;

	/**
	 * �u���b�N��CM�̒l
	 */
	private final int cm;

	/**
	 * ���̃u���b�N���܂ރu���b�N�y�A��ID
	 */
	private long blockPairId;

	/**
	 * ���̃u���b�N�����̃��r�W�����ւ̑J�ڎ��ɍ폜���ꂽ�t�@�C�����Ɋ܂܂�邩�ǂ���
	 */
	private final boolean inDeletedFile;

	/**
	 * ���̃u���b�N�̒���(= �g�[�N����)
	 */
	private final int length;

	/**
	 * ���̃u���b�N�̃T�C�N���}�`�b�N��
	 */
	private final int cc;

	/**
	 * ���̃u���b�N��Fan-Out��
	 */
	private final int fo;

	/**
	 * ���̃u���b�N�݂̂�CRD
	 */
	private final String discriminator;

	/**
	 * ���̃u���b�N���ǉ����ꂽ���ǂ���
	 */
	private final boolean isAdded;

	/**
	 * ���̃u���b�N���폜���ꂽ���ǂ���
	 */
	private final boolean isDeleted;

	/**
	 * ���̃u���b�N�����L����N���X�̖��O
	 */
	private final String rootClassName;

	/**
	 * ���̃u���b�N�����L���郁�\�b�h�̖��O
	 */
	private final String rootMethodName;

	private final List<String> rootMethodParams;

	private final String crdAfterRootMethod;
	
	private final String blockType;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param id
	 * @param revisionId
	 * @param fileId
	 * @param cloneSetId
	 * @param startLine
	 * @param endLine
	 * @param crdStr
	 */
	public RetrievedBlockInfo(final long id, final long fileId,
			final long parentUnitId, final long startRevId,
			final long endRevId, final int startLine, final int endLine,
			final int hash, final String crdStr, final int cm,
			final boolean inDeletedFile, final int length, final int cc,
			final int fo, final String discriminator, final boolean isAdded,
			final boolean isDeleted, final String rootClassName,
			final String rootMethodName, final String rootMethodParamsAsString,
			final String crdAfterRootMethod, final String blockType) {
		super(id);
		this.fileId = fileId;
		this.cloneSetId = -1;
		this.parentUnitId = parentUnitId;
		this.startRevId = startRevId;
		this.endRevId = endRevId;
		this.startLine = startLine;
		this.endLine = endLine;
		this.hash = hash;
		this.crdStr = crdStr;
		this.cm = cm;
		this.blockPairId = -1;
		this.inDeletedFile = inDeletedFile;
		this.length = length;
		this.cc = cc;
		this.fo = fo;
		this.discriminator = discriminator;
		this.isAdded = isAdded;
		this.isDeleted = isDeleted;
		this.rootClassName = rootClassName;
		this.rootMethodName = rootMethodName;
		this.rootMethodParams = new ArrayList<String>();
		if (rootMethodParamsAsString.length() != 0
				&& rootMethodParamsAsString.equals("N/A")) {
			for (final String param : rootMethodParamsAsString.split(",")) {
				this.rootMethodParams.add(param);
			}
		}
		this.crdAfterRootMethod = crdAfterRootMethod;
		this.blockType = blockType;
	}

	/**
	 * �t�@�C��ID���擾����
	 * 
	 * @return
	 */
	public final long getFileId() {
		return fileId;
	}

	/**
	 * �ЂƂO���̃��j�b�g��ID���擾����D�Ȃ����-1�D
	 */
	public final long getParentUnitId() {
		return parentUnitId;
	}

	/**
	 * �N���[���Z�b�gID���擾����
	 * 
	 * @return
	 */
	public final long getCloneSetId() {
		return cloneSetId;
	}

	/**
	 * �N���[���Z�b�gID���Z�b�g����
	 * 
	 * @param cloneSetId
	 */
	public final void setCloneSetId(final long cloneSetId) {
		this.cloneSetId = cloneSetId;
	}

	/**
	 * �J�n���r�W����ID���擾����
	 * 
	 * @return
	 */
	public final long getStartRevId() {
		return startRevId;
	}

	/**
	 * �I�����r�W����ID���擾����
	 * 
	 * @return
	 */
	public final long getEndRevId() {
		return endRevId;
	}

	/**
	 * �J�n�s�ԍ����擾����
	 * 
	 * @return
	 */
	public final int getStartLine() {
		return startLine;
	}

	/**
	 * �I���s�ԍ����擾����
	 * 
	 * @return
	 */
	public final int getEndLine() {
		return endLine;
	}

	/**
	 * �n�b�V���l���擾����
	 * 
	 * @return
	 */
	public final int getHash() {
		return hash;
	}

	/**
	 * CRD�̕�����\�L���擾����
	 * 
	 * @return
	 */
	public final String getCrdStr() {
		return crdStr;
	}

	/**
	 * CM�̒l���擾����
	 * 
	 * @return
	 */
	public final int getCm() {
		return cm;
	}

	/**
	 * ���̃u���b�N���܂ރu���b�N�y�A��ID���擾����
	 * 
	 * @return
	 */
	public final long getBlockPairId() {
		return blockPairId;
	}

	/**
	 * ���̃u���b�N�����̃��r�W�����ւ̑J�ڎ��ɍ폜���ꂽ�t�@�C�����Ɋ܂܂�邩�ǂ������擾����
	 * 
	 * @return
	 */
	public final boolean isInDeletedFile() {
		return inDeletedFile;
	}

	/**
	 * ���̃u���b�N�̃g�[�N�������擾����
	 * 
	 * @return
	 */
	public final int getLength() {
		return length;
	}

	/**
	 * ���̃u���b�N�̃T�C�N���}�`�b�N�����擾����
	 * 
	 * @return
	 */
	public final int getCC() {
		return cc;
	}

	/**
	 * ���̃u���b�N��Fan-Out�����擾����
	 * 
	 * @return
	 */
	public final int getFO() {
		return fo;
	}

	/**
	 * ���̃u���b�N�݂̂�CRD�\�L���擾����
	 * 
	 * @return
	 */
	public final String getDiscriminator() {
		return discriminator;
	}

	/**
	 * ���̃u���b�N���܂ރu���b�N�y�A��ID��ݒ肷��
	 * 
	 * @param blockPairId
	 */
	public final void setBlockPairId(final long blockPairId) {
		this.blockPairId = blockPairId;
	}

	/**
	 * CM���l�����Ȃ������ꍇ�ɁC�����̃u���b�N�����̃u���b�N�ƃ}�b�`���邩�𔻒肷��
	 * 
	 * @param anotherBlock
	 * @return
	 */
	public final boolean isMatchWithoutCm(RetrievedBlockInfo anotherBlock) {
		return crdStr.equals(anotherBlock.getCrdStr());
	}

	/**
	 * CM���l�������ꍇ�ɁC�����̃u���b�N�����̃u���b�N�ƃ}�b�`���邩�𔻒肷��
	 * 
	 * @param anotherBlock
	 * @return
	 */
	public final boolean isMatchWithCm(final RetrievedBlockInfo anotherBlock) {
		return isMatchWithoutCm(anotherBlock)
				&& this.cm == anotherBlock.getCm();
	}

	/**
	 * ���̃u���b�N���C�����ŗ^����ꂽID�̃��r�W�����Ƃ��̎��̃��r�W�����Ƃ̊ԂŏC�����ꂽ���ǂ������擾����
	 * 
	 * @param beforeRevId
	 * @return
	 */
	public final boolean isInChangedFile(final long revId) {
		return this.startRevId == revId || this.endRevId == revId;
	}

	/**
	 * ���̃u���b�N���������ꂽ�����擾����
	 * 
	 * @return
	 */
	public final boolean isAdded() {
		return isAdded;
	}

	/**
	 * ���̃u���b�N���폜���ꂽ�����擾����
	 * 
	 * @return
	 */
	public final boolean isDeleted() {
		return isDeleted;
	}

	public final String getRootClassName() {
		return rootClassName;
	}

	public final String getRootMethodName() {
		return rootMethodName;
	}

	public final List<String> getRootMethodParams() {
		return rootMethodParams;
	}

	public final void setCrdStr(final String crdStr) {
		this.crdStr = crdStr;
	}

	public final String getCrdAfterRootMethod() {
		return this.crdAfterRootMethod;
	}
	
	public final String getBlockType() {
		return this.blockType;
	}
	
}
