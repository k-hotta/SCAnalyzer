package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.AbstractDataDao;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.DBManager;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.IFileContentProvider;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.ISourceFileParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is for retrieving elements with volatile data from database.
 * 
 * @author k-hotta
 *
 * @param E
 *            the type of program element
 */
public class Retriever<E extends IProgramElement> {

	private static final Logger logger = LogManager.getLogger(Retriever.class);

	private final DBManager dbManager;

	private final IFileContentProvider<E> fileContentProvider;

	private final ISourceFileParser<E> parser;

	private final Map<Long, Segment<E>> segments = new TreeMap<>();

	private final Map<Long, CodeFragment<E>> codeFragments = new TreeMap<>();

	private final Map<Long, CloneClass<E>> cloneClasses = new TreeMap<>();

	private final Map<Long, SourceFile<E>> sourceFiles = new TreeMap<>();

	private final Map<Long, CloneClassMapping<E>> cloneClassMappings = new TreeMap<>();

	private final Map<Long, CodeFragmentMapping<E>> codeFragmentMappings = new TreeMap<>();

	private final Map<Long, Revision> revisions = new TreeMap<>();

	private final Map<Long, Version<E>> versions = new TreeMap<>();

	private final Map<Long, CloneGenealogy<E>> genealogies = new TreeMap<>();

	public Retriever(final DBManager dbManager,
			final IFileContentProvider<E> fileContentProvider,
			final ISourceFileParser<E> parser) {
		this.dbManager = dbManager;
		this.fileContentProvider = fileContentProvider;
		this.parser = parser;
	}

	public final Map<Long, Segment<E>> getSegments() {
		return Collections.unmodifiableMap(segments);
	}

	public final Map<Long, CodeFragment<E>> getCodeFragments() {
		return Collections.unmodifiableMap(codeFragments);
	}

	public final Map<Long, CloneClass<E>> getCloneClasses() {
		return Collections.unmodifiableMap(cloneClasses);
	}

	public final Map<Long, SourceFile<E>> getSourceFiles() {
		return Collections.unmodifiableMap(sourceFiles);
	}

	public final Map<Long, CloneClassMapping<E>> getCloneClassMappings() {
		return Collections.unmodifiableMap(cloneClassMappings);
	}

	public final Map<Long, CodeFragmentMapping<E>> getCodeFragmentMappings() {
		return Collections.unmodifiableMap(codeFragmentMappings);
	}

	public final Map<Long, Revision> getRevisions() {
		return Collections.unmodifiableMap(revisions);
	}

	public final Map<Long, Version<E>> getVersions() {
		return Collections.unmodifiableMap(versions);
	}

	public final Map<Long, CloneGenealogy<E>> getGenealogies() {
		return Collections.unmodifiableMap(genealogies);
	}

	/**
	 * Clear all the retrieved objects.
	 */
	public void clear() {
		segments.clear();
		codeFragments.clear();
		cloneClasses.clear();
		sourceFiles.clear();
		cloneClassMappings.clear();
		codeFragmentMappings.clear();
		revisions.clear();
		versions.clear();
		genealogies.clear();
	}

	/**
	 * Retrieve a clone genealogy whose id is the specified one. All the
	 * retrieved object will be stored into the maps of the files in this class.
	 * 
	 * @param id
	 *            the id of the genealogy to be retrieved
	 * 
	 * @return the retrieved genealogy
	 * 
	 * @throws Exception
	 *             if any error occurred when connecting database
	 */
	public CloneGenealogy<E> retrieveCloneGenealogy(final long id)
			throws Exception {
		// make sure the deep refreshing is OFF
		AbstractDataDao.setDeepRefresh(false);

		// make sure the auto refreshing is ON
		AbstractDataDao.setAutoRefresh(true);

		logger.info("retrieving genealogy " + id + " from database ...");

		final DBCloneGenealogy core = dbManager.getCloneGenealogyDao().get(id);

		if (core == null) {
			// cannot find the corresponding genealogy from database
			return null;
		}

		logger.info("persist information has successfully retrieved");
		logger.info("setting volatile inforamtion ...");

		CloneGenealogy<E> result = retrieveCloneGenealogy(core);

		logger.info("complete retrieving the genealogy " + id);

		return result;
	}

