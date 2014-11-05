package jp.ac.osaka_u.ist.sdl.c20r.genealogy.graphcreator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.RetrievedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.GenealogyEdge;

public abstract class DotWriter implements GraphSettings {

	protected final String dotFile;

	protected final Map<Long, RetrievedRevisionInfo> revisions;

	public DotWriter(final String dotFile,
			final Map<Long, RetrievedRevisionInfo> revisions) {
		this.dotFile = dotFile;
		this.revisions = revisions;
	}

	public final void write() {
		try {
			final PrintWriter pw = new PrintWriter(new BufferedWriter(
					new FileWriter(new File(dotFile))));

			writeHeader(pw);
			writeGraphContents(pw);

			pw.println("}");

			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("An Error Occured in Writing " + dotFile);
		}
	}

	protected abstract void writeHeader(final PrintWriter pw);

	protected abstract void writeGraphContents(final PrintWriter pw);

	protected void writePair(final PrintWriter pw, final GenealogyEdge edge) {

		if (edge.getBeforeCloneId() == -1) {
			if (edge.getAfterCloneId() == -1) {
				assert false; // here shouldn't be reached
				return;
			}
			pw.println("\tstart -> " + edge.getAfterCloneId() + " [label = \""
					+ edge.getRepresentativeStr() + "\"];");
		}

		else if (edge.getAfterCloneId() == -1) {
			pw.println("\t" + edge.getBeforeCloneId() + " -> end [label = \""
					+ edge.getRepresentativeStr() + "\"];");
		}

		else if (edge.containsHashChanged()) {
			pw.println("\t" + edge.getBeforeCloneId() + " -> "
					+ edge.getAfterCloneId() + " [label = \""
					+ edge.getRepresentativeStr() + "\", color = "
					+ EDGE_COLOR_HASHCHANGED + ", fontcolor = "
					+ EDGE_FONTCOLOR_HASHCHANGED + "];");
		}

		else {
			pw.println("\t" + edge.getBeforeCloneId() + " -> "
					+ edge.getAfterCloneId() + " [label = \""
					+ edge.getRepresentativeStr() + "\"];");
		}
	}

}
