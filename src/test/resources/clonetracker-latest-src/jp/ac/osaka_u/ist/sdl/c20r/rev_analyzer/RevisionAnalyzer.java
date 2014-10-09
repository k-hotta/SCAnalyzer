package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.DataManagerManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.FileManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.FilesInPreviousRevisionManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.file.FileInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.revision.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.register.DBRegister;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.settings.Settings;
import jp.ac.osaka_u.ist.sdl.c20r.util.StringUtilities;

/**
 * 単一リビジョンを解析し，ユニット情報を特定するクラス
 * 
 * @author k-hotta
 * 
 */
public class RevisionAnalyzer {

	/**
	 * 解析中リビジョンのリビジョン番号
	 */
	private final int revisionNum;

	/**
	 * 解析対象となるファイルのパスのリスト
	 */
	private final List<String> targetFiles;

	/**
	 * 解析の対象となるリビジョンの総数
	 */
	private final long maxRevisionCount;

	public RevisionAnalyzer(int revisionNum, String root,
			final long maxRevisionCount) {
		DBConnection.createInstance(Settings.getIntsance().getDbFile());

		this.revisionNum = revisionNum;
		this.targetFiles = detectTargetFiles(root);
		this.maxRevisionCount = maxRevisionCount;

		// 現在マネージャーに登録されている情報はすべて破棄
		DataManagerManager.getInstance().clearAll();
	}

	public RevisionAnalyzer(long revisionNum, String root,
			SortedSet<String> updatedFiles, final long maxRevisionCount) {
		DBConnection.createInstance(Settings.getIntsance().getDbFile());

		this.revisionNum = ((Long) revisionNum).intValue();
		this.targetFiles = new ArrayList<String>();
		registUpdatedFiles(root, updatedFiles);
		this.maxRevisionCount = maxRevisionCount;

		DataManagerManager.getInstance().clearAll();
	}

	public RevisionAnalyzer(long revisionNum, String root,
			SortedSet<String> updatedFiles, SortedSet<String> deletedDirs,
			long maxRevisionCount) {
		DBConnection.createInstance(Settings.getIntsance().getDbFile());

		this.revisionNum = ((Long) revisionNum).intValue();
		this.targetFiles = new ArrayList<String>();
		for (final String deletedDir : deletedDirs) {
			this.targetFiles.addAll(detectTargetFiles(StringUtilities
					.detectAbsolutePath(root, deletedDir)));
		}
		registUpdatedFiles(root, updatedFiles);
		this.maxRevisionCount = maxRevisionCount;

		DataManagerManager.getInstance().clearAll();
	}

	private void registUpdatedFiles(String root, SortedSet<String> updatedFiles) {
		for (final String updatedFile : updatedFiles) {
			final String updatedFilePath = StringUtilities.detectAbsolutePath(
					root, updatedFile);
			this.targetFiles.add((new File(updatedFilePath)).getAbsolutePath());
		}
	}

	public RevisionAnalyzer(Long revisionNum, String root,
			final long maxRevisionCount, final int threshold) {
		DBConnection.createInstance(Settings.getIntsance().getDbFile());

		this.revisionNum = ((Long) revisionNum).intValue();
		this.targetFiles = detectTargetFiles(root);
		this.maxRevisionCount = maxRevisionCount;
		Settings.getIntsance().setThreshold(threshold);

		DataManagerManager.getInstance().clearAll();
	}

	private List<String> detectTargetFiles(String root, String target) {
		final List<String> detected = detectTargetFiles(root);
		if (target == null) {
			return detected;
		}

		final List<String> copy = new LinkedList<String>();
		copy.addAll(detected);

		final String[] splitedTarget = target.split("/");
		final StringBuilder builder = new StringBuilder();
		builder.append(root);
		for (final String splited : splitedTarget) {
			if (splited.isEmpty()) {
				continue;
			}
			builder.append(File.separator);
			builder.append(splited);
		}
		final String prefix = builder.toString();

		for (String tmp : copy) {
			if (!tmp.startsWith(prefix)) {
				detected.remove(tmp);
			}
		}

		return detected;
	}

