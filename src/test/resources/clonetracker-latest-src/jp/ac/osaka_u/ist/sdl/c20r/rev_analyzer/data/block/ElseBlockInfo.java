package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block;

import java.util.LinkedList;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.BlockCRD;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.crd.CorroborationMetric;

public class ElseBlockInfo extends BlockInfo {

	private final List<String> predicates;

	private final String concatenatedPredicates;

	public ElseBlockInfo(long ownerRevisionId, long ownerFileId, long id,
			String core, List<String> predicates, CorroborationMetric cm,
			int startLine, int endLine, int length, String ownerFilePath,
			String discriminator) {
		super(ownerRevisionId, ownerFileId, id, BlockType.ELSE, core, cm,
				startLine, endLine, length, ownerFilePath, discriminator);
		this.predicates = new LinkedList<String>();
		this.predicates.addAll(predicates);

		StringBuilder builder = new StringBuilder();
		if (!predicates.isEmpty()) {
			builder.append("(");
			boolean isFirstPredicate = true;
			for (String predicate : predicates) {
				if (!isFirstPredicate) {
					builder.append(" && ");
				} else {
					isFirstPredicate = false;
				}
				builder.append("!(");
				builder.append(predicate);
				builder.append(")");
			}
			builder.append(")");
		}

		this.concatenatedPredicates = builder.toString();
		this.crdElement = new BlockCRD(bType, getDistinctiveStatement(), cm);
	}

	@Override
	public String getDistinctiveStatement() {
		return concatenatedPredicates;
	}

	public List<String> getPredicates() {
		return predicates;
	}

	public String getConcatenatedPredicates() {
		return concatenatedPredicates;
	}

}
