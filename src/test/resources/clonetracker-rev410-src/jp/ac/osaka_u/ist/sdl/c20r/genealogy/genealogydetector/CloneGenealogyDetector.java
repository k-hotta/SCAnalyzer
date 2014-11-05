package jp.ac.osaka_u.ist.sdl.c20r.genealogy.genealogydetector;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.RevisionsDetector;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.RetrievedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve.RevisionInfoRetriever;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.CloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.CloneSetPairInfoRetriever;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.RetrievedCloneSetPairInfo;

public class CloneGenealogyDetector {

	/**
	 * 特定したgenealogiesが保存されるマップ
	 */
	private final Map<Long, CloneGenealogyInfo> genealogies;

	/**
	 * 開始地点が異なるgenealogiesをマージするかどうか
	 */
	private final boolean mergeGenealogiesHavingDifferentStartPoint;

	private final int startRev;

	private final int endRev;

	public CloneGenealogyDetector(final int startRev, final int endRev,
			final boolean mergeGenealogiesHavingDifferentStartPoint) {
		this.startRev = startRev;
		this.endRev = endRev;
		this.genealogies = new TreeMap<Long, CloneGenealogyInfo>();
		this.mergeGenealogiesHavingDifferentStartPoint = mergeGenealogiesHavingDifferentStartPoint;
	}

	public void run() {
		System.out.println("detecting target revisions ...");
		final RevisionsDetector revDetector = new RevisionsDetector(startRev,
				endRev);
		final int[] revisions = revDetector.getRevisionsArray();

		System.out.println("\tthe number of target revisions is "
				+ revisions.length);

		System.out.println("detecting genealogies ...");
		detect(revisions);

		CloneGenealogyRegisterer.getInstance()
				.registerAll(genealogies.values());
		System.out.println(genealogies.size()
				+ " genealogies were detected and registered.");
	}

	public void detect(final int[] revisions) {
		for (final int revision : revisions) {
			detect(revision);
			System.out.println("\trevision " + revision + " was processed.");
		}
	}

	public void detect(final int revisionNum) {
		final RetrievedRevisionInfo revisionInfo = new RevisionInfoRetriever(0,
				revisionNum).retrieveAll().first();

		final CloneSetPairInfoRetriever pairRetriever = new CloneSetPairInfoRetriever(
				revisionInfo.getId());
		final SortedSet<RetrievedCloneSetPairInfo> cloneSetPairs = pairRetriever
				.retrieveAll();

		for (final RetrievedCloneSetPairInfo cloneSetPair : cloneSetPairs) {
			boolean registered = false;
			for (final CloneGenealogyInfo genealogy : genealogies.values()) {
				final boolean containsBeforeClone = genealogy
						.containsBeforeClone(cloneSetPair);
				final boolean containsAfterClone = genealogy
						.containsAfterClone(cloneSetPair);

				if (containsBeforeClone) {
					genealogy.addCloneSetPair(cloneSetPair);
					registered = true;
				} else if (mergeGenealogiesHavingDifferentStartPoint
						&& containsAfterClone) {
					genealogy.addCloneSetPair(cloneSetPair);
					registered = true;
				}
			}

			if (!registered) {
				final CloneGenealogyInfo newGenealogy = new CloneGenealogyInfo(
						revisionInfo.getId(), cloneSetPair.getAfterRevId());
				newGenealogy.addCloneSetPair(cloneSetPair);
				genealogies.put(newGenealogy.getId(), newGenealogy);
			}
		}
	}

}
