package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;
import jp.ac.osaka_u.ist.sdl.c20r.db.DBDeleter;
import jp.ac.osaka_u.ist.sdl.c20r.db.DBMaker;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.ast.HashType;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.DataManagerManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.FileManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.RevisionManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.UnitManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.block.UnitInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.settings.Language;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.settings.Settings;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

public class RevAnalyzerMain {

	// private static String targetFile =
	// "C:\\workspace\\fcpred\\src\\jp\\ac\\osaka_u\\ist\\sdl\\fcpred\\FcpredMain.java";
	// private static String targetFile = "C:\\workspace\\BCC\\src\\";
	private static String targetFile1 = "F:\\work\\ant-ui";
	private static String targetFile2 = "F:\\work\\ant-ui-another";

	// private static String targetFile =
	// "C:\\workspace\\BCC\\testdata\\Test.java";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final long start = System.currentTimeMillis();
		System.out.println("operations start");
		System.out.println("parsing command line arguments ...");
		parseArgs(args);
		try {
			DBDeleter.main(new String[] { "-d",
					Settings.getIntsance().getDbFile() });
		} catch (Exception e) {
			// ignore
		}
		DBMaker.main(new String[] { "-d", Settings.getIntsance().getDbFile() });
		RevisionAnalyzer analyzer = new RevisionAnalyzer(1, targetFile1, 2);
		analyzer.process();
		// SortedSet<String> updatedFiles = new TreeSet<String>();
		// updatedFiles.add("A.java");
		// updatedFiles.add("B.java");
		// updatedFiles.add("C.java");
		analyzer = new RevisionAnalyzer(2, targetFile2, 2);
		analyzer.process();
		UnitManager manager = DataManagerManager.getInstance().getUnitManager();
		Set<UnitInfo> units = manager.getAllElements();
		FileManager fileManager = DataManagerManager.getInstance()
				.getFileManager();
		RevisionManager revManager = DataManagerManager.getInstance()
				.getRevisionManager();
		DBConnection.getInstance().close();
		final long end = System.currentTimeMillis();
		System.out
				.println("operations are successfully completed (elapsed time is "
						+ ((end - start) / 1000) + "s)");
	}

	/**
	 * コマンドライン引数を処理
	 * 
	 * @param args
	 */
	private static void parseArgs(String[] args) {
		try {

			final Options options = new Options();

			// -d データベースファイルの位置
			{
				final Option d = new Option("d", "database", true, "database");
				d.setArgName("database");
				d.setArgs(1);
				d.setRequired(true);
				options.addOption(d);
			}

			// -h ハッシュ値の算出方法
			{
				final Option h = new Option("h", "hash", true, "hash");
				h.setArgName("hash");
				h.setArgs(1);
				h.setRequired(false);
				options.addOption(h);
			}

			// -l 対象の言語
			{
				final Option l = new Option("l", "language", true, "language");
				l.setArgName("language");
				l.setArgs(1);
				l.setRequired(false);
				options.addOption(l);
			}

			// -w スレッド数
			{
				final Option w = new Option("w", "threads", true, "threads");
				w.setArgName("threads");
				w.setArgs(1);
				w.setRequired(false);
				options.addOption(w);
			}

			// -v 冗長出力をするかどうか
			{
				final Option v = new Option("v", "verbose", true, "verbose");
				v.setArgName("verbose");
				v.setArgs(1);
				v.setRequired(false);
				options.addOption(v);
			}

			final CommandLineParser parser = new PosixParser();
			final CommandLine cmd = parser.parse(options, args);

			Settings.getIntsance().setDbFile(cmd.getOptionValue("d"));

			if (cmd.hasOption("h")) {
				final String hValue = cmd.getOptionValue("h");
				if (hValue.equalsIgnoreCase("javahash")) {
					Settings.getIntsance().setHashType(HashType.JAVA_STR_HASH);
				} else {
					System.err
							.println("usage: -h option must have \"javahash\"");
					System.exit(1);
				}
			}

			if (cmd.hasOption("l")) {
				final String lValue = cmd.getOptionValue("l");
				if (lValue.equalsIgnoreCase("java")) {
					Settings.getIntsance().setLanguage(Language.JAVA);
				} else {
					System.err.println("usage: -l option must have \"java\"");
					System.exit(1);
				}
			}

			if (cmd.hasOption("w")) {
				Settings.getIntsance().setThreadsCount(
						Integer.valueOf(cmd.getOptionValue("w")));
			}

			if (cmd.hasOption("v")) {
				final String vValue = cmd.getOptionValue("v");
				if (vValue.equalsIgnoreCase("yes")) {
					Settings.getIntsance().setVerbose(true);
				} else {
					Settings.getIntsance().setVerbose(false);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
