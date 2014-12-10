package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneModification;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneModification.Type;

/**
 * This class represents a clone modification.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class CloneModification<E extends IProgramElement> implements
		IDataElement<DBCloneModification> {

	/**
	 * The id of this modification
	 */
	private final long id;

	/**
	 * The core of this modification
	 */
	private final DBCloneModification core;

	/**
	 * The owner code fragment mapping
	 */
	private CodeFragmentMapping<E> codeFragmentMapping;

	/**
	 * The old segment
	 */
	private Segment<E> relatedOldSegment;

	/**
	 * The new segment
	 */
	private Segment<E> relatedNewSegment;

	/**
	 * The owner clone class mapping
	 */
	private CloneClassMapping<E> cloneClassMapping;

	public CloneModification(final DBCloneModification core) {
		this.id = core.getId();
		this.core = core;
		this.codeFragmentMapping = null;
		this.relatedOldSegment = null;
		this.relatedNewSegment = null;
		this.cloneClassMapping = null;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public DBCloneModification getCore() {
		return core;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CloneModification)) {
			return false;
		}

		final CloneModification<?> another = (CloneModification<?>) obj;

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
	 * Get the position of the first element affected by this modification in
	 * the before version.
	 * 
	 * @return the position of the first element
	 */
	public int getOldStartPosition() {
		return this.core.getOldStartPosition();
	}

	/**
	 * Get the position of the first element affected by this modification in
	 * the after version.
	 * 
	 * @return the position of the first element
	 */
	public int getNewStartPosition() {
		return this.core.getNewStartPosition();
	}

	/**
	 * Get the length of this modification.
	 * 
	 * @return the length of this modification
	 */
	public int getLength() {
		return this.core.getLength();
	}

	/**
	 * Get the type of this modification.
	 * 
	 * @return the type of this modification
	 */
	public Type getType() {
		return this.core.getType();
	}

	/**
	 * Get the hash value of affected elements by this modification.
	 * 
	 * @return the hash value generated from affected elements
	 */
	public int getContentHash() {
		return this.core.getContentHash();
	}

	/**
	 * Get the owner code fragment mapping.
	 * 
	 * @return the owner code fragment mapping, can be <code>null</code>
	 * 
	 * @throws IllegalStateException
	 *             if the owner code fragment mapping has not been set
	 */
	public CodeFragmentMapping<E> getCodeFragmentMapping() {
		if (this.core.getCodeFragmentMapping() != null
				&& codeFragmentMapping == null) {
			throw new IllegalStateException(
					"the code fragment mapping has not been set");
		}

		return codeFragmentMapping;
	}

	/**
	 * Set the owner code fragment mapping of this modification with the
	 * specified one. The core of the given code fragment must match to that in
	 * the core of this modification.
	 * 
	 * @param codeFragmentMapping
	 *            the owner code fragment mapping to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if the given code fragment mapping doesn't match to that in
	 *             the core of this modification
	 */
	public void setCodeFragmentMapping(
			final CodeFragmentMapping<E> codeFragmentMapping) {
		if (this.core.getCodeFragmentMapping() != null
				&& codeFragmentMapping == null) {
			throw new IllegalArgumentException(
					"the given code fragment mapping is null, but not in the core");
		} else if (this.core.getCodeFragmentMapping() == null
				&& codeFragmentMapping != null) {
			throw new IllegalArgumentException(
					"the given code fragment mapping is not null, but it is null in the core");
		} else if (codeFragmentMapping != null
				&& !this.core.getCodeFragmentMapping().equals(
						codeFragmentMapping.getCore())) {
			throw new IllegalArgumentException(
					"the given code fragment mapping doesn't match to that in the core");
		}

		this.codeFragmentMapping = codeFragmentMapping;
	}

	/**
	 * Get the related old segment of this modification.
	 * 
	 * @return the related old segment of this modification, can be
	 *         <code>null</code>
	 * 
	 * @throws IllegalStateException
	 *             if the related old segment has not been set
	 */
	public Segment<E> getRelatedOldSegment() {
		if (this.core.getRelatedOldSegment() != null
				&& relatedOldSegment == null) {
			throw new IllegalStateException(
					"the related old segment has not been set");
		}

		return relatedOldSegment;
	}

	/**
	 * Set the related old segment of this modification with the specified one.
	 * The given related old segment must match to that in the core.
	 * 
	 * @param relatedOldSegment
	 *            the related old segment to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if the given related old segment doesn't match to that in the
	 *             core
	 */
	public void setRelatedOldSegment(final Segment<E> relatedOldSegment) {
		if (this.core.getRelatedOldSegment() != null
				&& relatedOldSegment == null) {
			throw new IllegalArgumentException(
					"the given related old segment is null, but not in the core");
		} else if (this.core.getRelatedOldSegment() == null
				&& relatedOldSegment != null) {
			throw new IllegalArgumentException(
					"the given related old segment is not null, but it is null in the core");
		} else if (relatedOldSegment != null
				&& !this.core.getRelatedOldSegment().equals(
						relatedOldSegment.getCore())) {
			throw new IllegalArgumentException(
					"the given related old segment doesn't match to that in the core");
		}

		this.relatedOldSegment = relatedOldSegment;
	}

	/**
	 * Get the related new segment of this modification.
	 * 
	 * @return the related new segment of this modification, can be
	 *         <code>null</code>
	 * 
	 * @throws IllegalStateException
	 *             if the related new segment has not been set
	 */
	public Segment<E> getRelatedNewSegment() {
		if (this.core.getRelatedNewSegment() != null
				&& relatedNewSegment == null) {
			throw new IllegalStateException(
					"the related new segment has not been set");
		}

		return relatedNewSegment;
	}

	/**
	 * Set the related new segment of this modification with the specified one.
	 * The given related new segment must match to that in the core.
	 * 
	 * @param relatedNewSegment
	 *            the related new segment to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if the given related new segment doesn't match to that in the
	 *             core
	 */
	public void setRelatedNewSegment(final Segment<E> relatedNewSegment) {
		if (this.core.getRelatedNewSegment() != null
				&& relatedNewSegment == null) {
			throw new IllegalArgumentException(
					"the given related new segment is null, but not in the core");
		} else if (this.core.getRelatedNewSegment() == null
				&& relatedNewSegment != null) {
			throw new IllegalArgumentException(
					"the given related new segment is not null, but it is null in the core");
		} else if (relatedNewSegment != null
				&& !this.core.getRelatedNewSegment().equals(
						relatedNewSegment.getCore())) {
			throw new IllegalArgumentException(
					"the given related new segment doesn't match to that in the core");
		}

		this.relatedNewSegment = relatedNewSegment;
	}

	/**
	 * Get the owner clone class mapping of this modification.
	 * 
	 * @return the owner clone class mapping
	 * 
	 * @throws IllegalStateException
	 *             if the owner clone class mapping has not been set
	 */
	public CloneClassMapping<E> getCloneClassMapping() {
		if (cloneClassMapping == null) {
			throw new IllegalStateException(
					"the owner clone class mapping has not been set");
		}

		return cloneClassMapping;
	}

	/**
	 * Set the owner clone class mapping of this modification with the specified
	 * one.
	 * 
	 * @param cloneClassMapping
	 *            the owner clone class mapping to be set
	 * 
	 * @throws IllegalArgumentException
	 *             if the given owner clone class mapping doesn't match to that
	 *             in the core, or it is <code>nul</code>
	 */
	public void setCloneClassMapping(
			final CloneClassMapping<E> cloneClassMapping) {
		if (cloneClassMapping == null) {
			throw new IllegalArgumentException(
					"the given clone class mapping is null");
		}
		if (!this.core.getCloneClassMapping().equals(
				cloneClassMapping.getCore())) {
			throw new IllegalArgumentException(
					"the given clone class mapping doesn't match to that in the core");
		}

		this.cloneClassMapping = cloneClassMapping;
	}

}
