package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer;

import java.util.List;


/**
 * A class to calculate levenshtein distance
 * 
 * @author k-hotta
 * 
 */
public class LevenshteinDistanceCalculator implements ISimilarityCalculator {

	/**
	 * Calculate Levenshtein distance between two strings
	 * 
	 * @param str1
	 * @param str2
	 * @return Levenshtein distance between str1 and str2
	 */
	public static int calcLevenshteinDistance(List<Token> str1, List<Token> str2) {
		final int len1 = str1.size();
		final int len2 = str2.size();
		final int[][] d = new int[len1 + 1][len2 + 1];

		for (int i = 0; i < len1 + 1; i++) {
			d[i][0] = i;
		}
		for (int j = 0; j < len2 + 1; j++) {
			d[0][j] = j;
		}

		for (int i = 1; i < len1 + 1; i++) {
			for (int j = 1; j < len2 + 1; j++) {
				int cost;
				if (str1.get(i - 1).getHash() == str2.get(j - 1).getHash()) {
					cost = 0;
				} else {
					cost = 1;
				}
				d[i][j] = MathUtilities.min(d[i - 1][j] + 1, d[i][j - 1] + 1,
						d[i - 1][j - 1] + cost);
			}
		}
		return d[len1][len2];
	}

	/**
	 * Calculate normalized Levenshtein distance between two strings.
	 * 
	 * @param str1
	 * @param str2
	 * @return Normalized Levenshtein distance, which is calculated by dividing
	 *         original Levenshtein distance by the maximum length of two
	 *         strings.
	 */
	@Override
	public double calc(List<Token> str1, List<Token> str2) {
		if (str1 == null || str2 == null) {
			return 0.0;
		}
		final int maxLength = Math.max(str1.size(), str2.size());
		final int ld = calcLevenshteinDistance(str1, str2);
		return 1.0 - ((double) ld / (double) maxLength);
	}

}
