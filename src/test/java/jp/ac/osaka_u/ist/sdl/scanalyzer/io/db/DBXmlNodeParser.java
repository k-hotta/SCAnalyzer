package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.DBMS;
import jp.ac.osaka_u.ist.sdl.scanalyzer.config.Language;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Token;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBElementComparator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange.Type;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersionSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.TokenSourceFileParser;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class parses an xml file that describes data in a virtual database for
 * testing.
 * 
 * @author k-hotta
 * 
 */
public class DBXmlNodeParser {

	private static final AtomicLong counter = new AtomicLong(0);

	/*
	 * configurations of db gained from the xml file
	 */

	private DBMS dbms;

	private String path;

	/*
	 * maps having data gained from the xml file
	 */

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

	private SortedMap<Long, String> fileContentsStr;

	private SortedMap<Long, Map<Integer, Token>> fileContents;

	private TokenSourceFileParser parser = new TokenSourceFileParser(
			Language.JAVA);

	private static final DateFormat df = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");

	public DBXmlNodeParser(final SortedMap<Long, DBVersion> versions,
			final SortedMap<Long, DBRevision> revisions,
			final SortedMap<Long, DBSourceFile> sourceFiles,
			final SortedMap<Long, DBFileChange> fileChanges,
			final SortedMap<Long, DBSegment> segments,
			final SortedMap<Long, DBCodeFragment> codeFragments,
			final SortedMap<Long, DBCloneClass> cloneClasses,
			final SortedMap<Long, DBRawCloneClass> rawCloneClasses,
			final SortedMap<Long, DBRawClonedFragment> rawClonedFragments,
			final SortedMap<Long, DBVersionSourceFile> versionSourceFiles,
			final SortedMap<Long, String> fileContentsStr,
			final SortedMap<Long, Map<Integer, Token>> fileContents) {
		this.versions = versions;
		this.revisions = revisions;
		this.sourceFiles = sourceFiles;
		this.fileChanges = fileChanges;
		this.segments = segments;
		this.codeFragments = codeFragments;
		this.cloneClasses = cloneClasses;
		this.rawCloneClasses = rawCloneClasses;
		this.rawClonedFragments = rawClonedFragments;
		this.versionSourceFiles = versionSourceFiles;
		this.fileContentsStr = fileContentsStr;
		this.fileContents = fileContents;
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

	public final Map<Long, String> getFileContentsStr() {
		return fileContentsStr;
	}

	public final Map<Long, Map<Integer, Token>> getFileContents() {
		return fileContents;
	}

	public void processRootNode(final Node node) throws Exception {
		final NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);
			final String nodeName = child.getNodeName();

			if (nodeName.equals("db")) {
				processDbNode(child);
			}
		}
	}

	public void processDbNode(final Node node) throws Exception {
		final NodeList children = node.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);
			final String childName = child.getNodeName();

			if (childName.equals("config")) {
				processConfigNode(child);
			} else if (childName.equals("data")) {
				processDataNode(child);
			}
		}
	}

	public void processConfigNode(final Node node) throws Exception {
		final NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);
			final String childName = child.getNodeName();

			if (childName.equals("dbms")) {
				this.dbms = DBMS.valueOf(child.getFirstChild().getNodeValue());
			} else if (childName.equals("path")) {
				this.path = child.getFirstChild().getNodeValue();
			}
		}
	}

	public void processDataNode(final Node node) throws Exception {
		final NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);
			final String childName = child.getNodeName();

			if (childName.equals("versions")) {
				processVersionsNode(child);
			}
		}
	}

	public void processVersionsNode(final Node node) throws Exception {
		final NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);
			final String childName = child.getNodeName();

			if (childName.equals("version")) {
				processVersionNode(child);
			}
		}
	}

	public void processVersionNode(final Node node) throws Exception {
		final DBXmlNodeParser another = new DBXmlNodeParser(this.versions,
				new TreeMap<Long, DBRevision>(),
				new TreeMap<Long, DBSourceFile>(),
				new TreeMap<Long, DBFileChange>(),
				new TreeMap<Long, DBSegment>(),
				new TreeMap<Long, DBCodeFragment>(),
				new TreeMap<Long, DBCloneClass>(),
				new TreeMap<Long, DBRawCloneClass>(),
				new TreeMap<Long, DBRawClonedFragment>(),
				new TreeMap<Long, DBVersionSourceFile>(),
				new TreeMap<Long, String>(),
				new TreeMap<Long, Map<Integer, Token>>());
		another.visitVersionNode(node);
		// this.versions.putAll(another.getVersions());
		this.revisions.putAll(another.getRevisions());

		final DBVersion justProcessedVersion = versions.get(versions.lastKey());
		for (final DBSourceFile anotherSourceFile : another.getSourceFiles()
				.values()) {
			if (this.sourceFiles.containsKey(anotherSourceFile.getId())) {
				final DBSourceFile currentSourceFile = this.sourceFiles
						.get(anotherSourceFile.getId());
				final Map<Integer, Token> currentContent = this.fileContents
						.get(anotherSourceFile.getId());
				final String currentContentStr = this.fileContentsStr
						.get(anotherSourceFile.getId());
				justProcessedVersion.getSourceFiles().remove(anotherSourceFile);
				justProcessedVersion.getSourceFiles().add(currentSourceFile);
				this.sourceFiles.remove(anotherSourceFile.getId());
				this.sourceFiles.put(currentSourceFile.getId(),
						currentSourceFile);
				this.fileContents.remove(anotherSourceFile.getId());
				this.fileContents
						.put(currentSourceFile.getId(), currentContent);
				this.fileContentsStr.remove(anotherSourceFile.getId());
				this.fileContentsStr.put(currentSourceFile.getId(),
						currentContentStr);
			} else {
				this.sourceFiles.put(anotherSourceFile.getId(),
						anotherSourceFile);
				this.fileContents.put(anotherSourceFile.getId(), another
						.getFileContents().get(anotherSourceFile.getId()));
				this.fileContentsStr.put(anotherSourceFile.getId(), another
						.getFileContentsStr().get(anotherSourceFile.getId()));
			}
		}

		this.fileChanges.putAll(another.getFileChanges());
		this.segments.putAll(another.getSegments());
		this.codeFragments.putAll(another.getCodeFragments());
		this.cloneClasses.putAll(another.getCloneClasses());
		this.rawCloneClasses.putAll(another.getRawCloneClasses());
		this.rawClonedFragments.putAll(another.getRawClonedFragments());
		this.versionSourceFiles.putAll(another.getVersionSourceFiles());
	}

	public void visitVersionNode(final Node node) throws Exception {
		long id = -1;

		final NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);
			final String childName = child.getNodeName();

			if (childName.equals("id")) {
				id = Long.parseLong(child.getFirstChild().getNodeValue());
			} else if (childName.equals("revision")) {
				processRevisionNode(child);
			} else if (childName.equals("sourcefiles")) {
				processSourceFilesNode(child);
			} else if (childName.equals("filechanges")) {
				processFileChangesNode(child);
			} else if (childName.equals("rawcloneclasses")) {
				processRawCloneClassesNode(child);
			}
		}

		DBRevision revision = null;
		for (DBRevision value : this.revisions.values()) {
			revision = value;
			break;
		}

		if (revision == null) {
			throw new IllegalStateException(
					"the xml file seems to have a wrong format");
		}

		final Collection<DBSourceFile> currentSourceFiles = new ArrayList<DBSourceFile>();
		currentSourceFiles.addAll(sourceFiles.values());

		final Map<Long, Map<Integer, Token>> currentContents = new HashMap<Long, Map<Integer, Token>>();
		currentContents.putAll(fileContents);

		final DBVersion version = new DBVersion(id, revision,
				fileChanges.values(), rawCloneClasses.values(),
				cloneClasses.values(), currentSourceFiles, null);
		this.versions.put(id, version);

		for (final DBSourceFile sourceFile : this.sourceFiles.values()) {
			final DBVersionSourceFile vsf = new DBVersionSourceFile(
					counter.getAndIncrement(), version, sourceFile);
			this.versionSourceFiles.put(vsf.getId(), vsf);
		}

		for (final DBFileChange fileChange : version.getFileChanges()) {
			fileChange.setVersion(version);
		}

		for (final DBRawCloneClass rawCloneClass : version.getRawCloneClasses()) {
			rawCloneClass.setVersion(version);
			for (final DBRawClonedFragment rawClonedFragment : rawCloneClass
					.getElements()) {
				rawClonedFragment.setVersion(version);
			}
		}

		for (final DBCloneClass cloneClass : version.getCloneClasses()) {
			cloneClass.setVersion(version);
		}
	}

	public void processRevisionNode(final Node node) throws Exception {
		long id = -1;
		String identifier = null;
		Date date = null;

		final NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);
			final String childName = child.getNodeName();

			if (childName.equals("id")) {
				id = Long.parseLong(child.getFirstChild().getNodeValue());
			} else if (childName.equals("identifier")) {
				identifier = child.getFirstChild().getNodeValue();
			} else if (childName.equals("date")) {
				date = df.parse(child.getFirstChild().getNodeValue());
			}
		}

		if (identifier == null || date == null) {
			throw new IllegalStateException(
					"the xml file seems to have a wrong format");
		}

		if (revisions.containsKey(id)) {
			throw new IllegalStateException(
					"the xml file seems to have a wrong format");
		}

		final DBRevision revision = new DBRevision(id, identifier, date);
		revisions.put(id, revision);
	}

	public void processSourceFilesNode(final Node node) throws Exception {
		final NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);
			final String childName = child.getNodeName();

			if (childName.equals("sourcefile")) {
				processSourceFileNode(child);
			}
		}
	}

	public void processSourceFileNode(final Node node) throws Exception {
		long id = -1;
		String path = null;
		String contentStr = null;

		final NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);
			final String childName = child.getNodeName();

			if (childName.equals("id")) {
				id = Long.parseLong(child.getFirstChild().getNodeValue());
			} else if (childName.equals("path")) {
				path = child.getFirstChild().getNodeValue();
			} else if (childName.equals("contents")) {
				String str = child.getFirstChild().getNodeValue();
				contentStr = str.replaceAll("\\\\n", "\n");
			}
		}

		if (path == null) {
			throw new IllegalStateException(
					"the xml file seems to have a wrong format");
		}

		if (!sourceFiles.containsKey(id)) {
			final DBSourceFile sourceFile = new DBSourceFile(id, path);
			sourceFiles.put(id, sourceFile);
			fileContentsStr.put(id, contentStr);
			fileContents
					.put(id, parser.parse(new SourceFile<Token>(sourceFile),
							contentStr));
		}
	}

	public void processFileChangesNode(final Node node) throws Exception {
		final NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);
			final String childName = child.getNodeName();

			if (childName.equals("filechange")) {
				processFileChangeNode(child);
			}
		}
	}

	public void processFileChangeNode(final Node node) throws Exception {
		long id = -1;
		Long oldSourceFileId = null;
		Long newSourceFileId = null;
		String type = "";

		final NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);
			final String childName = child.getNodeName();

			if (childName.equals("id")) {
				id = Long.parseLong(child.getFirstChild().getNodeValue());
			} else if (childName.equals("old")) {
				if (child.getChildNodes().getLength() > 0) {
					oldSourceFileId = Long.parseLong(child.getFirstChild()
							.getNodeValue());
				}
			} else if (childName.equals("new")) {
				if (child.getChildNodes().getLength() > 0) {
					newSourceFileId = Long.parseLong(child.getFirstChild()
							.getNodeValue());
				}
			} else if (childName.equals("type")) {
				type = child.getFirstChild().getNodeValue();
			}
		}

		if (oldSourceFileId == null && newSourceFileId == null) {
			throw new IllegalStateException(
					"the xml file seems to have a wrong format");
		}

		if (fileChanges.containsKey(id)) {
			throw new IllegalStateException(
					"the xml file seems to have a wrong format");
		}

		DBSourceFile oldSourceFile = null;
		DBSourceFile newSourceFile = null;

		if (oldSourceFileId != null) {
			final DBVersion lastVersion = versions.get(versions.lastKey());
			for (final DBSourceFile sourceFileInLastVersion : lastVersion
					.getSourceFiles()) {
				if (sourceFileInLastVersion.getId() == oldSourceFileId) {
					oldSourceFile = sourceFileInLastVersion;
				}
			}
			if (oldSourceFile == null) {
				throw new IllegalStateException(
						"the xml file seems to have a wrong format");
			}
		}

		if (newSourceFileId != null) {
			newSourceFile = sourceFiles.get(newSourceFileId);
			if (newSourceFile == null) {
				throw new IllegalStateException(
						"the xml file seems to have a wrong format");
			}
		}

		final DBFileChange fileChange = new DBFileChange(id, oldSourceFile,
				newSourceFile, Type.valueOf(type), null);

		fileChanges.put(id, fileChange);
	}

	public void processRawCloneClassesNode(final Node node) throws Exception {
		final NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);
			final String childName = child.getNodeName();

			if (childName.equals("rawcloneclass")) {
				processRawCloneClassNode(child);
			}
		}
	}

	public void processRawCloneClassNode(final Node node) throws Exception {
		final DBXmlNodeParser another = new DBXmlNodeParser(this.versions,
				this.revisions, this.sourceFiles, this.fileChanges,
				new TreeMap<Long, DBSegment>(),
				new TreeMap<Long, DBCodeFragment>(),
				new TreeMap<Long, DBCloneClass>(),
				new TreeMap<Long, DBRawCloneClass>(),
				new TreeMap<Long, DBRawClonedFragment>(),
				this.versionSourceFiles, this.fileContentsStr,
				this.fileContents);
		another.visitRawCloneClassNode(node);
		this.segments.putAll(another.getSegments());
		this.codeFragments.putAll(another.getCodeFragments());
		this.cloneClasses.putAll(another.getCloneClasses());
		this.rawCloneClasses.putAll(another.getRawCloneClasses());
		this.rawClonedFragments.putAll(another.getRawClonedFragments());
	}

	public void visitRawCloneClassNode(final Node node) throws Exception {
		long id = -1;

		final NodeList children = node.getChildNodes();

		if (children.getLength() == 0) {
			return;
		}

		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);
			final String childName = child.getNodeName();

			if (childName.equals("id")) {
				id = Long.parseLong(child.getFirstChild().getNodeValue());
			} else if (childName.equals("rawclonedfragments")) {
				processRawClonedFragmentsNode(child);
			}
		}

		if (this.rawClonedFragments.size() < 2) {
			throw new IllegalStateException(
					"the xml file seems to have a wrong format");
		}

		final DBRawCloneClass rawCloneClass = new DBRawCloneClass(id, null,
				this.rawClonedFragments.values());

		for (final DBRawClonedFragment rawClonedFragment : rawCloneClass
				.getElements()) {
			rawClonedFragment.setCloneClass(rawCloneClass);
		}

		rawCloneClasses.put(id, rawCloneClass);

		final DBCloneClass cloneClass = new DBCloneClass(id, null, this
				.getCodeFragments().values(), new ArrayList<DBCodeFragment>());

		for (final DBCodeFragment codeFragment : cloneClass.getCodeFragments()) {
			codeFragment.setCloneClass(cloneClass);
		}

		cloneClasses.put(id, cloneClass);
	}

	public void processRawClonedFragmentsNode(final Node node) throws Exception {
		final NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);
			final String childName = child.getNodeName();

			if (childName.equals("rawclonedfragment")) {
				processRawClonedFragmentNode(child);
			}
		}
	}

	public void processRawClonedFragmentNode(final Node node) throws Exception {
		long id = -1;
		long ownerFileId = -1;
		int startLine = -1;
		int length = -1;

		final NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);
			final String childName = child.getNodeName();

			if (childName.equals("id")) {
				id = Long.parseLong(child.getFirstChild().getNodeValue());
			} else if (childName.equals("ownerfile")) {
				ownerFileId = Long.parseLong(child.getFirstChild()
						.getNodeValue());
			} else if (childName.equals("startline")) {
				startLine = Integer.parseInt(child.getFirstChild()
						.getNodeValue());
			} else if (childName.equals("length")) {
				length = Integer.parseInt(child.getFirstChild().getNodeValue());
			}
		}

		if (!sourceFiles.containsKey(ownerFileId)) {
			throw new IllegalStateException(
					"the xml file seems to have a wrong format");
		}

		if (startLine < 1 || length < 1) {
			throw new IllegalStateException(
					"the xml file seems to have a wrong format");
		}

		final DBSourceFile sourceFile = sourceFiles.get(ownerFileId);

		final DBRawClonedFragment rawClonedFragment = new DBRawClonedFragment(
				id, null, sourceFile, startLine, length, null);
		this.rawClonedFragments.put(id, rawClonedFragment);

		final DBSegment segment = new DBSegment(id, sourceFile, startLine,
				startLine + length - 1, null);
		final DBCodeFragment codeFragment = new DBCodeFragment(id,
				new TreeSet<DBSegment>(new DBElementComparator()), null);
		segment.setCodeFragment(codeFragment);
		codeFragment.getSegments().add(segment);

		this.segments.put(id, segment);
		this.codeFragments.put(id, codeFragment);
	}

}
