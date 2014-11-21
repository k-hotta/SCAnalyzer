package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.io.File;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.DBMS;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Token;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersionSourceFile;

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

	private SortedMap<Long, DBCodeFragment> codeFragments;

	private SortedMap<Long, DBCloneClass> cloneClasses;

	private SortedMap<Long, DBRawCloneClass> rawCloneClasses;

	private SortedMap<Long, DBRawClonedFragment> rawClonedFragments;

	private SortedMap<Long, DBVersionSourceFile> versionSourceFiles;

	private SortedMap<Long, Map<Integer, Token>> fileContents;

	private SortedMap<Long, String> fileContentsStr;

	private SortedMap<Long, Version<Token>> volatileVersions;

	private SortedMap<Long, Revision> volatileRevisions;

	private SortedMap<Long, SourceFile<Token>> volatileSourceFiles;

	private SortedMap<Long, FileChange<Token>> volatileFileChanges;

	private SortedMap<Long, Segment<Token>> volatileSegments;

	private SortedMap<Long, CodeFragment<Token>> volatileCodeFragments;

	private SortedMap<Long, CloneClass<Token>> volatileCloneClasses;

	private SortedMap<Long, RawCloneClass<Token>> volatileRawCloneClasses;

	private SortedMap<Long, RawClonedFragment<Token>> volatileRawClonedFragments;

	public static void main(String[] args) throws Exception {
		DBXmlParser parser = new DBXmlParser("src/test/resources/test-db.xml");
		parser.parse();
	}

	public DBXmlParser(final String xmlPath) {
		this.dbms = null;
		this.path = null;
		this.xmlPath = xmlPath;
		this.versions = new TreeMap<>();
		this.revisions = new TreeMap<>();
		this.sourceFiles = new TreeMap<>();
		this.fileChanges = new TreeMap<>();
		this.segments = new TreeMap<>();
		this.codeFragments = new TreeMap<>();
		this.cloneClasses = new TreeMap<>();
		this.rawCloneClasses = new TreeMap<>();
		this.rawClonedFragments = new TreeMap<>();
		this.versionSourceFiles = new TreeMap<>();
		this.fileContents = new TreeMap<>();
		this.fileContentsStr = new TreeMap<>();
		this.volatileVersions = new TreeMap<>();
		this.volatileRevisions = new TreeMap<>();
		this.volatileSourceFiles = new TreeMap<>();
		this.volatileFileChanges = new TreeMap<>();
		this.volatileSegments = new TreeMap<>();
		this.volatileCodeFragments = new TreeMap<>();
		this.volatileCloneClasses = new TreeMap<>();
		this.volatileRawCloneClasses = new TreeMap<>();
		this.volatileRawClonedFragments = new TreeMap<>();
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

	public final Map<Long, DBCodeFragment> getCodeFragments() {
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

	public final Map<Long, Map<Integer, Token>> getFileContents() {
		return fileContents;
	}

	public final Map<Long, String> getFileContentsStr() {
		return fileContentsStr;
	}

	public final String getXmlPath() {
		return xmlPath;
	}

	public final SortedMap<Long, Version<Token>> getVolatileVersions() {
		return volatileVersions;
	}

	public final SortedMap<Long, Revision> getVolatileRevisions() {
		return volatileRevisions;
	}

	public final SortedMap<Long, SourceFile<Token>> getVolatileSourceFiles() {
		return volatileSourceFiles;
	}

	public final SortedMap<Long, FileChange<Token>> getVolatileFileChanges() {
		return volatileFileChanges;
	}

	public final SortedMap<Long, Segment<Token>> getVolatileSegments() {
		return volatileSegments;
	}

	public final SortedMap<Long, CodeFragment<Token>> getVolatileCodeFragments() {
		return volatileCodeFragments;
	}

	public final SortedMap<Long, CloneClass<Token>> getVolatiileCloneClasses() {
		return volatileCloneClasses;
	}

	public final SortedMap<Long, RawCloneClass<Token>> getVolatileRawCloneClasses() {
		return volatileRawCloneClasses;
	}

	public final SortedMap<Long, RawClonedFragment<Token>> getVolatileRawClonedFragments() {
		return volatileRawClonedFragments;
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
				versionSourceFiles, fileContentsStr, fileContents);
		parser.processRootNode(root);

		this.dbms = parser.getDbms();
		this.path = parser.getPath();

		for (final DBVersion version : this.versions.values()) {
			setVolatileData(version);
		}
	}

	private void setVolatileData(final DBVersion dbVersion) {
		final Version<Token> version = new Version<>(dbVersion);
		this.volatileVersions.put(version.getId(), version);

		final Revision revision = new Revision(dbVersion.getRevision());
		version.setRevision(revision);
		this.volatileRevisions.put(revision.getId(), revision);

		for (final DBSourceFile dbSourceFile : dbVersion.getSourceFiles()) {
			final SourceFile<Token> sourceFile = new SourceFile<>(dbSourceFile);
			final Map<Integer, Token> contents = fileContents.get(dbSourceFile
					.getId());
			sourceFile.setContents(contents.values());
			this.volatileSourceFiles.put(sourceFile.getId(), sourceFile);

			version.addSourceFile(sourceFile);
		}

		for (final DBFileChange dbFileChange : dbVersion.getFileChanges()) {
			final FileChange<Token> fileChange = new FileChange<>(dbFileChange);
			final SourceFile<Token> oldSourceFile = (dbFileChange
					.getOldSourceFile() == null) ? null
					: this.volatileSourceFiles.get(dbFileChange
							.getOldSourceFile().getId());
			final SourceFile<Token> newSourceFile = (dbFileChange
					.getNewSourceFile() == null) ? null
					: this.volatileSourceFiles.get(dbFileChange
							.getNewSourceFile().getId());

			fileChange.setOldSourceFile(oldSourceFile);
			fileChange.setNewSourceFile(newSourceFile);
			fileChange.setVersion(version);

			this.volatileFileChanges.put(fileChange.getId(), fileChange);

			version.addFileChange(fileChange);
		}

		for (final DBCloneClass dbCloneClass : dbVersion.getCloneClasses()) {
			final CloneClass<Token> cloneClass = new CloneClass<>(dbCloneClass);
			this.volatileCloneClasses.put(cloneClass.getId(), cloneClass);
			cloneClass.setVersion(version);
			version.addCloneClass(cloneClass);

			for (final DBCodeFragment dbCodeFragment : dbCloneClass
					.getCodeFragments()) {
				final CodeFragment<Token> codeFragment = new CodeFragment<>(
						dbCodeFragment);
				this.volatileCodeFragments.put(codeFragment.getId(),
						codeFragment);

				cloneClass.addCodeFragment(codeFragment);
				codeFragment.setCloneClass(cloneClass);

				for (final DBSegment dbSegment : dbCodeFragment.getSegments()) {
					final Segment<Token> segment = new Segment<>(dbSegment);
					this.volatileSegments.put(segment.getId(), segment);

					segment.setSourceFile(this.volatileSourceFiles
							.get(dbSegment.getSourceFile().getId()));
					segment.setContents(segment
							.getSourceFile()
							.getContents()
							.subMap(dbSegment.getStartPosition(),
									dbSegment.getEndPosition() + 1).values());

					codeFragment.addSegment(segment);
					segment.setCodeFragment(codeFragment);
				}
			}
		}

		for (final DBRawCloneClass dbRawCloneClass : dbVersion
				.getRawCloneClasses()) {
			final RawCloneClass<Token> rawCloneClass = new RawCloneClass<>(
					dbRawCloneClass);
			rawCloneClass.setVersion(version);
			version.addRawCloneClass(rawCloneClass);
			this.volatileRawCloneClasses.put(rawCloneClass.getId(),
					rawCloneClass);

			for (final DBRawClonedFragment dbRawClonedFragment : dbRawCloneClass
					.getElements()) {
				final RawClonedFragment<Token> rawClonedFragment = new RawClonedFragment<>(
						dbRawClonedFragment);
				this.volatileRawClonedFragments.put(rawClonedFragment.getId(),
						rawClonedFragment);
				rawClonedFragment.setSourceFile(this.volatileSourceFiles
						.get(dbRawClonedFragment.getSourceFile().getId()));
				rawCloneClass.addRawClonedFragment(rawClonedFragment);
				rawClonedFragment.setRawCloneClass(rawCloneClass);
			}
		}

	}

}
