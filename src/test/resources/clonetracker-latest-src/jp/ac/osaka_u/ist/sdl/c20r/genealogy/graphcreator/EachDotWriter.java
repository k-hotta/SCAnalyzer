package jp.ac.osaka_u.ist.sdl.c20r.genealogy.graphcreator;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.RetrievedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.GenealogyEdge;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.RetrievedCloneGenealogyInfo;

public class EachDotWriter extends DotWriter {

	private final RetrievedCloneGenealogyInfo genealogy;

	private final Map<Long, String> nodeColors;

	private final Map<Long, Long> cloneRevisionMap;

	private final Set<GenealogyEdge> edges;

	public EachDotWriter(final String dotFile,
			final RetrievedCloneGenealogyInfo genealogy,
			final Map<Long, RetrievedRevisionInfo> revisions,
			final Set<GenealogyEdge> edges) {
		super(dotFile, revisions);
		this.genealogy = genealogy;
		this.nodeColors = new TreeMap<Long, String>();
		this.edges = edges;
		this.cloneRevisionMap = new TreeMap<Long, Long>();
		for (final GenealogyEdge edge : edges) {
			// this.pairs.put(edge.getId(), edge);

			/*
			 * 前クローンの色分け
			 */
			final long beforeCloneId = edge.getBeforeCloneId();
			final long beforeRevId = edge.getBeforeRevId();

			if (!cloneRevisionMap.containsKey(beforeCloneId)) {
				cloneRevisionMap.put(beforeCloneId, beforeRevId);
			}

			boolean containsHashChange = edge.containsHashChanged();
			boolean containsAddition = false;
			boolean containsDeletion = edge.containsDeletion();

			if (nodeColors.containsKey(beforeCloneId)) {
				final String registeredColor = nodeColors.get(beforeCloneId);
				if (registeredColor
						.equals(NODE_COLOR_ADDED_DELETED_HASHCHANGED)) {
					containsHashChange = true;
					containsAddition = true;
					containsDeletion = true;
				} else if (registeredColor.equals(NODE_COLOR_ADDED_HASHCHANGED)) {
					containsHashChange = true;
					containsAddition = true;
				} else if (registeredColor
						.equals(NODE_COLOR_DELETED_HASHCHANGED)) {
					containsHashChange = true;
					containsDeletion = true;
				} else if (registeredColor
						.equals(NODE_COLOR_ELEMENTS_ADDED_AND_DELETED)) {
					containsAddition = true;
					containsDeletion = true;
				} else if (registeredColor.equals(NODE_COLOR_CHANGEDPAIR)) {
					containsHashChange = true;
				} else if (registeredColor.equals(NODE_COLOR_ELEMENTS_ADDED)) {
					containsAddition = true;
				} else if (registeredColor.endsWith(NODE_COLOR_ELEMENTS_DELTED)) {
					containsDeletion = true;
				}
				nodeColors.remove(beforeCloneId);
			}

			if (containsHashChange && containsAddition && containsDeletion) {
				nodeColors.put(beforeCloneId,
						NODE_COLOR_ADDED_DELETED_HASHCHANGED);
			} else if (containsHashChange && containsAddition) {
				nodeColors.put(beforeCloneId, NODE_COLOR_ADDED_HASHCHANGED);
			} else if (containsHashChange && containsDeletion) {
				nodeColors.put(beforeCloneId, NODE_COLOR_DELETED_HASHCHANGED);
			} else if (containsAddition && containsDeletion) {
				nodeColors.put(beforeCloneId,
						NODE_COLOR_ELEMENTS_ADDED_AND_DELETED);
			} else if (containsHashChange) {
				nodeColors.put(beforeCloneId, NODE_COLOR_CHANGEDPAIR);
			} else if (containsAddition) {
				nodeColors.put(beforeCloneId, NODE_COLOR_ELEMENTS_ADDED);
			} else if (containsDeletion) {
				nodeColors.put(beforeCloneId, NODE_COLOR_ELEMENTS_DELTED);
			}

			/*
			 * 後クローンの色分け
			 */

			final long afterCloneId = edge.getAfterCloneId();
			final long afterRevId = edge.getAfterRevId();

			if (!cloneRevisionMap.containsKey(afterCloneId)) {
				cloneRevisionMap.put(afterCloneId, afterRevId);
			}

			containsHashChange = edge.containsHashChanged();
			containsAddition = edge.containsAddition();
			containsDeletion = false;

			if (nodeColors.containsKey(afterCloneId)) {
				final String registeredColor = nodeColors.get(afterCloneId);
				if (registeredColor
						.equals(NODE_COLOR_ADDED_DELETED_HASHCHANGED)) {
					containsHashChange = true;
					containsAddition = true;
					containsDeletion = true;
				} else if (registeredColor.equals(NODE_COLOR_ADDED_HASHCHANGED)) {
					containsHashChange = true;
					containsAddition = true;
				} else if (registeredColor
						.equals(NODE_COLOR_DELETED_HASHCHANGED)) {
					containsHashChange = true;
					containsDeletion = true;
				} else if (registeredColor
						.equals(NODE_COLOR_ELEMENTS_ADDED_AND_DELETED)) {
					containsAddition = true;
					containsDeletion = true;
				} else if (registeredColor.equals(NODE_COLOR_CHANGEDPAIR)) {
					containsHashChange = true;
				} else if (registeredColor.equals(NODE_COLOR_ELEMENTS_ADDED)) {
					containsAddition = true;
				} else if (registeredColor.endsWith(NODE_COLOR_ELEMENTS_DELTED)) {
					containsDeletion = true;
				}
				nodeColors.remove(afterCloneId);
			}

			if (containsHashChange && containsAddition && containsDeletion) {
				nodeColors.put(afterCloneId,
						NODE_COLOR_ADDED_DELETED_HASHCHANGED);
			} else if (containsHashChange && containsAddition) {
				nodeColors.put(afterCloneId, NODE_COLOR_ADDED_HASHCHANGED);
			} else if (containsHashChange && containsDeletion) {
				nodeColors.put(afterCloneId, NODE_COLOR_DELETED_HASHCHANGED);
			} else if (containsAddition && containsDeletion) {
				nodeColors.put(afterCloneId,
						NODE_COLOR_ELEMENTS_ADDED_AND_DELETED);
			} else if (containsHashChange) {
				nodeColors.put(afterCloneId, NODE_COLOR_CHANGEDPAIR);
			} else if (containsAddition) {
				nodeColors.put(afterCloneId, NODE_COLOR_ELEMENTS_ADDED);
			} else if (containsDeletion) {
				nodeColors.put(afterCloneId, NODE_COLOR_ELEMENTS_DELTED);
			}
		}
	}

