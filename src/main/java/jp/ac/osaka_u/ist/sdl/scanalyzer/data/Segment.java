package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;

/**
 * This class represents segment.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program elements
 */
public class Segment<E extends IProgramElement> implements
		IDataElement<DBSegment> {

	/**
	 * The id of this segment
	 */
	private final long id;

	/**
	 * The core of this segment
	 */
	private final DBSegment core;

	/**
	 * The contents of this segment. The key is the position in owner file, and
	 * the value is an element.
	 */
	private final SortedMap<Integer, E> contents;

	/**
	 * The owner source file of this segment
	 */
	private SourceFile<E> sourceFile;

	/**
	 * The owner code fragment of this segment
	 */
	private CodeFragment<E> codeFragment;

	/**
	 * The constructor with core
	 * 
	 * @param core
	 *            the core
	 */
	public Segment(final DBSegment core) {
		if (core == null) {
			throw new IllegalArgumentException("core is null");
		}
		this.id = core.getId();
		this.core = core;
		this.contents = new TreeMap<Integer, E>();
		this.sourceFile = null;
		this.codeFragment = null;
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

		final Segment<?> another = (Segment<?>) obj;

		return this.core.equals(another.getCore());
	}

	@Override
	public int hashCode() {
		return this.core.hashCode();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();

		for (final E content : this.contents.values()) {
			builder.append(content);
		}

		return builder.toString();
	}

	/**
	 * Get the contents of this segment as an unmodifiable map.
	 * 
	 * @return the map that represents the contents, each of whose keys is a
	 *         position in owner file, each of whose value is an element
	 */
	public SortedMap<Integer, E> getContents() {
		return Collections.unmodifiableSortedMap(contents);
	}

	/**
	 * Set the contents of this segment.
	 * 
	 * @param contents
	 *            the contents to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if one or more of the following four statements hold: (1) the
	 *             given collection of contents is null, (2) the given
	 *             collection has a content that is not in the owner source file
	 *             of this segment, (3) the given collection has a content that
	 *             doesn't locate in the range of this segment, (4) the given
	 *             collection has some elements that locate at the same position
	 */
	public void setContents(final Collection<E> contents) {
		if (contents == null) {
			throw new IllegalArgumentException(
					"the specified collection of contents is null");
		}

		this.contents.clear();
		for (final E content : contents) {
			if (!this.core.getSourceFile().equals(content.getOwnerSourceFile())) {
				throw new IllegalArgumentException(
						"the content is not in the source file");
			}
			final int position = content.getPosition();
			if (position < this.core.getStartPosition()
					|| this.core.getEndPosition() < position) {
				throw new IllegalArgumentException(
						"the given content is out of the range");
			}
			if (this.contents.containsKey(content.getPosition())) {
				throw new IllegalArgumentException("duplicate position");
			}
			this.contents.put(content.getPosition(), content);
		}
	}

	/**
	 * Get the owner source file of this segment.
	 * 
	 * @return the owner source file of this segment
	 * 
	 * @throws IllegalStateException
	 *             if the source file has not been set
	 */
	public SourceFile<E> getSourceFile() {
		if (sourceFile == null) {
			throw new IllegalStateException("the source file has not been set");
		}
		return sourceFile;
	}

	/**
	 * Set the owner source file of this segment with the specified one. The
	 * core of the given source file must match to that in the core of this
	 * segment.
	 * 
	 * @param sourceFile
	 *            the source file to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if the given source file doesn't match to that in the core of
	 *             this segment, or the given source file is <code>null</code>
	 */
	public void setSourceFile(final SourceFile<E> sourceFile) {
		if (sourceFile == null) {
			throw new IllegalArgumentException("the given source file is null");
		}

		if (!this.core.getSourceFile().equals(sourceFile.getCore())) {
			throw new IllegalArgumentException(
					"the given source file doesn't match to that in core");
		}

		this.sourceFile = sourceFile;
	}

	/**
	 * Get the owner code fragment of this segment.
	 * 
	 * @return the owner code fragment of this segment
	 * 
	 * @throws IllegalStateException
	 *             if the code fragment has not been set
	 */
	public CodeFragment<E> getCodeFragment() {
		if (codeFragment == null) {
			throw new IllegalStateException(
					"the code fragment has not been set");
		}
		return codeFragment;
	}

	/**
	 * Set the owner code fragment of this segment with the specified one. The
	 * core of the given code fragment must match to that in the core of this
	 * segment.
	 * 
	 * @param codeFragment
	 *            the owner code fragment to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if the given code fragment doesn't match to that in the core
	 *             of this segment, or the given code fragment is
	 *             <code>null</code>
	 */
	public void setCodeFragment(final CodeFragment<E> codeFragment) {
		if (codeFragment == null) {
			throw new IllegalArgumentException(
					"the given code fragment is null");
		}

		if (!this.core.getCodeFragment().equals(codeFragment.getCore())) {
			throw new IllegalArgumentException(
					"the given code fragment doesn't match to that in core");
		}

		this.codeFragment = codeFragment;
	}
}
