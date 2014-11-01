package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;

/**
 * This class maps each source file and its contents.
 * 
 * @author k-hotta
 * 
 * @param <E>
 *            the type of elements of source code, e.g. Token
 * 
 * @see DBSourceFile
 * @see IProgramElement
 */
public class SourceFileWithContent<E extends IProgramElement> {

	/**
	 * The source file
	 */
	private DBSourceFile sourceFile;

	/**
	 * The contents as a map. The key is the position of each content, and the
	 * value is the content itself.
	 */
	private SortedMap<Integer, E> contents;

	public SourceFileWithContent(final DBSourceFile sourceFile,
			final SortedMap<Integer, E> contents) {
		this.sourceFile = sourceFile;
		this.contents = contents;
	}

	/**
	 * Get the source file
	 * 
	 * @return the source file
	 */
	public final DBSourceFile getSourceFile() {
		return sourceFile;
	}

	/**
	 * Set the source file with the specified one.
	 * 
	 * @param sourceFile
	 *            the source file to be set
	 */
	public final void setSourceFile(final DBSourceFile sourceFile) {
		this.sourceFile = sourceFile;
	}

	/**
	 * Get the contents as a map
	 * 
	 * @return the contents
	 */
	public final SortedMap<Integer, E> getContents() {
		return this.contents;
	}

	/**
	 * Set the contents with the specified one.
	 * 
	 * @param contents
	 *            the contents to be set
	 */
	public final void setContents(final SortedMap<Integer, E> contents) {
		this.contents = contents;
	}

	/**
	 * Get the contents within the specified range. Both of the contents at the
	 * start position and at the end position are included in the result.
	 * 
	 * @param startPosition
	 *            the start position of the range, which must be smaller than or
	 *            equal to the size of the contents of this file
	 * @param endPosition
	 *            the end position of the range, which must be greater than or
	 *            equal to the start position. (This can be greater than the
	 *            size of the contents of this file. In this case all the
	 *            contents after the start position will be included in the
	 *            result)
	 * @return the contents within the specified range
	 */
	public final List<E> getContentsIn(final int startPosition,
			final int endPosition) {
		if (startPosition > this.contents.size()) {
			throw new IllegalArgumentException(
					"the start position exceeds the size of the contents");
		}
		if (startPosition > endPosition) {
			throw new IllegalArgumentException(
					"the end posidion exceeds the start position");
		}

		final List<E> result = new ArrayList<E>();

		final int fixedEndPosition = Math
				.min(endPosition, this.contents.size());
		for (int i = startPosition; i <= fixedEndPosition; i++) {
			result.add(this.contents.get(i));
		}

		return result;
	}

	/**
	 * Judge whether the given object equals to this object. <br>
	 * 
	 * @return <code>true</code> if the given object is an instance of
	 *         {@link SourceFileWithContent} and the owner files of the two objects
	 *         are the same to each other, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof SourceFileWithContent)) {
			return false;
		}

		final SourceFileWithContent<?> another = (SourceFileWithContent<?>) obj;

		return this.sourceFile.equals(another.getSourceFile());
	}

	/**
	 * Return a hash value, which equals to the hash code of {@link DBSourceFile}.
	 */
	@Override
	public int hashCode() {
		return this.sourceFile.hashCode();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();

		builder.append(this.sourceFile + "\n");
		for (final E content : this.contents.values()) {
			builder.append(content + " ");
		}

		return builder.toString();
	}

}
