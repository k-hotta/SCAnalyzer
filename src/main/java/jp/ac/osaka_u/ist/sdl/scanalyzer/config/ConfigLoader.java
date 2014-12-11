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
		usage.put("rp",
				String.format(format, "relative path", "rp", "relative"));
		usage.put("vcs", String.format(format + additional,
				"version control system", "vcs", "version-control",
				VersionControlSystem.canBe()));
		usage.put("e", String.format(format + additional, "program element",
				"e", "element", ElementType.canBe()));
		usage.put("c", String.format(format + additional, "clone detector",
				"c", "detector", CloneDetector.canBe()));
		usage.put("cr", String.format(format, "directory of the clone results",
				"cr", "result-directory"));
		usage.put("ff", String.format(format,
				"format of names of clone result files", "ff",
				"filename-format"));
		usage.put("rl", String.format(format,
				"additional file relocation finder", "rl", "relocation"));
		usage.put("eq", String.format(format + additional,
				"equalizer for program elements", "eq", "equalizer",
				ElementEqualizer.canBe()));
		usage.put("cm", String.format(format + additional,
				"mapping algorithm for clones", "cm", "clone-mapping",
				CloneClassMappingAlgorithm.canBe()));
		usage.put("em", String.format(format + additional,
				"mapping algorithm for elements", "em", "element-mapping",
				ElementMappingAlgorithm.canBe()));
		usage.put("start", String.format(format, "start revision identifier",
				"start", "start"));
		usage.put("end",
				String.format(format, "end revision identifier", "end", "end"));
		usage.put(
				"ow",
				"Please specify whether overwriting database if exists as a command line argument with \"-ow\" "
						+ "or as an xml node \"overwrite\" in the configuration file");
		usage.put("id",
				"Please specify the id of the genealogy to be shown with \"-id\"");
		usage.put("strategy", String.format(format + additional,
				"strategy of mining", "strategy", "mining-strategy",
				AvailableMiningStrategy.canBe()));
		usage.put("output", String.format(format, "output file path", "output",
				"output-file"));
		usage.put("max", String.format(format,
				"the maximum number of elements retrieved at a time", "max",
				"max-retrieved"));
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

		// relative path
		options.addOption(makeOption("rp", "relative", true, "relative path",
				1, false));

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

		// start revision
		options.addOption(makeOption("start", "start-revision", true,
				"identifier of start revision", 1, false));

		// end revision
		options.addOption(makeOption("end", "end-revision", true,
				"identifier of end revision", 1, false));

		// overwriting database
		options.addOption(makeOption("ow", "overwrite", true,
				"whether overwriting database", 1, false));

		// the id of genealogy to be shown
		// NOTE: this is just for UI mode
		options.addOption(makeOption("id", "genealogy-id", true,
				"the id of the genealogy to be shown", 1, false));

		// the mining strategy
		// NOTE: this is just for Mining
		options.addOption(makeOption("strategy", "mining-strategy", true,
				"the strategy of mining", 1, false));

		// the output file path
		// NOTE: this is just for Mining
		options.addOption(makeOption("output", "output-file", true,
				"the output file path", 1, false));

		// the maximum number of elements retrieved at a time
		// NOTE: this is just for Mining
		options.addOption(makeOption("max", "maximum", true,
				"the maximum number of elements retrieved at a time", 1, false));

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
	 * @return an instance of {@link Config} that has all the specified
	 *         configuration values, <code>null</code> if any error occurred.
	 * 
	 * @throws Exception
	 *             If any error occurred when loading
	 */
	public Config load(final String[] args) throws Exception {
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
			logErrorAndUsage(errors);

			// cannot continue further processing
			return null;
		}

		errors.clear();

		// setup an instance of Config
		final Config result = setupConfig(errors, configsAsText);

		// if any error occurred
		// log the cause of the errors
		// and the usage of configurations for which the errors occurred
		if (!errors.isEmpty()) {
			logErrorAndUsage(errors);

			// cannot continue further processing
			return null;
		}

		// Config has been successfully initialized
		return result;
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

		loadConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText, "rp",
				"relative", null, true);

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

		loadConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText, "start",
				"start", null, true);

		loadConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText, "end",
				"end", null, true);

		loadConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText, "ow",
				"overwrite", DEFAULT_OVERWRITING_DB.toString(), true);

		/*
		 * the following options are specific ones for modes
		 */

		if (cmd.hasOption("id")) {
			loadedConfigsAsText.put("id", cmd.getOptionValue("id"));
		}

		loadConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"strategy", "mining-strategy", null, true);

		loadConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText, "output",
				"output-file", null, true);

		loadConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText, "max",
				"max-retrieved", DEFAULT_MAXIMUM_RETRIEVED.toString(), true);

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
					.getOptionValue(optionName) : xmlParser.getNodes(nodeName)
					.get(0).getValue();
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

	/**
	 * Log error messages and usages
	 * 
	 * @param errors
	 *            error messages
	 */
	private void logErrorAndUsage(final Map<String, String> errors) {
		for (final Map.Entry<String, String> errorEntry : errors.entrySet()) {
			eLogger.fatal(errorEntry.getKey()
					+ " has not been correctly specified");
			eLogger.fatal(errorEntry.getValue());
		}

		for (final Map.Entry<String, String> errorEntry : errors.entrySet()) {
			uLogger.fatal(usage.get(errorEntry.getKey()));
		}
	}

	/**
	 * Set up an instance of {@link Config}
	 * 
	 * @param errors
	 * @param configsAsText
	 * @return
	 */
	private Config setupConfig(final Map<String, String> errors,
			final Map<String, String> configsAsText) {
		final DBMS dbms = DBMS.getCorrespondingDBMS(configsAsText.get("dbms"));
		final String dbPath = configsAsText.get("d");
		final Language language = Language
				.getCorrespondingLanguage(configsAsText.get("l"));
		final String repository = configsAsText.get("r");
		final String relativePath = configsAsText.get("rp");
		final VersionControlSystem vcs = VersionControlSystem
				.getCorrespondingVersionControlSystem(configsAsText.get("vcs"));
		final ElementType elementType = ElementType
				.getCorrespondingElementType(configsAsText.get("e"));
		final CloneDetector detector = CloneDetector
				.getCorrespondingCloneDetector(configsAsText.get("c"));
		final String cloneResultDirectory = configsAsText.get("cr");
		final String cloneResultFileFormat = configsAsText.get("ff");
		// final String relocationFinder = configsAsText.get("rl");
		final ElementEqualizer elementEqualizer = ElementEqualizer
				.getCorrespondingElementEqualizer(configsAsText.get("eq"));
		final CloneClassMappingAlgorithm cloneMappingAlgorithm = CloneClassMappingAlgorithm
				.getCorrespondingCloneClassMappingAlgorithm(configsAsText
						.get("cm"));
		final ElementMappingAlgorithm elementMappingAlgorithm = ElementMappingAlgorithm
				.getCorrespondingDBMS(configsAsText.get("em"));
		final String startRevisionIdentifier = configsAsText.get("start");
		final String endRevisionIdentifier = configsAsText.get("end");
		final String overwritingDb = configsAsText.get("ow");
		final String genealogyIdStr = configsAsText.get("id");
		final AvailableMiningStrategy miningStrategy = AvailableMiningStrategy
				.getCorrespondingStrategy(configsAsText.get("strategy"));
		final String outputFilePath = configsAsText.get("output");
		final String maximumRetrieved = configsAsText.get("max");

		final Config result = new Config();

		final String format = "a wrong value or no value is set to %s";

		if (dbms != null) {
			result.setDbms(dbms);
		} else {
			errors.put("dbms", String.format(format, "dbms"));
		}

		if (dbPath != null) {
			result.setDbPath(dbPath);
		} else {
			errors.put("d", String.format(format, "database path"));
		}

		if (language != null) {
			result.setLanguage(language);
		} else {
			errors.put("l", String.format(format, "language"));
		}

		if (repository != null) {
			result.setRepository(repository);
		} else {
			errors.put("r", String.format(format, "repository"));
		}

		if (relativePath != null) {
			result.setRelativePath(relativePath);
		}

		if (vcs != null) {
			result.setVcs(vcs);
		} else {
			errors.put("vcs", String.format(format, "version control system"));
		}

		if (elementType != null) {
			result.setElementType(elementType);
		} else {
			errors.put("e", String.format(format, "element type"));
		}

		if (detector != null) {
			result.setCloneDetector(detector);
		} else {
			errors.put("c", String.format(format, "clone detector"));
		}

		if (cloneResultDirectory != null) {
			result.setCloneResultDirectory(cloneResultDirectory);
		}

		if (cloneResultFileFormat != null) {
			result.setCloneResultFileFormat(cloneResultFileFormat);
		}

		if (elementEqualizer != null) {
			result.setElementEqualizer(elementEqualizer);
		} else {
			errors.put("eq", String.format(format, "element equalizer"));
		}

		if (cloneMappingAlgorithm != null) {
			result.setCloneMappingAlgorithm(cloneMappingAlgorithm);
		} else {
			errors.put("cm",
					String.format(format, "clone class mapping algorithm"));
		}

		if (elementMappingAlgorithm != null) {
			result.setElementMappingAlgorithm(elementMappingAlgorithm);
		} else {
			errors.put("em", String.format(format, "element mapping algorithm"));
		}

		if (startRevisionIdentifier != null) {
			result.setStartRevisionIdentifier(startRevisionIdentifier);
		}

		if (endRevisionIdentifier != null) {
			result.setEndRevisionIdentifier(endRevisionIdentifier);
		}

		if (overwritingDb != null) {
			result.setOverwriteDb(Boolean.valueOf(overwritingDb));
		}

		if (genealogyIdStr != null) {
			try {
				result.setGenealogyId(Long.parseLong(genealogyIdStr));
			} catch (Exception e) {
				errors.put("id", "cannot parse the given value "
						+ genealogyIdStr + " to long");
			}
		}

		if (miningStrategy != null) {
			result.setMiningStrategy(miningStrategy);
		}

		if (outputFilePath != null) {
			result.setOutputFilePath(outputFilePath);
		}

		if (maximumRetrieved != null) {
			try {
				result.setMaximumRetrieveCount(Integer
						.parseInt(maximumRetrieved));
			} catch (Exception e) {
				errors.put("max", "cannot parse the given value "
						+ maximumRetrieved + " to integer");
			}
		}

		return result;
	}

}
