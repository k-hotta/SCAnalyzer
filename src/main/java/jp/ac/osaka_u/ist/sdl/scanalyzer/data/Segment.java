package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

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
	 * The contents of this segment. The elements must be sorted based on their
	 * positions.
	 */
	private final SortedSet<E> contents;

	/**
	 * The owner source file of this segment
	 */
	private SourceFile<E> sourceFile;

	/**
	 * The owner code fragment of this segment
	 */
	private CodeFragment<E> codeFragment;

	/**
	 * The first element of this segment
	 */
	private E firstElement;

	/**
	 * The last element of this segment
	 */
	private E lastElement;

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
		this.contents = new TreeSet<E>(new PositionElementComparator<>());
		this.sourceFile = null;
		this.codeFragment = null;
		this.firstElement = null;
		this.lastElement = null;
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

		for (final E content : this.contents) {
			builder.append(content + " ");
		}

		return builder.toString();
	}

	/**
	 * Get the contents of this segment as an unmodifiable sorted set.
	 * 
	 * @return the contents as a sorted set, elements in which are sorted based
	 *         on their positions
	 */
	public SortedSet<E> getContents() {
		return Collections.unmodifiableSortedSet(contents);
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
			if (!this.core.getSourceFile().equals(
					content.getOwnerSourceFile().getCore())) {
				throw new IllegalArgumentException(
						"the content is not in the source file");
			}
			final int position = content.getPosition();
			if (position < this.core.getStartPosition()
					|| this.core.getEndPosition() < position) {
				throw new IllegalArgumentException(
						"the given content is out of the range");
			}
			if (this.contents.contains(content)) {
				throw new IllegalArgumentException("duplicate position");
			}
			this.contents.add(content);
		}

		this.firstElement = this.contents.first();
		this.lastElement = this.contents.last();
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

	/**
	 * Get the first element of this segment.
	 * 
	 * @return the first element of this segment
	 * 
	 * @throws IllegalStateException
	 *             if the contents have not been set
	 */
	public E getFirstElement() {
		if (this.firstElement == null) {
			throw new IllegalStateException("the contents have not been set");
		}

		return this.firstElement;
	}

	/**
	 * Get the last element of this segment.
	 * 
	 * @return the last element of this segment
	 * 
	 * @throws IllegalStateException
	 *             if the contents have not been set
	 */
	public E getLastElement() {
		if (this.lastElement == null) {
			throw new IllegalStateException("the contents have not been set");
		}

		return this.lastElement;
	}

}
