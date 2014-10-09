package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.BlockCRD;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.CorroborationMetric;

/**
 * catch ブロックを表すクラス
 * 
 * @author k-hotta
 * 
 */
public class CatchBlockInfo extends BlockInfo {

	private final String caughtExceptionType;

	public CatchBlockInfo(long ownerRevisionId, long ownerFileId, long id,
			String core, String caughtExceptionType, CorroborationMetric cm,
			int startLine, int endLine, int length, String ownerFilePath,
			String discriminator) {
		super(ownerRevisionId, ownerFileId, id, BlockType.CATCH, core, cm,
				startLine, endLine, length, ownerFilePath, discriminator);
		this.caughtExceptionType = caughtExceptionType;
		this.crdElement = new BlockCRD(bType, getDistinctiveStatement(), cm);
	}

	@Override
	public String getDistinctiveStatement() {
		return caughtExceptionType;
	}

	public String getCaughtExceptionType() {
		return caughtExceptionType;
	}

}
