package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

import java.util.Set;

/**
 * A class that contains configuration values of SCAnalyzer. <br>
 * 
 * @author k-hotta
 * 
 */
public class Config {

	/**
	 * The DBMS
	 */
	private DBMS dbms = null;

	/**
	 * The path of the database
	 */
	private String dbPath = null;

	/**
	 * The programming language in which the target is written
	 */
	private Language language = null;

	/**
	 * The path or URL of the target repository
	 */
	private String repository = null;

	/**
	 * The relative path of interest
	 */
	private String relativePath = "";

	/**
	 * The version control system under consideration
	 */
	private VersionControlSystem vcs = null;

	/**
	 * The type of program element
	 */
	private ElementType elementType = null;

	/**
	 * The clone detector to be used
	 */
	private CloneDetector cloneDetector = null;

	/**
	 * The directory having all the clone results (only for clone detectors that
	 * read result files that have been created before running SCAnalyzer)
	 */
	private String cloneResultDirectory = null;

	/**
	 * The format of file names for clone result files (only for clone detectors
	 * that read result files that have been created before running SCAnalyzer)
	 */
	private String cloneResultFileFormat = null;

	/**
	 * The equalizer for program elements
	 */
	private ElementEqualizer elementEqualizer = null;

	/**
	 * The algorithm to map clone classes
	 */
	private CloneClassMappingAlgorithm cloneMappingAlgorithm = null;

	/**
	 * The algorithm to map program elements
	 */
	private ElementMappingAlgorithm elementMappingAlgorithm = null;

	/**
	 * The identifier of start revision
	 */
	private String startRevisionIdentifier = null;

	/**
	 * The identifier of end revision
	 */
	private String endRevisionIdentifier = null;

	/**
	 * Whether overwriting database if it already exists
	 */
	private boolean overwriteDb = false;

	/**
	 * The id of the genealogy to be shown
	 */
	private long genealogyId = -1;

	/**
	 * The mining strategies
	 */
	private Set<AvailableMiningStrategy> miningStrategies = null;

	/**
	 * The pattern of the output file where the result of mining should be
	 * stored
	 */
	private String outputFilePattern = null;

	/**
	 * The maximum number of elements retrieved at a time
	 */
	private int maximumRetrieveCount = 100;

	/*
	 * getters and setters follow
	 */

	/**
	 * Get DBMS
	 * 
	 * @return DBMS
	 */
	public final DBMS getDbms() {
		return dbms;
	}

	/**
	 * Set DBMS with the specified one
	 * 
	 * @param dbms
	 *            DBMS to be set
	 */
	public final void setDbms(DBMS dbms) {
		this.dbms = dbms;
	}

	/**
	 * Get the path of database
	 * 
	 * @return the path of database
	 */
	public final String getDbPath() {
		return dbPath;
	}

