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
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSegment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;

/**
 * This is a retriever which does not retrieve any volatile information.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class PersistObjectRetriever<E extends IProgramElement> implements
		IRetriever<E> {

	private final RetrievedObjectManager<E> manager;

	public PersistObjectRetriever(final RetrievedObjectManager<E> manager) {
		this.manager = manager;
	}

	@Override
	public CloneGenealogy<E> retrieveCloneGenealogy(DBCloneGenealogy dbGenealogy) {
		CloneGenealogy<E> result = manager.getGenealogy(dbGenealogy.getId());

		if (result == null) {
			result = new CloneGenealogy<E>(dbGenealogy);
			manager.add(result);
		}

		return result;
	}

	@Override
	public CloneClassMapping<E> retrieveCloneClassMapping(
			DBCloneClassMapping dbCloneClassMapping) {
		CloneClassMapping<E> result = manager
				.getCloneClassMapping(dbCloneClassMapping.getId());

		if (result == null) {
			result = new CloneClassMapping<E>(dbCloneClassMapping);
			manager.add(result);
		}

		return result;
	}

	@Override
	public CloneClass<E> retrieveCloneClass(DBCloneClass dbCloneClass) {
		CloneClass<E> result = manager.getCloneClass(dbCloneClass.getId());

		if (result == null) {
			result = new CloneClass<E>(dbCloneClass);
			manager.add(result);
		}

		return result;
	}

	@Override
	public CodeFragment<E> retrieveCodeFragment(DBCodeFragment dbCodeFragment) {
		CodeFragment<E> result = manager
				.getCodeFragment(dbCodeFragment.getId());

		if (result == null) {
			result = new CodeFragment<E>(dbCodeFragment);
			manager.add(result);
		}

		return result;
	}

	@Override
	public Segment<E> retrieveSegment(DBSegment dbSegment) {
		Segment<E> result = manager.getSegment(dbSegment.getId());

		if (result == null) {
			result = new Segment<E>(dbSegment);
			manager.add(result);
		}

		return result;
	}

	@Override
	public SourceFile<E> retrieveSourceFile(DBSourceFile dbSourceFile,
			DBVersion dbVersion) {
		SourceFile<E> result = manager.getSourceFile(dbSourceFile.getId());

		if (result == null) {
			result = new SourceFile<E>(dbSourceFile);
			manager.add(result);
		}

		return result;
	}

	@Override
	public CodeFragmentMapping<E> retrieveCodeFragmentMapping(
			DBCodeFragmentMapping dbCodeFragmentMapping) {
		CodeFragmentMapping<E> result = manager
				.getCodeFragmentMapping(dbCodeFragmentMapping.getId());

		if (result == null) {
			result = new CodeFragmentMapping<E>(dbCodeFragmentMapping);
			manager.add(result);
		}

		return result;
	}

	@Override
	public Revision retrieveRevision(DBRevision dbRevision) {
		Revision result = manager.getRevision(dbRevision.getId());
		
		if (result == null) {
			result = new Revision(dbRevision);
			manager.add(result);
		}
		
		return result;
	}
}
