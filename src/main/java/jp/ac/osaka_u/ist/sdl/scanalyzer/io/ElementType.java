package jp.ac.osaka_u.ist.sdl.scanalyzer.io;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IAtomicElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Token;

/**
 * This is an enumeration which describes what kind of elements can be analyzed.
 * Here, a kind of element corresponds to an implementation of
 * {@link jp.ac.osaka_u.ist.sdl.scanalzer.IAtomicElement}.
 * 
 * @author k-hotta
 * 
 */
public enum ElementType {

	TOKEN;

	public Class<? extends IAtomicElement> getElementClass() {
		switch (this) {
		case TOKEN:
			return Token.class;
		}

		return null;
	}

}
