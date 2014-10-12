package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

/**
 * This class represents each entry of changes of files. It has the information
 * about paths of the files before/after changed in addition to the type of
 * change.
 * <p>
 * Note: This class has only raw information of file changes. It is necessary to
 * detect file relocations afterward.
 * </p>
 * 
 * @author k-hotta
 * 
 */
public class FileChangeEntry {

	/**
	 * The path of the file before changed. <br>
	 * This can be null in case the file was added without copying or moving
	 * from any other files.
	 */
	private final String beforePath;

	/**
	 * The path of the file after changed. <br>
	 * This can be null in case the file was deleted.
	 */
	private final String afterPath;

	/**
	 * The type of the change. <br>
	 * The supported types are M(modify), A(addition), D(deletion), and
	 * R(relocation).
	 */
	private final char type;

	/**
	 * The constructor with all the values specified
	 * 
	 * @param beforePath
	 *            the path of the file before changed
	 * @param afterPath
	 *            the path of the file after changed
	 * @param type
	 *            the type of the change
	 */
	public FileChangeEntry(final String beforePath, final String afterPath,
			final char type) {
		assert beforePath != null || afterPath != null;
		this.beforePath = beforePath;
		this.afterPath = afterPath;
		this.type = type;
	}

	/**
	 * Get the path of the file before changed.
	 * 
	 * @return the path of the file before changed
	 */
	public final String getBeforePath() {
		return beforePath;
	}

	/**
	 * Get the path of the file after changed.
	 * 
	 * @return the path of the file after changed
	 */
	public final String getAfterPath() {
		return afterPath;
	}

	/**
	 * Get the type of the change.
	 * 
	 * @return the type of the change
	 */
	public final char getType() {
		return type;
	}

	@Override
	public String toString() {
		final String typeStr = String.valueOf(type);
		if (beforePath == null) {
			return typeStr + ":  " + afterPath;
		} else if (afterPath == null) {
			return typeStr + ": " + beforePath;
		} else {
			return typeStr + ": " + beforePath + " => " + afterPath;
		}
	}

}
