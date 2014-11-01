package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.io.File;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBFileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBRawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBRawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBSegment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFileWithContent;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Token;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBVersionSourceFile;

import org.w3c.dom.Node;

/**
 * This class parses an xml file that describes data in a virtual database for
 * testing.
 * 
 * @author k-hotta
 * 
 */
public class DBXmlParser {

	/**
	 * The path of the xml file to be parsed
	 */
	private String xmlPath;

	private DBMS dbms;

	private String path;

	private SortedMap<Long, DBVersion> versions;

	private SortedMap<Long, DBRevision> revisions;

	private SortedMap<Long, DBSourceFile> sourceFiles;

	private SortedMap<Long, DBFileChange> fileChanges;

	private SortedMap<Long, DBSegment> segments;

	private SortedMap<Long, CodeFragment> codeFragments;

	private SortedMap<Long, DBCloneClass> cloneClasses;

	private SortedMap<Long, DBRawCloneClass> rawCloneClasses;

	private SortedMap<Long, DBRawClonedFragment> rawClonedFragments;

	private SortedMap<Long, DBVersionSourceFile> versionSourceFiles;

	private SortedMap<Long, SourceFileWithContent<Token>> fileContents;

	private SortedMap<Long, String> fileContentsStr;

	public static void main(String[] args) throws Exception {
		DBXmlParser parser = new DBXmlParser("src/test/resources/test-db.xml");
		parser.parse();
	}

	public DBXmlParser(final String xmlPath) {
		this.dbms = null;
		this.path = null;
		this.xmlPath = xmlPath;
		this.versions = new TreeMap<Long, DBVersion>();
		this.revisions = new TreeMap<Long, DBRevision>();
		this.sourceFiles = new TreeMap<Long, DBSourceFile>();
		this.fileChanges = new TreeMap<Long, DBFileChange>();
		this.segments = new TreeMap<Long, DBSegment>();
		this.codeFragments = new TreeMap<Long, CodeFragment>();
		this.cloneClasses = new TreeMap<Long, DBCloneClass>();
		this.rawCloneClasses = new TreeMap<Long, DBRawCloneClass>();
		this.rawClonedFragments = new TreeMap<Long, DBRawClonedFragment>();
		this.versionSourceFiles = new TreeMap<Long, DBVersionSourceFile>();
		this.fileContents = new TreeMap<Long, SourceFileWithContent<Token>>();
		this.fileContentsStr = new TreeMap<Long, String>();
	}

	public final DBMS getDbms() {
		return dbms;
	}

	public final String getPath() {
		return path;
	}

	public final Map<Long, DBVersion> getVersions() {
		return versions;
	}

	public final Map<Long, DBRevision> getRevisions() {
		return revisions;
	}

	public final Map<Long, DBSourceFile> getSourceFiles() {
		return sourceFiles;
	}

	public final Map<Long, DBFileChange> getFileChanges() {
		return fileChanges;
	}

	public final Map<Long, DBSegment> getSegments() {
		return segments;
	}

	public final Map<Long, CodeFragment> getCodeFragments() {
		return codeFragments;
	}

	public final Map<Long, DBCloneClass> getCloneClasses() {
		return cloneClasses;
	}

	public final Map<Long, DBRawCloneClass> getRawCloneClasses() {
		return rawCloneClasses;
	}

	public final Map<Long, DBRawClonedFragment> getRawClonedFragments() {
		return rawClonedFragments;
	}

	public final Map<Long, DBVersionSourceFile> getVersionSourceFiles() {
		return versionSourceFiles;
	}

	public final Map<Long, SourceFileWithContent<Token>> getFileContents() {
		return fileContents;
	}

	public final Map<Long, String> getFileContentsStr() {
		return fileContentsStr;
	}

	public void parse() throws Exception {
		final DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		final Node root = builder.parse(new File(xmlPath));

		if (root.getNodeType() != Node.DOCUMENT_NODE) {
			throw new IllegalStateException("the root is not document");
		}

		final DBXmlNodeParser parser = new DBXmlNodeParser(versions, revisions,
				sourceFiles, fileChanges, segments, codeFragments,
				cloneClasses, rawCloneClasses, rawClonedFragments,
				versionSourceFiles, fileContents, fileContentsStr);
		parser.processRootNode(root);

		this.dbms = parser.getDbms();
		this.path = parser.getPath();
	}

}
