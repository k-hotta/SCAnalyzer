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
 * This class represents the protocol of how to retrieve information of objects.
 * 
 * @author k-hotta
 *
 * @param E
 *            the type of program element
 */
public interface IRetriever<E extends IProgramElement> {

	public CloneGenealogy<E> retrieveCloneGenealogy(
			final DBCloneGenealogy dbGenealogy);

	public CloneClassMapping<E> retrieveCloneClassMapping(
			final DBCloneClassMapping dbCloneClassMapping);

	public CloneClass<E> retrieveCloneClass(final DBCloneClass dbCloneClass);

	public CodeFragment<E> retrieveCodeFragment(
			final DBCodeFragment dbCodeFragment);

	public Segment<E> retrieveSegment(final DBSegment dbSegment);

	public SourceFile<E> retrieveSourceFile(final DBSourceFile dbSourceFile,
			final DBVersion dbVersion);

	public CodeFragmentMapping<E> retrieveCodeFragmentMapping(
			final DBCodeFragmentMapping dbCodeFragmentMapping);

	public Revision retrieveRevision(final DBRevision dbRevision);

}
