package jp.ac.osaka_u.ist.sdl.c20r;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.Tracer;
import jp.ac.osaka_u.ist.sdl.c20r.csv_analyzer.CSVAnalyzer;
import jp.ac.osaka_u.ist.sdl.c20r.diff.DifferenceManager;
import jp.ac.osaka_u.ist.sdl.c20r.diff.Hunk;
import jp.ac.osaka_u.ist.sdl.c20r.diff.HunkDetector;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.clonedetector.CloneDetector;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.clonepairdetector.ClonePairDetectorWithDB;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.genealogydetector.CloneGenealogyDetector;
import jp.ac.osaka_u.ist.sdl.c20r.genealogy.graphcreator.GenealogyGraphCreator;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.RevisionAnalyzer;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.settings.Settings;
import jp.ac.osaka_u.ist.sdl.c20r.ui.UIMain;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

/**
 * -R 検出対象リビジョンを特定するモード -C CRD抽出とクローン検出をするモード -T トラッキングにより，消えたクローンを特定するモード
 * 
 * @author higo
 * 
 */
public class C20R {

	public static Config CONFIG;

	public static void main(final String[] args) {

		final long startTime = System.nanoTime();

		final Options options = new Options();

		{
			final Option C = new Option("C", "Clone", false,
					"Identifying code clones");
			C.setRequired(false);
			options.addOption(C);
		}

		{
			final Option R = new Option("R", "Revision", false,
					"Identifying target revisions");
			R.setRequired(false);
			options.addOption(R);
		}

		{
			final Option T = new Option("T", "Tracking", false,
					"Tracking code clones");
			T.setRequired(false);
			options.addOption(T);
		}

		{
			final Option U = new Option("U", "UI", false,
					"Visualizing tracking results");
			U.setRequired(false);
			options.addOption(U);
		}

		{
			final Option A = new Option("A", "Analyze", false, "Analyzing");
			A.setRequired(false);
			options.addOption(A);
		}

		{
			final Option S = new Option("S", "Sequential", false,
					"Detect Clones and Registers Them");
			S.setRequired(false);
			options.addOption(S);
		}

		{
			final Option G = new Option("G", "Graph", false,
					"Creating Graphs of Genealogies");
			G.setRequired(false);
			options.addOption(G);
		}

		{
			final Option e = new Option("e", "end", true, "end revision");
			e.setRequired(false);
			options.addOption(e);
		}

		{
			final Option f = new Option("f", "file", true, "revision file");
			f.setRequired(false);
			options.addOption(f);
		}

		{
			final Option l = new Option("l", "location", true,
					"location of database");
			l.setRequired(false);
			options.addOption(l);
		}

		{
			final Option n = new Option("n", "name", true, "name of database");
			n.setRequired(false);
			options.addOption(n);
		}

		{
			final Option r = new Option("r", "repository", true,
					"path to repository");
			r.setRequired(false);
			options.addOption(r);
		}

		{
			final Option s = new Option("s", "start", true, "start revision");
			s.setRequired(false);
			options.addOption(s);
		}

		{
			final Option v = new Option("v", "verbose", false,
					"output progress verbosely");
			v.setRequired(false);
			options.addOption(v);
		}

		{
			final Option w = new Option("w", "working", true,
					"working directory");
			w.setRequired(false);
			options.addOption(w);
		}

		{
			final Option th = new Option("th", "threads", true, "threads count");
			th.setRequired(false);
			options.addOption(th);
		}

		{
			final Option o = new Option("o", "output", true, "output directory");
			o.setRequired(false);
			options.addOption(o);
		}

		{
			final Option i = new Option("i", "input", true, "input csv file");
			i.setRequired(false);
			options.addOption(i);
		}

		{
			final Option aw = new Option("aw", "anotherworking", true,
					"another working directory");
			aw.setRequired(false);
			options.addOption(aw);
		}

		{
			final Option m = new Option("m", "minimum", true,
					"minimum size of target blocks");
			m.setRequired(false);
			options.addOption(m);
		}

		{
			final Option ot = new Option("ot", "outputtime", true,
					"output file to record time elapsed");
			ot.setRequired(false);
			options.addOption(ot);
		}

		{
			final Option ta = new Option("ta", "target", true, "target");
			ta.setRequired(false);
			options.addOption(ta);
		}

		{
			final Option mg = new Option("mg", "merge", true,
					"merge genealogies");
			mg.setRequired(false);
			options.addOption(mg);
		}

		try {

			final CommandLineParser parser = new PosixParser();
			CONFIG = new Config(parser.parse(options, args));

		} catch (org.apache.commons.cli.ParseException e) {
			e.printStackTrace();
		}

		final C20R c20r = new C20R();
		Map<Long, Long> elapsedTime = new TreeMap<Long, Long>();

		switch (CONFIG.getMODE()) {
		case CLONE:
			elapsedTime = c20r.checkoutOneByOne();
			break;
		case REVISION:
			c20r.identifyTargetRevisions();
			break;
		case TRACKING:
			c20r.trace();
			break;
		case UI:
			c20r.showUI();
			break;
		case ANALYZING:
			c20r.analyze();
			break;
		case SEQUENTIAL:
			c20r.sequentialAnalysis();
			break;
		case GRAPH:
			c20r.createGenealogyGraph();
			break;
		}

		final long endTime = System.nanoTime();
		final long denominator = 1000 * 1000 * 1000;

		System.out.println("operations have finished.");
		System.out.println("total elapsed time is "
				+ ((endTime - startTime) / denominator) + " s");

		final String OUTPUT = CONFIG.getOUTPUT_FILE_PATH_FOR_TIME();
		if (OUTPUT != null) {
			try {
				File outputFile = new File(OUTPUT);
				PrintWriter pw = new PrintWriter(new BufferedWriter(
						new FileWriter(outputFile)));
				pw.print("TOTAL\t:\t");
				pw.print((endTime - startTime) / 1000 / 1000);
				pw.println("\t[ms]");
				pw.println();

				for (Map.Entry<Long, Long> entry : elapsedTime.entrySet()) {
					pw.println(entry.getKey() + "\t:\t" + entry.getValue()
							+ "\t[ms]");
				}

				pw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private boolean identifyTargetRevisions() {

		try {

			final String REVISION_FILE = CONFIG.getREVISION_FILE();
			final String REPOSITORY_PATH = CONFIG.getREPOSITORY_PATH();
			final long startRevision = CONFIG.getSTART_REVISION();
			final long endRevision = CONFIG.getEND_REVISION();
			final String targetPath = CONFIG.getTARGET();

			SVNURL url = SVNURL.parseURIDecoded("file:///" + REPOSITORY_PATH);
			if (targetPath != null) {
				url = SVNURL.parseURIDecoded("file:///" + REPOSITORY_PATH
						+ targetPath);
			}
			// final SVNURL url = SVNURL.parseURIDecoded("file:///"
			// + REPOSITORY_PATH);
			FSRepositoryFactory.setup();
			final SVNRepository repository = FSRepositoryFactory.create(url);

			final SortedSet<Long> revisions = new TreeSet<Long>();
			repository.log(null, startRevision, endRevision, true, false,
					new ISVNLogEntryHandler() {
						public void handleLogEntry(SVNLogEntry logEntry)
								throws SVNException {

							for (final Map.Entry<String, SVNLogEntryPath> entry : logEntry
									.getChangedPaths().entrySet()) {

								// .javaなファイルが更新されている場合
								if (entry.getKey().endsWith(".java")) {
									final long revision = logEntry
											.getRevision();
									System.out.print(Long.toString(revision));
									System.out.println(" is being checked.");
									revisions.add(revision);
									break;
								}

								// .javaでないファイルが更新されていない場合でも，
								// ディレクトリの削除の可能性がある場合は，対象リビジョンに追加
								else if (('D' == entry.getValue().getType())
										|| ('R' == entry.getValue().getType())) {
									final long revision = logEntry
											.getRevision();
									System.out.print(Long.toString(revision));
									System.out.println(" is being checked.");
									revisions.add(revision);
									break;
								}
							}
						}
					});

			final BufferedWriter writer = new BufferedWriter(new FileWriter(
					REVISION_FILE));

			if (revisions.first() > 1) {
				writer.write(Long.toString(revisions.first() - 1));
				writer.newLine();
			}

			for (long revison : revisions) {
				writer.write(Long.toString(revison));
				writer.newLine();
			}
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	private Map<Long, Long> checkoutOneByOne() {

		final Map<Long, Long> elapsedTime = new TreeMap<Long, Long>();

		try {

			final String REVISION_FILE = CONFIG.getREVISION_FILE();
			final String REPOSITORY_PATH = CONFIG.getREPOSITORY_PATH();
			final String DB_LOCATION = CONFIG.getDB_LOCATION();
			final String DB_NAME = CONFIG.getDB_NAME();
			final String WORKING_DIRECTORY = CONFIG.getWORKING_DIRECTORY();
			final int THRESHOLD = CONFIG.getMIN_SIZE();
			final int START_REV = CONFIG.getSTART_REVISION();
			final int END_REV = CONFIG.getEND_REVISION();
			final String targetPath = CONFIG.getTARGET();
			final int THREADS_COUNT = CONFIG.getTHREADS_COUNT();

			final SortedSet<Long> targetRevisions = detectRevisions(
					REVISION_FILE, START_REV, END_REV);
			final int allRevisionsCount = targetRevisions.size();

			if (allRevisionsCount == 0) {
				return new TreeMap<Long, Long>();
			}

			// final int allRevisionsCount =
			// detectRevisionsCount(REVISION_FILE);
			int processedRevisionsCount = 0;

			SVNURL url = SVNURL.parseURIDecoded("file:///" + REPOSITORY_PATH);
			if (targetPath != null) {
				url = SVNURL.parseURIDecoded("file:///" + REPOSITORY_PATH
						+ targetPath);
			}
			final int targetPathLength = (targetPath == null) ? 0 : targetPath
					.length();
			// final SVNURL url = SVNURL.parseURIDecoded("file:///"
			// + REPOSITORY_PATH);
			final SVNRepository repository = FSRepositoryFactory.create(url);
			FSRepositoryFactory.setup();
			final SVNUpdateClient updateClient = SVNClientManager.newInstance()
					.getUpdateClient();
			updateClient.setIgnoreExternals(false);

			long firstRevision = targetRevisions.first();

			updateClient
					.doCheckout(url, new File(WORKING_DIRECTORY),
							SVNRevision.create(firstRevision),
							SVNRevision.create(firstRevision),
							SVNDepth.INFINITY, false);
			System.out.println("revision " + Long.toString(firstRevision)
					+ " was checked out.");

			loadRevAnalyzerSettings(DB_LOCATION, THREADS_COUNT);

			RevisionAnalyzer analyzer = new RevisionAnalyzer(firstRevision,
					WORKING_DIRECTORY, allRevisionsCount, THRESHOLD);
			final long elapsedFirst = analyzer.process();
			elapsedTime.put(firstRevision, elapsedFirst);
			System.out.println("revision " + Long.toString(firstRevision)
					+ " was successfully processed. ["
					+ (++processedRevisionsCount) + "/" + allRevisionsCount
					+ "]");

			final SVNDiffClient diffClient = SVNClientManager.newInstance()
					.getDiffClient();

			long previousRevision = firstRevision;
			for (final long revision : targetRevisions) {

				if (revision == firstRevision) {
					continue;
				}

				System.out.println("revision " + Long.toString(revision)
						+ " was updated.");

				final SortedSet<String> updatedFiles = new TreeSet<String>();
				final SortedSet<String> deletedDirectories = new TreeSet<String>();
				// repository.log(null, previousRevision, revision, true, false,
				repository.log(null, revision, revision, true, false,
						new ISVNLogEntryHandler() {
							public void handleLogEntry(SVNLogEntry logEntry)
									throws SVNException {

								for (final Map.Entry<String, SVNLogEntryPath> entry : logEntry
										.getChangedPaths().entrySet()) {

									final String path = entry.getKey();

									// .javaなファイルが更新されている場合
									if (path.endsWith(".java")) {
										if (targetPath == null
												|| path.startsWith(targetPath)) {
											updatedFiles.add(path
													.substring(targetPathLength));
										}
									}

									// .javaでないファイルが更新されていない場合でも，
									// ディレクトリの削除の可能性がある場合は，deletedDirectoriesに追加
									else if (('D' == entry.getValue().getType())
											|| ('R' == entry.getValue()
													.getType())) {
										if (targetPath == null
												|| path.startsWith(targetPath)) {
											deletedDirectories.add(path
													.substring(targetPathLength));
										}
									}
								}
							}
						});

				if (updatedFiles.isEmpty() && deletedDirectories.isEmpty()) {
					final long zero = 0;
					elapsedTime.put(revision, zero);
					continue;
				}

				// ここは表示のためだけなので消してもOK
				// updateFilesに変更されたファイルのパスが格納されています
				for (final String path : updatedFiles) {
					System.out.print(Long.toString(revision));
					System.out.print(": ");
					System.out.println(path);
				}
				for (final String path : deletedDirectories) {
					System.out.print(Long.toString(revision));
					System.out.print(": ");
					System.out.println(path);
				}

				final StringBuilder diffText = new StringBuilder();
				diffClient.doDiff(url, SVNRevision.create(previousRevision),
						url, SVNRevision.create(revision), SVNDepth.INFINITY,
						true, new OutputStream() {
							@Override
							public void write(int arg0) throws IOException {
								diffText.append((char) arg0);
							}
						});

				final Map<Long, Set<Hunk>> hunks = HunkDetector.detectHunks(
						diffText.toString(), previousRevision, revision,
						WORKING_DIRECTORY);
				DifferenceManager.createInstance(hunks.get(previousRevision),
						hunks.get(revision));

				analyzer = new RevisionAnalyzer(revision, WORKING_DIRECTORY,
						updatedFiles, deletedDirectories, allRevisionsCount);

				updateClient.doUpdate(new File(WORKING_DIRECTORY),
						SVNRevision.create(revision), SVNDepth.INFINITY, true,
						true);

				final long elapsed = analyzer.process();
				elapsedTime.put(revision, elapsed);

				System.out.println("revision " + Long.toString(revision)
						+ " was successfully processed. ["
						+ (++processedRevisionsCount) + "/" + allRevisionsCount
						+ "]");
				previousRevision = revision;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return elapsedTime;
	}

	private SortedSet<Long> detectRevisions(String filePath, int start, int end)
			throws IOException {
		final SortedSet<Long> result = new TreeSet<Long>();

		BufferedReader reader = new BufferedReader(new FileReader(new File(
				filePath)));
		String line;

		while ((line = reader.readLine()) != null) {
			final long rev = Long.parseLong(line);
			if (start <= rev && rev <= end) {
				result.add(rev);
			}
		}

		reader.close();

		return Collections.unmodifiableSortedSet(result);
	}

	private int detectRevisionsCount(String filePath) {
		int count = 0;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					filePath)));

			while (reader.ready()) {
				reader.readLine();
				count++;
			}

			reader.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			System.exit(1);
		}

		return count;
	}

	/**
	 * DBに登録されている全リビジョン間でトラッキングを行う
	 */
	private void trace() {
		final String DB_LOCATION = CONFIG.getDB_LOCATION();
		final String OUTPUT_DIR = CONFIG.getOUTPUT_PATH();
		final String WORKING_DIR = CONFIG.getWORKING_DIRECTORY();
		final int THREADS_COUNT = CONFIG.getTHREADS_COUNT();
		final int START_REV = CONFIG.getSTART_REVISION();
		final int END_REV = CONFIG.getEND_REVISION();

		final Tracer tracer = new Tracer(DB_LOCATION, OUTPUT_DIR, WORKING_DIR,
				THREADS_COUNT, START_REV, END_REV);
		tracer.trace();
	}

	private void showUI() {
		final String REPOSITORY_PATH = CONFIG.getREPOSITORY_PATH();
		final String CSVFILE_PATH = CONFIG.getINPUT_PATH();

		try {
			final UIMain uiMain = new UIMain(REPOSITORY_PATH);
			uiMain.start(CSVFILE_PATH);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void analyze() {
		final String INPUT_DIR = CONFIG.getINPUT_PATH();
		final String OUTPUT_FILE = CONFIG.getOUTPUT_PATH();
		final int THREADS_COUNT = CONFIG.getTHREADS_COUNT();

		final CSVAnalyzer analyzer = new CSVAnalyzer(INPUT_DIR, THREADS_COUNT,
				OUTPUT_FILE);
		analyzer.analyze();
	}

	private void sequentialAnalysis() {
		final String DB_LOCATION = CONFIG.getDB_LOCATION();
		final int THREADS_COUNT = CONFIG.getTHREADS_COUNT();
		final int START_REV = CONFIG.getSTART_REVISION();
		final int END_REV = CONFIG.getEND_REVISION();
		final boolean MERGE = CONFIG.getMERGE();

		System.out.println("DETECTING CLONES");
		final CloneDetector detector = new CloneDetector(START_REV, END_REV,
				THREADS_COUNT, DB_LOCATION);
		detector.run();

		System.out.println("DETECTING CLONE PAIRS");
		final ClonePairDetectorWithDB pairDetector = new ClonePairDetectorWithDB(
				START_REV, END_REV, THREADS_COUNT, DB_LOCATION);
		pairDetector.run();

		System.out.println("DETECTING GENEALOGIES");
		final CloneGenealogyDetector genealogyDetector = new CloneGenealogyDetector(
				START_REV, END_REV, MERGE);
		genealogyDetector.run();
	}

	private void createGenealogyGraph() {
		final String DB_LOCATION = CONFIG.getDB_LOCATION();
		final int THREADS_COUNT = CONFIG.getTHREADS_COUNT();
		final String OUTPUT_DIR = CONFIG.getOUTPUT_PATH();

		final GenealogyGraphCreator creator = new GenealogyGraphCreator(
				OUTPUT_DIR, THREADS_COUNT, DB_LOCATION);
		creator.run();
	}

	/**
	 * RevAnalyzer 用の引数を生成し，RevAnalyzer に与える
	 * 
	 * @param dbLocation
	 */
	private static void loadRevAnalyzerSettings(final String dbLocation,
			final int threads) {
		String[] args = new String[] { "-d", dbLocation, "-w",
				((Integer) threads).toString() };
		Settings.parseArgs(args);
	}
}
