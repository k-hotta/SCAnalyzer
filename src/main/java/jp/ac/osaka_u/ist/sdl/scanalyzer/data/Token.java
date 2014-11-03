package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * This class represents tokens, which is one of the atomic code elements.
 * 
 * @author k-hotta
 * 
 */
public class Token implements IProgramElement {

	/**
	 * The type of token
	 */
	private ETokenType type;

	/**
	 * The source file
	 */
	private SourceFile<Token> sourceFile;

	/**
	 * The line within the source file
	 */
	private int line;

	/**
	 * The character offset within the source file
	 */
	private int offset;

	/**
	 * The position within the source file, which is in terms of tokens
	 */
	private int position;

	/**
	 * The value of this token
	 */
	private String value;

	/**
	 * Construct an object with the specified values
	 * 
	 * @param token
	 *            the object of token gained by conqat scanner
	 * @param sourceFile
	 *            the source file
	 * @param position
	 *            the position
	 */
	public Token(final IToken token, final SourceFile<Token> sourceFile,
			final int position) {
		this.type = token.getType();
		this.sourceFile = sourceFile;
		this.line = token.getLineNumber() + 1;
		this.offset = token.getOffset();
		this.position = position;

		final String s = (token.getType() == ETokenType.EOF) ? Long.valueOf(
				sourceFile.getId()).toString() : token.getText();

		this.value = s;
	}

	/**
	 * Get the source file
	 */
	@Override
	public SourceFile<Token> getOwnerSourceFile() {
		return sourceFile;
	}

	/**
	 * Get the position
	 */
	@Override
	public int getPosition() {
		return position;
	}

	/**
	 * Get the value
	 */
	@Override
	public String getValue() {
		return value;
	}

	/**
	 * Get the type of this token
	 * 
	 * @return the type
	 */
	public ETokenType getType() {
		return type;
	}

	/**
	 * Get the line
	 * 
	 * @return the line
	 */
	@Override
	public int getLine() {
		return line;
	}

	/**
	 * Get the offset
	 * 
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Judge whether the given object equals to this object. <br>
	 * 
	 * @return <code>true</code> if the given object is an instance of
	 *         {@link Token} and the values of the two tokens are the same to
	 *         each other, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Token)) {
			return false;
		}

		final Token another = (Token) obj;

		return this.sourceFile.equals(another.getOwnerSourceFile())
				&& this.position == another.getPosition();
	}

	/**
	 * Return a hash code value of this object, which is calculated based on
	 * hash value of source file and the position.
	 * 
	 * @return the hash value
	 */
	@Override
	public int hashCode() {
		final int sourceFileHash = 31 * (this.sourceFile.hashCode() + 13);
		final int positionHash = 23 * this.position;

		return sourceFileHash + positionHash;
	}

	@Override
	public String toString() {
		return this.value;
	}

}
