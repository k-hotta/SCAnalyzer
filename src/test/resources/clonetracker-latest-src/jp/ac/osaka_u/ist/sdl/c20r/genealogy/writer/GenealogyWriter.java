package jp.ac.osaka_u.ist.sdl.c20r.genealogy.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Set;

import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.CloneGenealogyInfoRetriever;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.RetrievedCloneGenealogyInfo;

public class GenealogyWriter {

	private final String outputPath;

	public GenealogyWriter(final String outputPath, final String dbPath) {
		this.outputPath = outputPath;
		DBConnection.createInstance(dbPath);
	}

	public static void main(String[] args) {
		final GenealogyWriter writer = new GenealogyWriter(args[0], args[1]);
		try {
			writer.write();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void write() {
		try {
			final CloneGenealogyInfoRetriever genealogyRetriever = new CloneGenealogyInfoRetriever();
			final Set<RetrievedCloneGenealogyInfo> genealogies = genealogyRetriever
					.retrieveAll();

			final Statement stmt = DBConnection.getInstance().createStatement();
			final ResultSet rs = stmt
					.executeQuery("select MAX(REVISION_ID) from REVISION");

			long maxId = 0;
			while (rs.next()) {
				maxId = rs.getLong(1);
			}

			write(genealogies, maxId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void write(
			final Collection<RetrievedCloneGenealogyInfo> genealogies,
			final long maxId) throws Exception {
		final PrintWriter pw = new PrintWriter(new BufferedWriter(
				new FileWriter(new File(outputPath))));

		pw.println("ID,START,END,#_REVISION,#_CHANGED,#_HASH_CHANGED,#_ADDITION,#_DELETION");

		for (final RetrievedCloneGenealogyInfo genealogy : genealogies) {
			pw.print(genealogy.getId() + ",");
			pw.print(genealogy.getStartRev() + ",");
			pw.print(genealogy.getEndRev() + ",");
			pw.print(genealogy.getEndRev() - genealogy.getStartRev() + 1);
			pw.print(",");
			int changedCount = 0;
			changedCount += genealogy.getHashChanged();
			changedCount += genealogy.getAddedRevs();
			changedCount += genealogy.getDeletedRevs();
			if (genealogy.getEndRev() != maxId) {
				changedCount++;
			}
			pw.print(changedCount);
			pw.print(",");
			pw.print(genealogy.getHashChanged() + ",");
			pw.print(genealogy.getAddedRevs() + ",");
			pw.print(genealogy.getDeletedRevs());
			pw.println();
		}

		pw.close();
	}

}
