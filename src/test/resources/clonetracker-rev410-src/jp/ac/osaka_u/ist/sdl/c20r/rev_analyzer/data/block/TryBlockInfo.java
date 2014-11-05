package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block;

import java.util.LinkedList;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.BlockCRD;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.CorroborationMetric;

/**
 * try •¶‚ð•\‚·ƒNƒ‰ƒX
 * 
 * @author k-hotta
 * 
 */
public class TryBlockInfo extends BlockInfo {

	private final List<String> caughtExceptionTypes;

	private final String concatenatedCaughtExceptionTypes;

	public TryBlockInfo(long ownerRevisionId, long ownerFileId, long id,
			String core, List<String> caughtExceptionTypes,
			CorroborationMetric cm, int startLine, int endLine, int length,
			String ownerFilePath, String discriminator) {
		super(ownerRevisionId, ownerFileId, id, BlockType.TRY, core, cm,
				startLine, endLine, length, ownerFilePath, discriminator);
		this.caughtExceptionTypes = new LinkedList<String>();
		this.caughtExceptionTypes.addAll(caughtExceptionTypes);
		StringBuilder builder = new StringBuilder();
		for (String caughtExceptionType : caughtExceptionTypes) {
			builder.append(caughtExceptionType + " ");
		}
		this.concatenatedCaughtExceptionTypes = builder.toString();
		this.crdElement = new BlockCRD(bType, getDistinctiveStatement(), cm);
	}

	@Override
	public String getDistinctiveStatement() {
		return concatenatedCaughtExceptionTypes;
	}

	public List<String> getCaughtExceptionTypes() {
		return caughtExceptionTypes;
	}

	public String getConcatenatedCaughtExceptionTypes() {
		return concatenatedCaughtExceptionTypes;
	}

}
