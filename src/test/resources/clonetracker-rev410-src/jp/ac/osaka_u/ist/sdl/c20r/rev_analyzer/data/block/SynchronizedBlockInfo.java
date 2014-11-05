package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.BlockCRD;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.CorroborationMetric;

public class SynchronizedBlockInfo extends BlockInfo {

	private final String expression;

	public SynchronizedBlockInfo(long ownerRevisionId, long ownerFileId,
			long id, String core, String expression, CorroborationMetric cm,
			int startLine, int endLine, int length, String ownerFilePath,
			String discriminator) {
		super(ownerRevisionId, ownerFileId, id, BlockType.SYNCHRONIZED, core,
				cm, startLine, endLine, length, ownerFilePath, discriminator);
		this.expression = expression;
		this.crdElement = new BlockCRD(bType, getDistinctiveStatement(), cm);
	}

	@Override
	public String getDistinctiveStatement() {
		return expression;
	}

	public String getExpression() {
		return expression;
	}

}
