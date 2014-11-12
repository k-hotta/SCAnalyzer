package jp.ac.osaka_u.ist.sdl.scanalyzer;

/**
 * This is the main class of SCAnalyzer.
 * 
 * @author k-hotta
 *
 */
public class SCAnalyzerMain {

	/**
	 * This is the main method of SCAnalyzer.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * create a new instance of this class and then delegate all the
		 * necessary processing to the instance
		 */
		final SCAnalyzerMain main = new SCAnalyzerMain();
		main.run(args);
	}

	public void run(final String[] args) {
		loadConfig(args);
	}

	private void loadConfig(final String[] args) {

	}

}
