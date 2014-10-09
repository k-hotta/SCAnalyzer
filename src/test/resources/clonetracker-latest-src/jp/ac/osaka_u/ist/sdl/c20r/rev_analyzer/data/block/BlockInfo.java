package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.CorroborationMetric;

/**
 * ブロックを表す抽象クラス
 * 
 * @author k-hotta
 * 
 */
public abstract class BlockInfo extends UnitInfo {

	/**
	 * ブロックの種類
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
	 * ブロックの種類を取得する
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
	 * 同種のブロックを識別するための文字列を取得する
	 * 
	 * @return
	 */
	public abstract String getDistinctiveStatement();

}
