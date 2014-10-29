package jp.ac.osaka_u.ist.sdl.scanalyzer.io;

import org.conqat.lib.scanner.ELanguage;

/**
 * This enumeration describes usable programming languages and their relative
 * suffixes.
 * 
 * @author k-hotta
 * 
 */
public enum Language {

	/**
	 * Java
	 */
	JAVA(ELanguage.JAVA, "java"),

	/**
	 * C
	 */
	C(ELanguage.CPP, "c", "h"),

	/**
	 * C++
	 */
	CPP(ELanguage.CPP, "c", "cpp", "cxx", "h", "hxx", "hpp");

	/**
	 * The array of suffixes
	 */
	private final String[] suffixes;

	/**
	 * The language defined in conqat
	 */
	private final ELanguage eLanguage;

	/**
	 * The constructor with the array of suffixes
	 * 
	 * @param suffixes
	 *            the array of suffixes
	 */
	private Language(ELanguage eLanguage, String... suffixes) {
		this.eLanguage = eLanguage;
		this.suffixes = suffixes;
	}

	/**
	 * Get the corresponding ELanguage.
	 * 
	 * @return the corresponding ELanguage
	 */
	public final ELanguage getCorrespondingELanguage() {
		return this.eLanguage;
	}

	/**
	 * Get the relevant suffixes of the language as an array.
	 * 
	 * @return an array that has relevant suffixes of this language
	 */
	public final String[] getSuffixes() {
		return this.suffixes;
	}

	/**
	 * Judge whether the given file is a relevant file of this language.
	 * 
	 * @param filePath
	 *            the file to be judged
	 * @return <code>true</code> if the given file is relevant to this language,
	 *         <code>false</code> otherwise
	 */
	public final boolean isRelevantFile(final String filePath) {
		for (final String suffix : suffixes) {
			if (filePath.endsWith("." + suffix)) {
				return true;
			}
		}

		return false;
	}

}
