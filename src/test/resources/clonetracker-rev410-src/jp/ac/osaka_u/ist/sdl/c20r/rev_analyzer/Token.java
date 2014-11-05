package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer;

/**
 * トークンを表すクラス
 * 
 * @author k-hotta
 * 
 */
public class Token {

	/**
	 * トークンの文字列表記
	 */
	private final String str;

	/**
	 * トークンの種類
	 */
	private final Symbol symbol;

	/**
	 * 行番号
	 */
	private final int line;

	/**
	 * 列番号
	 */
	private final int column;

	/**
	 * ハッシュ値
	 */
	private final int hash;

	public Token(final String str, final Symbol symbol, final int line,
			final int column) {
		this.str = str;
		this.symbol = symbol;
		this.line = line;
		this.column = column;
		this.hash = str.hashCode();
	}

	/**
	 * トークンの文字列表記を取得する
	 * 
	 * @return
	 */
	public final String getStr() {
		return str;
	}

	/**
	 * トークンの種類を取得する
	 * 
	 * @return
	 */
	public final Symbol getSymbol() {
		return symbol;
	}

	/**
	 * 行番号を取得する
	 * 
	 * @return
	 */
	public final int getLine() {
		return line;
	}

	/**
	 * 列番号を取得する
	 * 
	 * @return
	 */
	public final int getColumn() {
		return column;
	}

	public final int getHash() {
		return hash;
	}

}
