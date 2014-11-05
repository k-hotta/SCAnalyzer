package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.Token;

/**
 * �O�̃��r�W�����ɑ��݂���t�@�C���̕������ێ����Ă������߂̃}�l�[�W���[�N���X�D�t�@�C���̃��l�[���Ή��p�D
 * 
 * @author k-hotta
 * 
 */
public class FilesInPreviousRevisionManager {

	/**
	 * �V���O���g���I�u�W�F�N�g
	 */
	private static FilesInPreviousRevisionManager SINGLETON;

	/**
	 * �O���r�W�������̃t�@�C�����D�L�[�̓t�@�C���p�X�C�l�̓t�@�C�����瓾��ꂽ�g�[�N����D
	 */
	private final Map<String, List<Token>> files;

	private FilesInPreviousRevisionManager() {
		this.files = new TreeMap<String, List<Token>>();
	}

	public static FilesInPreviousRevisionManager getInstance() {
		synchronized (FilesInPreviousRevisionManager.class) {
			if (SINGLETON == null) {
				SINGLETON = new FilesInPreviousRevisionManager();
			}
		}

		return SINGLETON;
	}

	public static void clear() {
		SINGLETON = null;
	}

	public void addContent(final String path, final List<Token> content) {
		this.files.put(path, content);
	}

	public final List<Token> getContent(final String path) {
		if (files.containsKey(path)) {
			return files.get(path);
		} else {
			return null;
		}
	}
	
	public final Map<String, List<Token>> getAll() {
		return Collections.unmodifiableMap(files);
	}

}
