package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.settings;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.ast.HashType;

/**
 * �ݒ���Ǘ��N���X
 * 
 * @author k-hotta
 * 
 */
public class Settings {

	/**
	 * �V���O���g���I�u�W�F�N�g
	 */
	private static Settings SINGLETON = null;

	/**
	 * �n�b�V���l�̎Z�o���@
	 */
	private HashType hashType;

	/**
	 * ����
	 */
	private Language language;

	/**
	 * �X���b�h��
	 */
	private int threadsCount;

	/**
	 * �f�[�^�x�[�X�t�@�C���̈ʒu
	 */
	private String dbFile;

	/**
	 * �璷�o�͂����邩�ǂ���
	 */
	private boolean verbose;
	
	/**
	 * PreparedStatement �̃o�b�`�������ǂꂾ�����߂Ă�����s����̂�
	 */
	private int maxBatchCount;
	
	/**
	 * DB�ɓo�^����u���b�N�̍ŏ��m�[�h��
	 */
	private int threshold;

	/**
	 * �R���X�g���N�^ <br>
	 * �K�{�̈����ȊO�̓f�t�H���g�l�����[�h
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
	 * �R�}���h���C������������
	 * 
	 * @param args
	 */
	public static void parseArgs(String[] args) {
		try {

			final Options options = new Options();

			// -d �f�[�^�x�[�X�t�@�C���̈ʒu
			{
				final Option d = new Option("d", "database", true, "database");
				d.setArgName("database");
				d.setArgs(1);
				d.setRequired(true);
				options.addOption(d);
			}

			// -h �n�b�V���l�̎Z�o���@
			{
				final Option h = new Option("h", "hash", true, "hash");
				h.setArgName("hash");
				h.setArgs(1);
				h.setRequired(false);
				options.addOption(h);
			}

			// -l �Ώۂ̌���
			{
				final Option l = new Option("l", "language", true, "language");
				l.setArgName("language");
				l.setArgs(1);
				l.setRequired(false);
				options.addOption(l);
			}

			// -w �X���b�h��
			{
				final Option w = new Option("w", "threads", true, "threads");
				w.setArgName("threads");
				w.setArgs(1);
				w.setRequired(false);
				options.addOption(w);
			}

			// -v �璷�o�͂����邩�ǂ���
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
