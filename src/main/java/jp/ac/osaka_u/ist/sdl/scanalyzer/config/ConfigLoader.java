package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is for loading configurations from command line arguments and/or
 * configuration files.
 * 
 * @author k-hotta
 *
 */
public class ConfigLoader implements DefaultConfiguration {

	/**
	 * The logger for errors
	 */
	private static final Logger eLogger = LogManager.getLogger("error");

	/**
	 * The logger for usage
	 */
	private static final Logger uLogger = LogManager.getLogger("usage");

	/**
	 * The usage for each configuration element. This will be used in case where
	 * the configuration has not been correctly specified.
	 */
	private static final Map<String, String> usage = new TreeMap<>();

	/**
	 * Static initializer for usage
	 */
	static {
		final String format = "Please specify the %s as a command line argument with \"-%s\" "
				+ "or as an xml node \"%s\" in the configuration file";
		final String additional = ", whose values can be %s";
		usage.put(
				"dbms",
				String.format(format + additional, "DBMS", "dbms", "dbms",
						DBMS.canBe()));
		usage.put("d", String.format(format, "database", "d", "database"));
		usage.put("l", String.format(format + additional, "language", "l",
				"language", Language.canBe()));
		usage.put("r", String.format(format, "repository", "r", "repository"));
		usage.put("vcs", String.format(format + additional,
				"version control system", "vcs", "version-control",
				VersionControlSystem.canBe()));
		usage.put("e", String.format(format + additional, "program element",
				"e", "element", ElementType.canBe()));
		usage.put("c", String.format(format, "clone detector", "c", "detector"));
		usage.put("cr", String.format(format, "directory of the clone results",
				"cr", "result-directory"));
		usage.put("ff", String.format(format,
				"format of names of clone result files", "ff",
				"filename-format"));
		usage.put("rl", String.format(format,
				"additional file relocation finder", "rl", "relocation"));
		usage.put("eq", String.format(format, "equalizer for program elements",
				"eq", "equalizer"));
		usage.put("cm", String.format(format, "mapping algorithm for clones",
				"cm", "clone-mapping"));
		usage.put("em", String.format(format, "mapping algorithm for elements",
				"em", "element-mapping"));
	}

	/**
	 * The options
	 */
	private final Options options;

	public ConfigLoader() {
		this.options = prepareOptions();
	}

	/**
	 * Define command line arguments.
	 * 
	 * @return prepared options
	 */
	private Options prepareOptions() {
		final Options options = new Options();

		// configuration file
		options.addOption(makeOption("config", "configuration", true,
				"configuration file", 1, false));

		// database management system
		options.addOption(makeOption("dbms", "database-management", true,
				"database management system", 1, false));

		// database path
		options.addOption(makeOption("d", "db", true, "path of database file",
				1, false));

		// language
		options.addOption(makeOption("l", "language", true, "language", 1,
				false));

		// repository
		options.addOption(makeOption("r", "repository", true,
				"path of target repository", 1, false));

		// version control system
		options.addOption(makeOption("vcs", "version-control", true,
				"version control system", 1, false));

		// program element
		options.addOption(makeOption("e", "element", true, "program element",
				1, false));

		// clone detector
		options.addOption(makeOption("c", "clone-detector", true,
				"clone detector to be used", 1, false));

		// clone result directory
		options.addOption(makeOption("cr", "clone-results", true,
				"clone results directory", 1, false));

		// file name format of clone result files
		options.addOption(makeOption("ff", "file-format", true,
				"file name format of clone result files", 1, false));

		// relocation finder (additional)
		options.addOption(makeOption("rl", "relocation", true,
				"relocation finder", 1, false));

		// equalizer for elements
		options.addOption(makeOption("eq", "equalizer", true,
				"equalizer of elements", 1, false));

		// mapping algorithm for clones
		options.addOption(makeOption("cm", "clone-mapping", true,
				"mapping algorithm for clones", 1, false));

		// mapping algorithm for elements
		options.addOption(makeOption("em", "element-mapping", true,
				"mapping algorithm for elements", 1, false));

		return options;
	}

	/**
	 * Make an instance of Option.
	 * 
	 * @param opt
	 * @param longOpt
	 * @param hasArg
	 * @param description
	 * @param argNum
	 * @param required
	 * @return
	 */
	private Option makeOption(final String opt, final String longOpt,
			final boolean hasArg, final String description, final int argNum,
			final boolean required) {
		final Option option = new Option(opt, longOpt, hasArg, description);
		if (hasArg) {
			option.setArgs(argNum);
		}
		option.setRequired(required);
		return option;
	}

