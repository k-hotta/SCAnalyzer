package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data;

import java.util.LinkedList;
import java.util.List;

/**
 * �e��}�l�[�W���[���Ǘ����邽�߂̃}�l�[�W���[
 * 
 * @author k-hotta
 * 
 */
public class DataManagerManager {

	/**
	 * �V���O���g���I�u�W�F�N�g
	 */
	private static DataManagerManager SINGLETON = null;

	/**
	 * ���r�W�����}�l�[�W���[
	 */
	private final RevisionManager revisionManager = RevisionManager
			.getInstance();

	/**
	 * �t�@�C���}�l�[�W���[
	 */
	private final FileManager fileManager = FileManager.getInstance();

	/**
	 * ���j�b�g�}�l�[�W���[
	 */
	private final UnitManager unitManager = UnitManager.getInstance();

	/**
	 * �N���[���Z�b�g�}�l�[�W���[
	 */
	//private final CloneSetManager cloneManager = CloneSetManager.getInstance();
	private final CloneSetManager cloneManager = null;

	/**
	 * �S�}�l�[�W���[��A���������X�g
	 */
	private final List<AbstractElementManager<?>> allManagers;

	private DataManagerManager() {
		this.allManagers = new LinkedList<AbstractElementManager<?>>();
		this.allManagers.add(revisionManager);
		this.allManagers.add(fileManager);
		this.allManagers.add(unitManager);
		//this.allManagers.add(cloneManager);
	}

	/**
	 * �C���X�^���X���擾
	 * 
	 * @return
	 */
	public static DataManagerManager getInstance() {
		synchronized (DataManagerManager.class) {
			if (SINGLETON == null) {
				SINGLETON = new DataManagerManager();
			}
		}

		return SINGLETON;
	}

	/**
	 * ���r�W�����}�l�[�W���[���擾
	 * 
	 * @return
	 */
	public RevisionManager getRevisionManager() {
		return revisionManager;
	}

	/**
	 * �t�@�C���}�l�[�W���[���擾
	 * 
	 * @return
	 */
	public FileManager getFileManager() {
		return fileManager;
	}

	/**
	 * ���j�b�g�}�l�[�W���[���擾
	 * 
	 * @return
	 */
	public UnitManager getUnitManager() {
		return unitManager;
	}

	/**
	 * �N���[���Z�b�g�}�l�[�W���[���擾
	 * 
	 * @return
	 */
	public CloneSetManager getCloneSetManager() {
		return cloneManager;
	}

	/**
	 * �S�}�l�[�W���[��������
	 */
	public void clearAll() {
		for (AbstractElementManager<?> manager : allManagers) {
			manager.clear();
		}
		SINGLETON = null;
	}

}
