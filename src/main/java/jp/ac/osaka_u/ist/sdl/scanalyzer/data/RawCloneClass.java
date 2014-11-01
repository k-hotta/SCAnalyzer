package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawCloneClass;

public class RawCloneClass implements IDataElement<DBRawCloneClass> {

	private final long id;

	private final DBRawCloneClass core;

	public RawCloneClass(final DBRawCloneClass core) {
		this.id = core.getId();
		this.core = core;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public DBRawCloneClass getCore() {
		return core;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof RawCloneClass)) {
			return false;
		}

		final RawCloneClass another = (RawCloneClass) obj;

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
