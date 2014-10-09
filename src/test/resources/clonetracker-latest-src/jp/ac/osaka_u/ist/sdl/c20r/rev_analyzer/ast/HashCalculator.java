package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.ast;

import java.util.List;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.settings.Settings;

/**
 * ハッシュ値の計算を行う抽象クラス<br>
 * 具体的な計算は子クラスで定める
 * 
 * @author k-hotta
 * 
 */
public abstract class HashCalculator {

	/**
	 * シングルトンオブジェクト
	 */
	private static HashCalculator SINGLETON = null;

	protected HashCalculator() {

	}

	/**
	 * インスタンスを取得
	 * 
	 * @return
	 */
	public static HashCalculator getInstance() {
		if (SINGLETON == null) {
			SINGLETON = createInstance();
		}
		return SINGLETON;
	}

	/**
	 * インスタンスを作成
	 * 
	 * @return
	 */
	private static HashCalculator createInstance() {
		switch (Settings.getIntsance().getHashType()) {
		case JAVA_STR_HASH:
			return new JavaStringHashCalculator();
		default:
			assert false; // here shouldn't be reached
			return null;
		}
	}

	/**
	 * 引数で与えられた文字列リストを連結してハッシュ値を計算する
	 * 
	 * @param lines
	 * @return
	 */
	public int getHash(List<String> lines) {
		StringBuilder builder = new StringBuilder();
		for (String line : lines) {
			builder.append(line);
		}
		return getHash(builder.toString());
	}

	/**
	 * ハッシュ値の具体的な計算方法を記述
	 * 
	 * @param str
	 * @return
	 */
	public abstract int getHash(String str);

}
