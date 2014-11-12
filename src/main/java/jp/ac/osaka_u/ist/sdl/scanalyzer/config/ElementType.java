package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Token;

/**
 * This is an enumeration which describes what kind of elements can be analyzed.
 * Here, a kind of element corresponds to an implementation of
 * {@link jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement}.
 * 
 * @author k-hotta
 * 
 */
public enum ElementType {

	TOKEN;

	public Class<? extends IProgramElement> getElementClass() {
		switch (this) {
		case TOKEN:
			return Token.class;
		}

		return null;
	}

	/**
	 * Get the corresponding element of this enum for the given string.
	 * 
	 * @param str
	 *            the query to get ElementType ignoring the case
	 * @return the corresponding ElementType if found, <code>null</code> if not
	 *         found
	 */
	public static ElementType getCorrespondingElementType(final String str) {
		if (str == null) {
			return null;
		}

		final String upperStr = str.toUpperCase();

		ElementType result = null;
		try {
			result = ElementType.valueOf(upperStr);
		} catch (Exception e) {
			// ignore
		}

		return result;
	}

	/**
	 * Provide a string that represents which values can be used as ElementType.
	 * 
	 * @return a string representing available values
	 */
	public static String canBe() {
		final StringBuilder builder = new StringBuilder();
		builder.append("{ ");

		for (final DBMS value : DBMS.values()) {
			builder.append(value.toString() + ", ");
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.deleteCharAt(builder.length() - 1);

		builder.append(" }");

		return builder.toString();
	}

}
