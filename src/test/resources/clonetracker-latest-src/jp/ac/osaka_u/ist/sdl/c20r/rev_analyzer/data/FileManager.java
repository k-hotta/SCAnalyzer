package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.file.FileInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.manager.AbstractDBElementManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.manager.DBFileManager;

/**
 * ファイル情報を管理するマネージャー
 * 
 * @author k-hotta
 * 
 */
public class FileManager extends AbstractElementManager<FileInfo> {

	/**
	 * シングルトンオブジェクト
	 */
	private static FileManager SINGLETON = null;

	/**
	 * すでにDBに登録されているファイルパス
	 */
	private final Map<String, Long> registeredFiles;

	private final Set<Long> deletedFileIds;

	private final List<String> deletedFilePaths;

	private final Map<String, String> renamedFiles;

	private FileManager() {
		super();
		this.registeredFiles = DBFileManager.getInstance()
				.getRegisteredElements();
		this.deletedFileIds = new TreeSet<Long>();
		this.deletedFilePaths = new ArrayList<String>();
		this.renamedFiles = new TreeMap<String, String>();
	}

	/**
	 * インスタンスを取得
	 * 
	 * @return
	 */
	static FileManager getInstance() {
		synchronized (FileManager.class) {
			if (SINGLETON == null) {
				SINGLETON = new FileManager();
			}
		}

		return SINGLETON;
	}

	@Override
	void clear() {
		SINGLETON = null;
	}

	@Override
	protected AbstractDBElementManager<FileInfo> getDbManager() {
		return DBFileManager.getInstance();
	}

	/**
	 * 引数で指定されたパスのファイルがDBにあればそのIDを，なければ新しく発行したIDを返す
	 * 
	 * @param path
	 * @return
	 */
	public long getCorrespondentId(final String path) {
		if (isRegisteredFile(path)) {
			return registeredFiles.get(path);
		}

		return getNextId();
	}

	public boolean isRegisteredFile(final String path) {
		return registeredFiles.containsKey(path);
	}

	public boolean isRegisteredFile(final long id) {
		return registeredFiles.containsValue(id);
	}

	public Set<FileInfo> getAddedFiles() {
		final Set<FileInfo> result = new HashSet<FileInfo>();

		for (FileInfo file : getAllElements()) {
			if (!isRegisteredFile(file.getId())) {
				result.add(file);
			}
		}

		return result;
	}

	public Set<Long> getDeletedFileIds() {
		return deletedFileIds;
	}

	public void addDeletedFile(final String path) {
		final long id = registeredFiles.get(path);
		if (deletedFileIds.add(id)) {
			deletedFilePaths.add(path);
		}
	}

	public void addRenamedFiles(final String beforePath, final String afterPath) {
		this.renamedFiles.put(beforePath, afterPath);
	}

	public List<String> getDeletedFilePaths() {
		return Collections.unmodifiableList(deletedFilePaths);
	}

	public List<String> getAddedFilePaths() {
		final List<String> result = new ArrayList<String>();
		final Set<FileInfo> addedFiles = getAddedFiles();
		for (final FileInfo addedFile : addedFiles) {
			result.add(addedFile.getPath());
		}
		return Collections.unmodifiableList(result);
	}

	public boolean isRenamed(final String path) {
		return this.renamedFiles.containsKey(path);
	}

	public boolean isRenamed(final long id) {
		String path = null;
		for (Map.Entry<String, Long> entry : registeredFiles.entrySet()) {
			if (entry.getValue() == id) {
				path = entry.getKey();
				break;
			}
		}

		assert path != null;
		return this.isRenamed(path);
	}
	
	public long getIdHavingSpecifiedPath(final String path) {
		for (final Map.Entry<Long, FileInfo> entry : elements.entrySet()) {
			if (entry.getValue().getPath().equals(path)) {
				return entry.getKey();
			}
		}
		return -1;
	}

}
