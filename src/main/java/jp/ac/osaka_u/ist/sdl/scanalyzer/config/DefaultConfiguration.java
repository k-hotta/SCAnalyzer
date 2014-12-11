package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

/**
 * This interface contains default configuration values for non-mandatory
 * configurations.
 * 
 * @author k-hotta
 *
 */
public interface DefaultConfiguration {

	// public static final String DEFAULT_CONFIGURATION_FILE =
	// "/scanalyzer-config.xml";
	public static final String DEFAULT_CONFIGURATION_FILE = DefaultConfiguration.class
			.getClassLoader().getResource("scanalyzer-config.xml").getPath();

	public static final String DEFAULT_DBMS = "SQLITE";

	public static final String DEFAULT_ELEMENT = "TOKEN";

	public static final String DEFAULT_DETECTOR = "Scorpio";

	public static final String DEFAULT_EQUALIZER = "near-miss";

	public static final String DEFAULT_CLONE_MAPPING = "ICLONES";

	public static final String DEFAULT_ELEMENT_MAPPING = "Traditional-diff";

	public static final Boolean DEFAULT_OVERWRITING_DB = false;

	public static final Integer DEFAULT_MAXIMUM_RETRIEVED = 100;

	public static final Integer DEFAULT_MAXIMUM_OF_IN = 500;

	public static final String DEFAULT_MINING_UNIT = "GENEALOGY";

}
