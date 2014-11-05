package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import difflib.myers.Equalizer;

/**
 * This class compares tokens for Type-2 clones, which is based on types of
 * tokens rather than values of them.
 * 
 * @author k-hotta
 *
 */
public class Type2TokenEqualizer implements Equalizer<Token> {

	/**
	 * Compare tokens based on their types. If the types of both tokens equal to
	 * each other and the type is a special one, this method compares the values
	 * of tokens. Otherwise, this method compares only the types of tokens,
	 * which means that the result will be true if the types of the tokens are
	 * the same to each other.
	 */
	@Override
	public boolean equals(Token original, Token revised) {
		if (original.getType().isSpecial()
				&& original.getType() == revised.getType()) {
			return original.getValue().equals(revised.getValue());
		}

		return original.getType() == revised.getType();
	}

}