	/**
	 * Retrieve a clone genealogy.
	 * 
	 * @param dbGenealogy
	 *            the core of the genealogy to be retrieved
	 * 
	 * @return the retrieved genealogy
	 */
	public CloneGenealogy<E> retrieveCloneGenealogy(
			final DBCloneGenealogy dbGenealogy) {
		final CloneGenealogy<E> genealogy = new CloneGenealogy<E>(dbGenealogy);

		{
			Version<E> startVersion = versions.get(dbGenealogy
					.getStartVersion().getId());
			if (startVersion == null) {
				startVersion = retrieveVersionOnlyWithRevision(dbGenealogy
						.getStartVersion());
				versions.put(startVersion.getId(), startVersion);
			}
			genealogy.setStartVersion(startVersion);
		}

		{
			Version<E> endVersion = versions.get(dbGenealogy.getEndVersion()
					.getId());
			if (endVersion == null) {
				endVersion = retrieveVersionOnlyWithRevision(dbGenealogy
						.getEndVersion());
				versions.put(endVersion.getId(), endVersion);
			}
			genealogy.setEndVersion(endVersion);
		}

		// set clone class mappings
		for (final DBCloneClassMapping dbCloneClassMapping : dbGenealogy
				.getCloneClassMappings()) {
			CloneClassMapping<E> cloneClassMapping = cloneClassMappings
					.get(dbCloneClassMapping.getId());

			if (cloneClassMapping == null) {
				cloneClassMapping = retrieveCloneClassMapping(dbCloneClassMapping);
				cloneClassMappings.put(cloneClassMapping.getId(),
						cloneClassMapping);
			}

			for (final DBCodeFragmentMapping dbCodeFragmentMapping : dbCloneClassMapping
					.getCodeFragmentMappings()) {
				CodeFragmentMapping<E> codeFragmentMapping = codeFragmentMappings
						.get(dbCodeFragmentMapping.getId());

				if (codeFragmentMapping == null) {
					codeFragmentMapping = retrieveCodeFragmentMapping(dbCodeFragmentMapping);
					codeFragmentMappings.put(codeFragmentMapping.getId(),
							codeFragmentMapping);
				}

				cloneClassMapping.addCodeFragmentMappings(codeFragmentMapping);
				codeFragmentMapping.setCloneClassMapping(cloneClassMapping);
			}

			genealogy.addCloneClassMapping(cloneClassMapping);
		}

		return genealogy;
	}

	/**
	 * Retrieve a clone class mapping.
	 * 
	 * @param dbCloneClassMapping
	 *            the core of the clone class mapping to be retrieved
	 * 
	 * @return the retrieved clone class mapping
	 */
	public CloneClassMapping<E> retrieveCloneClassMapping(
			final DBCloneClassMapping dbCloneClassMapping) {
		final CloneClassMapping<E> cloneClassMapping = new CloneClassMapping<E>(
				dbCloneClassMapping);

		final DBCloneClass dbOldCloneClass = dbCloneClassMapping
				.getOldCloneClass();
		final DBCloneClass dbNewCloneClass = dbCloneClassMapping
				.getNewCloneClass();

		if (dbOldCloneClass != null) {
			CloneClass<E> oldCloneClass = cloneClasses.get(dbOldCloneClass
					.getId());

			if (oldCloneClass == null) {
				oldCloneClass = retrieveCloneClass(dbOldCloneClass);
				cloneClasses.put(oldCloneClass.getId(), oldCloneClass);
			}

			cloneClassMapping.setOldCloneClass(oldCloneClass);
		}

		if (dbNewCloneClass != null) {
			CloneClass<E> newCloneClass = cloneClasses.get(dbNewCloneClass
					.getId());

			if (newCloneClass == null) {
				newCloneClass = retrieveCloneClass(dbNewCloneClass);
				cloneClasses.put(newCloneClass.getId(), newCloneClass);
			}

			cloneClassMapping.setNewCloneClass(newCloneClass);
		}

		return cloneClassMapping;
	}

