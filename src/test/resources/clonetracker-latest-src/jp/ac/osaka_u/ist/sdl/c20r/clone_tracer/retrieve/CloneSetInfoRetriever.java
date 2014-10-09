package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.block.RetrievedBlockInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone.RetrievedCloneSetInfo;

public class CloneSetInfoRetriever {

	private final Set<RetrievedBlockInfo> blocks;

	private final boolean isBeforeRevision;

	public CloneSetInfoRetriever(Set<RetrievedBlockInfo> blocks,
			final boolean isBeforeRevision) {
		this.blocks = blocks;
		this.isBeforeRevision = isBeforeRevision;
	}

	public SortedSet<RetrievedCloneSetInfo> retrieveAll() {
		// 特定したクローンセットを格納するマップ (keyはハッシュ値)
		final Map<Integer, RetrievedCloneSetInfo> cloneSets = new TreeMap<Integer, RetrievedCloneSetInfo>();

		// 要素数が1でもとりあえずクローンセットを作る
		for (final RetrievedBlockInfo block : blocks) {
			final int hash = block.getHash();

			if (cloneSets.containsKey(hash)) {
				final RetrievedCloneSetInfo cloneSet = cloneSets.get(hash);
				cloneSet.addElement(block);
			} else {
				final RetrievedCloneSetInfo newCloneSet = new RetrievedCloneSetInfo(
						hash);
				newCloneSet.addElement(block);
				cloneSets.put(hash, newCloneSet);
			}
		}

		// 要素数1のクローンセットを除外，かつ極大クローンを特定
		final SortedSet<RetrievedCloneSetInfo> result = detectMaximumCloneSets(cloneSets
				.values());

		for (final RetrievedCloneSetInfo cloneSet : result) {
			final long cloneSetId = cloneSet.getId();
			for (final RetrievedBlockInfo block : cloneSet.getAllElements()) {
				block.setCloneSetId(cloneSetId);
			}
		}

		return Collections.unmodifiableSortedSet(result);
	}

	private SortedSet<RetrievedCloneSetInfo> detectMaximumCloneSets(
			final Collection<RetrievedCloneSetInfo> clones) {
		final Set<RetrievedCloneSetInfo> toBeRemoved = new HashSet<RetrievedCloneSetInfo>();

		for (final RetrievedCloneSetInfo clone : clones) {
			if (clone.getCount() <= 1) {
				toBeRemoved.add(clone);
				continue;
			}
			if (!isBeforeRevision) {
				continue;
			}

			for (final RetrievedCloneSetInfo anotherClone : clones) {
				if (clone == anotherClone) {
					continue;
				}
				if (clone.containsAll(anotherClone)) {
					toBeRemoved.add(anotherClone);
					break;
				}
			}
		}

		final SortedSet<RetrievedCloneSetInfo> result = new TreeSet<RetrievedCloneSetInfo>();
		result.addAll(clones);
		result.removeAll(toBeRemoved);

		return result;
	}

}
