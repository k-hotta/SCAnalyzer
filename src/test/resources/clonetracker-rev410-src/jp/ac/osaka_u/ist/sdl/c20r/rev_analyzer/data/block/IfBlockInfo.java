package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.BlockCRD;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.CorroborationMetric;

/**
 * if •¶‚ð•\‚·ƒNƒ‰ƒX
 * 
 * @author k-hotta
 * 
 */
public class IfBlockInfo extends BlockInfo {

	private final String expression;

	public IfBlockInfo(long ownerRevisionId, long ownerFileId, long id,
			String core, String expression, CorroborationMetric cm,
			int startLine, int endLine, int length, String ownerFilePath,
			String discriminator) {
		super(ownerRevisionId, ownerFileId, id, BlockType.IF, core, cm,
				startLine, endLine, length, ownerFilePath, discriminator);
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
