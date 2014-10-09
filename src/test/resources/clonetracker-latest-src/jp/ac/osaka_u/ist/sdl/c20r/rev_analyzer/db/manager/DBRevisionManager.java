package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.manager;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.revision.RevisionInfo;

public class DBRevisionManager extends AbstractDBElementManager<RevisionInfo> {

	private static DBRevisionManager SINGLETON = null;

	private DBRevisionManager() {
		super();
	}

	public static DBRevisionManager getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new DBRevisionManager();
		}

		return SINGLETON;
	}

	@Override
	public String getMaxIdQuery() {
		return "select MAX(REVISION_ID) from REVISION";
	}

}