	/**
	 * 解析対象となるファイルのパスを特定し，リストとして返す
	 * 
	 * @param root
	 * @return
	 */
	private List<String> detectTargetFiles(String root) {
		List<String> result = new LinkedList<String>();
		File rootFile = new File(root);
		if (rootFile.exists()) {
			parse(result, rootFile);
		}
		return result;
	}

	private void parse(List<String> list, File targetFile) {
		if (targetFile.isDirectory()) {
			for (File child : targetFile.listFiles()) {
				parse(list, child);
			}
		} else if (targetFile.isFile()) {
			if (Settings.getIntsance().getLanguage()
					.isTargetFile(targetFile.getName())) {
				list.add(targetFile.getAbsolutePath());
			}
		}
	}

	public long process() {
		final long start = System.nanoTime();
		analyze();
		detectRenamesOfFiles();
		registToDB();
		saveFiles();
		final long end = System.nanoTime();
		final long elapsedMs = (end - start) / 1000 / 1000;
		return elapsedMs;
	}

	/**
	 * リビジョンの解析を実行
	 */
	private void analyze() {
		final long revisionId = DataManagerManager.getInstance()
				.getRevisionManager().getNextId();

		System.out.println("now analyzing revision ID:" + revisionId);
		System.out.println("\tparse all files ...");

		final Thread[] threads = new Thread[Settings.getIntsance()
				.getThreadsCount()];
		final String[] targetFileArray = targetFiles.toArray(new String[0]);

		final AtomicInteger index = new AtomicInteger(0);

		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new BlockDetectThread(revisionId,
					targetFileArray, index));
			threads[i].start();
		}

		for (final Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		registRevisionInfo(revisionId);
	}

	/**
	 * 特定したリビジョン情報を登録する
	 */
	private void registRevisionInfo(final long revisionId) {
		final int numOfFiles = DataManagerManager.getInstance()
				.getFileManager().size();
		final int numOfBlocks = DataManagerManager.getInstance()
				.getUnitManager().size();

		RevisionInfo revision = new RevisionInfo(revisionId, revisionNum,
				numOfFiles, numOfBlocks);

		DataManagerManager.getInstance().getRevisionManager().add(revision);
	}

	private void detectRenamesOfFiles() {
		// TODO 類似度の閾値がハードコーディングされてる
		System.out.println("\tdetecting file renaming ... ");
		final long start = System.currentTimeMillis();
		final FileRenamesDetector detector = new FileRenamesDetector(
				new LevenshteinDistanceCalculator(), 0.5, Settings
						.getIntsance().getThreadsCount());
		final FileManager fileManager = DataManagerManager.getInstance()
				.getFileManager();
		final Map<String, String> renamedFiles = detector.detectRenames(
				FilesInPreviousRevisionManager.getInstance().getAll(),
				fileManager.getDeletedFilePaths(),
				fileManager.getAddedFilePaths());

		for (final Map.Entry<String, String> entry : renamedFiles.entrySet()) {
			fileManager.addRenamedFiles(entry.getKey(), entry.getValue());
		}
		final long end = System.currentTimeMillis();

		System.out.println("\t\t" + renamedFiles.size()
				+ " pairs of files are detected as renamed (elapsed time "
				+ (end - start) + " ms)");
	}

	private void registToDB() {
		System.out.println("\tregistering all the elements of revision "
				+ revisionNum + " into the database");
		DBRegister register = new DBRegister(maxRevisionCount);
		register.regist();
		System.out
				.println("\tall the elements are registered into the database");
	}

	private void saveFiles() {
		System.out.print("\tsaving lists of tokens ... ");
		FilesInPreviousRevisionManager.clear();
		final FilesInPreviousRevisionManager previousManager = FilesInPreviousRevisionManager
				.getInstance();
		for (final FileInfo file : DataManagerManager.getInstance()
				.getFileManager().getAllElements()) {
			previousManager.addContent(file.getPath(), file.getTokens());
		}
		System.out.println("done");
	}
}
