package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawCloneClass;

/**
 * This class represents raw clone class.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class RawCloneClass<E extends IProgramElement> implements
		IDataElement<DBRawCloneClass> {

	/**
	 * The id of this raw clone class
	 */
	private final long id;

	/**
	 * The core of this raw clone class
	 */
	private final DBRawCloneClass core;

	/**
	 * The owner version of this raw clone class
	 */
	private Version<E> version;

	/**
	 * The raw cloned fragments in this raw clone class
	 */
	private final SortedMap<Long, RawClonedFragment<E>> rawClonedFragments;

	/**
	 * The constructor with core
	 * 
	 * @param core
	 *            the core
	 */
	public RawCloneClass(final DBRawCloneClass core) {
		this.id = core.getId();
		this.core = core;
		this.version = null;
		this.rawClonedFragments = new TreeMap<Long, RawClonedFragment<E>>();
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

		final RawCloneClass<?> another = (RawCloneClass<?>) obj;

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
	 * Get the owner version of this raw clone class.
	 * 
	 * @return the owner version of this raw clone class
	 * 
	 * @throws IllegalStateException
	 *             if the owner version has not been set
	 */
	public Version<E> getVersion() {
		if (version == null) {
			throw new IllegalStateException("the version has not been set");
		}
		return version;
	}

	/**
	 * Set the owner version of this raw clone class with the specified one. The
	 * core of the given version must match to that in the core of this raw
	 * clone class.
	 * 
	 * @param version
	 *            the owner version to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if the given version doesn't match to that in the core of
	 *             this raw clone class, or the given version is
	 *             <code>null</code>
	 */
	public void setVersion(final Version<E> version) {
		if (version == null) {
			throw new IllegalArgumentException("the given version is null");
		}

		if (!this.core.getVersion().equals(version.getCore())) {
			throw new IllegalArgumentException(
					"the given version doesn't match to that in the core");
		}

		this.version = version;
	}

	/**
	 * Get the raw code fragments of this raw clone class as an unmodifiable
	 * map.
	 * 
	 * @return the map having the raw code fragments in this raw clone class,
	 *         each of whose key is the id of a raw code fragment, each of whose
	 *         value is the raw code fragment
	 * 
	 * @throws IllegalStateException
	 *             if the number of raw code fragments is less than 2
	 */
	public SortedMap<Long, RawClonedFragment<E>> getRawClonedFragments() {
		if (this.rawClonedFragments.size() < 2) {
			throw new IllegalStateException(
					"the raw clone class doesn't have enough number of raw code fragments:"
							+ " it should have at least two fragments");
		}
		return Collections.unmodifiableSortedMap(rawClonedFragments);
	}

	/**
	 * Add the given raw code fragment to this raw clone class.
	 * 
	 * @param rawClonedFragment
	 *            the raw code fragment to be added
	 * @throws IllegalArgumentException
	 *             if the given raw code fragment is not included in the raw
	 *             code fragments in the core, or the given raw code fragment is
	 *             <code>null</code>
	 */
	public void addRawClonedFragment(
			final RawClonedFragment<E> rawClonedFragment) {
		if (rawClonedFragment == null) {
			throw new IllegalArgumentException(
					"the given raw cloned fragment is null");
		}

		if (!this.core.getElements().contains(rawClonedFragment.getCore())) {
			throw new IllegalArgumentException(
					"the given raw cloned fragment is not included in those of the core");
		}

		this.rawClonedFragments.put(rawClonedFragment.getId(),
				rawClonedFragment);
	}

}