	@Override
	protected void writeHeader(final PrintWriter pw) {
		pw.println("digraph genealogy_" + genealogy.getId() + " {");
		//pw.println("\tgraph [rankdir = LR];");
		pw.println("\tstart [style = filled, fillcolor = \"" + NODE_COLOR_START
				+ "\", shape = " + NODE_SHAPE_START + "];");
		pw.println("\tend [style = filled, fillcolor = \"" + NODE_COLOR_END
				+ "\", shape = " + NODE_SHAPE_END + "];");
	}

	private String getNodeLabel(Long cloneId) {
		try {
			final long revId = cloneRevisionMap.get(cloneId);

			return cloneId + " [Rev: " + revisions.get(revId).getRevisionNum()
					+ "(" + revId + ")]";
		} catch (Exception e) {
			return cloneId + "[Rev: ???]";
		}
	}

	@Override
	public void writeGraphContents(final PrintWriter pw) {
		final Set<Long> nodes = new TreeSet<Long>();
		for (final GenealogyEdge edge : edges) {
			if (edge.getBeforeRevId() == 1) {
				pw.println("\tstart -> " + edge.getBeforeCloneId());
				writePair(pw, edge);
				nodes.add(edge.getBeforeCloneId());
			} else {
				writePair(pw, edge);
				nodes.add(edge.getBeforeCloneId());
				nodes.add(edge.getAfterCloneId());
			}
		}
		for (final long cloneId : nodes) {
			if (cloneId == -1) {
				continue;
			}
			if (nodeColors.containsKey(cloneId)) {
				pw.println("\t" + cloneId + " [style = filled, fillcolor = \""
						+ nodeColors.get(cloneId) + "\", label = \""
						+ getNodeLabel(cloneId) + "\"];");
			} else {
				pw.println("\t" + cloneId + "[label = \""
						+ getNodeLabel(cloneId) + "\"];");
			}
		}
	}
}
