package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.BlockCRD;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.CorroborationMetric;

/**
 * Šg’£for•¶‚ð•\‚·ƒNƒ‰ƒX
 * 
 * @author k-hotta
 * 
 */
public class EnhancedForBlockInfo extends BlockInfo {

	private final String parameter;

	private final String expression;

	public EnhancedForBlockInfo(long ownerRevisionId, long ownerFileId,
			long id, String core, String parameter, String expression,
			CorroborationMetric cm, int startLine, int endLine, int length,
			String ownerFilePath, String discriminator) {
		super(ownerRevisionId, ownerFileId, id, BlockType.FOR, core, cm,
				startLine, endLine, length, ownerFilePath, discriminator);
		this.parameter = parameter;
		this.expression = expression;
		this.crdElement = new BlockCRD(bType, getDistinctiveStatement(), cm);
	}

	@Override
	public String getDistinctiveStatement() {
		return expression;
	}

	public String getParameter() {
		return parameter;
	}

	public String getExpression() {
		return expression;
	}

}
