package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;

/**
 * This class represents source file.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class SourceFile<E extends IProgramElement> implements
		IDataElement<DBSourceFile> {

	/**
	 * The id of this source file
	 */
	private final long id;

	/**
	 * The core of this source file
	 */
	private final DBSourceFile core;

	/**
	 * The contents of this source file
	 */
	private final SortedMap<Integer, E> contents;

	/**
	 * The constructor with core
	 * 
	 * @param core
	 *            the core
	 */
	public SourceFile(final DBSourceFile core) {
		this.id = core.getId();
		this.core = core;
		this.contents = new TreeMap<Integer, E>();
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public DBSourceFile getCore() {
		return core;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof SourceFile)) {
			return false;
		}

		final SourceFile<?> another = (SourceFile<?>) obj;

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
	 * Get the contents of this source file as an unmodifiable map.
	 * 
	 * @return the map having the contents of this source file, each of whose
	 *         key is the position, each of whose value is the content
	 */
	public SortedMap<Integer, E> getContents() {
		return Collections.unmodifiableSortedMap(contents);
	}

	/**
	 * Set the contents of this source file.
	 * 
	 * @param contents
	 *            the contents to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if the contents include those that are in other source files,
	 *             some of the contents locate at the same position, or the
	 *             given collection is <code>null</code>
	 */
	public void setContents(final Collection<E> contents) {
		if (contents == null) {
			throw new IllegalArgumentException(
					"the specified collection of contents is null");
		}

		this.contents.clear();
		for (final E content : contents) {
			if (!this.core.equals(content.getOwnerSourceFile())) {
				throw new IllegalArgumentException(
						"the content is not in the source file");
			}
			if (this.contents.containsKey(content.getPosition())) {
				throw new IllegalArgumentException("duplicate position");
			}
			this.contents.put(content.getPosition(), content);
		}
	}

	/**
	 * Get the path of this source file.
	 * 
	 * @return the path of this source file
	 */
	public String getPath() {
		return this.core.getPath();
	}

}
