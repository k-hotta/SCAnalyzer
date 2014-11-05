package jp.ac.osaka_u.ist.sdl.c20r.genealogy;

import java.util.Collections;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;


public class CloneGenealogyInfo implements Comparable<CloneGenealogyInfo> {

	private static AtomicLong count = new AtomicLong(0);

	private final long id;

	private long startRev;

	private long endRev;

	private final SortedSet<Long> correspondentPairs;

	private final SortedMap<Long, Set<Long>> correspondentClones;

	private int hashChanged = 0;

	private int addedTotal = 0;

	private int deletedTotal = 0;

	private int deletedInFileDelTotal = 0;

	private int addedRevCount = 0;

	private int deletedRevCount = 0;

	private int deletedFileDelCount = 0;

	public CloneGenealogyInfo(final long startRev, final long endRev) {
		this.id = count.getAndIncrement();
		this.startRev = startRev;
		this.endRev = endRev;
		this.correspondentPairs = new TreeSet<Long>();
		this.correspondentClones = new TreeMap<Long, Set<Long>>();
	}

	public long getStartRev() {
		return startRev;
	}

	public long getEndRev() {
		return endRev;
	}

	public long getId() {
		return id;
	}

	public SortedSet<Long> getCorrespondentPairs() {
		return Collections.unmodifiableSortedSet(correspondentPairs);
	}

	public int getHashChanged() {
		return hashChanged;
	}

	public int getAddedTotal() {
		return addedTotal;
	}

	public int getDeletedTotal() {
		return deletedTotal;
	}

	public int getDeletedInFileDelTotal() {
		return deletedInFileDelTotal;
	}

	public int getAddedRevCount() {
		return addedRevCount;
	}

	public int getDeletedRevCount() {
		return deletedRevCount;
	}

	public int getDeletedFileDelCount() {
		return deletedFileDelCount;
	}

	/**
	 * クローンセットペアを追加
	 * 
	 * @param pair
	 */
	public void addCloneSetPair(final RetrievedCloneSetPairInfo pair) {
		// ペアID管理用の集合にペアのIDを追加
		final long id = pair.getId();
		this.correspondentPairs.add(id);

		final long beforeRevId = pair.getBeforeRevId();
		final long afterRevId = pair.getAfterRevId();

		final Set<Long> beforeCorrespondentClones = correspondentClones
				.get(beforeRevId);
		final Set<Long> afterCorrespondentClones = correspondentClones
				.get(afterRevId);

		// 前リビジョンにおけるクローンセット情報を更新
		if (pair.getBeforeCloneId() != -1) {
			if (beforeCorrespondentClones == null) {
				final Set<Long> newCorrespondentClonesSet = new TreeSet<Long>();
				newCorrespondentClonesSet.add(pair.getBeforeCloneId());
				correspondentClones.put(beforeRevId, newCorrespondentClonesSet);
			} else {
				beforeCorrespondentClones.add(pair.getBeforeCloneId());
			}
		}

		// 後リビジョンにおけるクローンセット情報を更新
		if (pair.getAfterCloneId() != -1) {
			if (afterCorrespondentClones == null) {
				final Set<Long> newCorrespondentClonesSet = new TreeSet<Long>();
				newCorrespondentClonesSet.add(pair.getAfterCloneId());
				correspondentClones.put(afterRevId, newCorrespondentClonesSet);
			} else {
				afterCorrespondentClones.add(pair.getAfterCloneId());
			}
		}

		// 開始リビジョンと終了リビジョンを更新
		if (beforeRevId < this.startRev) {
			this.startRev = beforeRevId;
		}
		if (afterRevId > this.endRev) {
			this.endRev = afterRevId;
		}

		// 各種メトリクスを更新
		if (pair.isHashChanged()) {
			this.hashChanged++;
		}

		if (pair.getAddedElementsCount() > 0) {
			this.addedTotal += pair.getAddedElementsCount();
			this.addedRevCount++;
		}

		if (pair.getDeletedElementsCount() > 0) {
			this.deletedTotal += pair.getDeletedElementsCount();
			this.deletedRevCount++;
		}

		if (pair.getDeletedElementsInDeletedFilesCount() > 0) {
			this.deletedInFileDelTotal = pair
					.getDeletedElementsInDeletedFilesCount();
			this.deletedFileDelCount++;
		}
	}

	public boolean containsBeforeClone(final RetrievedCloneSetPairInfo pair) {
		final long beforeCloneId = pair.getBeforeCloneId();
		final long beforeRevId = pair.getBeforeRevId();

		boolean containsBeforeClone = false;
		if (correspondentClones.containsKey(beforeRevId)) {
			containsBeforeClone = correspondentClones.get(beforeRevId)
					.contains(beforeCloneId);
		}

		return containsBeforeClone;
	}

	public boolean containsAfterClone(final RetrievedCloneSetPairInfo pair) {
		final long afterCloneId = pair.getAfterCloneId();
		final long afterRevId = pair.getAfterRevId();

		boolean containsAfterClone = false;
		if (correspondentClones.containsKey(afterRevId)) {
			containsAfterClone = correspondentClones.get(afterRevId).contains(
					afterCloneId);
		}

		return containsAfterClone;
	}

	public String getPairsAsString() {
		final StringBuilder builder = new StringBuilder();
		for (final long pairId : correspondentPairs) {
			builder.append(pairId + ",");
		}
		if (builder.length() > 0) {
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
	}

	@Override
	public int compareTo(CloneGenealogyInfo o) {
		return ((Long) id).compareTo(o.getId());
	}

}
