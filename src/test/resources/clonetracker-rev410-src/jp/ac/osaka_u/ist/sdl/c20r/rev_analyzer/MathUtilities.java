package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer;

/**
 * A class to provide utilities for mathematical operations.
 * 
 * @author k-hotta
 * 
 */
public class MathUtilities {

	/**
	 * Get the minimum value of input values
	 * 
	 * @param args
	 *            values
	 * @return the minimum value
	 */
	public static int min(int... args) {
		if (args.length == 0) {
			return -1;
		}

		int result = args[0];
		for (final int tmp : args) {
			if (tmp < result) {
				result = tmp;
			}
		}

		return result;
	}

}
