package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.Date;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;

/**
 * This class represents revision.
 * 
 * @author k-hotta
 *
 */
public class Revision implements IDataElement<DBRevision> {

	/**
	 * The id of this revision
	 */
	private final long id;

	/**
	 * The core of this revision
	 */
	private final DBRevision core;

	/**
	 * The constructor with core
	 * 
	 * @param core
	 *            the core
	 */
	public Revision(final DBRevision core) {
		if (core == null) {
			throw new IllegalArgumentException("core is null");
		}
		this.id = core.getId();
		this.core = core;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public DBRevision getCore() {
		return core;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Revision)) {
			return false;
		}

		final Revision another = (Revision) obj;

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

	/**
	 * Get the identifier of this revision.
	 * 
	 * @return the identifier of this revision
	 */
	public String getIdentifier() {
		return core.getIdentifier();
	}

	/**
	 * Get the date of this revision
	 * 
	 * @return the date of this revision
	 */
	public Date getDate() {
		return core.getDate();
	}

}
