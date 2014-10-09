package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.revision.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.manager.AbstractDBElementManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.manager.DBRevisionManager;

/**
 * ���r�W���������Ǘ�����N���X <br>
 * �P�ꃊ�r�W�����̉�͎��́C1�̃��r�W�������݂̂��Ǘ����C�����̃��r�W�������̓o�^�͋����Ȃ�
 * 
 * @author k-hotta
 * 
 */
public class RevisionManager extends AbstractElementManager<RevisionInfo> {

	/**
	 * �V���O���g���I�u�W�F�N�g
	 */
	private static RevisionManager SINGLETON = null;

	private RevisionManager() {
		super();
	}

	/**
	 * �C���X�^���X���擾����
	 * 
	 * @return
	 */
	public static RevisionManager getInstance() {
		synchronized (RevisionManager.class) {
			if (SINGLETON == null) {
				SINGLETON = new RevisionManager();
			}
		}

		return SINGLETON;
	}

	/**
	 * �����̗v�f�̒ǉ���h�����߂ɐe�N���X�̃��\�b�h���I�[�o�[���C�h
	 */
	@Override
	public void add(RevisionInfo element) {
		synchronized (RevisionManager.class) {
			if (count.get() == 0) {
				final long key = element.getId();
				elements.put(key, element);
				count.getAndIncrement(); // �C���N�������g�����������Ȃ̂�get�����l�͎g��Ȃ�
			}
		}
	}

	/**
	 * ���݂̃��r�W���������擾���� <br>
	 * ���o�^�ł���� null
	 * 
	 * @return
	 */
	public RevisionInfo getCurrentRevision() {
		for (RevisionInfo revision : elements.values()) {
			return revision;
		}
		return null;
	}

	@Override
	void clear() {
		SINGLETON = null;
	}

	@Override
	protected AbstractDBElementManager<RevisionInfo> getDbManager() {
		return DBRevisionManager.getInstance();
	}

}
