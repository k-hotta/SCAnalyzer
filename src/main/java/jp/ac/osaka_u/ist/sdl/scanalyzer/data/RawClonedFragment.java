package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawClonedFragment;

/**
 * This class represents raw cloned fragment.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class RawClonedFragment<E extends IProgramElement> implements
		IDataElement<DBRawClonedFragment> {

	/**
	 * The id of this raw cloned fragment
	 */
	private final long id;

	/**
	 * The core of this raw cloned fragment
	 */
	private final DBRawClonedFragment core;

	/**
	 * The owner source file of this raw cloned fragment
	 */
	private SourceFile<E> sourceFile;

	/**
	 * The owner raw clone class of this raw cloned fragment
	 */
	private RawCloneClass rawCloneClass;

	/**
	 * The constructor with core
	 * 
	 * @param core
	 *            the core
	 */
	public RawClonedFragment(final DBRawClonedFragment core) {
		this.id = core.getId();
		this.core = core;
		this.sourceFile = null;
		this.rawCloneClass = null;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public DBRawClonedFragment getCore() {
		return core;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof RawClonedFragment)) {
			return false;
		}

		final RawClonedFragment<?> another = (RawClonedFragment<?>) obj;

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
	 * Get the owner source file of this raw cloned fragment.
	 * 
	 * @return the owner source file of this raw cloned fragment
	 * 
	 * @throws IllegalStateException
	 *             if the owner source file has not been set
	 */
	public SourceFile<E> getSourceFile() {
		if (sourceFile == null) {
			throw new IllegalStateException("the source file has not been set");
		}
		return sourceFile;
	}

	/**
	 * Set the owner source file of this raw cloned fragment with the specified
	 * one. The core of the given source file must match to that in the core of
	 * this raw cloned fragment.
	 * 
	 * @param sourceFile
	 *            the source file to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if the given source file doesn't match to that in the core of
	 *             this raw cloned fragment, or the given source file is
	 *             <code>null</code>
	 */
	public void setSourceFile(final SourceFile<E> sourceFile) {
		if (sourceFile == null) {
			throw new IllegalArgumentException("the given source file is null");
		}
		if (!this.core.getSourceFile().equals(sourceFile.getCore())) {
			throw new IllegalArgumentException(
					"the given source file doesn't match to that in the core");
		}

		this.sourceFile = sourceFile;
	}

	/**
	 * Get the owner raw clone class of this raw cloned fragment.
	 * 
	 * @return the owner raw clone class of this raw cloned fragment
	 * 
	 * @throws IllegalStateException
	 *             if the owner raw clone class has not been set
	 */
	public RawCloneClass getRawCloneClass() {
		if (rawCloneClass == null) {
			throw new IllegalStateException(
					"the raw clone class has not been set");
		}
		return rawCloneClass;
	}

	/**
	 * Set the owner raw clone class of this raw cloned fragment with the
	 * specified one. The core of the given raw clone class must match to that
	 * in the core of this raw cloned fragment.
	 * 
	 * @param rawCloneClass
	 *            the raw clone class to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if the given raw clone class doesn't match to that in the
	 *             core of this raw cloned fragment, or the given raw clone
	 *             class is <code>null</code>
	 */
	public void setRawCloneClass(final RawCloneClass rawCloneClass) {
		if (rawCloneClass == null) {
			throw new IllegalArgumentException(
					"the given raw clone class is null");
		}
		if (!this.core.getCloneClass().equals(rawCloneClass.getCore())) {
			throw new IllegalArgumentException(
					"the given raw clone class doesn't match to that in the core");
		}

		this.rawCloneClass = rawCloneClass;
	}

	/**
	 * Get the start line of this raw cloned fragment.
	 * 
	 * @return the start line of this raw cloned fragment
	 */
	public int getStartLine() {
		return this.core.getStartLine();
	}

	/**
	 * Get the length of this raw cloned fragment.
	 * 
	 * @return the length of this raw cloned fragment
	 */
	public int getLength() {
		return this.core.getLength();
	}

}
