package jp.ac.osaka_u.ist.sdl.c20r.genealogy;


/**
 * RetrievedCloneGenealogy におけるクローンセット間のマッピングを表す
 * 
 * @author k-hotta
 * 
 */
public class GenealogyEdge implements Comparable<GenealogyEdge> {

	private final long beforeCloneId;

	private final long beforeRevId;

	private final long afterCloneId;

	private final long afterRevId;

	private final String representativeStr;

	private final boolean containsHashChanged;

	private final boolean containsAddition;

	private final boolean containsDeletion;

	public GenealogyEdge(final RetrievedCloneSetPairInfo pair) {
		this.beforeCloneId = pair.getBeforeCloneId();
		this.beforeRevId = pair.getBeforeRevId();
		this.afterCloneId = pair.getAfterCloneId();
		this.afterRevId = pair.getAfterRevId();
		this.representativeStr = ((Long) pair.getId()).toString();
		this.containsHashChanged = pair.isHashChanged();
		this.containsAddition = pair.getAddedElementsCount() > 0;
		this.containsDeletion = pair.getDeletedElementsCount()
				- pair.getDeletedElementsInDeletedFilesCount() > 0;
	}

	public GenealogyEdge(final long beforeCloneId, final long beforeRevId,
			final long afterCloneId, final long afterRevId) {
		this.beforeCloneId = beforeCloneId;
		this.beforeRevId = beforeRevId;
		this.afterCloneId = afterCloneId;
		this.afterRevId = afterRevId;
		this.representativeStr = "...";
		this.containsHashChanged = false;
		this.containsAddition = false;
		this.containsDeletion = false;
	}

	public long getBeforeCloneId() {
		return beforeCloneId;
	}

	public long getBeforeRevId() {
		return beforeRevId;
	}

	public long getAfterCloneId() {
		return afterCloneId;
	}

	public long getAfterRevId() {
		return afterRevId;
	}

	public String getRepresentativeStr() {
		return representativeStr;
	}

	public boolean containsHashChanged() {
		return containsHashChanged;
	}

	public boolean containsAddition() {
		return containsAddition;
	}

	public boolean containsDeletion() {
		return containsDeletion;
	}

	public boolean isUnchanged() {
		return !(containsHashChanged || containsAddition || containsDeletion);
	}

	public static boolean canMerge(final GenealogyEdge edge1,
			final GenealogyEdge edge2) {
		if (edge1.getBeforeCloneId() == -1) {
			return false;
		}

		if (edge2.getAfterCloneId() == -1) {
			return false;
		}

		if (!edge1.isUnchanged() || !edge2.isUnchanged()) {
			return false;
		}

		if (edge1.getAfterCloneId() != edge2.getBeforeCloneId()) {
			return false;
		}

		return true;
	}

	public static GenealogyEdge merge(final GenealogyEdge edge1,
			final GenealogyEdge edge2) {
		return new GenealogyEdge(edge1.getBeforeCloneId(),
				edge1.getBeforeRevId(), edge2.getAfterCloneId(),
				edge2.getAfterRevId());
	}

	@Override
	public int compareTo(GenealogyEdge o) {
		final int beforeRevComp = ((Long) beforeRevId).compareTo(o
				.getBeforeRevId());
		if (beforeRevComp != 0) {
			return beforeRevComp;
		}

		final int afterRevComp = ((Long) afterRevId).compareTo(o
				.getAfterRevId());
		if (afterRevComp != 0) {
			return afterRevComp;
		}

		final int beforeCloneComp = ((Long) beforeCloneId).compareTo(o
				.getBeforeCloneId());
		if (beforeCloneComp != 0) {
			return beforeCloneComp;
		}

		final int afterCloneComp = ((Long) afterCloneId).compareTo(o
				.getAfterCloneId());
		if (afterCloneComp != 0) {
			return afterCloneComp;
		}

		return 0;
	}

}
