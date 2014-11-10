package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragmentMapping;

/**
 * This class represents mapping between code fragments.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class CodeFragmentMapping<E extends IProgramElement> implements
		IDataElement<DBCodeFragmentMapping> {

	/**
	 * The id of this mapping
	 */
	private final long id;

	/**
	 * The core of this mapping
	 */
	private final DBCodeFragmentMapping core;

	/**
	 * The old code fragment of this mapping
	 */
	private CodeFragment<E> oldCodeFragment;

	/**
	 * The new code fragment of this mapping
	 */
	private CodeFragment<E> newCodeFragment;

	/**
	 * The owner clone class mapping of this mapping
	 */
	private CloneClassMapping<E> cloneClassMapping;

	public CodeFragmentMapping(final DBCodeFragmentMapping core) {
		this.id = core.getId();
		this.core = core;
		this.oldCodeFragment = null;
		this.newCodeFragment = null;
		this.cloneClassMapping = null;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public DBCodeFragmentMapping getCore() {
		return core;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CodeFragmentMapping)) {
			return false;
		}

		final CodeFragmentMapping<?> another = (CodeFragmentMapping<?>) obj;

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
	 * Get the old code fragment of this mapping.
	 * 
	 * @return the old code fragment of this mapping
	 * 
	 * @throws IllegalStateException
	 *             if the old code fragment and/or the new code fragment have
	 *             not been set
	 */
	public CodeFragment<E> getOldCodeFragment() {
		if (oldCodeFragment == null && newCodeFragment == null) {
			throw new IllegalStateException(
					"the old code fragment and/or the new code fragment have not been set");
		}

		return this.oldCodeFragment;
	}

	/**
	 * Set the old code fragment of this mapping with the specified one.
	 * 
	 * @param oldCodeFragment
	 *            the old code fragment to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if any of the following statements hold: (1) the given old
	 *             code fragment is <code>null</code> but that in the core is
	 *             not <code>null</code>, (2) the given old code fragment is not
	 *             <code>null</code> but that in the core is <code>null</code>,
	 *             (3) the given old code fragment does not match to that in the
	 *             core
	 */
	public void setOldCodeFragment(final CodeFragment<E> oldCodeFragment) {
		if (oldCodeFragment != null) {
			if (this.core.getOldCodeFragment() == null) {
				throw new IllegalArgumentException(
						"the given old code fragment is not null, but that in the core is null");
			} else if (!this.core.getOldCodeFragment().equals(
					oldCodeFragment.getCore())) {
				throw new IllegalArgumentException(
						"the given old code fragment does not match to that in the core");
			}
		} else if (this.core.getOldCodeFragment() != null) {
			throw new IllegalArgumentException(
					"the given old code fragment is null, but that in the core is not");
		}

		this.oldCodeFragment = oldCodeFragment;
	}

	/**
	 * Get the new code fragment of this mapping.
	 * 
	 * @return the new code fragment of this mapping
	 * 
	 * @throws IllegalStateException
	 *             if the old code fragment and/or the new code fragment have
	 *             not been set
	 */
	public CodeFragment<E> getNewCodeFragment() {
		if (oldCodeFragment == null && newCodeFragment == null) {
			throw new IllegalStateException(
					"the old code fragment and/or the new code fragment have not been set");
		}

		return this.newCodeFragment;
	}

	/**
	 * Set the new code fragment of this mapping with the specified one.
	 * 
	 * @param newCodeFragment
	 *            the new code fragment to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if any of the following statements hold: (1) the given code
	 *             fragment is <code>null</code> but that in the core is not
	 *             <code>null</code>, (2) the given code fragment is not
	 *             <code>null</code> but that in the core is <code>null</code>,
	 *             (3) the given code fragment does not match to that in the
	 *             core
	 */
	public void setNewCodeFragment(final CodeFragment<E> newCodeFragment) {
		if (newCodeFragment != null) {
			if (this.core.getNewCodeFragment() == null) {
				throw new IllegalArgumentException(
						"the given new code fragment is not null, but that in the core is null");
			} else if (!this.core.getNewCodeFragment().equals(
					newCodeFragment.getCore())) {
				throw new IllegalArgumentException(
						"the given new code fragment does not match to that in the core");
			}
		} else if (this.core.getNewCodeFragment() != null) {
			throw new IllegalArgumentException(
					"the given new code fragment is null, but that in the core is not");
		}

		this.newCodeFragment = newCodeFragment;
	}

	/**
	 * Get the owner clone class mapping of this mapping.
	 * 
	 * @return the owner clone class mapping of this mapping
	 * 
	 * @throws IllegalStateException
	 *             if the clone class mapping has not been set
	 */
	public CloneClassMapping<E> getCloneClassMapping() {
		if (cloneClassMapping == null) {
			throw new IllegalStateException(
					"the clone class mapping has not been set");
		}

		return cloneClassMapping;
	}

	/**
	 * Set the owner clone class mapping of this mapping with the specified one.
	 * 
	 * @param cloneClassMapping
	 *            the owner clone class mapping to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if the given clone class mapping does not match to that in
	 *             the core of this mapping, or the given clone class mapping is
	 *             <code>null</code>
	 */
	public void setCloneClassMapping(
			final CloneClassMapping<E> cloneClassMapping) {
		if (cloneClassMapping == null) {
			throw new IllegalArgumentException(
					"the given clone class mapping is null");
		}

		if (!this.core.getCloneClassMapping().equals(
				cloneClassMapping.getCore())) {
			throw new IllegalArgumentException(
					"the given clone class mapping does not match to that in the core");
		}

		this.cloneClassMapping = cloneClassMapping;
	}

}
