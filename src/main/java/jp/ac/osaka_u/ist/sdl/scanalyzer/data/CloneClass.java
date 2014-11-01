package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;

/**
 * This class represents a clone class.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of the program element
 */
public class CloneClass<E extends IProgramElement> implements
		IDataElement<DBCloneClass> {

	/**
	 * The id of this clone class
	 */
	private final long id;

	/**
	 * The core of this clone class
	 */
	private final DBCloneClass core;

	/**
	 * The owner version of this clone class
	 */
	private Version<E> version;

	/**
	 * The code fragments in this clone class
	 */
	private final SortedMap<Long, CodeFragment<E>> codeFragments;

	/**
	 * The constructor with core
	 * 
	 * @param core
	 *            the core
	 */
	public CloneClass(final DBCloneClass core) {
		this.id = core.getId();
		this.core = core;
		this.version = null;
		this.codeFragments = new TreeMap<Long, CodeFragment<E>>();
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public DBCloneClass getCore() {
		return core;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CloneClass)) {
			return false;
		}

		final CloneClass<?> another = (CloneClass<?>) obj;

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
	 * Get the owner version of this clone class.
	 * 
	 * @return the owner version of this clone class
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
	 * Set the owner version of this clone class with the specified one. The
	 * core of the given version must match to that in the core of this clone
	 * class.
	 * 
	 * @param version
	 *            the owner version to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if the given version doesn't match to that in the core of
	 *             this clone class, or the given version is <code>null</code>
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
	 * Get the code fragments of this clone class as an unmodifiable map.
	 * 
	 * @return the map having the code fragments in this clone class, each of
	 *         whose key is the id of a code fragment, each of whose value is
	 *         the code fragment
	 * 
	 * @throws IllegalStateException
	 *             if the number of code fragments is less than 2
	 */
	public SortedMap<Long, CodeFragment<E>> getCodeFragments() {
		if (this.codeFragments.size() < 2) {
			throw new IllegalStateException(
					"the clone class doesn't have enough number of code fragments:"
							+ " it should have at least two fragments");
		}
		return Collections.unmodifiableSortedMap(codeFragments);
	}

	/**
	 * Add the given code fragment to this clone class.
	 * 
	 * @param codeFragment
	 *            the code fragment to be added
	 * @throws IllegalArgumentException
	 *             if the given code fragment is not included in the code
	 *             fragments in the core, or the given code fragment is
	 *             <code>null</code>
	 */
	public void addCodeFragment(final CodeFragment<E> codeFragment) {
		if (codeFragment == null) {
			throw new IllegalArgumentException(
					"the given code fragment is null");
		}

		if (!this.core.getCodeFragments().contains(codeFragment.getCore())) {
			throw new IllegalArgumentException(
					"the given code fragment is not in the clone class");
		}

		this.codeFragments.put(codeFragment.getId(), codeFragment);
	}

}
