package jp.ac.osaka_u.ist.sdl.c20r.genealogy.graphcreator;

import java.io.File;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.RetrievedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.CloneSetPairInfoRetriever;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.GenealogyEdge;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.RetrievedCloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.RetrievedCloneSetPairInfo;

public class GraphCreateThread implements Runnable {

	private final RetrievedCloneGenealogyInfo[] genealogies;

	private final AtomicInteger index;

	private final String outputDir;

	private final ConcurrentMap<Long, RetrievedRevisionInfo> revisions;

	public GraphCreateThread(final RetrievedCloneGenealogyInfo[] genealogies,
			final AtomicInteger index, final String outputDir,
			final ConcurrentMap<Long, RetrievedRevisionInfo> revisions) {
		this.genealogies = genealogies;
		this.index = index;
		this.outputDir = outputDir;
		this.revisions = revisions;
	}

	@Override
	public void run() {
		while (true) {
			final int currentIndex = index.getAndIncrement();

			if (currentIndex >= genealogies.length) {
				break;
			}

			final RetrievedCloneGenealogyInfo genealogy = genealogies[currentIndex];

			System.out.println("\tgenealogy " + genealogy.getId()
					+ " is being analyzed ... [" + (currentIndex + 1) + "/"
					+ genealogies.length + "]");

			// beforeRevId は不要だけど仕様上指定しないと動かないので-1にしてる
			final CloneSetPairInfoRetriever pairsRetriever = new CloneSetPairInfoRetriever(
					-1);
			final SortedSet<RetrievedCloneSetPairInfo> cloneSetPairs = pairsRetriever
					.retrieve(genealogy.getPairs());

			final Set<GenealogyEdge> edges = detectEdges(genealogy,
					cloneSetPairs);

			final EachDotWriter writer = new EachDotWriter(
					getOutputFilePath(genealogy), genealogy, revisions, edges);
			writer.write();

		}
	}

	private String getOutputFilePath(final RetrievedCloneGenealogyInfo genealogy) {
		return outputDir + File.separator + genealogy.getId() + ".dot";
	}

	private Set<GenealogyEdge> detectEdges(
			final RetrievedCloneGenealogyInfo genealogy,
			final SortedSet<RetrievedCloneSetPairInfo> pairs) {
		Set<GenealogyEdge> edges = new TreeSet<GenealogyEdge>();
		for (final RetrievedCloneSetPairInfo pair : pairs) {
			edges.add(new GenealogyEdge(pair));
		}

		while (true) {
			Set<GenealogyEdge> refined = new TreeSet<GenealogyEdge>();
			Set<GenealogyEdge> tobeRemoved = new TreeSet<GenealogyEdge>();
			for (final GenealogyEdge edge : edges) {
				if (edge.getBeforeRevId() == 1 || edge.getBeforeCloneId() == -1) {
					continue;
				}
				if (edge.getAfterCloneId() == -1) {
					continue;
				}
				if (tobeRemoved.contains(edge)) {
					continue;
				}
				for (final GenealogyEdge anotherEdge : edges) {
					if (anotherEdge.getAfterCloneId() == -1) {
						continue;
					}
					if (edge == anotherEdge) {
						continue;
					}
					if (tobeRemoved.contains(anotherEdge)) {
						continue;
					}
					if (GenealogyEdge.canMerge(edge, anotherEdge)) {
						refined.add(GenealogyEdge.merge(edge, anotherEdge));
						tobeRemoved.add(edge);
						tobeRemoved.add(anotherEdge);
						break;
					}
				}
			}
			edges.removeAll(tobeRemoved);
			edges.addAll(refined);

			if (refined.isEmpty()) {
				break;
			}
		}

		return edges;
	}

}
