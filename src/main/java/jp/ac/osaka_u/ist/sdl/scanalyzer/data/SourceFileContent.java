package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.Map;

/**
 * This class maps each source file and its contents.
 * 
 * @author k-hotta
 * 
 * @param <E>
 *            the type of elements of source code, e.g. Token
 * 
 * @see SourceFile
 * @see IAtomicElement
 */
public class SourceFileContent<E extends IAtomicElement> {

	/**
	 * The source file
	 */
	private SourceFile sourceFile;

	/**
	 * The contents as a map. The key is the position of each content, and the
	 * value is the content itself.
	 */
	private Map<Integer, E> contents;

	public SourceFileContent(final SourceFile sourceFile,
			final Map<Integer, E> contents) {
		this.sourceFile = sourceFile;
		this.contents = contents;
	}

	/**
	 * Get the source file
	 * 
	 * @return the source file
	 */
	public final SourceFile getSourceFile() {
		return sourceFile;
	}

	/**
	 * Set the source file with the specified one.
	 * 
	 * @param sourceFile
	 *            the source file to be set
	 */
	public final void setSourceFile(final SourceFile sourceFile) {
		this.sourceFile = sourceFile;
	}

	/**
	 * Get the contents as a map
	 * 
	 * @return the contents
	 */
	public final Map<Integer, E> getContents() {
		return this.contents;
	}

	/**
	 * Set the contents with the specified one.
	 * 
	 * @param contents
	 *            the contents to be set
	 */
	public final void setContents(final Map<Integer, E> contents) {
		this.contents = contents;
	}

	/**
	 * Judge whether the given object equals to this object. <br>
	 * 
	 * @return <code>true</code> if the given object is an instance of
	 *         {@link SourceFileContent} and the owner files of the two objects
	 *         are the same to each other, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof SourceFileContent)) {
			return false;
		}

		final SourceFileContent<?> another = (SourceFileContent<?>) obj;

		return this.sourceFile.equals(another.getSourceFile());
	}

	/**
	 * Return a hash value, which equals to the hash code of {@link SourceFile}.
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
			builder.append(content);
		}

		return builder.toString();
	}

}
