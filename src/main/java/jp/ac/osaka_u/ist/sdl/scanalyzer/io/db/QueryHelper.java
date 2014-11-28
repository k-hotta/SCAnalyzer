package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.util.Collection;

/**
 * This is a helper class for building queries.
 * 
 * @author k-hotta
 *
 */
public class QueryHelper {

	private static int maximumOfIn = 1000;

	public static void setMaximumOfIn(final int maximumOfIn) {
		QueryHelper.maximumOfIn = maximumOfIn;
	}

	public static final String querySelectIdIn(final String tableName,
			final String idColumn, final Collection<Long> ids) {
		final StringBuilder builder = new StringBuilder();

		builder.append("select * from " + tableName + " where ");

		int count = 0;
		for (final long id : ids) {
			if (count % maximumOfIn == 0) {
				if (count != 0) {
					builder.append("or ");
				}
				builder.append(idColumn + " in (");
			}

			builder.append(id + ",");
			count++;

			if (count % maximumOfIn == 0) {
				builder.deleteCharAt(builder.length() - 1);
				builder.append(") ");
			}
		}

		if (count % maximumOfIn != 0) {
			builder.deleteCharAt(builder.length() - 1);
			builder.append(")");
		}

		return builder.toString();
	}

}
