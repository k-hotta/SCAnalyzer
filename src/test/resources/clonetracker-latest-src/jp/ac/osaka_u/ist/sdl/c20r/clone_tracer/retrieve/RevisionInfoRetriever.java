package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve;

import java.sql.ResultSet;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.RetrievedRevisionInfo;

public class RevisionInfoRetriever extends
		AbstractElementRetriever<RetrievedRevisionInfo> {
	
	private final int revisionNum;

	public RevisionInfoRetriever(final long revisionId, final int revisionNum) {
		super(revisionId);
		this.revisionNum = revisionNum;
	}

	@Override
	protected String getSelectStatement(long revisionId) {
		return "select * from REVISION where REVISION_NUM = " + revisionNum;
	}

	@Override
	protected RetrievedRevisionInfo createElement(ResultSet rs)
			throws Exception {
		final long revisionId = rs.getLong(1);
		final int revisionNum = rs.getInt(2);
		final int files = rs.getInt(3);
		final int blocks = rs.getInt(4);
		final int cloneSets = rs.getInt(5);

		return new RetrievedRevisionInfo(revisionId, revisionNum, files,
				blocks, cloneSets);
	}

}
