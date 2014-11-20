package jp.ac.osaka_u.ist.sdl.scanalyzer.genealogy;

import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.AbstractDataDao;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.DBManager;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.RevisionDao;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.IFileContentProvider;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.ISourceFileParser;

/**
 * This class is for retrieving a clone genealogy with volatile data from
 * database.
 * 
 * @author k-hotta
 *
 * @param E
 *            the type of program element
 */
public class CloneGenealogyRetriever<E extends IProgramElement> {

	private final DBManager dbManager;

	private final IFileContentProvider<E> fileContentProvider;

	private final ISourceFileParser<E> parser;

	public CloneGenealogyRetriever(final DBManager dbManager,
			final IFileContentProvider<E> fileContentProvider,
			final ISourceFileParser<E> parser) {
		this.dbManager = dbManager;
		this.fileContentProvider = fileContentProvider;
		this.parser = parser;
	}

	public CloneGenealogy<E> retrieve(final long id) throws Exception {
		// make sure the deep refreshing is off
		AbstractDataDao.setDeepRefresh(false);

		final DBCloneGenealogy core = dbManager.getCloneGenealogyDao().get(id);

		if (core == null) {
			// cannot find the corresponding genealogy from database
			return null;
		}

		final Map<Long, Segment<E>> segments = new TreeMap<>();
		final Map<Long, CodeFragment<E>> codeFragments = new TreeMap<>();
		final Map<Long, CloneClass<E>> cloneClasses = new TreeMap<>();
		final Map<Long, SourceFile<E>> sourceFiles = new TreeMap<>();

		for (final DBCloneClassMapping dbMapping : core.getCloneClassMappings()) {
			final DBCloneClass dbOldCloneClass = dbMapping.getOldCloneClass();
			final DBCloneClass dbNewCloneClass = dbMapping.getNewCloneClass();

			for (final DBCodeFragment dbCodeFragment : dbOldCloneClass
					.getCodeFragments()) {
				for (final DBSegment dbSegment : dbCodeFragment.getSegments()) {
					if (!segments.containsKey(dbSegment.getId())) {
						final Segment<E> segment = new Segment<E>(dbSegment);

						if (!sourceFiles.containsKey(dbSegment.getSourceFile()
								.getId())) {
							final DBSourceFile dbSourceFile = dbSegment
									.getSourceFile();
							final SourceFile<E> sourceFile = new SourceFile<E>(
									dbSourceFile);

							final DBVersion ownerVersion = dbSegment
									.getCodeFragment().getCloneClass()
									.getVersion();

							final String fileContentsStr = fileContentProvider
									.getFileContent(dbSegment.getCodeFragment()
											.getCloneClass().getVersion(),
											dbSourceFile);
							sourceFile.setContents(parser.parse(sourceFile,
									fileContentsStr).values());
						}
					}
				}
			}
		}

		// TODO
		return null;
	}

	private void refreshRevisionsInVersions(final DBRevision dbRevision)
			throws Exception {
		final RevisionDao dao = dbManager.getRevisionDao();
		dao.refresh(dbRevision);
	}

}
