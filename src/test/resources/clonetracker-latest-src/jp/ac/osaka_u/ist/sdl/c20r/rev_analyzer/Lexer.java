package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer;

import java.util.List;

/**
 * �����͊��\�����ۃN���X
 * 
 * @author k-hotta
 * 
 */
public abstract class Lexer {
	
	/**
	 * �����͂����s���C�g�[�N������擾����
	 * 
	 * @param filePath
	 * @return
	 */
	public abstract List<Token> runLexicalAnalysis();

}
