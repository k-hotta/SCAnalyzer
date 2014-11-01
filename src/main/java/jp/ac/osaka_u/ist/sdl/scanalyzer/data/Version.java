package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;

public class Version implements IDataElement<DBVersion> {

	private final long id;

	private final DBVersion core;

	public Version(final DBVersion core) {
		this.id = core.getId();
		this.core = core;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public DBVersion getCore() {
		return core;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Version)) {
			return false;
		}

		final Version another = (Version) obj;

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
