package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.xml.ConfigXMLParser;

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
public class ConfigLoader implements DefaultConfiguration, ConfigConstant {

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
				"strategy of mining", "strategy", "strategy",
				AvailableMiningStrategy.canBe()));
		usage.put("output", String.format(format,
				"pattern of the output file path", "output",
				"output-file-pattern"));
		usage.put("max", String.format(format,
				"the maximum number of elements retrieved at a time", "max",
				"max-retrieved"));
		usage.put("unit", String.format(format + additional, "unit of mining",
				"unit", "unit", MiningUnit.canBe()));
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

		// the unit of mining
		// NOTE: this is just for Mining
		options.addOption(makeOption("unit", "mining-unit", true,
				"the unit of mining", 1, false));

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
		final ConfigXMLParser xmlParser = new ConfigXMLParser();
		try {
			xmlParser.parse(configFile);
		} catch (Exception e) {
			eLogger.fatal("cannot read the configuration file");
			eLogger.fatal("if you don't want to use the default file, you have to specify the alternative");
			throw e;
		}

		// load configurations as texts
		// the command line arguments superior to xml files
		// that is, for each of configuration, if the value of it is specified
		// as the command line argument, that in the xml file will be ignored
		final Map<String, String> singleConfigsAsText = loadSingleConfigsAsText(
				cmd, errors, xmlParser);
		final Map<String, List<String>> multipleConfigsAsText = loadMultipleConfigsAsText(
				cmd, errors, xmlParser);

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
		final Config result = setupConfig(errors, singleConfigsAsText,
				multipleConfigsAsText);

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
	private Map<String, String> loadSingleConfigsAsText(final CommandLine cmd,
			final Map<String, String> errors, final ConfigXMLParser xmlParser) {
		final Map<String, String> loadedConfigsAsText = new TreeMap<>();

		loadSingleConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"dbms", "dbms", DEFAULT_DBMS, false);

		loadSingleConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"d", "database", null, false);

		loadSingleConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"l", "language", null, false);

		loadSingleConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"r", "repository", null, false);

		loadSingleConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"rp", "relative", null, true);

		loadSingleConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"vcs", "version-control", null, false);

		loadSingleConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"e", "element", DEFAULT_ELEMENT, false);

		loadSingleConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"c", "detector", DEFAULT_DETECTOR, false);

		loadSingleConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"cr", "result-directory", null, true);

		loadSingleConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"ff", "filename-format", null, true);

		loadSingleConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"rl", "relocation", null, true);

		loadSingleConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"eq", "equalizer", DEFAULT_EQUALIZER, false);

		loadSingleConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"cm", "clone-mapping", DEFAULT_CLONE_MAPPING, false);

		loadSingleConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"em", "element-mapping", DEFAULT_ELEMENT_MAPPING, false);

		loadSingleConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"start", "start", null, true);

		loadSingleConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"end", "end", null, true);

		loadSingleConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"ow", "overwrite", DEFAULT_OVERWRITING_DB.toString(), true);

		/*
		 * the following options are specific ones for modes
		 */

		if (cmd.hasOption("id")) {
			loadedConfigsAsText.put("id", cmd.getOptionValue("id"));
		}

		loadSingleConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"output", "output-file-pattern", null, true);

		loadSingleConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"max", "max-retrieved", DEFAULT_MAXIMUM_RETRIEVED.toString(),
				true);

		loadSingleConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"unit", "unit", DEFAULT_MINING_UNIT, true);

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
	private void loadSingleConfigAsText(final CommandLine cmd,
			final Map<String, String> errors, final ConfigXMLParser xmlParser,
			final Map<String, String> loadedConfigAsText,
			final String optionName, final String nodeName,
			final String defaultValue, final boolean nullable) {
		String valueStr = defaultValue;
		try {
			valueStr = (cmd.hasOption(optionName)) ? cmd
					.getOptionValue(optionName) : xmlParser
					.getSingleValue(nodeName);
		} catch (Exception e) {
			if (!nullable) {
				errors.put(optionName, e.toString());
			}
		}
		if (valueStr != null) {
			loadedConfigAsText.put(optionName, valueStr);
		} else if (!nullable && !errors.containsKey(optionName)) {
			errors.put(optionName, "the specified value is null");
		}
	}

	/**
	 * Load every configuration value which can be multiply specified.
	 * 
	 * @param cmd
	 * @param errors
	 * @param xmlParser
	 * @return
	 */
	private Map<String, List<String>> loadMultipleConfigsAsText(
			final CommandLine cmd, final Map<String, String> errors,
			final ConfigXMLParser xmlParser) {
		final Map<String, List<String>> loadedConfigsAsText = new TreeMap<>();

		loadMultipleConfigAsText(cmd, errors, xmlParser, loadedConfigsAsText,
				"strategy", "strategy", null, true);

		return loadedConfigsAsText;
	}

	/**
	 * Load a configuration value which can be multiply specified.
	 * 
	 * @param cmd
	 * @param errors
	 * @param xmlParser
	 * @param loadedConfigsAsText
	 * @param optionName
	 * @param nodeName
	 * @param defaultValue
	 * @param nullable
	 */
	private void loadMultipleConfigAsText(final CommandLine cmd,
			final Map<String, String> errors, final ConfigXMLParser xmlParser,
			final Map<String, List<String>> loadedConfigsAsText,
			final String optionName, final String nodeName,
			final String defaultValue, final boolean nullable) {
		List<String> values = new ArrayList<>();

		try {
			if (cmd.hasOption(optionName)) {
				values.add(cmd.getOptionValue(optionName));
			} else {
				final List<String> valuesInFile = xmlParser
						.getMultipleValues(nodeName);

				if (valuesInFile == null || valuesInFile.isEmpty()) {
					values.addAll(valuesInFile);
				} else if (defaultValue != null) {
					values.add(defaultValue);
				}
			}
		} catch (Exception e) {
			if (!nullable) {
				errors.put(optionName, e.toString());
			}
		}

		if (values.isEmpty()) {
			values = null;
		}

		if (values != null) {
			loadedConfigsAsText.put(optionName, values);
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
	 * @param singleConfigsAsText
	 * @return
	 */
	private Config setupConfig(final Map<String, String> errors,
			final Map<String, String> singleConfigsAsText,
			final Map<String, List<String>> multipleConfigsAsText) {
		final DBMS dbms = DBMS.getCorrespondingDBMS(singleConfigsAsText
				.get("dbms"));
		final String dbPath = singleConfigsAsText.get("d");
		final Language language = Language
				.getCorrespondingLanguage(singleConfigsAsText.get("l"));
		final String repository = singleConfigsAsText.get("r");
		final String relativePath = singleConfigsAsText.get("rp");
		final VersionControlSystem vcs = VersionControlSystem
				.getCorrespondingVersionControlSystem(singleConfigsAsText
						.get("vcs"));
		final ElementType elementType = ElementType
				.getCorrespondingElementType(singleConfigsAsText.get("e"));
		final CloneDetector detector = CloneDetector
				.getCorrespondingCloneDetector(singleConfigsAsText.get("c"));
		final String cloneResultDirectory = singleConfigsAsText.get("cr");
		final String cloneResultFileFormat = singleConfigsAsText.get("ff");
		// final String relocationFinder = configsAsText.get("rl");
		final ElementEqualizer elementEqualizer = ElementEqualizer
				.getCorrespondingElementEqualizer(singleConfigsAsText.get("eq"));
		final CloneClassMappingAlgorithm cloneMappingAlgorithm = CloneClassMappingAlgorithm
				.getCorrespondingCloneClassMappingAlgorithm(singleConfigsAsText
						.get("cm"));
		final ElementMappingAlgorithm elementMappingAlgorithm = ElementMappingAlgorithm
				.getCorrespondingDBMS(singleConfigsAsText.get("em"));
		final String startRevisionIdentifier = singleConfigsAsText.get("start");
		final String endRevisionIdentifier = singleConfigsAsText.get("end");
		final String overwritingDb = singleConfigsAsText.get("ow");
		final String genealogyIdStr = singleConfigsAsText.get("id");
		final Set<AvailableMiningStrategy> miningStrategies = new HashSet<>();
		if (multipleConfigsAsText.containsKey("strategy")) {
			for (final String miningStrategyStr : multipleConfigsAsText
					.get("strategy")) {
				miningStrategies.add(AvailableMiningStrategy
						.getCorrespondingStrategy(miningStrategyStr));
			}
		}
		final String outputFilePath = singleConfigsAsText.get("output");
		final String maximumRetrieved = singleConfigsAsText.get("max");
		final MiningUnit miningUnit = MiningUnit
				.getCorrespondingUnit(singleConfigsAsText.get("unit"));

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

		if (!miningStrategies.isEmpty()) {
			result.setMiningStrategies(miningStrategies);
		}

		if (outputFilePath != null) {
			result.setOutputFilePattern(outputFilePath);
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

		if (miningUnit != null) {
			result.setMiningUnit(miningUnit);
		}

		return result;
	}

}
