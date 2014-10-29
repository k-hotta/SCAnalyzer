package jp.ac.osaka_u.ist.sdl.scanalyzer.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * This class contains some utility methods.
 * 
 * @author k-hotta
 * 
 */
public class StringUtil {

	/**
	 * Guess the encoding of the given bytes.
	 * 
	 * @param bytes
	 *            the target byte array
	 * @return the corresponding character set if found,
	 *         {@link java.nio.charset.StandardCharsets#UTF_8 UTF_8} otherwise.
	 */
	public static Charset guessEncoding(final byte[] bytes) {
		UniversalDetector detector = new UniversalDetector(null);
		detector.handleData(bytes, 0, bytes.length);
		detector.dataEnd();

		String encoding = detector.getDetectedCharset();
		detector.reset();

		Charset result = null;
		try {
			result = Charset.forName(encoding);
		} catch (Exception e) {
			result = StandardCharsets.UTF_8;
		}

		return result;
	}

}
