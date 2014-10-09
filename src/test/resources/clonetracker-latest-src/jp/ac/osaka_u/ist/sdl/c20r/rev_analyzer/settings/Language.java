package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.settings;

/**
 * ��͑Ώی����\���񋓌^
 * 
 * @author k-hotta
 * 
 */
public enum Language {

	JAVA("Java", new String[] { "java" }),

	OTHER("", new String[] {});

	private final String str;

	private final String[] suffixes;

	private Language(String str, String[] suffixes) {
		this.str = str;
		this.suffixes = suffixes;
	}
	
	@Override
	public String toString() {
		return str;
	}
	
	public String[] getSuffixes() {
		return suffixes;
	}
	
	public boolean isTargetFile(String targetFileName) {
		for (String suffix : suffixes) {
			if (targetFileName.endsWith(suffix)) {
				return true;
			}
		}
		
		return false;
	}

}
