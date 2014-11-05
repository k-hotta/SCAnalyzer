package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;

/**
 * This class represents mapping between clone classes.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class CloneClassMapping<E extends IProgramElement> implements
		IDataElement<DBCloneClassMapping> {

	/**
	 * The id of this mapping
	 */
	private final long id;

	/**
	 * The core of this mapping
	 */
	private final DBCloneClassMapping core;

	/**
	 * The old clone class of this mapping
	 */
	private CloneClass<E> oldCloneClass;

	/**
	 * The new clone class of this mapping
	 */
	private CloneClass<E> newCloneClass;

	/**
	 * The constructor with core
	 * 
	 * @param core
	 *            the core
	 */
	public CloneClassMapping(final DBCloneClassMapping core) {
		this.id = core.getId();
		this.core = core;
		this.oldCloneClass = null;
		this.newCloneClass = null;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public DBCloneClassMapping getCore() {
		return core;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CloneClassMapping)) {
			return false;
		}

		final CloneClassMapping<?> another = (CloneClassMapping<?>) obj;

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
	 * Get the old clone class of this mapping.
	 * 
	 * @return the old clone class of this mapping
	 * 
	 * @throws IllegalStateException
	 *             if the old clone class and/or the new clone class have not
	 *             been set
	 */
	public CloneClass<E> getOldCloneClass() {
		if (oldCloneClass == null && newCloneClass == null) {
			throw new IllegalStateException(
					"the old clone class and/or the new clone class have not been set");
		}

		return this.oldCloneClass;
	}

	/**
	 * Set the old clone class of this mapping with the specified one.
	 * 
	 * @param oldCloneClass
	 *            the old clone class to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if any of the following statements hold: (1) the given old
	 *             clone class is <code>null</code> but that in the core is not
	 *             <code>null</code>, (2) the given old clone class is not
	 *             <code>null</code> but that in the core is <code>null</code>,
	 *             (3) the given old clone class does not match to that in the
	 *             core
	 */
	public void setOldCloneClass(final CloneClass<E> oldCloneClass) {
		if (oldCloneClass != null) {
			if (this.core.getOldCloneClass() == null) {
				throw new IllegalArgumentException(
						"the given old clone class is not null, but that in the core is null");
			} else if (!this.core.getOldCloneClass().equals(
					oldCloneClass.getCore())) {
				throw new IllegalArgumentException(
						"the given old clone class does not match to that in the core");
			}
		} else if (this.core.getOldCloneClass() != null) {
			throw new IllegalArgumentException(
					"the given old clone class is null, but that in the core is not");
		}

		this.oldCloneClass = oldCloneClass;
	}

	/**
	 * Get the new clone class of this mapping.
	 * 
	 * @return the new clone class of this mapping
	 * 
	 * @throws IllegalStateException
	 *             if the old clone class and/or the new clone class have not
	 *             been set
	 */
	public CloneClass<E> getNewCloneClass() {
		if (oldCloneClass == null && newCloneClass == null) {
			throw new IllegalStateException(
					"the old clone class and/or the new clone class have not been set");
		}

		return this.newCloneClass;
	}

	/**
	 * Set the new clone class of this mapping with the specified one.
	 * 
	 * @param newCloneClass
	 *            the new clone class to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if any of the following statements hold: (1) the given new
	 *             clone class is <code>null</code> but that in the core is not
	 *             <code>null</code>, (2) the given new clone class is not
	 *             <code>null</code> but that in the core is <code>null</code>,
	 *             (3) the given new clone class does not match to that in the
	 *             core
	 */
	public void setNewCloneClass(final CloneClass<E> newCloneClass) {
		if (newCloneClass != null) {
			if (this.core.getNewCloneClass() == null) {
				throw new IllegalArgumentException(
						"the given new clone class is not null, but that in the core is null");
			} else if (!this.core.getNewCloneClass().equals(
					newCloneClass.getCore())) {
				throw new IllegalArgumentException(
						"the given new clone class does not match to that in the core");
			}
		} else if (this.core.getNewCloneClass() != null) {
			throw new IllegalArgumentException(
					"the given new clone class is null, but that in the core is not");
		}
	}

}
