package jp.ac.osaka_u.ist.sdl.scanalyzer.exception;

/**
 * This class represents an exception expected to be thrown when a given clone
 * result file has illegal format.
 * 
 * @author k-hotta
 * 
 */
public class IllegalCloneResultFileFormatException extends Exception {

	private static final long serialVersionUID = 7984718590916247734L;

	/**
	 * @see Exception#Exception()
	 */
	public IllegalCloneResultFileFormatException() {

	}

	/**
	 * @see Exception#Exception(String)
	 */
	public IllegalCloneResultFileFormatException(String msg) {
		super(msg);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public IllegalCloneResultFileFormatException(Throwable cause) {
		super(cause);
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public IllegalCloneResultFileFormatException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
