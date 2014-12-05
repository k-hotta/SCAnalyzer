package jp.ac.osaka_u.ist.sdl.scanalyzer.data.db;

/**
 * This interface contains all the names of database tables.
 * 
 * @author k-hotta
 *
 */
public interface TableName {
	
	public static final String CLONE_CLASS = "CLONE_CLASS";

	public static final String CLONE_CLASS_MAPPING = "CLONE_CLASS_MAPPING";
	
	public static final String CLONE_GENEALOGY = "CLONE_GENEALOGY";
	
	public static final String CLONE_GENEALOGY_CLONE_CLASS = "CLONE_GENEALOGY_CLONE_CLASS";
	
	public static final String CLONE_MODIFICATION = "CLONE_MODIFICATION";
	
	public static final String CODE_FRAGMENT = "CODE_FRAGMENT";
	
	public static final String CODE_FRAGMENT_MAPPING = "CODE_FRAGMENT_MAPPING";
	
	public static final String FILE_CHANGE = "FILE_CHANGE";
	
	public static final String RAW_CLONE_CLASS = "RAW_CLONE_CLASS";
	
	public static final String RAW_CLONED_FRAGMENT = "RAW_CLONED_FRAGMENT";
	
	public static final String REVISION = "REVISION";
	
	public static final String SEGMENT = "SEGMENT";
	
	public static final String SOURCE_FILE = "SOURCE_FILE";
	
	public static final String VERSION = "VERSION";
	
	public static final String VERSION_SOURCE_FILE = "VERSION_SOURCE_FILE";

}
