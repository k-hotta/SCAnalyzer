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
	private Long id;

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
	 * The default constructor, which is required for this class to be a POJO.
	 */
	public Token() {
		this(null, null, -1);
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
	 */
	public Token(final Long id, final String tokenTypeStr, final int offset) {
		this.id = id;
		this.tokenTypeStr = tokenTypeStr;
		this.offset = offset;
	}

	/**
	 * Get the id of the token
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Set the id of the token with the specified value
	 */
	public void setId(Long id) {
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

}