	/**
	 * Load the given array that contains all the command line arguments and
	 * specified configuration file.
	 * 
	 * @param args
	 *            the command line arguments
	 * 
	 * @return <code>true</code> if the loading procedure has successfully
	 *         finished, <code>false</code> otherwise.
	 * 
	 * @throws Exception
	 *             If any error occurred when loading
	 */
	public boolean load(final String[] args) throws Exception {
		final CommandLineParser parser = new PosixParser();
		final CommandLine cmd = parser.parse(options, args);

		final String configFile = (cmd.hasOption("config")) ? cmd
				.getOptionValue("config") : DEFAULT_CONFIGURATION_FILE;

		// contains which property has not been correctly specified
		// and the error message corresponds to it
		final Map<String, String> errors = new TreeMap<>();

		// parse the configuration file
		final ConfigFileParser xmlParser = new ConfigFileParser();
		try {
			xmlParser.parse(configFile);
		} catch (Exception e) {
			eLogger.fatal("cannot read the configuration file");
			eLogger.fatal("if you want to use not default file, you have to specify the alternative");
			throw e;
		}

		// load configurations as texts
		// the command line arguments superior to xml files
		// that is, for each of configuration, if the value of it is specified
		// as the command line argument, that in the xml file will be ignored
		final Map<String, String> configsAsText = loadConfigsAsText(cmd,
				errors, xmlParser);

		// if any error occurred
		// log the cause of the errors
		// and the usage of configurations for which the errors occurred
		if (!errors.isEmpty()) {
			for (final Map.Entry<String, String> errorEntry : errors.entrySet()) {
				eLogger.fatal(errorEntry.getKey()
						+ " has not been correctly specified");
				eLogger.fatal(errorEntry.getValue());
			}

			for (final Map.Entry<String, String> errorEntry : errors.entrySet()) {
				uLogger.fatal(usage.get(errorEntry.getKey()));
			}

			// cannot continue further processing
			return false;
		}

		errors.clear();

		final DBMS dbms = DBMS.getCorrespondingDBMS(configsAsText.get("dbms"));
		final String dbPath = configsAsText.get("d");
		final Language language = Language
				.getCorrespondingLanguage(configsAsText.get("l"));
		final String repository = configsAsText.get("r");
		final VersionControlSystem vcs = VersionControlSystem
				.getCorrespondingVersionControlSystem(configsAsText.get("vcs"));
		final ElementType elementType = ElementType
				.getCorrespondingElementType(configsAsText.get("e"));
		final CloneDetector detector = CloneDetector
				.getCorrespondingCloneDetector(configsAsText.get("c"));
		final ElementEqualizer elementEqualizer = ElementEqualizer
				.getCorrespondingElementEqualizer(configsAsText.get("eq"));

		return true;
	}

	/**
	 * Load every configuration element as a text.
	 * 
	 * @param cmd
	 * @param errors
	 * @param xmlParser
	 * @return
	 */
	private Map<String, String> loadConfigsAsText(final CommandLine cmd,
			final Map<String, String> errors, final ConfigFileParser xmlParser) {
		final Map<String, String> loadedConfigsAsText = new TreeMap<>();

		loadConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText, "dbms",
				"dbms", DEFAULT_DBMS, false);

		loadConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText, "d",
				"database", null, false);

		loadConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText, "l",
				"language", null, false);

		loadConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText, "r",
				"repository", null, false);

		loadConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText, "vcs",
				"version-control", null, false);

		loadConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText, "e",
				"element", DEFAULT_ELEMENT, false);

		loadConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText, "c",
				"detector", DEFAULT_DETECTOR, false);

		loadConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText, "cr",
				"result-directory", null, true);

		loadConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText, "ff",
				"filename-format", null, true);

		loadConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText, "rl",
				"relocation", null, true);

		loadConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText, "eq",
				"equalizer", DEFAULT_EQUALIZER, false);

		loadConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText, "cm",
				"clone-mapping", DEFAULT_CLONE_MAPPING, false);

		loadConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText, "em",
				"element-mapping", DEFAULT_ELEMENT_MAPPING, false);

		// TODO implement

		return loadedConfigsAsText;
	}

	/**
	 * Load a configuration as a text.
	 * 
	 * @param cmd
	 * @param errors
	 * @param xmlParser
	 * @param loadedConfigAsText
	 * @param optionName
	 * @param nodeName
	 * @param defaultValue
	 * @param nullable
	 */
	private void loadConfigAsText(final CommandLine cmd,
			final Map<String, String> errors, final ConfigFileParser xmlParser,
			final Map<String, String> loadedConfigAsText,
			final String optionName, final String nodeName,
			final String defaultValue, final boolean nullable) {
		String dbmsStr = defaultValue;
		try {
			dbmsStr = (cmd.hasOption(optionName)) ? cmd
					.getOptionValue(optionName) : xmlParser.getValue(nodeName);
		} catch (Exception e) {
			if (!nullable) {
				errors.put(optionName, e.toString());
			}
		}
		if (dbmsStr != null) {
			loadedConfigAsText.put(optionName, dbmsStr);
		} else if (!nullable && !errors.containsKey(optionName)) {
			errors.put(optionName, "the specified value is null");
		}
	}
}
