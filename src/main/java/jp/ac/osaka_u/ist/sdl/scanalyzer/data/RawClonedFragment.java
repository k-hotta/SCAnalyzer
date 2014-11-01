package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawClonedFragment;

public class RawClonedFragment implements IDataElement<DBRawClonedFragment> {

	private final long id;
	
	private final DBRawClonedFragment core;
	
	public RawClonedFragment(final DBRawClonedFragment core) {
		this.id = core.getId();
		this.core = core;
	}
	
	@Override
	public long getId() {
		return id;
	}

	@Override
	public DBRawClonedFragment getCore() {
		return core;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof RawClonedFragment)) {
			return false;
		}

		final RawClonedFragment another = (RawClonedFragment) obj;

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
