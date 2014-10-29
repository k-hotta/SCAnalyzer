package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBElementComparator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange.Type;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
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

	private SortedMap<Long, Version> versions;

	private SortedMap<Long, Revision> revisions;

	private SortedMap<Long, SourceFile> sourceFiles;

	private SortedMap<Long, FileChange> fileChanges;

	private SortedMap<Long, Segment> segments;

	private SortedMap<Long, CodeFragment> codeFragments;

	private SortedMap<Long, CloneClass> cloneClasses;

	private SortedMap<Long, RawCloneClass> rawCloneClasses;

	private SortedMap<Long, RawClonedFragment> rawClonedFragments;

	private SortedMap<Long, VersionSourceFile> versionSourceFiles;

	private static final DateFormat df = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");

	public DBXmlNodeParser(final SortedMap<Long, Version> versions,
			final SortedMap<Long, Revision> revisions,
			final SortedMap<Long, SourceFile> sourceFiles,
			final SortedMap<Long, FileChange> fileChanges,
			final SortedMap<Long, Segment> segments,
			final SortedMap<Long, CodeFragment> codeFragments,
			final SortedMap<Long, CloneClass> cloneClasses,
			final SortedMap<Long, RawCloneClass> rawCloneClasses,
			final SortedMap<Long, RawClonedFragment> rawClonedFragments,
			final SortedMap<Long, VersionSourceFile> versionSourceFiles) {
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

	public final Map<Long, Segment> getSegments() {
		return segments;
	}

	public final Map<Long, CodeFragment> getCodeFragments() {
		return codeFragments;
	}

	public final Map<Long, CloneClass> getCloneClasses() {
		return cloneClasses;
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
				new TreeMap<Long, Revision>(), new TreeMap<Long, SourceFile>(),
				new TreeMap<Long, FileChange>(), new TreeMap<Long, Segment>(),
				new TreeMap<Long, CodeFragment>(),
				new TreeMap<Long, CloneClass>(),
				new TreeMap<Long, RawCloneClass>(),
				new TreeMap<Long, RawClonedFragment>(),
				new TreeMap<Long, VersionSourceFile>());
		another.visitVersionNode(node);
		// this.versions.putAll(another.getVersions());
		this.revisions.putAll(another.getRevisions());

		final Version justProcessedVersion = versions.get(versions.lastKey());
		for (final SourceFile anotherSourceFile : another.getSourceFiles()
				.values()) {
			if (this.sourceFiles.containsKey(anotherSourceFile.getId())) {
				final SourceFile currentSourceFile = this.sourceFiles
						.get(anotherSourceFile.getId());
				justProcessedVersion.getSourceFiles().remove(anotherSourceFile);
				justProcessedVersion.getSourceFiles().add(currentSourceFile);
				this.sourceFiles.remove(anotherSourceFile.getId());
				this.sourceFiles.put(currentSourceFile.getId(),
						currentSourceFile);
			} else {
				this.sourceFiles.put(anotherSourceFile.getId(),
						anotherSourceFile);
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

		Revision revision = null;
		for (Revision value : this.revisions.values()) {
			revision = value;
			break;
		}

		if (revision == null) {
			throw new IllegalStateException(
					"the xml file seems to have a wrong format");
		}

		final Collection<SourceFile> currentSourceFiles = new ArrayList<SourceFile>();
		currentSourceFiles.addAll(sourceFiles.values());
		final Version version = new Version(id, revision, fileChanges.values(),
				rawCloneClasses.values(), cloneClasses.values(),
				currentSourceFiles);
		this.versions.put(id, version);

		for (final SourceFile sourceFile : this.sourceFiles.values()) {
			final VersionSourceFile vsf = new VersionSourceFile(
					counter.getAndIncrement(), version, sourceFile);
			this.versionSourceFiles.put(vsf.getId(), vsf);
		}

		for (final FileChange fileChange : version.getFileChanges()) {
			fileChange.setVersion(version);
		}

		for (final RawCloneClass rawCloneClass : version.getRawCloneClasses()) {
			rawCloneClass.setVersion(version);
			for (final RawClonedFragment rawClonedFragment : rawCloneClass
					.getElements()) {
				rawClonedFragment.setVersion(version);
			}
		}

		for (final CloneClass cloneClass : version.getCloneClasses()) {
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

		final Revision revision = new Revision(id, identifier, date);
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

		final NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);
			final String childName = child.getNodeName();

			if (childName.equals("id")) {
				id = Long.parseLong(child.getFirstChild().getNodeValue());
			} else if (childName.equals("path")) {
				path = child.getFirstChild().getNodeValue();
			}
		}

		if (path == null) {
			throw new IllegalStateException(
					"the xml file seems to have a wrong format");
		}

		if (!sourceFiles.containsKey(id)) {
			final SourceFile sourceFile = new SourceFile(id, path);
			sourceFiles.put(id, sourceFile);
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

		SourceFile oldSourceFile = null;
		SourceFile newSourceFile = null;

		if (oldSourceFileId != null) {
			final Version lastVersion = versions.get(versions.lastKey());
			for (final SourceFile sourceFileInLastVersion : lastVersion
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

		final FileChange fileChange = new FileChange(id, oldSourceFile,
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
				new TreeMap<Long, Segment>(),
				new TreeMap<Long, CodeFragment>(),
				new TreeMap<Long, CloneClass>(),
				new TreeMap<Long, RawCloneClass>(),
				new TreeMap<Long, RawClonedFragment>(), this.versionSourceFiles);
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

		final RawCloneClass rawCloneClass = new RawCloneClass(id, null,
				this.rawClonedFragments.values());

		for (final RawClonedFragment rawClonedFragment : rawCloneClass
				.getElements()) {
			rawClonedFragment.setCloneClass(rawCloneClass);
		}

		rawCloneClasses.put(id, rawCloneClass);

		final CloneClass cloneClass = new CloneClass(id, null, this
				.getCodeFragments().values());

		for (final CodeFragment codeFragment : cloneClass.getCodeFragments()) {
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

		final SourceFile sourceFile = sourceFiles.get(ownerFileId);

		final RawClonedFragment rawClonedFragment = new RawClonedFragment(id,
				null, sourceFile, startLine, length, null);
		this.rawClonedFragments.put(id, rawClonedFragment);

		final Segment segment = new Segment(id, sourceFile, startLine,
				startLine + length - 1, null);
		final CodeFragment codeFragment = new CodeFragment(id,
				new TreeSet<Segment>(new DBElementComparator()), null);
		segment.setCodeFragment(codeFragment);
		codeFragment.getSegments().add(segment);

		this.segments.put(id, segment);
		this.codeFragments.put(id, codeFragment);
	}

}