	/**
	 * Retrieve a clone class.
	 * 
	 * @param dbCloneClass
	 *            the core of the clone class to be retrieved
	 * 
	 * @return the retrieved clone class
	 */
	public CloneClass<E> retrieveCloneClass(final DBCloneClass dbCloneClass) {
		final CloneClass<E> cloneClass = new CloneClass<E>(dbCloneClass);

		// set cloned & ghost code fragments
		for (final DBCodeFragment dbCodeFragment : dbCloneClass
				.getCodeFragments()) {
			CodeFragment<E> codeFragment = codeFragments.get(dbCodeFragment
					.getId());

			if (codeFragment == null) {
				codeFragment = retrieveCodeFragment(dbCodeFragment);
				codeFragments.put(codeFragment.getId(), codeFragment);
			}

			cloneClass.addCodeFragment(codeFragment);
			codeFragment.setCloneClass(cloneClass);
		}

		// set version
		Version<E> version = versions.get(dbCloneClass.getVersion().getId());

		if (version == null) {
			version = retrieveVersionOnlyWithRevision(dbCloneClass.getVersion());
		}

		cloneClass.setVersion(version);

		return cloneClass;
	}

	/**
	 * Retrieve a code fragment.
	 * 
	 * @param dbCodeFragment
	 *            the core of the code fragment to be retrieved
	 * 
	 * @return the retrieved code fragment
	 */
	public CodeFragment<E> retrieveCodeFragment(
			final DBCodeFragment dbCodeFragment) {
		final CodeFragment<E> codeFragment = new CodeFragment<E>(dbCodeFragment);

		for (final DBSegment dbSegment : dbCodeFragment.getSegments()) {
			Segment<E> segment = segments.get(dbSegment.getId());

			if (segment == null) {
				segment = retrieveSegment(dbSegment);
				segments.put(segment.getId(), segment);
			}

			codeFragment.addSegment(segment);
			segment.setCodeFragment(codeFragment);
		}

		return codeFragment;
	}

	/**
	 * Retrieve a segment.
	 * 
	 * @param dbSegment
	 *            the core of the segment to be retrieved
	 * 
	 * @return the retrieved segment
	 */
	public Segment<E> retrieveSegment(final DBSegment dbSegment) {
		final Segment<E> segment = new Segment<E>(dbSegment);

		try {
			dbManager.getNativeDao(DBCodeFragment.class).refresh(
					dbSegment.getCodeFragment());
			dbManager.getNativeDao(DBCloneClass.class).refresh(
					dbSegment.getCodeFragment().getCloneClass());
			dbManager.getNativeDao(DBSourceFile.class).refresh(
					dbSegment.getSourceFile());
		} catch (Exception e) {
			throw new IllegalStateException("fail to refresh the segment "
					+ dbSegment.getId(), e);
		}

		SourceFile<E> sourceFile = sourceFiles.get(dbSegment.getSourceFile()
				.getId());

		// retrieve source file if it has not been retrieved yet
		if (sourceFile == null) {
			final DBSourceFile dbSourceFile = dbSegment.getSourceFile();

			final DBVersion dbOwnerVersion = dbSegment.getCodeFragment()
					.getCloneClass().getVersion();

			Version<E> ownerVersion = versions.get(dbOwnerVersion.getId());
			if (ownerVersion == null) {
				ownerVersion = retrieveVersionOnlyWithRevision(dbOwnerVersion);
				versions.put(ownerVersion.getId(), ownerVersion);
			}

			sourceFile = retrieveSourceFile(dbSourceFile,
					ownerVersion.getCore());
			sourceFiles.put(sourceFile.getId(), sourceFile);
		}

		segment.setSourceFile(sourceFile);
		segment.setContents(sourceFile
				.getContents()
				.subMap(dbSegment.getStartPosition(),
						dbSegment.getEndPosition() + 1).values());

		return segment;
	}

