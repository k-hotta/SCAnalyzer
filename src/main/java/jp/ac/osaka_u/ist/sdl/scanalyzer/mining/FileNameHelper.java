package jp.ac.osaka_u.ist.sdl.scanalyzer.mining;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.ConfigConstant;

/**
 * This is a helper class to provide file names.
 * 
 * @author k-hotta
 *
 */
public class FileNameHelper {

	/**
	 * Get the file name from the given pattern. If the pattern contains
	 * pre-fixed pattern strings defined in {@link ConfigConstant}, the
	 * pre-fixed pattern strings will be replaced with the actual values.
	 * 
	 * @param strategy
	 *            the strategy under consideration
	 * @param pattern
	 *            the pattern of the file name
	 * 
	 * @return the generated file name
	 * 
	 * @see ConfigConstant
	 */
	public static String getFileName(
			final WriteFileMiningStrategy<?, ?> strategy, final String pattern) {
		final StringBuilder builder = new StringBuilder(pattern);

		int index = builder.indexOf(ConfigConstant.FILE_PATTERN_STRATEGY_NAME);
		if (index >= 0) {
			// strategy name should be included
			final String strategyName = strategy.getStrategyName();
			builder.replace(index, index
					+ ConfigConstant.FILE_PATTERN_STRATEGY_NAME.length() - 1,
					strategyName);
		}

		index = builder.indexOf(ConfigConstant.FILE_PATTERN_DATABASE_NAME);
		if (index >= 0) {
			// database name should be included
			final String databaseName = strategy.getDatabaseName();
			builder.replace(index, index
					+ ConfigConstant.FILE_PATTERN_DATABASE_NAME.length() - 1,
					databaseName);
		}

		return builder.toString();
	}
}