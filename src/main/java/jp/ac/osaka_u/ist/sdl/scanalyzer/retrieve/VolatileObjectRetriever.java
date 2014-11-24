package jp.ac.osaka_u.ist.sdl.scanalyzer.retrieve;

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
public class VolatileObjectRetriever<E extends IProgramElement> implements
		IRetriever<E> {

	private static final Logger logger = LogManager
			.getLogger(VolatileObjectRetriever.class);

	private final DBManager dbManager;

	private final IFileContentProvider<E> fileContentProvider;

	private final ISourceFileParser<E> parser;

	private final RetrievedObjectManager<E> manager;

	public VolatileObjectRetriever(final DBManager dbManager,
			final IFileContentProvider<E> fileContentProvider,
			final ISourceFileParser<E> parser,
			final RetrievedObjectManager<E> manager) {
		this.dbManager = dbManager;
		this.fileContentProvider = fileContentProvider;
		this.parser = parser;
		this.manager = manager;
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
			Version<E> startVersion = manager.getVersion(dbGenealogy
					.getStartVersion().getId());
			if (startVersion == null) {
				startVersion = retrieveVersionOnlyWithRevision(dbGenealogy
						.getStartVersion());
				manager.add(startVersion);
			}
			genealogy.setStartVersion(startVersion);
		}

		{
			Version<E> endVersion = manager.getVersion(dbGenealogy
					.getEndVersion().getId());
			if (endVersion == null) {
				endVersion = retrieveVersionOnlyWithRevision(dbGenealogy
						.getEndVersion());
				manager.add(endVersion);
			}
			genealogy.setEndVersion(endVersion);
		}

		// set clone class mappings
		for (final DBCloneClassMapping dbCloneClassMapping : dbGenealogy
				.getCloneClassMappings()) {
			CloneClassMapping<E> cloneClassMapping = manager
					.getCloneClassMapping(dbCloneClassMapping.getId());

			if (cloneClassMapping == null) {
				cloneClassMapping = retrieveCloneClassMapping(dbCloneClassMapping);
				manager.add(cloneClassMapping);
			}

			for (final DBCodeFragmentMapping dbCodeFragmentMapping : dbCloneClassMapping
					.getCodeFragmentMappings()) {
				CodeFragmentMapping<E> codeFragmentMapping = manager
						.getCodeFragmentMapping(dbCodeFragmentMapping.getId());

				if (codeFragmentMapping == null) {
					codeFragmentMapping = retrieveCodeFragmentMapping(dbCodeFragmentMapping);
					manager.add(codeFragmentMapping);
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
			CloneClass<E> oldCloneClass = manager.getCloneClass(dbOldCloneClass
					.getId());

			if (oldCloneClass == null) {
				oldCloneClass = retrieveCloneClass(dbOldCloneClass);
				manager.add(oldCloneClass);
			}

			cloneClassMapping.setOldCloneClass(oldCloneClass);
		}

		if (dbNewCloneClass != null) {
			CloneClass<E> newCloneClass = manager.getCloneClass(dbNewCloneClass
					.getId());

			if (newCloneClass == null) {
				newCloneClass = retrieveCloneClass(dbNewCloneClass);
				manager.add(newCloneClass);
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
			CodeFragment<E> codeFragment = manager
					.getCodeFragment(dbCodeFragment.getId());

			if (codeFragment == null) {
				codeFragment = retrieveCodeFragment(dbCodeFragment);
				manager.add(codeFragment);
			}

			cloneClass.addCodeFragment(codeFragment);
			codeFragment.setCloneClass(cloneClass);
		}

		// set version
		Version<E> version = manager.getVersion(dbCloneClass.getVersion()
				.getId());

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
			Segment<E> segment = manager.getSegment(dbSegment.getId());

			if (segment == null) {
				segment = retrieveSegment(dbSegment);
				manager.add(segment);
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

		SourceFile<E> sourceFile = manager.getSourceFile(dbSegment
				.getSourceFile().getId());

		// retrieve source file if it has not been retrieved yet
		if (sourceFile == null) {
			final DBSourceFile dbSourceFile = dbSegment.getSourceFile();

			final DBVersion dbOwnerVersion = dbSegment.getCodeFragment()
					.getCloneClass().getVersion();

			Version<E> ownerVersion = manager
					.getVersion(dbOwnerVersion.getId());
			if (ownerVersion == null) {
				ownerVersion = retrieveVersionOnlyWithRevision(dbOwnerVersion);
				manager.add(ownerVersion);
			}

			sourceFile = retrieveSourceFile(dbSourceFile,
					ownerVersion.getCore());
			manager.add(sourceFile);
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
			CodeFragment<E> oldCodeFragment = manager
					.getCodeFragment(dbCodeFragmentMapping.getOldCodeFragment()
							.getId());
			if (oldCodeFragment == null) {
				oldCodeFragment = retrieveCodeFragment(dbCodeFragmentMapping
						.getOldCodeFragment());
				manager.add(oldCodeFragment);
			}
			codeFragmentMapping.setOldCodeFragment(oldCodeFragment);
		}

		if (dbCodeFragmentMapping.getNewCodeFragment() != null) {
			CodeFragment<E> newCodeFragment = manager
					.getCodeFragment(dbCodeFragmentMapping.getNewCodeFragment()
							.getId());
			if (newCodeFragment == null) {
				newCodeFragment = retrieveCodeFragment(dbCodeFragmentMapping
						.getNewCodeFragment());
				manager.add(newCodeFragment);
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

		Revision revision = manager
				.getRevision(dbVersion.getRevision().getId());

		if (revision == null) {
			try {
				dbManager.getNativeDao(DBRevision.class).refresh(
						dbVersion.getRevision());
			} catch (Exception e) {
				throw new IllegalStateException("cannot refresh the revision "
						+ dbVersion.getRevision().getIdentifier(), e);
			}

			revision = retrieveRevision(dbVersion.getRevision());
			manager.add(revision);
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
