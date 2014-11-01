package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange;

public class FileChange implements IDataElement<DBFileChange> {

	private final long id;

	private final DBFileChange core;

	public FileChange(final DBFileChange core) {
		this.id = core.getId();
		this.core = core;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public DBFileChange getCore() {
		return core;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof FileChange)) {
			return false;
		}

		final FileChange another = (FileChange) obj;

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
