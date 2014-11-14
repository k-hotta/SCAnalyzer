package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

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
	 * The mapping of code fragments relating to this clone class mapping
	 */
	private final SortedMap<Long, CodeFragmentMapping<E>> codeFragmentMappings;

	/**
	 * The owner version of this mapping
	 */
	private Version<E> version;

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
		this.codeFragmentMappings = new TreeMap<>();
		this.version = null;
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

		this.newCloneClass = newCloneClass;
	}

	/**
	 * Get the mapping of code fragments as a sorted map.
	 * 
	 * @return the mapping of code fragments
	 */
	public SortedMap<Long, CodeFragmentMapping<E>> getCodeFragmentMappings() {
		return Collections.unmodifiableSortedMap(codeFragmentMappings);
	}

	/**
	 * Add the given code fragment mapping to this instance.
	 * 
	 * @param codeFragmentMapping
	 *            a code fragment mapping to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the given code fragment mapping does not match to that in
	 *             the core of this instance, or the given code fragment is
	 *             <code>null</code>
	 */
	public void addCodeFragmentMappings(
			final CodeFragmentMapping<E> codeFragmentMapping) {
		if (codeFragmentMapping == null) {
			throw new IllegalArgumentException(
					"the given code fragment mapping is null");
		}

		if (!this.core.getCodeFragmentMappings().contains(
				codeFragmentMapping.getCore())) {
			throw new IllegalArgumentException(
					"the given code fragment mapping does not match to that in the core");
		}

		this.codeFragmentMappings.put(codeFragmentMapping.getId(),
				codeFragmentMapping);
	}

	/**
	 * Get the owner version of this mapping.
	 * 
	 * @return the owner version of this mapping
	 * 
	 * @throws IllegalStateException
	 *             if the version has not been set
	 */
	public Version<E> getVersion() {
		if (version == null) {
			throw new IllegalStateException("the version has not been set");
		}

		return version;
	}

	/**
	 * Set the owner version of this mapping with the specified one.
	 * 
	 * @param version
	 *            the version to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if the given version does not match to that in the core, or
	 *             the given version is <code>null</code>
	 */
	public void setVersion(final Version<E> version) {
		if (version == null) {
			throw new IllegalArgumentException("the given version is null");
		}

		if (!this.core.getVersion().equals(version.getCore())) {
			throw new IllegalArgumentException(
					"the given version does not match to that in the core");
		}

		this.version = version;
	}

}
