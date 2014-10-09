package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.ast.ASTCreator;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.ast.BlockDetector;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.DataManagerManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.FileManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.UnitManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.file.FileInfo;

/**
 * リビジョン中のファイルを解析してユニットの特定を行うスレッド
 * 
 * @author k-hotta
 * 
 */
public class BlockDetectThread implements Runnable {

	private final long currentRevisionId;

	private final String[] files;

	private final AtomicInteger index;

	private final FileManager fileManager;

	private final UnitManager unitManager;

	public BlockDetectThread(final long currentRevisionId,
			final String[] targetFiles, final AtomicInteger index) {
		this.currentRevisionId = currentRevisionId;
		this.files = targetFiles;
		this.index = index;
		this.fileManager = DataManagerManager.getInstance().getFileManager();
		this.unitManager = DataManagerManager.getInstance().getUnitManager();
	}

	@Override
	public void run() {
		while (true) {
			final int i = this.index.getAndIncrement();
			if (!(i < this.files.length)) {
				break;
			}

			final String target = files[i];

			print(target);

			// final long fileId = fileManager.getNextId();
			final long fileId = fileManager.getCorrespondentId(target);

			try {

				ASTCreator astCreator = new ASTCreator(target);
				BlockDetector bDetector = new BlockDetector(target,
						astCreator.getRoot(), currentRevisionId, fileId);
				astCreator.getRoot().accept(bDetector);

				unitManager.addAll(bDetector.getDetectedUnits());
				File file = new File(target);
				FileInfo fileInfo = new FileInfo(fileId, currentRevisionId,
						file.getName(), file.getAbsolutePath(), bDetector
								.getDetectedUnits().size(),
						astCreator.getTokens());
				fileManager.add(fileInfo);

			} catch (FileNotFoundException e) {
				// ここに到達するのは対象としたファイルが削除されたものであった場合のみ
				fileManager.addDeletedFile(target);
				System.out.println("\t\t\tdeleted.");
				continue;
			}
		}
	}

	private void print(String target) {
		System.out.println("\t\tparsing " + target + " ...");
	}

}
