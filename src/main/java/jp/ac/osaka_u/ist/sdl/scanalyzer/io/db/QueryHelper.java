package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.DefaultConfiguration;

/**
 * This is a helper class for building queries.
 * 
 * @author k-hotta
 *
 */
public class QueryHelper {

	private static int maximumOfIn = DefaultConfiguration.DEFAULT_MAXIMUM_OF_IN;

	public static void setMaximumOfIn(final int maximumOfIn) {
		QueryHelper.maximumOfIn = maximumOfIn;
	}

	public static final String querySelectColumnIdIn(final String tableName,
			final String idColumn, final String targetColumnName,
			final Collection<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return "";
		}

		final StringBuilder builder = new StringBuilder();

		builder.append("select " + targetColumnName + " from " + tableName);

		builder.append(" where ");

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

	public static final String querySelectIdIn(final String tableName,
			final String idColumn, final Collection<Long> ids) {
		return querySelectColumnIdIn(tableName, idColumn, "*", ids);
	}

}
