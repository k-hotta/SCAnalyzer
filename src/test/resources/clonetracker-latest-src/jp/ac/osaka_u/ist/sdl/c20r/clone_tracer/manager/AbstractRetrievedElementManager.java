package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.manager;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.AbstractRetrievedElementInfo;

/**
 * DBから回収した各種データのマネージャー用抽象クラス
 * 
 * @author k-hotta
 * 
 * @param <T>
 */
public abstract class AbstractRetrievedElementManager<T extends AbstractRetrievedElementInfo> {

	/**
	 * 要素のマップ <br>
	 * キーは各要素のID
	 */
	private final Map<Long, T> elementsMap;

	/**
	 * コンストラクタ
	 */
	public AbstractRetrievedElementManager() {
		this.elementsMap = new TreeMap<Long, T>();
	}

	/**
	 * 全要素をマップのまま取得
	 * 
	 * @return
	 */
	public final Map<Long, T> getMap() {
		return Collections.unmodifiableMap(elementsMap);
	}

	/**
	 * 要素を全て取得
	 * 
	 * @return
	 */
	public final Set<T> getAllElements() {
		Set<T> result = new HashSet<T>();
		result.addAll(elementsMap.values());
		return result;
	}

	/**
	 * 指定したIDの要素を取得
	 * 
	 * @param id
	 * @return
	 */
	public final T getElement(final long id) {
		return elementsMap.get(id);
	}

	/**
	 * 要素を追加
	 * 
	 * @param element
	 */
	public final void add(T element) {
		elementsMap.put(element.getId(), element);
	}

	/**
	 * 要素を一括で追加
	 * 
	 * @param elements
	 */
	public final void addAll(Collection<T> elements) {
		for (T element : elements) {
			add(element);
		}
	}

	/**
	 * 要素数を取得
	 * 
	 * @return
	 */
	public final int size() {
		return elementsMap.size();
	}

}
