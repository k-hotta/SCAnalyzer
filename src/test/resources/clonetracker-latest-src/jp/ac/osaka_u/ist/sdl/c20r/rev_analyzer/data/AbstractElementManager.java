package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.manager.AbstractDBElementManager;

/**
 * �f�[�^�x�[�X�ɓo�^����e�v�f���Ǘ�����}�l�[�W���[��\�����ۃN���X
 * 
 * @author k-hotta
 * 
 * @param <T>
 *            ���̃}�l�[�W���[���Ǘ�����v�f�̌^
 */
public abstract class AbstractElementManager<T extends AbstractElementInfo> {

	/**
	 * �o�^����Ă���v�f
	 */
	protected final ConcurrentMap<Long, T> elements;

	/**
	 * �o�^����Ă���v�f�̐�
	 */
	protected final AtomicInteger count;

	/**
	 * ID�Ǘ��p�}�l�[�W���[
	 */
	protected final IdManager idManager;

	/**
	 * �R���X�g���N�^ <br>
	 * �q�N���X�̃V���O���g���I�u�W�F�N�g����z�肵�āC������ protected
	 */
	protected AbstractElementManager() {
		elements = new ConcurrentHashMap<Long, T>();
		count = new AtomicInteger(0);
		idManager = new IdManager(getDbManager());
	}

	protected abstract AbstractDBElementManager<T> getDbManager();

	/**
	 * �S�v�f���ꊇ�Ŏ擾����
	 * 
	 * @return
	 */
	public final Set<T> getAllElements() {
		Set<T> result = new HashSet<T>();
		for (T element : elements.values()) {
			result.add(element);
		}
		return result;
	}

	/**
	 * �o�^����Ă���v�f�̐����擾����
	 * 
	 * @return
	 */
	public final int size() {
		return count.get();
	}

	/**
	 * �v�f��1�ǉ�����
	 * 
	 * @param element
	 */
	public void add(T element) {
		// final int key = count.getAndIncrement();
		final long key = element.getId();
		elements.put(key, element);
		count.getAndIncrement();
	}

	/**
	 * �v�f���܂Ƃ߂Ēǉ�����
	 * 
	 * @param elements
	 */
	public final void addAll(Collection<T> elements) {
		for (T element : elements) {
			add(element);
		}
	}

	/**
	 * �}�l�[�W���[������������
	 */
	abstract void clear();

	/**
	 * �����Ŏw�肳�ꂽID�����v�f���擾����
	 * 
	 * @param id
	 * @return
	 */
	public final T getElement(long id) {
		return elements.get(id);
	}

	/**
	 * ���ɓo�^����v�f�����ׂ�ID����肵�ĕԂ�
	 * 
	 * @return
	 */
	public final long getNextId() {
		return idManager.getNextId();
	}

	/**
	 * �����Ŏw�肳�ꂽID�����v�f���o�^����Ă��邩�ǂ������擾�����
	 * 
	 * @param id
	 * @return
	 */
	public final boolean contains(final long id) {
		return elements.containsKey(id);
	}

}