	/**
	 * Set the path of database
	 * 
	 * @param dbPath
	 *            the path of database to be set
	 */
	public final void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}

	/**
	 * Get the programming language in which the target is written
	 * 
	 * @return the programming language
	 */
	public final Language getLanguage() {
		return language;
	}

	/**
	 * Set the programming language in which the target is written
	 * 
	 * @param language
	 *            the programming language to be set
	 */
	public final void setLanguage(Language language) {
		this.language = language;
	}

	/**
	 * Get the path or URL of the target repository
	 * 
	 * @return the path or URL of the target repository
	 */
	public final String getRepository() {
		return repository;
	}

	/**
	 * Set the path or URL of the target repository
	 * 
	 * @param repository
	 *            the path or URL of the target repository to be set
	 */
	public final void setRepository(String repository) {
		this.repository = repository;
	}

	/**
	 * Get the relative path.
	 * 
	 * @return the relative path
	 */
	public final String getRelativePath() {
		return relativePath;
	}

	/**
	 * Set the relative path with the specified one.
	 * 
	 * @param relativePath
	 *            the relative path to be set
	 */
	public final void setRelativePath(final String relativePath) {
		this.relativePath = relativePath;
	}

	/**
	 * Get the version control system under consideration
	 * 
	 * @return the version control system
	 */
	public final VersionControlSystem getVcs() {
		return vcs;
	}

	/**
	 * Set the version control system under consideration
	 * 
	 * @param vcs
	 *            the version control system under consideration to be set
	 */
	public final void setVcs(VersionControlSystem vcs) {
		this.vcs = vcs;
	}

	/**
	 * Get the type of program element.
	 * 
	 * @return the type of program element
	 */
	public final ElementType getElementType() {
		return elementType;
	}

	/**
	 * Set the type of program element with the specified one.
	 * 
	 * @param elementType
	 *            the type of program element to be set
	 */
	public final void setElementType(ElementType elementType) {
		this.elementType = elementType;
	}

	/**
	 * Get the clone detector.
	 * 
	 * @return the clone detector
	 */
	public final CloneDetector getCloneDetector() {
		return cloneDetector;
	}

	/**
	 * Set the clone detector with the specified one.
	 * 
	 * @param cloneDetector
	 *            the clone detector to be set
	 */
	public final void setCloneDetector(CloneDetector cloneDetector) {
		this.cloneDetector = cloneDetector;
	}

	/**
	 * Get the directory of clone result files.
	 * 
	 * @return the directory of clone result files
	 */
	public final String getCloneResultDirectory() {
		return cloneResultDirectory;
	}

	/**
	 * Set the directory of clone result files with the specified one.
	 * 
	 * @param cloneResultDirectory
	 *            the directory to be set
	 */
	public final void setCloneResultDirectory(String cloneResultDirectory) {
		this.cloneResultDirectory = cloneResultDirectory;
	}

	/**
	 * Get the format of file names of clone result files.
	 * 
	 * @return the format of file names of clone result files
	 */
	public final String getCloneResultFileFormat() {
		return cloneResultFileFormat;
	}

	/**
	 * Set the format of file names of clone result files with the specified
	 * one.
	 * 
	 * @param cloneResultFileFormat
	 *            the format of file names to be set
	 */
	public final void setCloneResultFileFormat(String cloneResultFileFormat) {
		this.cloneResultFileFormat = cloneResultFileFormat;
	}

	/**
	 * Get the equalizer for program elements.
	 * 
	 * @return the equalizer for program elements
	 */
	public final ElementEqualizer getElementEqualizer() {
		return elementEqualizer;
	}

	/**
	 * Set the equalizer for program elements with the specified one.
	 * 
	 * @param elementEqualizer
	 *            the equalizer to be set
	 */
	public final void setElementEqualizer(ElementEqualizer elementEqualizer) {
		this.elementEqualizer = elementEqualizer;
	}

	/**
	 * Get the algorithm for mapping clone classes.
	 * 
	 * @return the algorithm for mapping clone classes
	 */
	public final CloneClassMappingAlgorithm getCloneMappingAlgorithm() {
		return cloneMappingAlgorithm;
	}

	/**
	 * Set the algorithm for mapping clone classes with the specified one.
	 * 
	 * @param cloneMappingAlgorithm
	 *            the algorithm to be set
	 */
	public final void setCloneMappingAlgorithm(
			CloneClassMappingAlgorithm cloneMappingAlgorithm) {
		this.cloneMappingAlgorithm = cloneMappingAlgorithm;
	}

	/**
	 * Get the algorithm for mapping program elements.
	 * 
	 * @return the algorithm for mapping program elements
	 */
	public final ElementMappingAlgorithm getElementMappingAlgorithm() {
		return elementMappingAlgorithm;
	}

	/**
	 * Set the algorithm for mapping program elements with the specified one.
	 * 
	 * @param elementMappingAlgorithm
	 *            the algorithm to be set
	 */
	public final void setElementMappingAlgorithm(
			ElementMappingAlgorithm elementMappingAlgorithm) {
		this.elementMappingAlgorithm = elementMappingAlgorithm;
	}

	/**
	 * Get the identifier of the start revision.
	 * 
	 * @return the identifier of start revision
	 */
	public final String getStartRevisionIdentifier() {
		return startRevisionIdentifier;
	}

	/**
	 * Set the identifier of the start revision with the specified one.
	 * 
	 * @param startRevisionIdentifier
	 *            the identifier to be set
	 */
	public final void setStartRevisionIdentifier(String startRevisionIdentifier) {
		this.startRevisionIdentifier = startRevisionIdentifier;
	}

	/**
	 * Get the identifier of the end revision
	 * 
	 * @return the identifier of end revision
	 */
	public final String getEndRevisionIdentifier() {
		return endRevisionIdentifier;
	}

	/**
	 * Set the identifier of the end revision with the specified one.
	 * 
	 * @param endRevisionIdentifier
	 *            the identifier to be set
	 */
	public final void setEndRevisionIdentifier(String endRevisionIdentifier) {
		this.endRevisionIdentifier = endRevisionIdentifier;
	}

	/**
	 * Get whether overwriting database or not.
	 * 
	 * @return whether overwriting database or not
	 */
	public final boolean isOverwriteDb() {
		return overwriteDb;
	}

	/**
	 * Set whether overwriting database or not.
	 * 
	 * @param overwriteDb
	 *            the boolean value to be set
	 */
	public final void setOverwriteDb(boolean overwriteDb) {
		this.overwriteDb = overwriteDb;
	}

	/**
	 * Get the genealogy id to be shown.
	 * 
	 * @return the id of the genealogy to be shown
	 */
	public final long getGenealogyId() {
		return genealogyId;
	}

	/**
	 * Set the genealogy id to be shown with the specified value.
	 * 
	 * @param genealogyId
	 *            the id of the genealogy to be shown
	 */
	public final void setGenealogyId(final long genealogyId) {
		this.genealogyId = genealogyId;
	}

	/**
	 * Get the mining strategies.
	 * 
	 * @return the set of the mining strategies
	 */
	public Set<AvailableMiningStrategy> getMiningStrategies() {
		return miningStrategies;
	}

	/**
	 * Set the mining strategies.
	 * 
	 * @param miningStrategy
	 *            the strategies to be added
	 */
	public void setMiningStrategies(
			final Set<AvailableMiningStrategy> miningStrategies) {
		this.miningStrategies = miningStrategies;
	}

	/**
	 * Get the output file pattern
	 * 
	 * @return the output file pattern
	 */
	public String getOutputFilePattern() {
		return outputFilePattern;
	}

	/**
	 * Set the output file pattern
	 * 
	 * @param outputFilePattern
	 *            the pattern of the output file path to be stored
	 */
	public void setOutputFilePattern(final String outputFilePattern) {
		this.outputFilePattern = outputFilePattern;
	}

	/**
	 * Get the maximum number of elements retrieved at a time.
	 * 
	 * @return the maximum number of elements retrieved at a time
	 */
	public int getMaximumRetrieveCount() {
		return maximumRetrieveCount;
	}

	/**
	 * Set the maximum number of elements retrieved at a time.
	 * 
	 * @param maximumRetrieveCount
	 *            the integer value to be set
	 */
	public void setMaximumRetrieveCount(final int maximumRetrieveCount) {
		this.maximumRetrieveCount = maximumRetrieveCount;
	}

}
