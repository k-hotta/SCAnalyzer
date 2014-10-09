package jp.ac.osaka_u.ist.sdl.c20r.genealogy;

/**
 * DBから回収したクローセット対
 * 
 * @author k-hotta
 * 
 */
public class RetrievedCloneSetPairInfo implements
		Comparable<RetrievedCloneSetPairInfo> {

	private final long id;

	private final long beforeRevId;

	private final long afterRevId;

	private final long beforeCloneId;

	private final long afterCloneId;

	private final boolean hashChanged;

	private final int addedElementsCount;

	private final int deletedElementsCount;

	private final int deletedElementsInDeletedFilesCount;

	public RetrievedCloneSetPairInfo(final long id, final long beforeRevId,
			final long afterRevId, final long beforeCloneId,
			final long afterCloneId, final boolean hashChanged,
			final int addedElementsCount, final int deletedElementsCount,
			final int deletedElementsInDeletedFilesCount) {
		this.id = id;
		this.beforeRevId = beforeRevId;
		this.afterRevId = afterRevId;
		this.beforeCloneId = beforeCloneId;
		this.afterCloneId = afterCloneId;
		this.hashChanged = hashChanged;
		this.addedElementsCount = addedElementsCount;
		this.deletedElementsCount = deletedElementsCount;
		this.deletedElementsInDeletedFilesCount = deletedElementsInDeletedFilesCount;
	}

	public long getId() {
		return id;
	}

	public long getBeforeRevId() {
		return beforeRevId;
	}

	public long getAfterRevId() {
		return afterRevId;
	}

	public long getBeforeCloneId() {
		return beforeCloneId;
	}

	public long getAfterCloneId() {
		return afterCloneId;
	}

	public boolean isHashChanged() {
		return hashChanged;
	}

	public int getAddedElementsCount() {
		return addedElementsCount;
	}

	public int getDeletedElementsCount() {
		return deletedElementsCount;
	}

	public int getDeletedElementsInDeletedFilesCount() {
		return deletedElementsInDeletedFilesCount;
	}

	@Override
	public int compareTo(RetrievedCloneSetPairInfo o) {
		final int revCompare = ((Long) beforeRevId).compareTo(o
				.getBeforeRevId());

		if (revCompare != 0) {
			return revCompare;
		} else {
			return ((Long) id).compareTo(o.getId());
		}
	}

}
