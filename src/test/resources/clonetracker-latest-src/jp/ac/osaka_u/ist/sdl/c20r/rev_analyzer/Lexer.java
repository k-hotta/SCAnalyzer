package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer;

import java.util.List;

/**
 * 字句解析器を表す抽象クラス
 * 
 * @author k-hotta
 * 
 */
public abstract class Lexer {
	
	/**
	 * 字句解析を実行し，トークン列を取得する
	 * 
	 * @param filePath
	 * @return
	 */
	public abstract List<Token> runLexicalAnalysis();

}
