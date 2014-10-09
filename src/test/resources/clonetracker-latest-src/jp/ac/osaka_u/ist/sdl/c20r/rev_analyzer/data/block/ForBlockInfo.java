package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.BlockCRD;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.CorroborationMetric;

/**
 * For•¶‚ð•\‚·ƒNƒ‰ƒX
 * 
 * @author k-hotta
 * 
 */
public class ForBlockInfo extends BlockInfo {

	private final String initializer;

	private final String experssion;

	private final String updater;

	public ForBlockInfo(long ownerRevisionId, long ownerFileId, long id,
			String core, String initializer, String expression, String updater,
			CorroborationMetric cm, int startLine, int endLine, int length,
			String ownerFilePath, String discriminator) {
		super(ownerRevisionId, ownerFileId, id, BlockType.FOR, core, cm,
				startLine, endLine, length, ownerFilePath, discriminator);
		this.initializer = initializer;
		this.experssion = expression;
		this.updater = updater;
		this.crdElement = new BlockCRD(bType, getDistinctiveStatement(), cm);
	}

	@Override
	public String getDistinctiveStatement() {
		return experssion;
	}

	public String getInitializer() {
		return initializer;
	}

	public String getExpression() {
		return experssion;
	}

	public String getUpdater() {
		return updater;
	}

}
