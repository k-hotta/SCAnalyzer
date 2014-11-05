package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.settings;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.ast.HashType;

/**
 * 設定情報管理クラス
 * 
 * @author k-hotta
 * 
 */
public class Settings {

	/**
	 * シングルトンオブジェクト
	 */
	private static Settings SINGLETON = null;

	/**
	 * ハッシュ値の算出方法
	 */
	private HashType hashType;

	/**
	 * 言語
	 */
	private Language language;

	/**
	 * スレッド数
	 */
	private int threadsCount;

	/**
	 * データベースファイルの位置
	 */
	private String dbFile;

	/**
	 * 冗長出力をするかどうか
	 */
	private boolean verbose;
	
	/**
	 * PreparedStatement のバッチ処理をどれだけ貯めてから実行するのか
	 */
	private int maxBatchCount;
	
	/**
	 * DBに登録するブロックの最小ノード数
	 */
	private int threshold;

	/**
	 * コンストラクタ <br>
	 * 必須の引数以外はデフォルト値をロード
	 */
	private Settings() {
		hashType = HashType.JAVA_STR_HASH;
		language = Language.JAVA;
		threadsCount = 8;
		verbose = true;
		maxBatchCount = 10000;
		threshold = 30;
	}

	public static Settings getIntsance() {
		if (SINGLETON == null) {
			SINGLETON = new Settings();
		}
		return SINGLETON;
	}
	
	/**
	 * コマンドライン引数を処理
	 * 
	 * @param args
	 */
	public static void parseArgs(String[] args) {
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

	/*
	 * setters & getters
	 */

	public HashType getHashType() {
		return hashType;
	}

	public void setHashType(HashType hashType) {
		this.hashType = hashType;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public int getThreadsCount() {
		return threadsCount;
	}

	public void setThreadsCount(int threadsCount) {
		this.threadsCount = threadsCount;
	}

	public String getDbFile() {
		return dbFile;
	}

	public void setDbFile(String dbFile) {
		this.dbFile = dbFile;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public int getMaxBatchCount() {
		return maxBatchCount;
	}

	public void setMaxBatchCount(int maxBatchCount) {
		this.maxBatchCount = maxBatchCount;
	}

	public int getThreshold() {
		return threshold;
	}
	
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
	
}