	/**
	 * Retrieve a source file.
	 * 
	 * @param dbSourceFile
	 *            the core of the source file to be retrieved
	 * @param dbVersion
	 *            the core of the owner version of the source file
	 * 
	 * @return the retrieved source file
	 */
	public SourceFile<E> retrieveSourceFile(final DBSourceFile dbSourceFile,
			final DBVersion dbVersion) {
		final SourceFile<E> sourceFile = new SourceFile<E>(dbSourceFile);

		final String fileContentsStr = fileContentProvider.getFileContent(
				dbVersion, dbSourceFile);
		sourceFile.setContents(parser.parse(sourceFile, fileContentsStr)
				.values());

		return sourceFile;
	}

	/**
	 * Retrieve a code fragment mapping.
	 * 
	 * @param dbCodeFragmentMapping
	 *            the core of the code fragment mapping to be retrieved
	 * 
	 * @return the retrieved code fragment mapping
	 */
	public CodeFragmentMapping<E> retrieveCodeFragmentMapping(
			final DBCodeFragmentMapping dbCodeFragmentMapping) {
		final CodeFragmentMapping<E> codeFragmentMapping = new CodeFragmentMapping<E>(
				dbCodeFragmentMapping);

		if (dbCodeFragmentMapping.getOldCodeFragment() != null) {
			CodeFragment<E> oldCodeFragment = codeFragments
					.get(dbCodeFragmentMapping.getOldCodeFragment().getId());
			if (oldCodeFragment == null) {
				oldCodeFragment = retrieveCodeFragment(dbCodeFragmentMapping
						.getOldCodeFragment());
				codeFragments.put(oldCodeFragment.getId(), oldCodeFragment);
			}
			codeFragmentMapping.setOldCodeFragment(oldCodeFragment);
		}

		if (dbCodeFragmentMapping.getNewCodeFragment() != null) {
			CodeFragment<E> newCodeFragment = codeFragments
					.get(dbCodeFragmentMapping.getNewCodeFragment().getId());
			if (newCodeFragment == null) {
				newCodeFragment = retrieveCodeFragment(dbCodeFragmentMapping
						.getNewCodeFragment());
				codeFragments.put(newCodeFragment.getId(), newCodeFragment);
			}
			codeFragmentMapping.setNewCodeFragment(newCodeFragment);
		}

		return codeFragmentMapping;
	}

	/**
	 * Retrieve a version, with only revision among all whose values retrieved.
	 * 
	 * @param dbVersion
	 *            the core of the version to be retrieved
	 * 
	 * @return the retrieved version
	 */
	public Version<E> retrieveVersionOnlyWithRevision(final DBVersion dbVersion) {
		final Version<E> version = new Version<E>(dbVersion);

		try {
			dbManager.getNativeDao(DBVersion.class).refresh(dbVersion);
		} catch (Exception e) {
			throw new IllegalStateException("cannot refresh the version "
					+ dbVersion.getId(), e);
		}

		Revision revision = revisions.get(dbVersion.getRevision().getId());

		if (revision == null) {
			try {
				dbManager.getNativeDao(DBRevision.class).refresh(
						dbVersion.getRevision());
			} catch (Exception e) {
				throw new IllegalStateException("cannot refresh the revision "
						+ dbVersion.getRevision().getIdentifier(), e);
			}

			revision = retrieveRevision(dbVersion.getRevision());
			revisions.put(revision.getId(), revision);
		}

		version.setRevision(revision);

		return version;
	}

	/**
	 * Retrieve a revision.
	 * 
	 * @param dbRevision
	 *            the core of the revision to be retrieved
	 * 
	 * @return the retrieved revision
	 */
	public Revision retrieveRevision(final DBRevision dbRevision) {
		return new Revision(dbRevision);
	}

}
