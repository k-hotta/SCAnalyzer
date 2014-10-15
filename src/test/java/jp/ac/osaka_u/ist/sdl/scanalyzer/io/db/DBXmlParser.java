package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange.Type;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.VersionSourceFile;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

	private Map<Long, Version> versions;

	private Map<Long, Revision> revisions;

	private Map<Long, SourceFile> sourceFiles;

	private Map<Long, FileChange> fileChanges;

	private Map<Long, RawCloneClass> rawCloneClasses;

	private Map<Long, RawClonedFragment> rawClonedFragments;

	private Map<Long, VersionSourceFile> versionSourceFiles;

	public static void main(String[] args) throws Exception {
		DBXmlParser parser = new DBXmlParser("src/test/resources/test-db.xml");
		parser.parse();
	}

	public DBXmlParser(final String xmlPath) {
		this.dbms = null;
		this.path = null;
		this.xmlPath = xmlPath;
		this.versions = new TreeMap<Long, Version>();
		this.revisions = new TreeMap<Long, Revision>();
		this.sourceFiles = new TreeMap<Long, SourceFile>();
		this.fileChanges = new TreeMap<Long, FileChange>();
		this.rawCloneClasses = new TreeMap<Long, RawCloneClass>();
		this.rawClonedFragments = new TreeMap<Long, RawClonedFragment>();
		this.versionSourceFiles = new TreeMap<Long, VersionSourceFile>();
	}

	public final DBMS getDbms() {
		return dbms;
	}

	public final String getPath() {
		return path;
	}

	public final Map<Long, Version> getVersions() {
		return versions;
	}

	public final Map<Long, Revision> getRevisions() {
		return revisions;
	}

	public final Map<Long, SourceFile> getSourceFiles() {
		return sourceFiles;
	}

	public final Map<Long, FileChange> getFileChanges() {
		return fileChanges;
	}

	public final Map<Long, RawCloneClass> getRawCloneClasses() {
		return rawCloneClasses;
	}

	public final Map<Long, RawClonedFragment> getRawClonedFragments() {
		return rawClonedFragments;
	}

	public final Map<Long, VersionSourceFile> getVersionSourceFiles() {
		return versionSourceFiles;
	}

	public void parse() throws Exception {
		final DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		final Node root = builder.parse(new File(xmlPath));

		if (root.getNodeType() != Node.DOCUMENT_NODE) {
			throw new IllegalStateException("the root is not document");
		}

		final DBXmlNodeParser parser = new DBXmlNodeParser(versions, revisions,
				sourceFiles, fileChanges, rawCloneClasses, rawClonedFragments,
				versionSourceFiles);
		parser.processRootNode(root);

		this.dbms = parser.getDbms();
		this.path = parser.getPath();
	}

}
