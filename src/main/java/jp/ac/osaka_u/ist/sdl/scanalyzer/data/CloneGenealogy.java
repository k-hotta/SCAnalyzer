package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneGenealogy;

/**
 * This class represents clone genealogy.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class CloneGenealogy<E extends IProgramElement> implements
		IDataElement<DBCloneGenealogy> {

	/**
	 * The id of this fragment
	 */
	private final long id;

	/**
	 * The core of this clone genealogy
	 */
	private final DBCloneGenealogy core;

	/**
	 * The start version of this genealogy
	 */
	private Version<E> startVersion;

	/**
	 * The end version of this genealogy
	 */
	private Version<E> endVersion;

	/**
	 * The clone classes contained in this genealogy
	 */
	private SortedMap<Long, CloneClass<E>> cloneClasses;

	/**
	 * The clone class mapping contained in this genealogy
	 */
	private SortedMap<Long, CloneClassMapping<E>> cloneClassMappings;

	/**
	 * The constructor with core.
	 * 
	 * @param core
	 *            the core
	 */
	public CloneGenealogy(final DBCloneGenealogy core) {
		this.id = core.getId();
		this.core = core;
		this.startVersion = null;
		this.endVersion = null;
		this.cloneClasses = new TreeMap<>();
		this.cloneClassMappings = new TreeMap<>();
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public DBCloneGenealogy getCore() {
		return core;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CloneGenealogy)) {
			return false;
		}

		final CloneGenealogy<?> another = (CloneGenealogy<?>) obj;

		return this.core.equals(another.getCore());
	}

	@Override
	public int hashCode() {
		return this.core.hashCode();
	}

	/**
	 * Get the start version of this genealogy.
	 * 
	 * @return the start version of this genealogy.
	 * 
	 * @throws IllegalStateException
	 *             If the start version has not been set
	 */
	public Version<E> getStartVersion() {
		if (startVersion == null) {
			throw new IllegalStateException(
					"the start version has not been set");
		}

		return startVersion;
	}

	/**
	 * Set the start version of this genealogy with the specified one.
	 * 
	 * @param startVersion
	 *            the start version to be set
	 * 
	 * @throws IllegalArgumentException
	 *             If the given start version does not match to that in the core
	 *             of this genealogy, or it is <code>null</code>.
	 */
	public void setStartVersion(final Version<E> startVersion) {
		if (startVersion == null) {
			throw new IllegalArgumentException(
					"the given start version is null");
		}
		if (!this.core.getStartVersion().equals(startVersion.getCore())) {
			throw new IllegalArgumentException(
					"the given start version does not match to that in the core");
		}

		this.startVersion = startVersion;
	}

	/**
	 * Get the end version of this genealogy.
	 * 
	 * @return the end version of this genealogy.
	 * 
	 * @throws IllegalStateException
	 *             If the end version has not been set
	 */
	public Version<E> getEndVersion() {
		if (endVersion == null) {
			throw new IllegalStateException("the end version has not been set");
		}

		return endVersion;
	}

	/**
	 * Set the end version of this genealogy with the specified one.
	 * 
	 * @param endVersion
	 *            the end version to be set
	 * 
	 * @throws IllegalArgumentException
	 *             If the given end version does not match to that in the core
	 *             of this genealogy, or it is <code>null</code>.
	 */
	public void setEndVersion(final Version<E> endVersion) {
		if (endVersion == null) {
			throw new IllegalArgumentException("the given end version is null");
		}
		if (!this.core.getStartVersion().equals(endVersion.getCore())) {
			throw new IllegalArgumentException(
					"the given end version does not match to that in the core");
		}

		this.endVersion = endVersion;
	}

	/**
	 * Get the clone classes in this genealogy as an unmodifiable map.
	 * 
	 * @return an unmodifiable map that contains the clone classes in this
	 *         genealogy
	 * 
	 * @throws IllegalStateException
	 *             if the clone classes have not been set
	 */
	public SortedMap<Long, CloneClass<E>> getCloneClasses() {
		if (cloneClasses.isEmpty()) {
			throw new IllegalStateException("there are no clone classes");
		}

		return Collections.unmodifiableSortedMap(cloneClasses);
	}

	/**
	 * Get the clone class mappings in this genealogy as an unmodifiable map.
	 * 
	 * @return an unmodifiable map that contains the clone class mappings in
	 *         this genealogy
	 * 
	 * @throws IllegalStateException
	 *             if the clone class mappings have not been set
	 */
	public SortedMap<Long, CloneClassMapping<E>> getCloneClassMapping() {
		if (cloneClassMappings.isEmpty()) {
			throw new IllegalStateException("there are no clone class mappings");
		}

		return Collections.unmodifiableSortedMap(cloneClassMappings);
	}

	/**
	 * Add the given clone class mapping into this genealogy. The related clone
	 * classes will be added as well.
	 * 
	 * @param cloneClassMapping
	 *            the clone class mapping to be added
	 * 
	 * @throws IllegalArgumentException
	 *             If the given clone class mapping is not included in those of
	 *             the core, or it is <code>null</code>
	 */
	public void addCloneClassMapping(
			final CloneClassMapping<E> cloneClassMapping) {
		if (cloneClassMapping == null) {
			throw new IllegalArgumentException(
					"the given clone class mapping is null");
		}
		if (!this.core.getCloneClassMappings().contains(
				cloneClassMapping.getCore())) {
			throw new IllegalArgumentException(
					"the given clona class mapping is not included in those of the core");
		}

		this.cloneClassMappings.put(cloneClassMapping.getId(),
				cloneClassMapping);

		if (cloneClassMapping.getOldCloneClass() != null) {
			this.cloneClasses.put(cloneClassMapping.getOldCloneClass().getId(),
					cloneClassMapping.getOldCloneClass());
		}

		if (cloneClassMapping.getNewCloneClass() != null) {
			this.cloneClasses.put(cloneClassMapping.getNewCloneClass().getId(),
					cloneClassMapping.getNewCloneClass());
		}
	}

}
