package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import difflib.myers.Equalizer;

/**
 * This class compares tokens based on the values of tokens.
 * 
 * @author k-hotta
 *
 */
public class Type1TokenEqualizer implements Equalizer<Token> {

	@Override
	public boolean equals(Token original, Token revised) {
		return original.getValue().equals(revised.getValue());
	}

}
