package jp.ac.osaka_u.ist.sdl.c20r.genealogy.graphcreator;

import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.RetrievedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.AllRevisionInfoRetriever;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.CloneGenealogyInfoRetriever;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.RetrievedCloneGenealogyInfo;

public class GenealogyGraphCreator {

	private final String outputDir;

	private final int threadsCount;

	public GenealogyGraphCreator(final String outputDir,
			final int threadsCount, final String dbPath) {
		this.outputDir = outputDir;
		this.threadsCount = threadsCount;
		DBConnection.createInstance(dbPath);
	}

	public void run() {
		System.out.println("retrieving all the revisions ...");
		final AllRevisionInfoRetriever revRetriever = new AllRevisionInfoRetriever();
		final ConcurrentMap<Long, RetrievedRevisionInfo> revisions = new ConcurrentHashMap<Long, RetrievedRevisionInfo>();
		for (final RetrievedRevisionInfo rev : revRetriever.retrieveAll()) {
			revisions.put(rev.getId(), rev);
		}

		System.out.println("retrieving all the genealogies ...");
		final CloneGenealogyInfoRetriever genealogyRetriever = new CloneGenealogyInfoRetriever();
		final SortedSet<RetrievedCloneGenealogyInfo> genealogies = genealogyRetriever
				.retrieveAll();

		System.out.println("\tthe number of retrieved genealogies is "
				+ genealogies.size());

		System.out.println("creating graphs ...");

		final Thread[] threads = new Thread[threadsCount];
		final AtomicInteger index = new AtomicInteger(0);
		final RetrievedCloneGenealogyInfo[] genealogiesArray = genealogies
				.toArray(new RetrievedCloneGenealogyInfo[0]);

		for (int i = 0; i < threadsCount; i++) {
			threads[i] = new Thread(new GraphCreateThread(genealogiesArray,
					index, outputDir, revisions));
			threads[i].start();
		}

		for (final Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
