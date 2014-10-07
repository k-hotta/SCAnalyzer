package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

/**
 * This class represents token, which is the base unit of any code fragments. <br>
 * This class implements {@link IDBElement IDBElement} so the objects should be
 * persistent and stored in DB. <br>
 * 
 * @author k-hotta
 * 
 */
public class Token implements IDBElement {

	/**
	 * The id of the token. This is the primary key of the token.
	 */
	private long id;

	/**
	 * The string representation of the type of this token. <br>
	 * The type of tokens in this class depends on
	 * org.conqat.lib.scanner.ETokenType. <br>
	 * This class uses string representations instead of ETokenType itself for
	 * the ease of storing into DB. <br>
	 */
	private String tokenTypeStr;

	/**
	 * The offset of this token within the owner source file.
	 */
	private int offset;

	/**
	 * The value of this token
	 */
	private String value;

	/**
	 * The default constructor, which is required to use hibernate.
	 */
	public Token() {
		this(null, null, -1, null);
	}

	/**
	 * Constructor with all the values to be specified
	 * 
	 * @param id
	 *            the value of id to be set
	 * @param tokenTypeStr
	 *            the string representation of the type of this token
	 * @param offset
	 *            the offset of this token within the source file
	 * @param value
	 *            the value of this token
	 */
	public Token(final long id, final String tokenTypeStr, final int offset,
			final String value) {
		this.id = id;
		this.tokenTypeStr = tokenTypeStr;
		this.offset = offset;
		this.value = value;
	}

	/**
	 * Get the id of the token
	 */
	public long getId() {
		return id;
	}

	/**
	 * Set the id of the token with the specified value
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get the string representation of the type of this token
	 * 
	 * @return the string representation of the type of this token
	 */
	public String getTokenTypeStr() {
		return tokenTypeStr;
	}

	/**
	 * Set the string representation of the token
	 * 
	 * @param tokenTypeStr
	 *            the string representation of the type of this token to be set
	 */
	public void setTokenTypeStr(final String tokenTypeStr) {
		this.tokenTypeStr = tokenTypeStr;
	}

	/**
	 * Get the offset of this token
	 * 
	 * @return the offset of this token
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Set the offset of this token
	 * 
	 * @param offset
	 *            the offset value to be set
	 */
	public void setOffset(final int offset) {
		this.offset = offset;
	}

	/**
	 * Get the value of this token
	 * 
	 * @return the value of this token
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the value of this token
	 * 
	 * @param value
	 *            the value of token to be set
	 */
	public void setValue(final String value) {
		this.value = value;
	}

}
