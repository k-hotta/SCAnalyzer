package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.manager.AbstractDBElementManager;

/**
 * データベースに登録する各要素を管理するマネージャーを表す抽象クラス
 * 
 * @author k-hotta
 * 
 * @param <T>
 *            このマネージャーが管理する要素の型
 */
public abstract class AbstractElementManager<T extends AbstractElementInfo> {

	/**
	 * 登録されている要素
	 */
	protected final ConcurrentMap<Long, T> elements;

	/**
	 * 登録されている要素の数
	 */
	protected final AtomicInteger count;

	/**
	 * ID管理用マネージャー
	 */
	protected final IdManager idManager;

	/**
	 * コンストラクタ <br>
	 * 子クラスのシングルトンオブジェクト化を想定して，可視性は protected
	 */
	protected AbstractElementManager() {
		elements = new ConcurrentHashMap<Long, T>();
		count = new AtomicInteger(0);
		idManager = new IdManager(getDbManager());
	}

	protected abstract AbstractDBElementManager<T> getDbManager();

	/**
	 * 全要素を一括で取得する
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
	 * 登録されている要素の数を取得する
	 * 
	 * @return
	 */
	public final int size() {
		return count.get();
	}

	/**
	 * 要素を1つ追加する
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
	 * 要素をまとめて追加する
	 * 
	 * @param elements
	 */
	public final void addAll(Collection<T> elements) {
		for (T element : elements) {
			add(element);
		}
	}

	/**
	 * マネージャーを初期化する
	 */
	abstract void clear();

	/**
	 * 引数で指定されたIDを持つ要素を取得する
	 * 
	 * @param id
	 * @return
	 */
	public final T getElement(long id) {
		return elements.get(id);
	}

	/**
	 * 次に登録する要素が持つべきIDを特定して返す
	 * 
	 * @return
	 */
	public final long getNextId() {
		return idManager.getNextId();
	}

	/**
	 * 引数で指定されたIDを持つ要素が登録されているかどうかを取得するる
	 * 
	 * @param id
	 * @return
	 */
	public final boolean contains(final long id) {
		return elements.containsKey(id);
	}

}
