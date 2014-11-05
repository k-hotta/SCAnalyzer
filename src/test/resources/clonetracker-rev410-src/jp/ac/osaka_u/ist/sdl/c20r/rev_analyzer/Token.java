package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer;

/**
 * �g�[�N����\���N���X
 * 
 * @author k-hotta
 * 
 */
public class Token {

	/**
	 * �g�[�N���̕�����\�L
	 */
	private final String str;

	/**
	 * �g�[�N���̎��
	 */
	private final Symbol symbol;

	/**
	 * �s�ԍ�
	 */
	private final int line;

	/**
	 * ��ԍ�
	 */
	private final int column;

	/**
	 * �n�b�V���l
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
	 * �g�[�N���̕�����\�L���擾����
	 * 
	 * @return
	 */
	public final String getStr() {
		return str;
	}

	/**
	 * �g�[�N���̎�ނ��擾����
	 * 
	 * @return
	 */
	public final Symbol getSymbol() {
		return symbol;
	}

	/**
	 * �s�ԍ����擾����
	 * 
	 * @return
	 */
	public final int getLine() {
		return line;
	}

	/**
	 * ��ԍ����擾����
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
