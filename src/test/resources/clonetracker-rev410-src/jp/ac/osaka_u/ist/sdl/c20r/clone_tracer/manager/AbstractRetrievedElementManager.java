package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.manager;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.AbstractRetrievedElementInfo;

/**
 * DB�����������e��f�[�^�̃}�l�[�W���[�p���ۃN���X
 * 
 * @author k-hotta
 * 
 * @param <T>
 */
public abstract class AbstractRetrievedElementManager<T extends AbstractRetrievedElementInfo> {

	/**
	 * �v�f�̃}�b�v <br>
	 * �L�[�͊e�v�f��ID
	 */
	private final Map<Long, T> elementsMap;

	/**
	 * �R���X�g���N�^
	 */
	public AbstractRetrievedElementManager() {
		this.elementsMap = new TreeMap<Long, T>();
	}

	/**
	 * �S�v�f���}�b�v�̂܂܎擾
	 * 
	 * @return
	 */
	public final Map<Long, T> getMap() {
		return Collections.unmodifiableMap(elementsMap);
	}

	/**
	 * �v�f��S�Ď擾
	 * 
	 * @return
	 */
	public final Set<T> getAllElements() {
		Set<T> result = new HashSet<T>();
		result.addAll(elementsMap.values());
		return result;
	}

	/**
	 * �w�肵��ID�̗v�f���擾
	 * 
	 * @param id
	 * @return
	 */
	public final T getElement(final long id) {
		return elementsMap.get(id);
	}

	/**
	 * �v�f��ǉ�
	 * 
	 * @param element
	 */
	public final void add(T element) {
		elementsMap.put(element.getId(), element);
	}

	/**
	 * �v�f���ꊇ�Œǉ�
	 * 
	 * @param elements
	 */
	public final void addAll(Collection<T> elements) {
		for (T element : elements) {
			add(element);
		}
	}

	/**
	 * �v�f�����擾
	 * 
	 * @return
	 */
	public final int size() {
		return elementsMap.size();
	}

}
