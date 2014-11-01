package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;

public class CodeFragment implements IDataElement<DBCodeFragment> {

	private final long id;

	private final DBCodeFragment core;

	public CodeFragment(final DBCodeFragment core) {
		this.id = core.getId();
		this.core = core;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public DBCodeFragment getCore() {
		return core;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CodeFragment)) {
			return false;
		}

		final CodeFragment another = (CodeFragment) obj;

		return this.core.equals(another.getCore());
	}

	@Override
	public int hashCode() {
		return this.core.hashCode();
	}

	@Override
	public String toString() {
		return this.core.toString();
	}

}
