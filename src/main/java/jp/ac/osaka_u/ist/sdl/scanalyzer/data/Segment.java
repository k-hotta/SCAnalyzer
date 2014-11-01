package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;

public class Segment implements IDataElement<DBSegment> {

	private final long id;

	private final DBSegment core;

	public Segment(final DBSegment core) {
		this.id = core.getId();
		this.core = core;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public DBSegment getCore() {
		return core;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Segment)) {
			return false;
		}

		final Segment another = (Segment) obj;

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
