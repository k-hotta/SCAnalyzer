package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

public enum AvailableMiningStrategy {

	GENEALOGY_PERSIST_PERIOD;

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

}
