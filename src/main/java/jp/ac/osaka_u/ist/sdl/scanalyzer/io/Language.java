package jp.ac.osaka_u.ist.sdl.scanalyzer.io;

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
	JAVA("java"),

	/**
	 * C
	 */
	C("c", "h"),

	/**
	 * C++
	 */
	CPP("c", "cpp", "cxx", "h", "hxx", "hpp");

	/**
	 * The array of suffixes
	 */
	private final String[] suffixes;

	/**
	 * The constructor with the array of suffixes
	 * 
	 * @param suffixes
	 *            the array of suffixes
	 */
	private Language(String... suffixes) {
		this.suffixes = suffixes;
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
	 * Judge whether the given file is a relative file of this language.
	 * 
	 * @param filePath
	 *            the file to be judged
	 * @return <code>true</code> if the given file is relative to this language,
	 *         <code>false</code> otherwise
	 */
	public final boolean isRelativeFile(final String filePath) {
		for (final String suffix : suffixes) {
			if (filePath.endsWith("." + suffix)) {
				return true;
			}
		}

		return false;
	}

}
