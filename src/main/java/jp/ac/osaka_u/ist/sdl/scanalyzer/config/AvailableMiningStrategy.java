package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

public enum AvailableMiningStrategy {

	GENEALOGY_PERSIST_PERIOD("persist"),

	GENEALOGY_COMMON_ELEMENTS_PERIOD("common-elements"),

	GENEALOGY_SIMILARITY_GHOST_PERIOD("similarity"),
	
	GENEALOGY_SIMILARITY_GHOST_AVERAGE("average-similarity"),

	GENEALOGY_MODIFICATIONS("modifications"),
	
	GENEALOGY_GHOST_MODIFICATIONS("ghost-modifications");

	/**
	 * The shortened name
	 */
	private final String shortName;

	private AvailableMiningStrategy(final String shortName) {
		this.shortName = shortName;
	}

	/**
	 * Get the shortened name
	 * 
	 * @return
	 */
	public final String getShortName() {
		return shortName;
	}

	/**
	 * Provide a string that represents which values can be used as
	 * AvailableMiningStrategy.
	 * 
	 * @return a string representing available values
	 */
	public static String canBe() {
		final StringBuilder builder = new StringBuilder();
		builder.append("{ ");

		for (final AvailableMiningStrategy value : values()) {
			builder.append(value.toString() + ", ");
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.deleteCharAt(builder.length() - 1);

		builder.append(" }");

		return builder.toString();
	}

	/**
	 * Get the corresponding element of this enum for the given string.
	 * 
	 * @param str
	 *            the query to get AvailableMiningStrategy ignoring the case
	 * @return the corresponding AvailableMiningStrategy if found,
	 *         <code>null</code> if not found
	 */
	public static AvailableMiningStrategy getCorrespondingStrategy(
			final String str) {
		if (str == null) {
			return null;
		}

		final String upperStr = str.toUpperCase();

		AvailableMiningStrategy result = null;
		try {
			result = AvailableMiningStrategy.valueOf(upperStr);
		} catch (Exception e) {
			// ignore
		}

		if (result != null) {
			return result;
		}

		for (AvailableMiningStrategy strategy : AvailableMiningStrategy
				.values()) {
			if (strategy.getShortName().equalsIgnoreCase(str)) {
				result = strategy;
				break;
			}
		}

		return result;
	}

}
