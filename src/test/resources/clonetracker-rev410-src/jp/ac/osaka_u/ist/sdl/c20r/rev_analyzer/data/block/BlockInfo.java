package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.CorroborationMetric;

/**
 * �u���b�N��\�����ۃN���X
 * 
 * @author k-hotta
 * 
 */
public abstract class BlockInfo extends UnitInfo {

	/**
	 * �u���b�N�̎��
	 */
	protected final BlockType bType;

	public BlockInfo(long ownerRevisionId, long ownerFileId, long id,
			BlockType bType, String core, CorroborationMetric cm,
			int startLine, int endLine, int length, String ownerFilePath, String discriminator) {
		super(ownerRevisionId, ownerFileId, id, core, cm, UnitType.BLOCK,
				startLine, endLine, length, ownerFilePath, discriminator);
		this.bType = bType;
	}

	/**
	 * �u���b�N�̎�ނ��擾����
	 */
	public final BlockType getBlockType() {
		return bType;
	}

	@Override
	public String getReplaceStatement() {
		return bType.toString() + ": " + getDistinctiveStatement();
	}
	
	@Override
	public String getUnitTypeString() {
		return bType.name();
	}

	/**
	 * ����̃u���b�N�����ʂ��邽�߂̕�������擾����
	 * 
	 * @return
	 */
	public abstract String getDistinctiveStatement();

}
